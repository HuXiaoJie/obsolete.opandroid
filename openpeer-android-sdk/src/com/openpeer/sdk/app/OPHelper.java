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

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;

import com.openpeer.javaapi.OPCache;
import com.openpeer.javaapi.OPCacheDelegate;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.javaapi.OPMediaEngine;
import com.openpeer.javaapi.OPMediaEngineDelegate;
import com.openpeer.javaapi.OPSettings;
import com.openpeer.javaapi.OPSettingsDelegate;
import com.openpeer.javaapi.OPStack;
import com.openpeer.javaapi.OPStackDelegate;
import com.openpeer.javaapi.OPStackMessageQueue;
import com.openpeer.javaapi.VideoOrientations;
import com.openpeer.sdk.delegates.OPCacheDelegateImpl;
import com.openpeer.sdk.delegates.OPSettingsDelegateImpl;
import com.openpeer.sdk.delegates.OPStackMessageQueueDelegateImpl;
import com.openpeer.sdk.model.CallManager;
import com.openpeer.sdk.model.ConversationManager;

/**
 * Helper class for openpeer sdk initialization and state changes. The object will hold reference
 * to Application context and core api delegates
 * 
 *
 */
public class OPHelper {
    private static final String TAG = OPHelper.class.getSimpleName();
    private static final String DEFAULT_LOG_SERVER = "LOG.OPP.ME:8115";
    private static final String DEFAULT_LOG_FILE = "/storage/emulated/0/hflog";
    public static final int MODE_CONTACTS_BASED = 0;
    public static final int MODE_GROUP_BASED = 1;

    private boolean mAppInBackground;
    private boolean isSigningOut;

    private static OPHelper instance;
    Context mContext;
    private OPStackMessageQueue mStackMessageQueue;
    private OPCacheDelegate mCacheDelegate;
    private OPSettingsDelegate mSettingsDelegate;
    public Context getApplicationContext() {
        return mContext;
    }

    public static OPHelper getInstance() {
        if (instance == null) {
            instance = new OPHelper();
        }
        return instance;
    }

    public void toggleOutgoingTelnetLogging(boolean enable, String url) {
        if (enable) {
            if (TextUtils.isEmpty(url)) {
                url = DEFAULT_LOG_SERVER;
            }

            String deviceId = Secure.getString(mContext.getContentResolver(),
                    Secure.ANDROID_ID);
            String instanceId = OPSdkConfig.getInstance().getInstanceId();
            String telnetLogString = deviceId + "-" + instanceId + "\n";
            OPLogger.installOutgoingTelnetLogger(url, true, telnetLogString);
        } else {

            OPLogger.uninstallOutgoingTelnetLogger();
        }
    }

    public void toggleFileLogger(boolean enabled, String fileName) {
        if (enabled) {
            if (TextUtils.isEmpty(fileName)) {
                fileName = DEFAULT_LOG_FILE;
            }
            OPLogger.installFileLogger(fileName, true);
        } else {
            OPLogger.uninstallDebuggerLogger();
        }
    }

    public void toggleTelnetLogging(boolean enable, int port) {
        if (enable) {
            OPLogger.installTelnetLogger(port, 60, true);
        } else {
            OPLogger.uninstallTelnetLogger();
        }
    }

    private void initMediaEngine() {
        long start = SystemClock.uptimeMillis();
        OPMediaEngine.getInstance().setEcEnabled(true);
        OPMediaEngine.getInstance().setAgcEnabled(true);
        OPMediaEngine.getInstance().setNsEnabled(false);
        OPMediaEngine.getInstance().setMuteEnabled(false);
        OPMediaEngine.getInstance().setLoudspeakerEnabled(false);
        OPMediaEngine.getInstance().setContinuousVideoCapture(true);
        OPMediaEngine.getInstance().setDefaultVideoOrientation(
                VideoOrientations.VideoOrientation_Portrait);
        OPMediaEngine.getInstance().setRecordVideoOrientation(
                VideoOrientations.VideoOrientation_LandscapeRight);
        OPMediaEngine.getInstance().setFaceDetection(false);

        Log.d("performance",
                "initMediaEngine time " + (SystemClock.uptimeMillis() - start));
    }

