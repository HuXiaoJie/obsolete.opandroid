/*******************************************************************************
 *
 *  Copyright (c) 2014 , Hookflash Inc.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those
 *  of the authors and should not be interpreted as representing official policies,
 *  either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
package com.openpeer.sdk.app;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.openpeer.javaapi.AccountStates;
import com.openpeer.javaapi.IdentityStates;
import com.openpeer.javaapi.OPAccount;
import com.openpeer.javaapi.OPAccountDelegate;
import com.openpeer.javaapi.OPCallDelegate;
import com.openpeer.javaapi.OPConversationThreadDelegate;
import com.openpeer.javaapi.OPIdentity;
import com.openpeer.javaapi.OPIdentityDelegate;
import com.openpeer.javaapi.OPLogLevel;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.sdk.model.CallManager;
import com.openpeer.sdk.model.ConversationManager;

import java.util.Hashtable;
import java.util.List;

public class LoginManager implements OPIdentityDelegate,OPAccountDelegate{

    private LoginDelegate mDelegate;

    private static LoginManager instance;

    private boolean mAccountLoggingIn;
    private List<LoginRecord> mLoginRecords;
    Hashtable<Long, OPIdentity> mIdentitiesLoggingIn;
    AccountStates pendingState;
    String mPendingCommand;

    private boolean mLoginPerformed;

    private static class LoginRecord {
        long time;
        int result;
        int failureReason;
    }

    public static LoginManager getInstance() {
        if (instance == null) {
            instance = new LoginManager();
        }
        return instance;
    }

    private LoginManager() {
    }

    public static String getAccountLoginUrl() {
        return OPSdkConfig.getInstance().getNamespaceGrantServiceUrl();
    }

    public static String getIdentityLoginUrl() {
        return OPSdkConfig.getInstance().getOuterFrameUrl();
    }

    public void startLogin() {
        String reloginInfo = OPDataManager.getInstance().getReloginInfo();
        if (reloginInfo == null || reloginInfo.length() == 0) {
            login(CallManager.getInstance(),
                    ConversationManager.getInstance());
        } else {
            relogin(CallManager.getInstance(),
                    ConversationManager.getInstance(),
                    reloginInfo);
        }
        mLoginPerformed = true;
    }

    /**
     *
     * @param callDelegate
     *            Global call delegate implementation. The object MUST be kept valid throughout the app lifecycle.
     * @param conversationThreadDelegate
     *            Global conversation thread delegate implementation. The object MUST be kept valid throughout the app lifecycle.
     */
    void login(
            OPCallDelegate callDelegate,
            OPConversationThreadDelegate conversationThreadDelegate) {

        OPAccount account = OPAccount.login(this,
                conversationThreadDelegate, callDelegate);
        OPDataManager.getInstance().setSharedAccount(account);
        mAccountLoggingIn = true;
        startIdentityLogin(null);
    }

    /**
     *

     * @param callDelegate
     *            Global call delegate implementation. The object MUST be kept valid throughout the app lifecycle.
     * @param conversationThreadDelegate
     *            Global conversation thread delegate implementation. The object MUST be kept valid throughout the app lifecycle.
     *
     * @param reloginInfo
     *            relogin jason blob stored from last login session
     */

    void relogin(
            OPCallDelegate callDelegate,
            OPConversationThreadDelegate conversationThreadDelegate,
            String reloginInfo) {
        OPAccount account = OPAccount.relogin(this,
                conversationThreadDelegate, callDelegate, reloginInfo);

        OPDataManager.getInstance().setSharedAccount(account);
        mAccountLoggingIn = true;
    }

    void startIdentityLogin(String uri) {
        OPAccount account = OPDataManager.getInstance().getSharedAccount();

        OPIdentity identity = OPIdentity.login(uri, account, this);
        identity.setIsLoggingIn(true);
        OPDataManager.getInstance().addIdentity(identity);

    }

    /**
     * Handle account state ready. If this is a login, it means the primary identity login has completed, so start downloading contacts. If
     * this is a relogin, attach Identity delegates to associated identities and start logging in identities
     *
     * @param account
     */
    public void onAccountStateReady(OPAccount account) {

        OPDataManager.getInstance().saveAccount();

        List<OPIdentity> identities = account.getAssociatedIdentities();
        if (identities.size() == 0) {
            Log.d("TODO", "Account login FAILED identities empty ");

            return;
        }

        for (OPIdentity identity : identities) {
            if (identity.getState() != IdentityStates.IdentityState_Ready) {
                addIdentityLoggingIn(identity);
            }
            if (!identity.isDelegateAttached()) {// This is relogin

                attachDelegateForIdentity(identity);
                OPDataManager.getInstance().addIdentity(identity);

            } else {

                String version = OPDataManager.getInstance()
                    .getDownloadedContactsVersion(identity.getIdentityURI());
                if (TextUtils.isEmpty(version)) {
                    OPLogger.debug(OPLogLevel.LogLevel_Detail,
                            "start download initial contacts");
                    identity.startRolodexDownload("");
                } else {
                    // check for new contacts
                    OPLogger.debug(OPLogLevel.LogLevel_Detail,
                            "start download  contacts since version " + version);
                    identity.startRolodexDownload(version);
                }
            }
        }
        if (!isIdentityLoginInprog()) {
            mAccountLoggingIn = false;
        }
    }

    public static void onLoginComplete() {
    }

    /**
     * If login in progress
     *
     * @return
     */
    public boolean isLoggingIn() {
        return mAccountLoggingIn || isIdentityLoginInprog();
    }

    /**
     * Handle account shutdown state change.
     */
    public void onAccountShutdown() {
        // release resources
    }

    public void onSignoutComplete(){
        if(mDelegate!=null){
            mDelegate.onSignoutComplete();
        }
    }
    public void onIdentityLoginSucceed(OPIdentity identity) {
        if (identity.isAssociating()) {
            String version = OPDataManager.getInstance()
                .getDownloadedContactsVersion(identity.getIdentityURI());
            if (TextUtils.isEmpty(version)) {
                OPLogger.debug(OPLogLevel.LogLevel_Detail,
                        "start download initial contacts");
                identity.startRolodexDownload("");
            } else {
                // check for new contacts
                OPLogger.debug(OPLogLevel.LogLevel_Detail,
                        "start download initial contacts");
                identity.startRolodexDownload(version);
            }
            identity.setIsAssocaiting(false);
        }
        identity.setIsLoggingIn(false);
    }

    public void onIdentityLoginFail(OPIdentity identity) {
        identity.setIsAssocaiting(false);
        identity.setIsLoggingIn(false);
        removeLoggingInIdentity(identity);
    }

    /**
     * @return
     */
    public LoginDelegate getListener() {
        // TODO Auto-generated method stub
        return mDelegate;
    }

    public void registerDelegate(LoginDelegate delegate) {
        mDelegate = delegate;
    }

    public void unregisterDelegate() {
        mDelegate = null;
    }

    public void addIdentityLoggingIn(OPIdentity identity) {
        if (mIdentitiesLoggingIn == null) {
            mIdentitiesLoggingIn = new Hashtable<Long, OPIdentity>();
        }
        mIdentitiesLoggingIn.put(identity.getID(), identity);
    }

    void removeLoggingInIdentity(OPIdentity identity) {
        if (mIdentitiesLoggingIn != null) {
            mIdentitiesLoggingIn.remove(identity.getID());
        }
    }

    boolean isIdentityLoginInprog() {
        return mIdentitiesLoggingIn != null && !mIdentitiesLoggingIn.isEmpty();
    }

    public boolean hasUnloggedinIdentities() {
        List<OPIdentity> identities = OPDataManager.getInstance().getSharedAccount()
            .getAssociatedIdentities();
        for (OPIdentity identity : identities) {
            if (identity.getState() != IdentityStates.IdentityState_Ready) {
                return true;
            }
        }
        return false;
    }


    /**
     *
     */
    public void afterSignout() {
        mIdentitiesLoggingIn = null;
        mAccountLoggingIn = false;
    }

    /**
     * @return
     */
    public boolean loginPerformed() {
        // TODO Auto-generated method stub
        return mLoginPerformed;
    }

    // START of OPAccountDelegate
    @Override
    public void onAccountStateChanged(OPAccount account, AccountStates state) {
        boolean processedByDelegate = mDelegate.onAccountStateChanged(account, state);
        if (!processedByDelegate) {
            pendingState = state;
            return;
        }
        pendingState = null;
        switch (state){
        case AccountState_WaitingForAssociationToIdentity:
            break;
        case AccountState_WaitingForBrowserWindowToBeLoaded:
            break;
        case AccountState_WaitingForBrowserWindowToBeMadeVisible:
            account.notifyBrowserWindowVisible();
            break;
        case AccountState_WaitingForBrowserWindowToClose:
            account.notifyBrowserWindowClosed();
            break;
        case AccountState_Ready:
            LoginManager.getInstance().onAccountStateReady(account);
            break;
        case AccountState_Shutdown:
            OPHelper.getInstance().onAccountShutdown();
            break;
        default:
            break;
        }

    }
    @Override
    public void onAccountAssociatedIdentitiesChanged(OPAccount account) {
        OPLogger.debug(OPLogLevel.LogLevel_Debug,
                       "onAccountAssociatedIdentitiesChanged");

        List<OPIdentity> identities = account.getAssociatedIdentities();
        if (identities == null) {
            OPLogger.error(OPLogLevel.LogLevel_Debug,
                           "onAccountAssociatedIdentitiesChanged getAssociatedIdentities is null");
            return;
        }

        for (OPIdentity identity : identities) {
            if (null == OPDataManager.getInstance().getStoredIdentityById(
                identity.getID())) {
                OPDataManager.getInstance().addIdentity(identity);
            }
            if (!identity.isDelegateAttached()) {
                attachDelegateForIdentity(identity);
                OPDataManager.getInstance().addIdentity(identity);
            }
        }
    }

    @Override
    public void onAccountPendingMessageForInnerBrowserWindowFrame(
        OPAccount account) {

        String msg = account.getNextMessageForInnerBrowerWindowFrame();
        String cmd = String.format("javascript:sendBundleToJS(\'%s\')", msg);
        if(mDelegate.onAccountPendingMessageForInnerBrowserWindowFrame(account,cmd)){
            mPendingCommand=null;
        } else {
            mPendingCommand = cmd;
        }
    }
    // ENDof OPAccountDelegate
    // START of OPIdentityDelegate
    @Override
    public void onIdentityStateChanged(OPIdentity identity, IdentityStates state) {
        boolean processedByDelegate = mDelegate.onIdentityStateChanged(identity, state);
        if (!processedByDelegate) {
            identity.setPendingState(state);
            return;
        }
        switch (state){
        case IdentityState_PendingAssociation:
            break;
        case IdentityState_WaitingAttachmentOfDelegate:
            break;
        case IdentityState_WaitingForBrowserWindowToBeLoaded:
            break;

        case IdentityState_WaitingForBrowserWindowToBeMadeVisible:
            identity.notifyBrowserWindowVisible();
            break;
        case IdentityState_WaitingForBrowserWindowToClose:
            //we assume the brower window is already closed.
            identity.notifyBrowserWindowClosed();
            break;
        case IdentityState_Ready:
            onIdentityLoginSucceed(identity);
            break;
        case IdentityState_Shutdown:
            onIdentityLoginFail(identity);
            break;
        default:
            break;
        }
    }

    @Override
    public void onIdentityPendingMessageForInnerBrowserWindowFrame(
        OPIdentity identity) {
        String msg = identity.getNextMessageForInnerBrowerWindowFrame();
        String cmd = String.format("javascript:sendBundleToJS(\'%s\')", msg);
        Log.w("login", "Identity webview Pass to JS: " + cmd);
        if(!mDelegate.onIdentityPendingMessageForInnerBrowserWindowFrame(identity,cmd)){
            identity.setPendingCommand(cmd);
        } else {
            identity.setPendingCommand(null);
        }
    }

    @Override
    public void onIdentityRolodexContactsDownloaded(OPIdentity identity) {
        OPDataManager.getInstance().onDownloadedRolodexContacts(identity);
    }

    void attachDelegateForIdentity(OPIdentity identity){
        identity.setIsAssocaiting(true);
        identity.attachDelegate(this, OPSdkConfig
            .getInstance().getRedirectUponCompleteUrl());
    }

    public void onEnteringForeground(){
        if(pendingState!=null){
            onAccountStateChanged(OPDataManager.getInstance().getSharedAccount(), pendingState);
        }

        if(OPDataManager.getInstance().isAccountReady()) {
            for (OPIdentity identity : OPDataManager.getInstance().getSharedAccount().getAssociatedIdentities()) {
                if (identity.getPendingState() != null) {
                    onIdentityStateChanged(identity, identity.getPendingState());
                }
            }
        }
    }
    // END of OPIdentityDelegate
}
