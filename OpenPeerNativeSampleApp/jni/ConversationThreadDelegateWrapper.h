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
	virtual void onConversationThreadContactConnectionStateChanged(
			IConversationThreadPtr conversationThread,
			IContactPtr contact,
			IConversationThread::ContactConnectionStates state
	);
    virtual void onConversationThreadContactStatusChanged(
                                                          IConversationThreadPtr conversationThread,
                                                          IContactPtr contact
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

typedef std::shared_ptr<ConversationThreadDelegateWrapper> ConversationThreadDelegateWrapperPtr;

#endif //_CONVERSATION_THREAD_DELEGATE_WRAPPER_H_
