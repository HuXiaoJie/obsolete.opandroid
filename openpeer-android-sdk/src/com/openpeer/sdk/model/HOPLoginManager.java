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
package com.openpeer.sdk.model;

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
import com.openpeer.sdk.app.HOPHelper;
import com.openpeer.sdk.app.HOPSettingsHelper;
import com.openpeer.sdk.login.HOPLoginDelegate;

import java.util.List;

public class HOPLoginManager implements OPIdentityDelegate,OPAccountDelegate{

    private HOPLoginDelegate mDelegate;

    private static HOPLoginManager instance;

    private boolean mAccountLoggingIn;
    private List<LoginRecord> mLoginRecords;
    AccountStates pendingState;
    String mPendingCommand;
    HOPAccount mAccount;

    private boolean mLoginPerformed;

    private static class LoginRecord {
        long time;
        int result;
        int failureReason;
    }

    public static HOPLoginManager getInstance() {
        if (instance == null) {
            instance = new HOPLoginManager();
        }
        return instance;
    }

    private HOPLoginManager() {
    }

    public static String getAccountLoginUrl() {
        return HOPSettingsHelper.getInstance().getNamespaceGrantServiceUrl();
    }

    public static String getIdentityLoginUrl() {
        return HOPSettingsHelper.getInstance().getOuterFrameUrl();
    }

    public void startLogin() {
        String reloginInfo = HOPDataManager.getInstance().getReloginInfo();
        if (reloginInfo == null || reloginInfo.length() == 0) {
            login(HOPCallManager.getInstance(),
                    HOPConversationManager.getInstance());
        } else {
            relogin(HOPCallManager.getInstance(),
                    HOPConversationManager.getInstance(),
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

        mAccount = HOPAccount.login(this,
                conversationThreadDelegate, callDelegate);
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
        mAccount = HOPAccount.relogin(this,
                conversationThreadDelegate, callDelegate, reloginInfo);
        mAccountLoggingIn = true;
    }

    void startIdentityLogin(String uri) {
        HOPAccountIdentity identity = HOPAccountIdentity.login(uri, mAccount.getAccount(), this);
        identity.setAssociating(true);
        mAccount.addIdentity(identity);
    }

    /**
     * Handle account state ready. If this is a login, it means the primary identity login has completed, so start downloading contacts. If
     * this is a relogin, attach Identity delegates to associated identities and start logging in identities
     *
     * @param account
     */
    void onAccountStateReady(HOPAccount account) {

        HOPDataManager.getInstance().saveAccount(mAccount);

        List<HOPAccountIdentity> identities = account.getAssociatedIdentities();
        if (identities.size() == 0) {
            Log.d("TODO", "Account login FAILED identities empty ");

            return;
        }

        for (HOPAccountIdentity identity : identities) {
            HOPAccountIdentity accountIdentity =mAccount.getIdentity(identity.getID());

            if (!identity.isDelegateAttached()) {// This is relogin
                attachDelegateForIdentity(accountIdentity);
            } else {

                String version = HOPDataManager.getInstance()
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

    public void signout(){
        mAccount.shutdown();
    }
    public void onSignoutComplete(){
        if(mDelegate!=null){
            mDelegate.onSignoutComplete();
        }
    }
    public void onIdentityLoginSucceed(HOPAccountIdentity identity) {
        if (identity.isAssociating()) {
            String version = HOPDataManager.getInstance()
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
            identity.setAssociating(false);
        }
        identity.setAssociating(false);
    }

    public void onIdentityLoginFail(HOPAccountIdentity identity) {
        identity.setAssociating(false);
        identity.setLoggingIn(false);
    }

    /**
     * @return
     */
    public HOPLoginDelegate getListener() {
        // TODO Auto-generated method stub
        return mDelegate;
    }

    public void registerDelegate(HOPLoginDelegate delegate) {
        mDelegate = delegate;
    }

    public void unregisterDelegate() {
        mDelegate = null;
    }

    boolean isIdentityLoginInprog() {
        List<HOPAccountIdentity> associatedIdentities = mAccount.getAssociatedIdentities();
        if (!associatedIdentities.isEmpty()) {
            for (HOPAccountIdentity identity : associatedIdentities) {

            }
        }
        return false;
    }

    public boolean hasUnloggedinIdentities() {
        List<HOPAccountIdentity> identities = mAccount.getAssociatedIdentities();
        for (HOPAccountIdentity identity: identities) {
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
        onAccountStateChanged(mAccount,state);
    }
    void onAccountStateChanged(HOPAccount account,AccountStates state){
        boolean processedByDelegate = mDelegate.onAccountStateChanged(mAccount, state);
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
            onAccountStateReady(account);
            break;
        case AccountState_Shutdown:
            HOPHelper.getInstance().onAccountShutdown();
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
            HOPAccountIdentity accountIdentity = mAccount.getIdentity(identity.getID());
            if (null == accountIdentity) {
                accountIdentity = new HOPAccountIdentity(identity);
                mAccount.addIdentity(accountIdentity);
            }
            if (!accountIdentity.isDelegateAttached()) {
                attachDelegateForIdentity(accountIdentity);
            }
        }
    }

    @Override
    public void onAccountPendingMessageForInnerBrowserWindowFrame(
        OPAccount account) {

        String msg = account.getNextMessageForInnerBrowerWindowFrame();
        String cmd = String.format("javascript:sendBundleToJS(\'%s\')", msg);
        if(mDelegate.onAccountPendingMessageForInnerBrowserWindowFrame(mAccount,cmd)){
            mPendingCommand=null;
        } else {
            mPendingCommand = cmd;
        }
    }
    // ENDof OPAccountDelegate
    // START of OPIdentityDelegate
    @Override
    public void onIdentityStateChanged(OPIdentity identity1, IdentityStates state) {
        HOPAccountIdentity identity=mAccount.getIdentity(identity1.getID());
        onIdentityStateChanged(identity,state);
    }

    void onIdentityStateChanged(HOPAccountIdentity identity, IdentityStates state) {
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
        OPIdentity identity1) {
        HOPAccountIdentity identity=mAccount.getIdentity(identity1.getID());

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
        HOPDataManager.getInstance().onDownloadedRolodexContacts(mAccount.getIdentity(identity.getID()));
    }

    void attachDelegateForIdentity(HOPAccountIdentity identity){
        identity.setAssociating(true);
        identity.attachDelegate(this, HOPSettingsHelper
            .getInstance().getRedirectUponCompleteUrl());
    }

    public void onEnteringForeground(){
        if(pendingState!=null){
            onAccountStateChanged(mAccount, pendingState);
        }

        if(HOPAccount.isAccountReady()) {
            List<HOPAccountIdentity> associatedIdentities = mAccount.getAssociatedIdentities();
            for (HOPAccountIdentity identity:associatedIdentities) {
                if (identity.getPendingState() != null) {
                    onIdentityStateChanged(identity, identity.getPendingState());
                }
            }
        }
    }
    // END of OPIdentityDelegate
}
