#include "openpeer/core/IIdentity.h"
#include <jni.h>

#ifndef _IDENTITY_DELEGATE_WRAPPER_H_
#define _IDENTITY_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class IdentityDelegateWrapper : public IIdentityDelegate
{
private:
	jobject javaDelegate;
public:
	IdentityDelegateWrapper(jobject delegate);
public:

	//IIdentityDelegate implementation
	virtual void onIdentityStateChanged(IIdentityPtr identity, IIdentity::IdentityStates state);
	virtual void onIdentityPendingMessageForInnerBrowserWindowFrame(IIdentityPtr identity);
	virtual void onIdentityRolodexContactsDownloaded(IIdentityPtr identity);

	virtual ~IdentityDelegateWrapper();
};

typedef std::shared_ptr<IdentityDelegateWrapper> IdentityDelegateWrapperPtr;

#endif //_IDENTITY_DELEGATE_WRAPPER_H_
