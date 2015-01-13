#include "openpeer/core/IPushMessaging.h"
#include <jni.h>

#ifndef _PUSH_MESSAGING_REGISTER_QUERY_DELEGATE_WRAPPER_H_
#define _PUSH_MESSAGING_REGISTER_QUERY_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class PushMessagingRegisterQueryDelegateWrapper : public IPushMessagingRegisterQueryDelegate
{
private:
	jobject javaDelegate;
public:
	PushMessagingRegisterQueryDelegateWrapper(jobject delegate);
public:

	//IPushMessagingRegisterQueryDelegate implementation
	virtual void onPushMessagingRegisterQueryCompleted(IPushMessagingRegisterQueryPtr query);

	virtual ~PushMessagingRegisterQueryDelegateWrapper();
};

typedef std::shared_ptr<PushMessagingRegisterQueryDelegateWrapper> PushMessagingRegisterQueryDelegateWrapperPtr;

#endif //_PUSH_MESSAGING_REGISTER_QUERY_DELEGATE_WRAPPER_H_
