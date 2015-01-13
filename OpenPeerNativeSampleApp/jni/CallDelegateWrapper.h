#include "openpeer/core/ICall.h"
#include <jni.h>

#ifndef _CALL_DELEGATE_WRAPPER_H_
#define _CALL_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class CallDelegateWrapper : public ICallDelegate
{
private:
	jobject javaDelegate;
public:
	CallDelegateWrapper(jobject delegate);
public:

	//ICallDelegate implementation
	virtual void onCallStateChanged(ICallPtr call, ICall::CallStates state);

	virtual ~CallDelegateWrapper();
};

typedef std::shared_ptr<CallDelegateWrapper> CallDelegateWrapperPtr;

#endif //_CALL_DELEGATE_WRAPPER_H_
