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

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.CookieManager;

import com.openpeer.sample.delegates.CallDelegateImpl;
import com.openpeer.sample.delegates.HOPConversationDelegateImpl;
import com.openpeer.sample.login.LoginDelegateImpl;
import com.openpeer.sample.push.PushManager;
import com.openpeer.sample.push.PushNotificationBuilder;
import com.openpeer.sample.push.PushIntentReceiver;
import com.openpeer.sample.push.UAPushService;
import com.openpeer.sample.push.parsepush.PFPushService;
import com.openpeer.sample.util.SettingsHelper;
import com.openpeer.sdk.app.HOPHelper;
import com.openpeer.sdk.model.HOPLoginManager;
import com.openpeer.sdk.model.HOPCall;
import com.openpeer.sdk.model.HOPConversation;
import com.openpeer.sample.push.PushServiceInterface;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;

import java.util.List;

public class OPApplication extends Application {
    private static final String TAG = OPApplication.class.getSimpleName();
    private static OPApplication instance;
    boolean DEVELOPER_MODE = false;
    private AppReceiver mReceiver = new AppReceiver();
    static PushServiceInterface pushService;

    public static PushServiceInterface getPushService() {
        return pushService;
    }

    static {
        try {
            System.loadLibrary("z_shared");
            System.loadLibrary("openpeer");

        } catch (UnsatisfiedLinkError use) {
            use.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        IntentFilter filter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        filter.addAction(Intent.ACTION_REBOOT);
        registerReceiver(mReceiver, filter);
        HOPHelper.getInstance().init(this);
        init();
    }

    public static OPApplication getInstance() {
        // TODO Auto-generated method stub
        return instance;
    }

    public void signout() {
        if (SettingsHelper.getInstance().isUAPushEnabled()) {
            PushManager.onSignOut();
        } else if(SettingsHelper.getInstance().isParsePushEnabled()){
            PFPushService.getInstance().onSignout();
        }
        CookieManager.getInstance().removeAllCookie();
        OPNotificationBuilder.cancelAllUponSignout();
        HOPHelper.getInstance().onSignOut();
    }

    private void init() {
        SettingsHelper.getInstance().initLoggers();
        if (SettingsHelper.getInstance().isParsePushEnabled()) {
            pushService = PFPushService.getInstance();
        } else if (SettingsHelper.getInstance().isUAPushEnabled()) {
            AirshipConfigOptions options = AirshipConfigOptions
                .loadDefaultOptions(this);
            UAirship.takeOff(this, options);
            com.urbanairship.push.PushManager.shared().setNotificationBuilder(
                new PushNotificationBuilder());
            com.urbanairship.push.PushManager.shared().setIntentReceiver(PushIntentReceiver.class);
           pushService = UAPushService.getInstance();
            Logger.logLevel = Log.VERBOSE;
        }
        HOPConversation.registerDelegate(HOPConversationDelegateImpl.getInstance());
        HOPCall.registerDelegate(CallDelegateImpl.getInstance());
        HOPLoginManager.getInstance().registerDelegate(LoginDelegateImpl.getInstance());
    }

    public static boolean isServiceAvailable(Class serviceClass) {
        final PackageManager packageManager = instance.getPackageManager();
        final Intent intent = new Intent(instance, serviceClass);
        List resolveInfo =
            packageManager.queryIntentServices(intent,
                                               PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo.size() > 0) {
            return true;
        }
        return false;
    }

    public static String getMetaInfo(String key) {
        try {
            ApplicationInfo ai = instance.getPackageManager().getApplicationInfo(instance
                                                                                     .getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(key);
        } catch(PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch(NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return null;
    }
}
