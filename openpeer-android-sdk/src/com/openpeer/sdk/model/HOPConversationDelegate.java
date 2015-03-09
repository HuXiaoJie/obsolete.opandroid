/**
 * Copyright (c) 2014, SMB Phone Inc. / Hookflash Inc.
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p/>
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package com.openpeer.sdk.model;

import com.openpeer.javaapi.ComposingStates;
import com.openpeer.javaapi.ContactConnectionStates;
import com.openpeer.javaapi.OPMessage;

/**
 * UI listener of session state changes
 */
public interface HOPConversationDelegate {
    public void onConversationContactStatusChanged(HOPConversation conversation,
                                               ComposingStates state, HOPContact user);

    public boolean onConversationMessage(HOPConversation conversation, OPMessage message);

//    public boolean onCallSystemMessageReceived(OPConversation conversation,
//                                               CallSystemMessage message,
//                                               OPUser sender);

    public boolean onConversationPushMessage(HOPConversation conversation, OPMessage message,
                                             HOPContact user);

    public boolean onConversationContactsChanged(HOPConversation conversation);

    public void onConversationMessageDeliveryStateChanged(HOPConversation conversation,
                                                          OPMessage message);

    public void onConversationContactConnectionStateChanged(HOPConversation conversation,
                                                            HOPContact HOPContact,
                                                            ContactConnectionStates state);
}
