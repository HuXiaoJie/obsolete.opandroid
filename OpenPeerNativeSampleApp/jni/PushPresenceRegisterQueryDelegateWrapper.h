#include "openpeer/core/IPushPresence.h"
#include <jni.h>

#ifndef _PUSH_PRESENCE_REGISTER_QUERY_DELEGATE_WRAPPER_H_
#define _PUSH_PRESENCE_REGISTER_QUERY_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class PushPresenceRegisterQueryDelegateWrapper : public IPushPresenceRegisterQueryDelegate
{
private:
	jobject javaDelegate;
public:
	PushPresenceRegisterQueryDelegateWrapper(jobject delegate);
public:

	//IPushPresenceRegisterQueryDelegate implementation
	virtual void onPushPresenceRegisterQueryCompleted(IPushPresenceRegisterQueryPtr query);

	virtual ~PushPresenceRegisterQueryDelegateWrapper();
};

typedef std::shared_ptr<PushPresenceRegisterQueryDelegateWrapper> PushPresenceRegisterQueryDelegateWrapperPtr;

#endif //_PUSH_PRESENCE_REGISTER_QUERY_DELEGATE_WRAPPER_H_
