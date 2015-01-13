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
import android.os.StrictMode;
import android.util.Log;
import android.webkit.CookieManager;

import com.openpeer.sample.push.OPPushManager;
import com.openpeer.sample.push.OPPushNotificationBuilder;
import com.openpeer.sample.push.PushIntentReceiver;
import com.openpeer.sample.push.UAPushService;
import com.openpeer.sample.push.parsepush.PFPushService;
import com.openpeer.sample.util.SettingsHelper;
import com.openpeer.sdk.app.OPHelper;
import com.openpeer.sdk.model.ConversationManager;
import com.parse.Parse;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class OPApplication extends Application {
    private static final String TAG = OPApplication.class.getSimpleName();
    private static OPApplication instance;
    boolean DEVELOPER_MODE = false;
    private AppReceiver mReceiver = new AppReceiver();

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
        filter.addAction(IntentData.ACTION_CALL_STATE_CHANGE);
        filter.addAction(Intent.ACTION_REBOOT);
        registerReceiver(mReceiver, filter);
        init();
    }

    public static OPApplication getInstance() {
        // TODO Auto-generated method stub
        return instance;
    }

    public void signout() {
        if (SettingsHelper.getInstance().isUAPushEnabled()) {
            OPPushManager.onSignOut();
        }
        CookieManager.getInstance().removeAllCookie();
        OPNotificationBuilder.cancelAllUponSignout();
        OPHelper.getInstance().onSignOut();
    }

    private void init() {
        OPHelper.getInstance().init(this);

        SettingsHelper.getInstance().initLoggers();
        if (SettingsHelper.getInstance().isParsePushEnabled()) {
            Parse.initialize(OPApplication.getInstance(),
                             "2QqtFCehrwxAX6nRr7ZIkHTGY75NhKjfsR1obDsl",
                             "ZkrGQ4afScH2T5h3DOZBaEn88YY6sdu9Ekix45Ae");
            PFPushService.getInstance().init();
            ConversationManager.getInstance().registerPushService(PFPushService.getInstance());
        } else if (SettingsHelper.getInstance().isUAPushEnabled()) {
            AirshipConfigOptions options = AirshipConfigOptions
                .loadDefaultOptions(this);
            UAirship.takeOff(this, options);
            PushManager.shared().setNotificationBuilder(
                new OPPushNotificationBuilder());
            PushManager.shared().setIntentReceiver(PushIntentReceiver.class);
            ConversationManager.getInstance().registerPushService(UAPushService.getInstance());
            Logger.logLevel = Log.VERBOSE;
        }

    }
}
