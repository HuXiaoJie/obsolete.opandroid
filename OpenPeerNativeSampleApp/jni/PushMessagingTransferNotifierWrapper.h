#include "openpeer/core/IPushMessaging.h"
#include <jni.h>

#ifndef _PUSH_MESSAGING_TRANSFER_NOTIFIER_WRAPPER_H_
#define _PUSH_MESSAGING_TRANSFER_NOTIFIER_WRAPPER_H_

using namespace openpeer::core;

class PushMessagingTransferNotifierWrapper : public IPushMessagingTransferNotifier
{
private:
	jobject javaDelegate;
public:
	PushMessagingTransferNotifierWrapper(jobject delegate);
public:

	//IPushMessagingTransferNotifier implementation
	virtual void notifyComplete(bool wasSuccessful);

	virtual ~PushMessagingTransferNotifierWrapper();
};

typedef std::shared_ptr<PushMessagingTransferNotifierWrapper> PushMessagingTransferNotifierWrapperPtr;

#endif //_PUSH_MESSAGING_TRANSFER_NOTIFIER_WRAPPER_H_
