#include "openpeer/core/IPushPresence.h"
#include <jni.h>

#ifndef _PUSH_PRESENCE_DELEGATE_WRAPPER_H_
#define _PUSH_PRESENCE_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class PushPresenceDelegateWrapper : public IPushPresenceDelegate
{
private:
	jobject javaDelegate;
public:
	PushPresenceDelegateWrapper(jobject delegate);
public:

    virtual void onPushPresenceStateChanged(
                                            IPushPresencePtr presence,
                                            PushPresenceStates state
                                            );

    virtual void onPushPresenceNewStatus(
                                         IPushPresencePtr presence,
                                         StatusPtr status
                                         );

	virtual ~PushPresenceDelegateWrapper();
};

typedef std::shared_ptr<PushPresenceDelegateWrapper> PushPresenceDelegateWrapperPtr;

#endif //_PUSH_PRESENCE_DELEGATE_WRAPPER_H_
