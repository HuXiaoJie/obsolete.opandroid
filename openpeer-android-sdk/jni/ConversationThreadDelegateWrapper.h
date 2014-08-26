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
#include "openpeer/core/IConversationThread.h"
#include <jni.h>

#ifndef _CONVERSATION_THREAD_DELEGATE_WRAPPER_H_
#define _CONVERSATION_THREAD_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class ConversationThreadDelegateWrapper : public IConversationThreadDelegate
{
private:
	jobject javaDelegate;
public:
	ConversationThreadDelegateWrapper(jobject delegate);
public:

	//IConversationThreadDelegate implementation
	virtual void onConversationThreadNew(IConversationThreadPtr conversationThread);
	virtual void onConversationThreadContactsChanged(IConversationThreadPtr conversationThread);
	virtual void onConversationThreadContactStateChanged(
			IConversationThreadPtr conversationThread,
			IContactPtr contact,
			IConversationThread::ContactStates state
	);
	virtual void onConversationThreadMessage(
			IConversationThreadPtr conversationThread,
			const char *messageID
	);
	virtual void onConversationThreadMessageDeliveryStateChanged(
			IConversationThreadPtr conversationThread,
			const char *messageID,
			IConversationThread::MessageDeliveryStates state
	);
	virtual void onConversationThreadPushMessage(
			IConversationThreadPtr conversationThread,
			const char *messageID,
			IContactPtr contact
	);

	virtual ~ConversationThreadDelegateWrapper();
};

typedef boost::shared_ptr<ConversationThreadDelegateWrapper> ConversationThreadDelegateWrapperPtr;

#endif //_CONVERSATION_THREAD_DELEGATE_WRAPPER_H_
