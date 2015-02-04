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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.openpeer.javaapi.OPIdentity;
import com.openpeer.sample.push.HackApiService;
import com.openpeer.sample.push.OPPushManager;
import com.openpeer.sample.push.parsepush.PFPushService;
import com.openpeer.sample.util.SettingsHelper;
import com.openpeer.sdk.app.LoginDelegate;
import com.openpeer.sdk.app.LoginManager;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.app.OPHelper;
import com.openpeer.sdk.app.OPIdentityLoginWebViewClient;
import com.openpeer.sdk.app.OPIdentityLoginWebview;
import com.urbanairship.push.PushManager;

import java.util.Hashtable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BaseActivity extends BaseFragmentActivity implements LoginDelegate{

    private static int mStack = 0;

    private ProgressDialog mSignoutDialog;

    @Override
    public void onResume() {
        super.onResume();
        LoginManager.getInstance().registerDelegate(this);
        if (mStack == 0) {
            OPHelper.getInstance().onEnteringForeground();
            LoginManager.getInstance().onEnteringForeground();
            BackgroundingManager.onEnteringForeground();
        }
        mStack++;

        if (OPHelper.getInstance().isSigningOut()) {
            showSignoutView();
        } else if (!OPDataManager.getInstance().isAccountReady()) {

            if (!LoginManager.getInstance().loginPerformed()) {
                LoginManager.getInstance().startLogin();
            } else if (!LoginManager.getInstance().isLoggingIn()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(
                        "Looks like you're disconnected. Do you want to login?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        LoginManager.getInstance().startLogin();
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        dialog.dismiss();

                                    }
                                }).create().show();

            }
        }

    }

    /**
     * 
     */
    protected void showSignoutView() {
        mSignoutDialog = new ProgressDialog(this);
        mSignoutDialog.setMessage("Signing out...");
        mSignoutDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        mStack--;
        if (mStack == 0) {
            OPHelper.getInstance().onEnteringBackground();
            BackgroundingManager.onEnteringBackground();
        }
        LoginManager.getInstance().unregisterDelegate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void showInvalidStateWarning(Context context) {
        Toast.makeText(context, R.string.msg_not_logged_in, Toast.LENGTH_LONG)
                .show();
    }

    //BEGINNING of LoginDelegateImpl
    WebView mAccountLoginWebView;
    ViewGroup mLoginViewContainer;
    ProgressDialog progressDialog;
    Hashtable<Long, OPIdentityLoginWebview> mIdentityWebviews = new Hashtable<Long, OPIdentityLoginWebview>();


    ViewGroup getLoginViewContainer() {
        if (mLoginViewContainer == null) {
            mLoginViewContainer = (ViewGroup) findViewById(R.id.fragment_login);
        }
        return mLoginViewContainer;
    }

    void showProgressView(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
//            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);

        progressDialog.show();
    }

    public OPIdentityLoginWebview getIdentityWebview(OPIdentity identity) {
        OPIdentityLoginWebview view = mIdentityWebviews.get(identity.getID());
        if (view == null) {
            view = new OPIdentityLoginWebview(getLoginViewContainer().getContext());
            OPIdentityLoginWebViewClient client = new OPIdentityLoginWebViewClient(identity);
            view.setClient(client);
            setupWebView(view);
            getLoginViewContainer().addView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
            mIdentityWebviews.put(identity.getID(), view);
        }
        return view;
    }

    public WebView getAccountWebview() {
        if (mAccountLoginWebView == null) {
            mAccountLoginWebView = new WebView(getLoginViewContainer().getContext());
            setupWebView(mAccountLoginWebView);
            getLoginViewContainer().addView(
                mAccountLoginWebView,
                new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
        return mAccountLoginWebView;
    }

    void hideProgressView() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /* START implementation of LoginUIListener */
    @Override
    public void onStartIdentityLogin(OPIdentity identity) {
        showProgressView(this.getString(R.string.msg_identity_login_started));
    }

    @Override
    public void onAccountLoginComplete() {
        hideProgressView();
        getLoginViewContainer().removeView(mAccountLoginWebView);
        Toast.makeText(this, R.string.msg_account_login_completed, Toast.LENGTH_LONG)
            .show();
        if(SettingsHelper.getInstance().isParsePushEnabled() && !PFPushService.getInstance().isInitialized()){
            PFPushService.getInstance().init();
        }
        if(SettingsHelper.getInstance().isUAPushEnabled()) {
            PushManager.enablePush();

            // TODO: move it to proper place after login refactoring.
            String apid = PushManager.shared().getAPID();
            if (!TextUtils.isEmpty(apid)) {
                OPPushManager.getInstance()
                    .associateDeviceToken(
                        OPDataManager.getInstance().getCurrentUser().getPeerUri(),
                        PushManager.shared().getAPID(),
                        new Callback<HackApiService.HackAssociateResult>() {
                            @Override
                            public void success(
                                HackApiService.HackAssociateResult hackAssociateResult,
                                Response response) {

                            }

                            @Override
                            public void failure(RetrofitError error) {
                            }
                        }
                    );
            }
        }
    }

    @Override
    public void onLoginError() {
        if (!getLoginViewContainer().hasWindowFocus()
            && mAccountLoginWebView != null) {
            getLoginViewContainer().removeView(mAccountLoginWebView);

            Toast.makeText(getLoginViewContainer().getContext(),
                           R.string.msg_failed_login,
                           Toast.LENGTH_LONG).show();

            // ((BaseFragmentActivity) getActivity()).hideLoginFragment();
        }
    }

    @Override
    public void onIdentityLoginWebViewMadeVisible(OPIdentity identity) {
        hideProgressView();
        OPIdentityLoginWebview view = getIdentityWebview(identity);
        view.setVisibility(View.VISIBLE);
        view.bringToFront();
    }

    @Override
    public void onAccountLoginWebViewMadeVisible() {
        hideProgressView();
        if (mAccountLoginWebView == null) {
            mAccountLoginWebView = new WebView(getLoginViewContainer()
                                                   .getContext());
            setupWebView(mAccountLoginWebView);
            getLoginViewContainer().addView(
                mAccountLoginWebView,
                new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            getLoginViewContainer().bringToFront();
        }
    }

    @Override
    public void onIdentityLoginWebViewClose(OPIdentity identity) {
        OPIdentityLoginWebview view = getIdentityWebview(identity);
        if (view != null) {
            getLoginViewContainer().removeView(view);
        }
    }

    @Override
    public void onAccountLoginWebViewMadeClose() {
        getLoginViewContainer().removeView(mAccountLoginWebView);
        mAccountLoginWebView = null;

    }

    /* END implementation of LoginUIListener */

    void setupWebView(WebView view) {
        WebSettings webSettings = view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.openpeer.sdk.app.LoginUIListener#onIdentityLoginCompleted(com.openpeer.javaapi.OPIdentity)
     */
    @Override
    public void onIdentityLoginCompleted(OPIdentity identity) {
        hideProgressView();
        Toast.makeText(
            this,
            this.getString(R.string.msg_identity_login_completed)
                + identity.getIdentityURI(),
            Toast.LENGTH_LONG).show();
    }

    @Override
    public void onIdentityLoginFail(OPIdentity identity) {
        hideProgressView();
        OPIdentityLoginWebview view = getIdentityWebview(identity);
        if (view != null) {
            getLoginViewContainer().removeView(view);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.openpeer.sdk.app.LoginUIListener#onStartAccountLogin()
     */
    @Override
    public void onStartAccountLogin() {
        showProgressView(this.getString(R.string.msg_account_login_started));
    }

    @Override
    public void onSignoutComplete() {
        if (mSignoutDialog != null) {
            mSignoutDialog.dismiss();
            mSignoutDialog = null;
        }

        MainActivity.cleanLaunch(this);
    }
    //End of LoginDelegateImpl

}
