/*
 * ******************************************************************************
 *  *
 *  *  Copyright (c) 2014 , Hookflash Inc.
 *  *  All rights reserved.
 *  *
 *  *  Redistribution and use in source and binary forms, with or without
 *  *  modification, are permitted provided that the following conditions are met:
 *  *
 *  *  1. Redistributions of source code must retain the above copyright notice, this
 *  *  list of conditions and the following disclaimer.
 *  *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  *  this list of conditions and the following disclaimer in the documentation
 *  *  and/or other materials provided with the distribution.
 *  *
 *  *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  *
 *  *  The views and conclusions contained in the software and documentation are those
 *  *  of the authors and should not be interpreted as representing official policies,
 *  *  either expressed or implied, of the FreeBSD Project.
 *  ******************************************************************************
 */

package com.openpeer.sample.push;

import com.openpeer.javaapi.MessageDeliveryStates;
import com.openpeer.javaapi.OPLogLevel;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.model.OPConversation;
import com.openpeer.sdk.model.OPUser;
import com.openpeer.sdk.model.PushServiceInterface;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by brucexia on 2014-11-28.
 */
public class UAPushService implements PushServiceInterface {

    private static UAPushService instance;

    public static UAPushService getInstance() {
        if (instance == null) {
            instance = new UAPushService();
        }
        return instance;
    }

    private UAPushService() {
    }

    @Override
    public void onConversationThreadPushMessage(final OPConversation conversation,
                                                final OPMessage message, OPUser contact) {
        OPPushManager.getInstance().getDeviceToken(
            contact.getPeerUri(), new Callback<PushToken>() {

                @Override
                public void success(PushToken token,
                                    Response response) {
                    OPLogger.debug(OPLogLevel.LogLevel_Detail,
                                   "onConversationThreadPushMessage push message "
                                       + message);
                    new UAPushProviderImpl().pushMessage(conversation,
                                                         message, token,
                                                         new Callback<PushResult>() {
                            @Override
                            public void success(
                                PushResult pushResult,
                                Response response) {
                                OPDataManager
                                    .getDatastoreDelegate()
                                    .updateMessageDeliveryStatus(
                                        message.getMessageId(),
                                        MessageDeliveryStates.MessageDeliveryState_Sent);

                            }

                            @Override
                            public void failure(
                                RetrofitError error) {

                                if (error != null) {
                                    OPLogger.debug(
                                        OPLogLevel.LogLevel_Basic,
                                        "eror pushing message "
                                            + error.getMessage());
                                }
                            }
                        });
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error != null) {
                        OPLogger.debug(OPLogLevel.LogLevel_Basic,
                                       "eror retrieving device token "
                                           + error.getMessage());
                    }
                }
            });
    }
}
