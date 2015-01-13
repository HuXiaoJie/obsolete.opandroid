#include "openpeer/core/IPushMessaging.h"
#include <jni.h>

#ifndef _PUSH_MESSAGING_QUERY_DELEGATE_WRAPPER_H_
#define _PUSH_MESSAGING_QUERY_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class PushMessagingQueryDelegateWrapper : public IPushMessagingQueryDelegate
{
private:
	jobject javaDelegate;
public:
	PushMessagingQueryDelegateWrapper(jobject delegate);
public:

	//IPushMessagingQueryDelegate implementation
    virtual void onPushMessagingQueryUploaded(IPushMessagingQueryPtr query);
    virtual void onPushMessagingQueryPushStatesChanged(IPushMessagingQueryPtr query);

	virtual ~PushMessagingQueryDelegateWrapper();
};

typedef std::shared_ptr<PushMessagingQueryDelegateWrapper> PushMessagingQueryDelegateWrapperPtr;

#endif //_PUSH_MESSAGING_QUERY_DELEGATE_WRAPPER_H_
