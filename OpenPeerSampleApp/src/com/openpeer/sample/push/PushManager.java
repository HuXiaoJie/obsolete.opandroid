package com.openpeer.sample.push;

import android.text.TextUtils;

import com.urbanairship.UAirship;

import java.util.Hashtable;

import retrofit.Callback;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
public class PushManager {
    HackApiService service;
    private Hashtable<String, PushToken> tokens;
    private static PushManager instance;

    public static PushManager getInstance() {
        if (instance == null) {
            instance = new PushManager();
        }
        return instance;
    }

    private PushManager() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(HackApiService.HCS_DOMAIN)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        service = restAdapter.create(HackApiService.class);
        tokens = new Hashtable<String, PushToken>();
    }

    public void associateDeviceToken(String peerUri, String deviceToken, Callback<HackApiService.HackAssociateResult> callback) {
        service.associate(new HackApiService.HackAssociate(peerUri, deviceToken), callback);
    }

    public PushToken getDeviceToken(final String peerUri, final Callback<PushToken> cb) {

        Callback<HackApiService.HackGetResult> callback = new Callback<HackApiService.HackGetResult>() {
            @Override
            public void success(HackApiService.HackGetResult hackGetResult, Response response) {
                String tokenString = hackGetResult.getResult().getDeviceToken();
                if (TextUtils.isEmpty(tokenString)) {
                    PushToken token = tokens.get(peerUri);
                    if (tokens.get(peerUri) != null) {
                        cb.success(token, null);
                    } else if (cb != null) {
                        cb.failure(null);
                    }
                } else {
                    PushToken token = new PushToken(hackGetResult.getResult().getType(), hackGetResult.getResult().getDeviceToken());
                    saveDeviceToken(peerUri, token);
                    if (cb != null) {
                        cb.success(token, response);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {

                PushToken token = tokens.get(peerUri);
                if (tokens.get(peerUri) != null) {
                    cb.success(token, null);
                } else if (cb != null) {
                    cb.failure(error);
                }

            }
        };
        service.get(new HackApiService.HackGet(peerUri), callback);
        return null;
    }

    void saveDeviceToken(String peerUri, PushToken token) {

        tokens.put(peerUri, token);
    }
    public static void onSignOut(){
        com.urbanairship.push.PushManager.disablePush();
        UAirship.land();
    }

}
