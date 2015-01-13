//#include "openpeer/stack/IServicePushMailboxDatabaseAbstractionDelegate.h"
#include <jni.h>

#ifndef _PUSH_MESSAGING_DATABASE_ABSTRACTION_DELEGATE_WRAPPER_H_
#define _PUSH_MESSAGING_DATABASE_ABSTRACTION_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class PushMessagingDatabaseAbstractionDelegateWrapper // : public IPushMessagingDatabaseAbstractionDelegate
{
private:
	jobject javaDelegate;
public:
	PushMessagingDatabaseAbstractionDelegateWrapper(jobject delegate);
public:

	//IPushMessagingDatabaseAbstractionDelegate implementation
	//TODO: implement delegate methods


	virtual ~PushMessagingDatabaseAbstractionDelegateWrapper();
};

typedef std::shared_ptr<PushMessagingDatabaseAbstractionDelegateWrapper> PushMessagingDatabaseAbstractionDelegateWrapperPtr;

#endif //_PUSH_MESSAGING_DATABASE_ABSTRACTION_DELEGATE_WRAPPER_H_
