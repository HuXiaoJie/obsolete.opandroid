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
package com.openpeer.sample;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.openpeer.javaapi.OPIdentity;
import com.openpeer.sample.push.HackApiService;
import com.openpeer.sample.push.OPPushManager;
import com.openpeer.sdk.app.LoginManager;
import com.openpeer.sdk.app.LoginUIListener;
import com.openpeer.sdk.app.OPAccountLoginWebViewClient;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.app.OPIdentityLoginWebViewClient;
import com.openpeer.sdk.app.OPIdentityLoginWebview;
import com.urbanairship.push.PushManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginFragment extends BaseFragment implements LoginUIListener {
    WebView mAccountLoginWebView;

    OPIdentityLoginWebview mIdentityLoginWebView;
    View progressView;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, null);
        progressView = view.findViewById(R.id.progress);
        mAccountLoginWebView = (WebView) view.findViewById(R.id.webview_account_login);
        mAccountLoginWebView.setWebViewClient(new OPAccountLoginWebViewClient());
        //TODO: Dynamic generate and attach webview
        mIdentityLoginWebView = (OPIdentityLoginWebview) view.findViewById(R.id.webview_identity_login);
        mIdentityLoginWebView.setClient(new OPIdentityLoginWebViewClient(null));

        setupWebView(mAccountLoginWebView);
        setupWebView(mIdentityLoginWebView);

        startLogin();
        return view;
    }

    void startLogin() {
        String reloginInfo = OPDataManager.getInstance().getReloginInfo();
        if (reloginInfo == null || reloginInfo.length() == 0) {
            LoginManager.getInstance()
                    .login(LoginFragment.this,
                            OPSessionManager.getInstance().getCallDelegate(),
                            OPSessionManager.getInstance().getConversationThreadDelegate());
        } else {
            LoginManager.getInstance()
                    .relogin(LoginFragment.this,
                            OPSessionManager.getInstance().getCallDelegate(),
                            OPSessionManager.getInstance().getConversationThreadDelegate(),
                            reloginInfo);
        }
    }

    void setupWebView(WebView view) {
        WebSettings webSettings = view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

    }

    /* START implementation of LoginUIListener */
    @Override
    public void onStartIdentityLogin() {
        progressView.setVisibility(View.GONE);
        mAccountLoginWebView.setVisibility(View.GONE);

        mIdentityLoginWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoginComplete() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (!isDetached()) {
                    ((BaseFragmentActivity) getActivity()).hideLoginFragment();
                }
            }
        });
        PushManager.enablePush();

        //TODO: move it to proper place after login refactoring.
        String apid = PushManager.shared().getAPID();
        if (!TextUtils.isEmpty(apid)) {
            OPPushManager.getInstance().associateDeviceToken(OPDataManager.getInstance().getSharedAccount().getPeerUri(),
                    PushManager.shared().getAPID(),
                    new Callback<HackApiService.HackAssociateResult>() {
                        @Override
                        public void success(HackApiService.HackAssociateResult hackAssociateResult, Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    }
            );
        }
    }

    @Override
    public void onLoginError() {
        if (!isDetached()) {
            Toast.makeText(getActivity(), R.string.msg_failed_login, Toast.LENGTH_LONG).show();

            ((BaseFragmentActivity) getActivity()).hideLoginFragment();
        }
    }

    @Override
    public void onIdentityLoginWebViewMadeVisible() {
        mIdentityLoginWebView.setVisibility(View.VISIBLE);
        mAccountLoginWebView.setVisibility(View.GONE);
        progressView.setVisibility(View.GONE);

    }

    @Override
    public void onAccountLoginWebViewMadeVisible() {
        progressView.setVisibility(View.GONE);
        mIdentityLoginWebView.setVisibility(View.GONE);
        mAccountLoginWebView.setVisibility(View.VISIBLE);
    }

    public void onIdentityLoginWebViewClose() {
        mIdentityLoginWebView.setVisibility(View.GONE);
    }

    public void onAccountLoginWebViewMadeClose() {
        mAccountLoginWebView.setVisibility(View.GONE);
    }

    @Override
    public WebView getAccountWebview() {
        return mAccountLoginWebView;
    }

	@Override
	public OPIdentityLoginWebview getIdentityWebview(OPIdentity identity) {
		return mIdentityLoginWebView;
	}
    /* END implementation of LoginUIListener */

}
