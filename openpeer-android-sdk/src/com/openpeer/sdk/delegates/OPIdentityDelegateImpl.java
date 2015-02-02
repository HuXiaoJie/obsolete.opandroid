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

import android.util.Log;

import com.openpeer.javaapi.IdentityStates;
import com.openpeer.javaapi.OPIdentity;
import com.openpeer.javaapi.OPIdentityDelegate;
import com.openpeer.javaapi.OPLogLevel;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.sdk.app.LoginManager;
import com.openpeer.sdk.app.LoginUIListener;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.app.OPIdentityLoginWebview;
import com.openpeer.sdk.app.OPSdkConfig;

public class OPIdentityDelegateImpl implements OPIdentityDelegate {

    private static OPIdentityDelegateImpl instance;

    public static OPIdentityDelegateImpl getInstance(OPIdentity identity) {
        if (instance == null) {
            instance = new OPIdentityDelegateImpl();
        }

        return instance;
    }

    private OPIdentityDelegateImpl() {
    }

    @Override
    public void onIdentityStateChanged(OPIdentity identity, IdentityStates state) {
        // TODO Auto-generated method stub
        Log.d("login", "identity state " + state);
        // why isn' this working? Weird!!
        // mLoginView = mListener.getIdentityWebview(identity);
        // mLoginView.getClient().setIdentity(identity);
        LoginUIListener mListener = LoginManager.getInstance().getListener();
        if (mListener == null) {
            OPLogger.error(OPLogLevel.LogLevel_Debug,
                    "No UI listener while state change " + state);

        }
        switch (state) {
        case IdentityState_PendingAssociation:
            break;
        case IdentityState_WaitingAttachmentOfDelegate:
            break;
        case IdentityState_WaitingForBrowserWindowToBeLoaded: {
            if (mListener == null) {
                return;
            }
            Log.d("login", "loading identity webview");
            OPIdentityLoginWebview mLoginView = mListener
                    .getIdentityWebview(identity);
            mLoginView.loadUrl(OPSdkConfig.getInstance().getOuterFrameUrl());
            break;
        }

        case IdentityState_WaitingForBrowserWindowToBeMadeVisible:

            mListener.onIdentityLoginWebViewMadeVisible(identity);

            identity.notifyBrowserWindowVisible();
            break;
        case IdentityState_WaitingForBrowserWindowToClose:

            mListener.onIdentityLoginWebViewClose(identity);

            identity.notifyBrowserWindowClosed();
            break;
        case IdentityState_Ready:
            LoginManager.getInstance().onIdentityLoginSucceed(identity);
            break;
        case IdentityState_Shutdown:
            // Temporary defensive code. Proper logic will be put in place soon.
            LoginManager.getInstance().onIdentityLoginFail(identity);

            break;
        default:
            break;
        }
    }

    @Override
    public void onIdentityPendingMessageForInnerBrowserWindowFrame(
            OPIdentity identity) {
        // TODO Auto-generated method stub
        String msg = identity.getNextMessageForInnerBrowerWindowFrame();
        Log.d("login", "identity pendingMessageForInnerFrame " + msg);

        String cmd = String.format("javascript:sendBundleToJS(\'%s\')", msg);
        Log.w("login", "Identity webview Pass to JS: " + cmd);
        OPIdentityLoginWebview mLoginView = LoginManager.getInstance()
                .getListener().getIdentityWebview(identity);
        mLoginView.loadUrl(cmd);
    }

    @Override
    public void onIdentityRolodexContactsDownloaded(OPIdentity identity) {
        OPDataManager.getInstance().onDownloadedRolodexContacts(identity);
    }

    public static void clearAfterSignout() {
    }
}
