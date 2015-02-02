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

import java.util.Hashtable;
import java.util.List;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.openpeer.javaapi.IdentityStates;
import com.openpeer.javaapi.OPAccount;
import com.openpeer.javaapi.OPCallDelegate;
import com.openpeer.javaapi.OPConversationThreadDelegate;
import com.openpeer.javaapi.OPIdentity;
import com.openpeer.javaapi.OPLogLevel;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.sdk.delegates.OPAccountDelegateImpl;
import com.openpeer.sdk.delegates.OPIdentityDelegateImpl;
import com.openpeer.sdk.model.CallManager;
import com.openpeer.sdk.model.ConversationManager;

public class LoginManager {

    private LoginUIListener mListener;

    private static LoginManager instance;

    private boolean mAccountLoggingIn;
    private List<LoginRecord> mLoginRecords;
    Hashtable<Long, OPIdentity> mIdentitiesLoggingIn;

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

        OPAccountDelegateImpl accountDelegate = OPAccountDelegateImpl
                .getInstance();
        OPAccount account = OPAccount.login(accountDelegate,
                conversationThreadDelegate, callDelegate);
        OPDataManager.getInstance().setSharedAccount(account);
        mAccountLoggingIn = true;
        mListener.onStartAccountLogin();

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
        OPAccountDelegateImpl accountDelegate = OPAccountDelegateImpl
                .getInstance();
        OPAccount account = OPAccount.relogin(accountDelegate,
                conversationThreadDelegate, callDelegate, reloginInfo);

        OPDataManager.getInstance().setSharedAccount(account);
        mAccountLoggingIn = true;
        mListener.onStartAccountLogin();
    }

    void startIdentityLogin(String uri) {
        OPAccount account = OPDataManager.getInstance().getSharedAccount();

        OPIdentity identity = new OPIdentity();

        OPIdentityDelegateImpl identityDelegate = OPIdentityDelegateImpl
                .getInstance(null);

        identity = OPIdentity.login(uri, account, identityDelegate);
        identity.setIsLoggingIn(true);
        OPDataManager.getInstance().addIdentity(identity);
        if (mListener != null) {
            mListener.onStartIdentityLogin(identity);
        }
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
                OPIdentityDelegateImpl identityDelegate = OPIdentityDelegateImpl
                        .getInstance(identity);
                identity.setIsAssocaiting(true);
                identity.attachDelegate(identityDelegate, OPSdkConfig
                        .getInstance().getRedirectUponCompleteUrl());
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
        if (mListener != null) {
            mListener.onAccountLoginComplete();
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
        mListener.onIdentityLoginCompleted(identity);

    }

    public void onIdentityLoginFail(OPIdentity identity) {
        if (mListener != null) {
            mListener.onLoginError();

        }
        Intent intent = new Intent(IntentData.ACTION_IDENTITY_SHUTDOWN);
        intent.putExtra(IntentData.PARAM_IDENTITY_URI,
                identity.getIdentityURI());
        OPHelper.getInstance().sendBroadcast(intent);
        identity.setIsAssocaiting(false);
        identity.setIsLoggingIn(false);
        removeLoggingInIdentity(identity);

    }

    /**
     * @return
     */
    public LoginUIListener getListener() {
        // TODO Auto-generated method stub
        return mListener;
    }

    public void registerListener(LoginUIListener listener) {
        mListener = listener;
    }

    public void unregisterListener() {
        mListener = null;
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
}
