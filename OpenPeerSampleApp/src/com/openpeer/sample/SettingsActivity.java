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

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.openpeer.javaapi.OPLogLevel;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.javaapi.OPSettings;
import com.openpeer.sample.settings.SettingsDownloader;
import com.openpeer.sample.util.SettingsHelper;
import com.openpeer.sdk.app.OPHelper;
import com.openpeer.sdk.app.OPSdkConfig;
import com.openpeer.sdk.model.CallManager;

public class SettingsActivity extends BaseActivity {
    static final String KEY_LOCAL_TELNET_LOGGER = "local_telnet_logger";
    static final String KEY_FILE_LOGGER = "file_logger";
    static final String KEY_FILE_LOGGER_PATH = "log_file";
    static final String KEY_RINGTONE = "ringtone";
    static final String KEY_NOTIFICATION_SOUND_SWITCH = "notification_sound_switch";
    static final String KEY_NOTIFICATION_SOUND_SELECT = "notification_sound_select";
    static final String KEY_SIGNOUT = "signout";
    private static final String TAG = "SettingsActivity";

    public static void launch(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {

        SwitchPreference logSwitchPref;
        EditTextPreference logServerPref;
        EditTextPreference logFilePref;
        RingtonePreference ringtonePref;
        RingtonePreference notificationSoundPref;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference);
            SwitchPreference outTelnetLogging = (SwitchPreference) findPreference(SettingsHelper.KEY_OUT_TELNET_LOGGER);
            SwitchPreference localTelnetLogging = (SwitchPreference) findPreference(KEY_LOCAL_TELNET_LOGGER);
            final SwitchPreference fileLogging = (SwitchPreference) findPreference(KEY_FILE_LOGGER);
            SwitchPreference notificationSoundSwitchPref = (SwitchPreference) findPreference(KEY_NOTIFICATION_SOUND_SWITCH);

            logSwitchPref = (SwitchPreference) findPreference(SettingsHelper.KEY_LOG_SWITCH);
            logServerPref = (EditTextPreference) findPreference(SettingsHelper.KEY_OUT_LOG_SERVER);
            logFilePref = (EditTextPreference) findPreference(KEY_FILE_LOGGER_PATH);

            outTelnetLogging
                    .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(
                                Preference preference, Object newValue) {
                            String url = logServerPref.getText();
                            OPHelper.getInstance().toggleOutgoingTelnetLogging(
                                    (Boolean) newValue, url);

                            return true;
                        }
                    });
            logServerPref
                    .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(
                                Preference preference, Object newValue) {
                            logServerPref.setTitle("Log server:  " + newValue);
                            SettingsHelper.setLogServer(newValue.toString());
                            return true;
                        }
                    });
            localTelnetLogging
                    .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(
                                Preference preference, Object newValue) {
                            OPHelper.getInstance().toggleTelnetLogging(
                                    (Boolean) newValue, 59999);
                            return true;
                        }
                    });
            fileLogging
                    .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(
                                Preference preference, Object newValue) {
                            String fileName = logFilePref.getText();
                            OPHelper.getInstance().toggleFileLogger(
                                    (Boolean) newValue, fileName);
                            return true;
                        }
                    });
            logFilePref
                    .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(
                                Preference preference, Object newValue) {
                            logFilePref.setTitle("Log file:  " + newValue);
                            return true;
                        }
                    });
            logSwitchPref
                    .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(
                                Preference preference, Object newValue) {
                            SettingsHelper.setLogEnabled((Boolean) newValue);

                            return true;
                        }
                    });
            notificationSoundPref = (RingtonePreference) findPreference(KEY_NOTIFICATION_SOUND_SELECT);
            ringtonePref = (RingtonePreference) findPreference(KEY_RINGTONE);
            Preference signoutPref = findPreference(KEY_SIGNOUT);
            signoutPref
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            ((SettingsActivity) getActivity()).doSignout();
                            return true;
                        }
                    });

            Preference scanPreference = findPreference("scan");
            scanPreference
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            IntentIntegrator integrator = new IntentIntegrator(
                                    getActivity());
                            integrator
                                    .initiateScan(IntentIntegrator.QR_CODE_TYPES);
                            return true;
                        }

                    });
            setupAboutInfo();
            setupLoggerScreen();
        }

        private static final String KEY_VERSION = "version";
        private static final String KEY_BUILD = "build";

        void setupAboutInfo() {
            Preference versionPref = findPreference(KEY_VERSION);
            Preference buildPref = findPreference(KEY_BUILD);
            PackageInfo pInfo;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(
                        getActivity().getPackageName(), 0);
                String version = pInfo.versionName;
                String versionCode = "" + pInfo.versionCode;
                versionPref.setSummary(version);
                buildPref.setSummary(versionCode);
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        void setupSettingDisplays() {
            logServerPref.setTitle("Log server:  "
                    + SettingsHelper.getLogServer());
            logFilePref.setTitle("Log file:  " + logFilePref.getText());
            Ringtone ringtone = SettingsHelper.getRingtone();
            ringtonePref.setSummary(ringtone.getTitle(getActivity()));
            Uri notificationSound = SettingsHelper.getNotificationSound();
            if (notificationSound != null) {
                notificationSoundPref.setSummary(RingtoneManager.getRingtone(
                        getActivity(), notificationSound).getTitle(
                        getActivity()));
            } else {
                notificationSoundPref.setSummary("None");
            }

            logSwitchPref.setChecked(SettingsHelper.isLogEnabled());
        }

        @Override
        public void onResume() {
            super.onResume();
            setupSettingDisplays();
        }

        private void setupLoggerScreen() {
            PreferenceCategory loggerScreen = (PreferenceCategory) findPreference("logLevels");
            final String loggerKeys[] = getActivity().getResources()
                    .getStringArray(R.array.logKeys);
            final String loggerTitles[] = getActivity().getResources()
                    .getStringArray(R.array.logTitles);
            String loggerDefaults[] = getActivity().getResources()
                    .getStringArray(R.array.logLevelDefaults);
            final String[] entries = getActivity().getResources()
                    .getStringArray(R.array.logLevel);
            final String entryValues[] = getActivity().getResources()
                    .getStringArray(R.array.logLevelValues);
            for (int i = 0; i < loggerKeys.length; i++) {
                String key = loggerKeys[i];
                ListPreference pref = new ListPreference(getActivity());
                pref.setKey(key);
                pref.setEntries(entries);
                pref.setEntryValues(entryValues);

                String value = SettingsHelper.getString(key, loggerDefaults[i]);
                pref.setValue(value);
                int index = Integer.parseInt(value);
                final String loggerTitle = loggerTitles[i];
                pref.setTitle(String.format(loggerTitle, entries[index]));

                pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference,
                            Object newValue) {
                        int intValue = Integer.parseInt((String) newValue);
                        OPLogLevel level = OPLogLevel.values()[intValue];
                        int index = Integer.parseInt(newValue.toString());
                        preference.setTitle(String.format(loggerTitle,
                                entries[index]));
                        if (SettingsHelper.isLogEnabled()) {
                            OPLogger.setLogLevel(level);
                        }

                        return true;
                    }
                });
                loggerScreen.addPreference(pref);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent);
        if (scanResult != null) {
            Log.d(TAG, "scan result " + scanResult);
            String url = scanResult.getContents();
            if (!TextUtils.isEmpty(url)) {
                Callback<JsonElement> callback = new Callback<JsonElement>() {

                    @Override
                    public void failure(RetrofitError arg0) {
                        Log.d(TAG, "failure " + arg0.toString());
                        if (hasWindowFocus()) {
                            Toast.makeText(
                                    SettingsActivity.this,
                                    "failed to download settings. Try again later",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void success(JsonElement arg0, Response arg1) {
                        String jsonBlob = arg0.toString();
                        if (!TextUtils.isEmpty(jsonBlob)) {
                            OPSettings.apply(jsonBlob);
                            //App settings have changed so regenerate authorized-application-id and instance-id
                            OPSdkConfig.getInstance().applyApplicationSettings();
                            SettingsHelper.getInstance().initLoggers();
                            
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SettingsActivity.this);
                            builder.setMessage(
                                    "New settings have been applied! Please relogin")
                                    .setNeutralButton(
                                            "Ok",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    doSignout();
                                                }
                                            });
                            builder.create().show();
                        }
                    }

                };

                SettingsDownloader.download(url, callback);
            }
        }

    }

    void doSignout() {
        if (CallManager.getInstance().hasCalls()) {
            Toast.makeText(SettingsActivity.this,
                    R.string.msg_cannot_signout_with_call, Toast.LENGTH_LONG)
                    .show();
        } else {
            showSignoutView();
            OPApplication.getInstance().signout();
        }
    }
}
