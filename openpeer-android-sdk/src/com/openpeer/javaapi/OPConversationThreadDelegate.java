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
package com.openpeer.javaapi;

public interface OPConversationThreadDelegate {

    /**
     * Callback when new thread created from peer request. *Not* called when creating thread locally
     * @param conversationThread
     */
	public abstract void onConversationThreadNew(OPConversationThread conversationThread);

	public abstract void onConversationThreadContactsChanged(OPConversationThread conversationThread);

	   public abstract void onConversationThreadContactConnectionStateChanged(
               OPConversationThread conversationThread,
               OPContact contact,
               ContactConnectionStates state);

    public abstract void onConversationThreadContactStatusChanged(
			OPConversationThread conversationThread,
			OPContact contact);

	public abstract void onConversationThreadMessage(
			OPConversationThread conversationThread,
			String messageID);

	public abstract void onConversationThreadMessageDeliveryStateChanged(
			OPConversationThread conversationThread,
			String messageID,
			MessageDeliveryStates state);

	/**
	 * Core has failed to deliver the message and deciced the application should try to send it through push.
	 * 
	 * @param conversationThread
	 * @param messageID
	 * @param contact
	 *            The message recipient
	 */
	public abstract void onConversationThreadPushMessage(
			OPConversationThread conversationThread,
			String messageID,
			OPContact contact);
}
