/**
 * Copyright (c) 2014, SMB Phone Inc. / Hookflash Inc.
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p/>
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package com.openpeer.sample.login;

import com.openpeer.javaapi.AccountStates;
import com.openpeer.javaapi.IdentityStates;
import com.openpeer.javaapi.OPAccount;
import com.openpeer.javaapi.OPIdentity;
import com.openpeer.sample.events.SignoutCompleteEvent;
import com.openpeer.sdk.login.HOPLoginDelegate;
import com.openpeer.sdk.login.HOPLoginManager;

/**
 * The listener monitor the Account/Identity login state changes and show appropriate UI
 * indications. Activity should register this to the
 * LoginManager upon creation and unregister upon destroy
 */
public class LoginDelegateImpl implements HOPLoginDelegate {
    LoginViewHandler viewHandler;//The actual activity that implements the same interface

    private static LoginDelegateImpl instance;

    public static LoginDelegateImpl getInstance() {
        if (instance == null) {
            instance = new LoginDelegateImpl();
        }
        return instance;
    }

    private LoginDelegateImpl() {
    }
    public void registerViewHandler(LoginViewHandler handler) {
        viewHandler = handler;
    }
    public void unregisterViewHandler(LoginViewHandler handler){
        viewHandler = null;
    }

    public boolean onAccountStateChanged(OPAccount account, AccountStates state) {
        switch (state){
        case AccountState_WaitingForAssociationToIdentity:
            break;
        case AccountState_WaitingForBrowserWindowToBeLoaded:
            if (viewHandler != null) {
                viewHandler.loadAccountLoginUrl(HOPLoginManager.getAccountLoginUrl());
                return true;
            }
            return false;
        case AccountState_WaitingForBrowserWindowToBeMadeVisible:
            if (viewHandler != null) {
                viewHandler.showAccountLoginWebView();
                return true;
            }
            return false;
        case AccountState_WaitingForBrowserWindowToClose:
            if (viewHandler != null) {
                viewHandler.closeAccountLoginWebView();
            }
            return true;
        case AccountState_Ready:
            if (viewHandler != null) {
                viewHandler.onAccountLoginComplete();
            }
            return true;
        case AccountState_Shutdown:
            if (viewHandler != null) {
                viewHandler.onAccountShutdown();
            }
            return true;
        default:
            break;
        }
        return false;
    }

    public boolean onIdentityStateChanged(OPIdentity identity, IdentityStates state) {
        switch (state){
        case IdentityState_PendingAssociation:
            return true;
        case IdentityState_WaitingAttachmentOfDelegate:
            return true;
        case IdentityState_WaitingForBrowserWindowToBeLoaded:{
            if (viewHandler != null) {
                viewHandler.loadIdentityLoginUrl(identity, HOPLoginManager.getIdentityLoginUrl());
                return true;
            }
            return false;
        }
        case IdentityState_WaitingForBrowserWindowToBeMadeVisible:
            if (viewHandler != null) {

                viewHandler.showIdentityLoginWebView(identity);
                return true;
            }
            return false;
        case IdentityState_WaitingForBrowserWindowToClose:
            if (viewHandler != null) {
                viewHandler.closeIdentityLoginWebView(identity);
                return true;
            }
            return false;
        case IdentityState_Ready:
            if (viewHandler != null) {
                viewHandler.onIdentityReady(identity);
            }
            return true;
        case IdentityState_Shutdown:
            if (viewHandler != null) {
                viewHandler.onIdentityShutdown(identity);
            }
            return true;
        default:
            return true;
        }
    }

    public boolean onAccountPendingMessageForInnerBrowserWindowFrame(OPAccount account,
                                                                     String message) {
        if (viewHandler != null) {
            viewHandler.processAccountMessageForInnerBrowserWindowFrame(account, message);
            return true;
        }
        return false;
    }

    @Override
    public boolean onIdentityPendingMessageForInnerBrowserWindowFrame(OPIdentity identity,
                                                                      String message) {
        if (viewHandler != null) {
            viewHandler.processIdentityMessageForInnerBrowserWindowFrame(identity, message);
            return true;
        }
        return false;
    }

    @Override
    public void onSignoutComplete() {
        new SignoutCompleteEvent().post();
    }
}
