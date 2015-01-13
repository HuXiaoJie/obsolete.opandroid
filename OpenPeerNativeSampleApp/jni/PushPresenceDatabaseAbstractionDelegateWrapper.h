//#include "openpeer/stack/IServicePushMailboxDatabaseAbstractionDelegate.h"
#include <jni.h>

#ifndef _PUSH_PRESENCE_DATABASE_ABSTRACTION_DELEGATE_WRAPPER_H_
#define _PUSH_PRESENCE_DATABASE_ABSTRACTION_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class PushPresenceDatabaseAbstractionDelegateWrapper // : public IPushPresenceDatabaseAbstractionDelegate
{
private:
	jobject javaDelegate;
public:
	PushPresenceDatabaseAbstractionDelegateWrapper(jobject delegate);
public:

	//IPushMessagingDatabaseAbstractionDelegate implementation
	//TODO: implement delegate methods


	virtual ~PushPresenceDatabaseAbstractionDelegateWrapper();
};

typedef std::shared_ptr<PushPresenceDatabaseAbstractionDelegateWrapper> PushPresenceDatabaseAbstractionDelegateWrapperPtr;

#endif //_PUSH_PRESENCE_DATABASE_ABSTRACTION_DELEGATE_WRAPPER_H_
