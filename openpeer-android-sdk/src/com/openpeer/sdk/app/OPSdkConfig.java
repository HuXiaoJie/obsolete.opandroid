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

import java.io.IOException;

import android.content.Context;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.text.format.Time;

import com.openpeer.javaapi.OPSettings;
import com.openpeer.javaapi.OPStack;
import com.openpeer.sdk.model.GroupChatMode;
import com.openpeer.sdk.utils.AssetUtils;

/**
 * 
 * Global SDK configurations. TODO: put them in a file like openpeersdk.properties
 * 
 * All configurable items should have a default value and the value will be overwritten if the configuration file exists
 */
public class OPSdkConfig {

    // TODO add configuration items in
    private static final String KEY_GRANT_ID = "openpeer/calculated/grantId";

    public static final String KEY_USER_AGENT = "openpeer/calculated/user-agent";

    public static final String KEY_OUTER_FRAME_URL = "outerFrameURL";
    public static final String KEY_IDENTITY_PROVIDE_DOMAIN = "identityProviderDomain";
    public static final String KEY_IDENTITY_BASE_URI = "identityFederateBaseURI";
    public static final String KEY_NAMESPACE_GRANT_SERVICE_URL = "namespaceGrantServiceURL";
    public static final String KEY_LOCKBOX_SERVICE_DOMAIN = "lockBoxServiceDomain";
    public static final String KEY_APP_ID = "applicationID";
    public static final String KEY_APP_APPKEY = "applicationSharedSecret";
    public static final String KEY_CHAT_MODE = "application/chatMode";
    public static final String KEY_REDIRECT_UPON_LOGIN_URL = "redirectAfterLoginCompleteURL";
    public static final String KEY_INSTANCE_ID = "openpeer/calculated/instance-id";
    public static final String KEY_AUTHORIZED_APP_ID = "openpeer/calculated/authorized-application-id";
    private static final String KEY_DEVICE_ID = "openpeer/calculated/device-id";
    private static final String KEY_SYSTEM = "openpeer/calculated/system";
    private static final String KEY_OS = "openpeer/calculated/os";

    static final long DURATION_ONE_YEAR_IN_MILLIS = 12 * 30 * 24 * 60 * 60
            * 1000l;

    private static OPSdkConfig instance;

    public String getInstanceId() {
        return OPHelper.getSettingsDelegate().getString(KEY_INSTANCE_ID);
    }

    private OPSdkConfig() {

    }

    public String getAppId() {
        return OPHelper.getSettingsDelegate().getString(KEY_APP_ID);
    }

    public static OPSdkConfig getInstance() {
        if (instance == null) {
            instance = new OPSdkConfig();
        }
        return instance;
    }

    public String getOuterFrameUrl() {
        return OPHelper.getSettingsDelegate().getString(KEY_OUTER_FRAME_URL);
    }

    public String getRedirectUponCompleteUrl() {
        return OPHelper.getSettingsDelegate().getString(
                KEY_REDIRECT_UPON_LOGIN_URL);
    }

    public String getIdentityProviderDomain() {
        return OPHelper.getSettingsDelegate().getString(
                KEY_IDENTITY_PROVIDE_DOMAIN);
    }

    public String getIdentityBaseUri() {
        return OPHelper.getSettingsDelegate().getString(KEY_IDENTITY_BASE_URI);
    }

    public String getNamespaceGrantServiceUrl() {
        return OPHelper.getSettingsDelegate().getString(
                KEY_NAMESPACE_GRANT_SERVICE_URL);
    }

    public String getLockboxServiceDomain() {
        return OPHelper.getSettingsDelegate().getString(
                KEY_LOCKBOX_SERVICE_DOMAIN);
    }

    public String getGrantId() {
        String id = OPHelper.getSettingsDelegate().getString(KEY_GRANT_ID);// mContext.getSharedPreferences(PREF_NAME,
        // Context.MODE_PRIVATE).getString(KEY_GRANT_ID, null);
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
            OPHelper.getSettingsDelegate().setString(KEY_GRANT_ID, id);
        }
        return id;
    }

    /**
     * This funciton need to be called on app launch to make sure device/os settings are up to date
     * 
     * @param context
     */
    public void applySystemSettings(Context context) {
        OPSettings.setString(KEY_DEVICE_ID, Secure.getString(
                context.getContentResolver(), Secure.ANDROID_ID));
        OPSettings.setString(KEY_OS,
                android.os.Build.VERSION.RELEASE);
        OPSettings.setString(KEY_SYSTEM, android.os.Build.MODEL);
    }

    /**
     * This funciton need to be called on app launch or after switching application to make sure application settings are up to date
     * 
     * @param context
     */
    public void applyApplicationSettings() {
        OPSettings.setString(OPSdkConfig.KEY_AUTHORIZED_APP_ID,
                OPSdkConfig.generateAuthorizedAppId());
        OPSettings.setString(OPSdkConfig.KEY_INSTANCE_ID,
                java.util.UUID.randomUUID().toString());
        OPSettings.setString(
                KEY_USER_AGENT,
                "user-agent-"
                        + OPHelper.getSettingsDelegate().getString(KEY_APP_ID));
    }

    private static String generateAuthorizedAppId() {
        Time expires = new Time();
        expires.set(System.currentTimeMillis() + DURATION_ONE_YEAR_IN_MILLIS);

        String id = OPStack.createAuthorizedApplicationID(
                OPHelper.getSettingsDelegate().getString(KEY_APP_ID),
                OPHelper.getSettingsDelegate().getString(KEY_APP_APPKEY),
                expires);
        return id;
    }

    public void init(Context context) {
        if (!TextUtils.isEmpty(OPSdkConfig.getInstance().getAppId())) {
            return;
        }
        try {
            byte[] bytes = AssetUtils.readAsset(context,
                    "app_settings.json");
            String str = new String(bytes, "UTF-8");
            OPSettings.apply(str);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    public GroupChatMode getGroupChatMode() {
        String mode = OPHelper.getSettingsDelegate().getString(KEY_CHAT_MODE);
        if (TextUtils.isEmpty(mode)) {
            return GroupChatMode.contact;
        } else {
            return GroupChatMode.valueOf(mode);
        }
    }

}
