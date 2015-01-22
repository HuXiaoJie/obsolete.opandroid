#include "openpeer/core/IPushPresence.h"
#include <jni.h>

#ifndef _PUSH_PRESENCE_TRANSFER_NOTIFIER_WRAPPER_H_
#define _PUSH_PRESENCE_TRANSFER_NOTIFIER_WRAPPER_H_

using namespace openpeer::core;

class PushPresenceTransferNotifierWrapper : public IPushPresenceTransferNotifier
{
private:
	jobject javaDelegate;
public:
	PushPresenceTransferNotifierWrapper(jobject delegate);
public:

	//IPushPresenceTransferNotifier implementation
	virtual void notifyComplete(bool wasSuccessful);

	virtual ~PushPresenceTransferNotifierWrapper();
};

typedef std::shared_ptr<PushPresenceTransferNotifierWrapper> PushPresenceTransferNotifierWrapperPtr;

#endif //_PUSH_PRESENCE_TRANSFER_NOTIFIER_WRAPPER_H_
