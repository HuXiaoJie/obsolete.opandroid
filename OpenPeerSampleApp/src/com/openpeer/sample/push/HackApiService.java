package com.openpeer.sample.push;

import com.openpeer.sdk.app.HOPSettingsHelper;

import java.util.UUID;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;

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
public interface HackApiService {
    static final String HCS_DOMAIN = "http://push-hack.hcs-stack-v2-i7957106-7.hcs.io";
    static final String HACK_SET = "device-associate-set";
    static final String HACK_GET = "device-associate-get";

    static final String HEADER_CONTENT_TYPE = "Content-Type: application/json";

    @Headers({HEADER_CONTENT_TYPE})
    @POST("/" + HACK_SET)
    void associate(@Body HackAssociate message, Callback<HackAssociateResult> cb);

    @POST("/" + HACK_GET)
    void get(@Body HackGet message, Callback<HackGetResult> cb);

    static class HackApiCommon {
        static final String HANDLER_HACK = "push-hack";
        String $domain;//": "identity-v1-rel-lespaul-i.hcs.io",
        String $appid;//": "com.hookflash.messenger",
        String $id;//: "abd23",
        String $handler;//": "push-hack",
        String $method;//": "device-associate-set",

        public HackApiCommon() {
        }

        public HackApiCommon(String method, String id) {
            this.$domain = HOPSettingsHelper.getInstance().getLockboxServiceDomain();
            this.$appid = HOPSettingsHelper.getInstance().getAppId();
            this.$id = id;
            this.$handler = HANDLER_HACK;
            this.$method = method;
        }
    }

    static class HackAssociate {
        HackAssociaterequest request;

        public HackAssociate() {
        }

        public HackAssociate(String uri, String deviceToken) {
            request = new HackAssociaterequest(uri, deviceToken);
        }

        static class HackAssociaterequest extends HackApiCommon {

            String type;//": "apid",
            String uri;//": "peer://identity-v1-rel-lespaul-i.hcs.io/bb0f790880ca46208126fa0fc8aa217f40272829c2848d12e0b0144c48c4e070",
            String deviceToken;//": "9e98efc4-9d28-41aa-9b4e-8526a3021d4b"

            public HackAssociaterequest() {
            }

            public HackAssociaterequest(String uri, String deviceToken) {
                super(HACK_SET, UUID.randomUUID().toString());
                this.uri = uri;
                this.deviceToken = deviceToken;
                type = "apid";
            }
        }
    }

    static class HackGet {
        HackGetRequest request;

        public HackGet() {
        }

        public HackGet(String uri) {
            request = new HackGetRequest(uri);
        }

        static class HackGetRequest extends HackApiCommon {

            String uri;//": "peer://identity-v1-rel-lespaul-i.hcs.io/bb0f790880ca46208126fa0fc8aa217f40272829c2848d12e0b0144c48c4e070",

            public HackGetRequest() {
            }

            public HackGetRequest(String uri) {
                super(HACK_GET, UUID.randomUUID().toString());
                this.uri = uri;
            }
        }
    }
    static class HackAssociateResult {
        HackAssociateResponse result;

        static class HackAssociateResponse extends HackApiCommon {
            long $timestamp;
        }
    }
    static class HackGetResult {
        public HackGetResponse getResult() {
            return result;
        }

        HackGetResponse result;

        static class HackGetResponse extends HackApiCommon {

            long $timestamp;
            String type;//": "peer://identity-v1-rel-lespaul-i.hcs.io/bb0f790880ca46208126fa0fc8aa217f40272829c2848d12e0b0144c48c4e070",
            String deviceToken;

            public String getType() {
                return type;
            }

            public String getDeviceToken() {
                return deviceToken;
            }

        }
    }

}
