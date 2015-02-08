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

package com.openpeer.sdk.model;

import com.openpeer.javaapi.CallClosedReasons;
import com.openpeer.javaapi.CallStates;
import com.openpeer.javaapi.OPCall;
import com.openpeer.javaapi.OPCallDelegate;
import com.openpeer.javaapi.OPConversationThread;
import com.openpeer.javaapi.OPMessage;
import com.openpeer.sdk.app.HOPDataManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public class HOPCallManager implements OPCallDelegate {

    Hashtable<String, HOPCall> mIdToCalls;//peerId to call map
    Hashtable<Long, HOPCall> mUserIdToCalls;//peerId to call map

    private HOPCallDelegate delegate;

    private static HOPCallManager instance;

    public static HOPCallManager getInstance() {
        if (instance == null) {
            instance = new HOPCallManager();
        }
        return instance;
    }

    private HOPCallManager() {
    }

    public void registerDelegate(HOPCallDelegate delegate) {
        this.delegate = delegate;
    }

    public void unregisterDelegate(HOPCallDelegate delegate) {
        this.delegate = null;
    }

    @Override
    public void onCallStateChanged(OPCall opcall, CallStates state) {
        OPConversationThread thread = opcall.getConversationThread();

        HOPCall call = findCallById(opcall.getCallID());
        if (call == null) {
            call = new HOPCall(opcall);
        }
        HOPConversation conversation = HOPConversationManager.getInstance().getConversation
            (thread, true);

        switch (state){
        case CallState_Preparing:{
            //Handle racing condition. SImply hangup the existing call for now.
            HOPCall oldCall = findCallForPeer(call.getPeer().getUserId());
            if (oldCall != null) {
                call.hangup(CallClosedReasons.CallClosedReason_NotAcceptableHere);
            } else {
                int direction = call.getCallDirection();
                HOPDataManager.getInstance().saveCall(
                    call.getCallID(),
                    conversation.getConversationId(),
                    call.getPeer().getUserId(),
                    direction,
                    call.hasVideo() ? CallSystemMessage.MEDIATYPE_VIDEO : CallSystemMessage
                        .MEDIATYPE_AUDIO);
                cacheCall(call);
                if (call.isOutgoing()) {
                    OPMessage message = HOPSystemMessage.getCallSystemMessage(
                        CallSystemMessage.STATUS_PLACED,
                        call);
                    conversation.sendMessage(message, false);
                    CallEvent event = new CallEvent(call.getCallID(),
                                                    CallSystemMessage.STATUS_PLACED,
                                                    message.getTime().toMillis(false));
                    HOPDataManager.getInstance().saveCallEvent(call.getCallID(),
                                                               conversation.getConversationId(),
                                                               event);
                }
            }
        }
        break;
        case CallState_Placed:{
        }
        break;
        case CallState_Open:{
            if (call.isOutgoing()) {
                OPMessage message = HOPSystemMessage.getCallSystemMessage(
                    CallSystemMessage.STATUS_ANSWERED,
                    call);
                conversation.sendMessage(message, false);
                CallEvent event = new CallEvent(call.getCallID(),
                                                CallSystemMessage.STATUS_ANSWERED,
                                                message.getTime().toMillis(false));
                HOPDataManager.getInstance().saveCallEvent(call.getCallID(),
                                                           conversation.getConversationId(),
                                                           event);
            }
        }
        break;

        case CallState_Closed:{
            if (call.isOutgoing()) {
                OPMessage message = HOPSystemMessage.getCallSystemMessage(
                    CallSystemMessage.STATUS_HUNGUP,
                    call);
                conversation.sendMessage(message, false);
                CallEvent event = new CallEvent(call.getCallID(),
                                                CallSystemMessage.STATUS_HUNGUP,
                                                message.getTime().toMillis(false));
                HOPDataManager.getInstance().saveCallEvent(call.getCallID(),
                                                           conversation.getConversationId(),
                                                           event);
            }
            removeCallCache(call);
        }
        break;
        default:
            break;

        }

        delegate.onCallStateChanged(call, state);
    }

    private void cacheCall(HOPCall call) {
        if (mIdToCalls == null) {
            mIdToCalls = new Hashtable<>();
        }
        mIdToCalls.put(call.getCallID(), call);
        if (mUserIdToCalls == null) {
            mUserIdToCalls = new Hashtable<>();
        }
        mUserIdToCalls.put(call.getPeer().getUserId(), call);
    }

    public void handleCallSystemMessage(JSONObject message, HOPContact user, String conversationId,
                                        long timestamp) {
        try {
            String callId = message.getString(CallSystemMessage.KEY_ID);
            HOPCall call = findCallById(callId);

            if (call == null) {
                //couldn't find call in memory. try to save call
                HOPDataManager.getInstance().saveCall(message.getString(CallSystemMessage.KEY_ID),
                                                      conversationId,
                                                      user.getUserId(),
                                                      HOPCall.DIRECTION_INCOMING,
                                                      message.getString(CallSystemMessage
                                                                            .KEY_CALL_STATUS_MEDIA_TYPE));
            }
            CallEvent event = new CallEvent(callId,
                                            message.getString(CallSystemMessage
                                                                  .KEY_CALL_STATUS_STATUS),
                                            timestamp);
            HOPDataManager.getInstance().saveCallEvent(callId, conversationId, event);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean hasCalls() {
        return mIdToCalls != null && mIdToCalls.size() > 0;
    }

    private void removeCallCache(HOPCall call) {
        long userId = call.getPeer().getUserId();
        if (mIdToCalls != null) {
            mIdToCalls.remove(call.getCallID());
            mUserIdToCalls.remove(call.getPeer().getUserId());

            if (mIdToCalls.isEmpty()) {
                mIdToCalls = null;
                mUserIdToCalls = null;
            }
        }
    }

    public HOPCall findCallById(String callId) {
        if (mIdToCalls != null) {
            return mIdToCalls.get(callId);
        }
        return null;
    }

    public HOPCall findCallForPeer(long userId) {
        if (mIdToCalls == null) {
            return null;
        }

        return mUserIdToCalls.get(userId);
    }

    public HOPCall findCallByCbcId(long cbcId) {
        if (mIdToCalls != null) {
            for (HOPCall call : mIdToCalls.values()) {
                if (call.getCbcId() == cbcId) {
                    return call;
                }
            }
        }
        return null;
    }

    public static void clearOnSignout() {
        if (instance != null) {
            instance.mIdToCalls = null;
            instance.mUserIdToCalls = null;
        }
    }
}
