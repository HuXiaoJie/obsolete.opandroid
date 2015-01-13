#include "openpeer/core/IIdentityLookup.h"
#include <jni.h>

#ifndef _IDENTITY_LOOKUP_DELEGATE_WRAPPER_H_
#define _IDENTITY_LOOKUP_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class IdentityLookupDelegateWrapper : public IIdentityLookupDelegate
{
private:
	jobject javaDelegate;
public:
	IdentityLookupDelegateWrapper(jobject delegate);
public:

	//IIdentityLookupDelegate implementation
	virtual void onIdentityLookupCompleted(IIdentityLookupPtr identity);

	virtual ~IdentityLookupDelegateWrapper();
};

typedef std::shared_ptr<IdentityLookupDelegateWrapper> IdentityLookupDelegateWrapperPtr;

#endif //_IDENTITY_LOOKUP_DELEGATE_WRAPPER_H_
