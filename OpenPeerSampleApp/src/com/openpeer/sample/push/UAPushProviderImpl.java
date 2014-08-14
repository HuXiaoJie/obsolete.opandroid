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
package com.openpeer.sample.push;

import android.util.Base64;

import com.openpeer.javaapi.OPMessage;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class UAPushProviderImpl {
    private static final String TAG = UAPushProviderImpl.class.getSimpleName();
    PushApiService service;

    public UAPushProviderImpl() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                request.addHeader("Authorization", getUAAuthHeader());
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(PushApiService.UA_EP)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        service = restAdapter.create(PushApiService.class);

    }

    public void pushMessage(OPMessage message, PushToken token, Callback<PushResult> callback) {
        PushMessage msg = PushMessage.fromOPMessage(message, token);
        service.push(msg, callback);
    }

    private String getUAAuthHeader() {
        AirshipConfigOptions options = UAirship.shared().getAirshipConfigOptions();

        return "Basic " + Base64.encodeToString((options.getAppKey() + ":" + options.getAppSecret()).getBytes(), Base64.DEFAULT);
    }

}
