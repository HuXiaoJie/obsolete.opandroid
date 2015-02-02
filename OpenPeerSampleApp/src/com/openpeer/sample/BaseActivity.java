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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.openpeer.javaapi.AccountStates;
import com.openpeer.sample.login.LoginUIListenerImpl;
import com.openpeer.sdk.app.LoginManager;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.app.OPHelper;

public class BaseActivity extends BaseFragmentActivity {

    private static int mStack = 0;

    public static boolean isAppInBackground() {
        return mStack == 0;
    }

    private BroadcastReceiver mSignoutReceiver;
    private ProgressDialog mSignoutDialog;

    @Override
    public void onResume() {
        super.onResume();
        if (mStack == 0) {
            OPHelper.getInstance().onEnteringForeground();
            BackgroundingManager.onEnteringForeground();
        }
        mStack++;

        if (OPHelper.getInstance().isSigningOut()) {
            registerSignoutReceiver();
            showSignoutView();
        } else if (!OPDataManager.getInstance().isAccountReady()) {

            if (!LoginManager.getInstance().loginPerformed()) {
                LoginManager.getInstance().registerListener(
                        new LoginUIListenerImpl(this));
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
                                        LoginManager
                                                .getInstance()
                                                .registerListener(
                                                        new LoginUIListenerImpl(
                                                                BaseActivity.this));
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
        if (mSignoutReceiver != null) {
            unregisterReceiver(mSignoutReceiver);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().unregisterListener();
    }

    public static void showInvalidStateWarning(Context context) {
        Toast.makeText(context, R.string.msg_not_logged_in, Toast.LENGTH_LONG)
                .show();
    }

    /**
     * @return
     */
    public ViewGroup getLoginViewContainer() {
        // TODO Auto-generated method stub
        return (ViewGroup) findViewById(R.id.fragment_login);
    }

    protected void onSignoutComplete() {
        if (mSignoutDialog != null) {
            mSignoutDialog.dismiss();
            mSignoutDialog = null;
        }
        MainActivity.cleanLaunch(this);
    }

    protected void registerSignoutReceiver() {
        mSignoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (com.openpeer.sdk.app.IntentData.ACTION_SIGNOUT_DONE
                        .equals(intent.getAction())) {
                    onSignoutComplete();
                }
            }
        };

        IntentFilter filter = new IntentFilter(
                com.openpeer.sdk.app.IntentData.ACTION_SIGNOUT_DONE);
        registerReceiver(mSignoutReceiver, filter);
    }
}