    /**
     * Intilialize the SDK. All the delegates will get default implementation if received null parameter
     *
     */
    public void init(Context context) {
        init(context, null, null, null, null);
    }

    /**
     * Intilialize the SDK. All the delegates will get default implementation if received null parameter
     * 
     * @param context
     * @param cacheDelegate
     * @param settingsDelegate
     * @param stackDelegate
     * @param mediaEngineDelegate
     */
    public void init(Context context,
            OPCacheDelegate cacheDelegate,
            OPSettingsDelegate settingsDelegate, OPStackDelegate stackDelegate,
            OPMediaEngineDelegate mediaEngineDelegate) {
        mCacheDelegate = cacheDelegate;
        mSettingsDelegate = settingsDelegate;

        mContext = context;

        mStackMessageQueue = OPStackMessageQueue.singleton();
        mStackMessageQueue.interceptProcessing(OPStackMessageQueueDelegateImpl
                .getInstance());

        if (mSettingsDelegate == null) {
            mSettingsDelegate = OPSettingsDelegateImpl.getInstance(mContext);
        }
        OPSettings.setup(mSettingsDelegate);

        OPSdkConfig.getInstance().init(context);

        OPSdkConfig.getInstance().applySystemSettings(context);
        OPSdkConfig.getInstance().applyApplicationSettings();
        OPSettings
                .setUInt(
                        "openpeer/stack/finder-connection-send-ping-keep-alive-after-in-seconds",
                        0);
        if (mCacheDelegate == null) {
            mCacheDelegate = OPCacheDelegateImpl.getInstance(mContext);
        }
        OPCache.setup(mCacheDelegate);

        OPMediaEngine.init(mContext);

        OPStack stack = OPStack.singleton();

        // OPSettings.applyDefaults();

        stack.setup(stackDelegate, mediaEngineDelegate);

    }

    private String createHttpSettings() {
        try {
            JSONObject parent = new JSONObject();
            JSONObject jsonObject = new JSONObject();

            jsonObject
                    .put("openpeer/stack/bootstrapper-force-well-known-over-insecure-http",
                            true);
            jsonObject.put(
                    "openpeer/stack/bootstrapper-force-well-known-using-post",
                    true);
            parent.put("root", jsonObject);
            return parent.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String createForceDashSetting() {
        try {
            JSONObject parent = new JSONObject();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(
                    "openpeer/core/authorized-application-id-split-char", "-");
            parent.put("root", jsonObject);
            return parent.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @ExcludeFromJavadoc
     * @param intent
     */
    public void sendBroadcast(Intent intent) {
        mContext.sendBroadcast(intent);
    }

    public boolean isAppInBackground() {
        return mAppInBackground;
    }

    public void onEnteringForeground() {
        mAppInBackground = false;
    }

    public void onEnteringBackground() {
        mAppInBackground = true;
    }

    /**
     * Handle user signout. This function will shutdown and clean up core data asynchrously and send
     * {@link com.openpeer.sdk.app.IntentData#ACTION_SIGNOUT_DONE} when shutdown is done. Application must capture this intent and handle
     * properly
     */
    public void onSignOut() {
        isSigningOut = true;
        OPDataManager.getInstance().onSignOut();
    }

    public boolean isSigningOut() {
        return isSigningOut;
    }

    /**
     * @ExcludeFromJavadoc
     */
    public void onAccountShutdown() {
        Intent intent = new Intent();
        if (isSigningOut) {
            CallManager.clearOnSignout();
            ConversationManager.clearOnSignout();
            mCacheDelegate.clear(null);
            intent.setAction(IntentData.ACTION_SIGNOUT_DONE);
            OPDataManager.getInstance().afterSignout();
            LoginManager.getInstance().afterSignout();
        } else {
            LoginManager.getInstance().onAccountShutdown();
            intent.setAction(IntentData.ACTION_ACCOUNT_SHUTDOWN);
        }
        mContext.sendBroadcast(intent);
        isSigningOut = false;
    }

    public static OPSettingsDelegate getSettingsDelegate() {
        return instance.mSettingsDelegate;
    }
}
