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
package com.openpeer.sdk.delegates;

import java.util.List;

import android.util.Log;

import com.openpeer.javaapi.AccountStates;
import com.openpeer.javaapi.OPAccount;
import com.openpeer.javaapi.OPAccountDelegate;
import com.openpeer.javaapi.OPIdentity;
import com.openpeer.javaapi.OPLogLevel;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.sdk.app.LoginManager;
import com.openpeer.sdk.app.LoginUIListener;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.app.OPHelper;
import com.openpeer.sdk.app.OPIdentityLoginWebview;
import com.openpeer.sdk.app.OPSdkConfig;

/**
 * @ExcludeFromJavadoc Default implmentation of OPAccountDelegate. This will be created as singleton and will be passed in
 *                     LoginManager.login
 */
public class OPAccountDelegateImpl implements OPAccountDelegate {
    private static OPAccountDelegateImpl instance;

    /**
     * Create and hold on the instance
     * 
     * @return
     */
    public static OPAccountDelegateImpl getInstance() {
        if (instance == null) {
            instance = new OPAccountDelegateImpl();
        }
        return instance;
    }

    private OPAccountDelegateImpl() {
    }

    @Override
    public void onAccountStateChanged(OPAccount account, AccountStates state) {
        Log.d("state", "Account state " + state);
        final LoginUIListener mListener = LoginManager.getInstance()
                .getListener();
        switch (state) {
        case AccountState_WaitingForAssociationToIdentity:
            break;
        case AccountState_WaitingForBrowserWindowToBeLoaded:

            mListener.getAccountWebview().loadUrl(
                    OPSdkConfig.getInstance().getNamespaceGrantServiceUrl());

            break;
        case AccountState_WaitingForBrowserWindowToBeMadeVisible:

            mListener.onAccountLoginWebViewMadeVisible();

            account.notifyBrowserWindowVisible();
            break;
        case AccountState_WaitingForBrowserWindowToClose:

            mListener.onAccountLoginWebViewMadeClose();

            account.notifyBrowserWindowClosed();
            break;
        case AccountState_Ready:
            Log.w("login", "Account READY !!!!!!!!!!!!");
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

        LoginUIListener listener = LoginManager.getInstance().getListener();
        for (OPIdentity identity : identities) {
            if (null == OPDataManager.getInstance().getStoredIdentityById(
                    identity.getID())) {
                OPDataManager.getInstance().addIdentity(identity);
            }
            if (!identity.isDelegateAttached()) {
                OPIdentityDelegateImpl delegate = OPIdentityDelegateImpl
                        .getInstance(identity);

                identity.attachDelegate(delegate, OPSdkConfig.getInstance()
                        .getRedirectUponCompleteUrl());
                OPDataManager.getInstance().addIdentity(identity);
            }
        }
    }

    @Override
    public void onAccountPendingMessageForInnerBrowserWindowFrame(
            OPAccount account) {

        String msg = account.getNextMessageForInnerBrowerWindowFrame();
        passMessageToJS(msg);
    }

    void passMessageToJS(final String msg) {
        final LoginUIListener mListener = LoginManager.getInstance()
                .getListener();
        String cmd = String.format("javascript:sendBundleToJS(\'%s\')", msg);
        mListener.getAccountWebview().loadUrl(cmd);

    }
}