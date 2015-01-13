#include "openpeer/core/IStack.h"
#include <jni.h>

#ifndef _STACK_DELEGATE_WRAPPER_H_
#define _STACK_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class StackDelegateWrapper : public IStackDelegate
{
private:
	jobject javaDelegate;
public:
	StackDelegateWrapper(jobject delegate);
public:

	//IStackDelegate implementation
	virtual void onStackShutdown(openpeer::core::IStackAutoCleanupPtr);

	virtual ~StackDelegateWrapper();
};

typedef std::shared_ptr<StackDelegateWrapper> StackDelegateWrapperPtr;

#endif //_STACK_DELEGATE_WRAPPER_H_
