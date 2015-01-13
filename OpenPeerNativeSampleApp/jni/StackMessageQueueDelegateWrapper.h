#include "openpeer/core/IStack.h"
#include <jni.h>

#ifndef _STACK_MESSAGE_QUEUE_DELEGATE_WRAPPER_H_
#define _STACK_MESSAGE_QUEUE_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class StackMessageQueueDelegateWrapper : public IStackMessageQueueDelegate
{
private:
	jobject javaDelegate;
public:
	StackMessageQueueDelegateWrapper(jobject delegate);
public:

	//IStackMessageQueueDelegate implementation
	virtual void onStackMessageQueueWakeUpCustomThreadAndProcessOnCustomThread();

	virtual ~StackMessageQueueDelegateWrapper();
};

typedef std::shared_ptr<StackMessageQueueDelegateWrapper> StackMessageQueueDelegateWrapperPtr;

#endif //_STACK_MESSAGE_QUEUE_DELEGATE_WRAPPER_H_
