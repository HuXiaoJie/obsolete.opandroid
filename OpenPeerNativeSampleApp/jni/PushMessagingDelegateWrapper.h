#include "openpeer/core/IPushMessaging.h"
#include <jni.h>

#ifndef _PUSH_MESSAGING_DELEGATE_WRAPPER_H_
#define _PUSH_MESSAGING_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class PushMessagingDelegateWrapper : public IPushMessagingDelegate
{
private:
	jobject javaDelegate;
public:
	PushMessagingDelegateWrapper(jobject delegate);
public:

	//IPushMessagingDelegate implementation
    virtual void onPushMessagingStateChanged(
                                             IPushMessagingPtr messaging,
                                             PushMessagingStates state
                                             );

    virtual void onPushMessagingNewMessages(IPushMessagingPtr messaging);

	virtual ~PushMessagingDelegateWrapper();
};

typedef std::shared_ptr<PushMessagingDelegateWrapper> PushMessagingDelegateWrapperPtr;

#endif //_PUSH_MESSAGING_DELEGATE_WRAPPER_H_
