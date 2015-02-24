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

public class IntentData {
    static final int BASE_REQUEST_CODE = 10000;
    public static final int REQUEST_CODE_ADD_CONTACTS = BASE_REQUEST_CODE + 1;
    public static final int REQUEST_CODE_PARTICIPANTS = BASE_REQUEST_CODE + 2;
    public static final int REQUEST_CODE_GET_CALLEE = BASE_REQUEST_CODE + 3;
    public static final String ACTION_CALL_STATE_CHANGE = "com.openpeer.CALL_STATE";

    public static final String ARG_PEER_CONTACT_ID = "peerId";
    public static final String ARG_CONVERSATION_ACTION = "action";
    public static final String ACTION_CALL = "call";
    public static final String ACTION_CHAT = "chat";
    public static final String ARG_PEER_USER_IDS = "userIds";
    public static final String ARG_USER_IDS_INCLUDE = "userIdsInclude";
    public static final String ARG_VIDEO = "video";
    public static final String ARG_PEER_URI = "peerUri";
    public static final String ARG_CALL_STATE = "callState";
    public static final String ARG_CALL_ID = "callId";
    public static final String ARG_CONVERSATION_ID = "conversationId";
    public static final String ARG_CONVERSATION_TYPE = "conversationType";

}
