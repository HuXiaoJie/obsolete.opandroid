#include "openpeer/core/IAccount.h"
#include <jni.h>

#ifndef _ACCOUNT_DELEGATE_WRAPPER_H_
#define _ACCOUNT_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class AccountDelegateWrapper : public IAccountDelegate
{
private:
	jobject javaDelegate;
public:
	AccountDelegateWrapper(jobject delegate);
public:

	//IAccountDelegate implementation
	virtual void onAccountStateChanged(IAccountPtr account, IAccount::AccountStates state);
	virtual void onAccountAssociatedIdentitiesChanged(IAccountPtr account);
	virtual void onAccountPendingMessageForInnerBrowserWindowFrame(IAccountPtr account);

	virtual ~AccountDelegateWrapper();
};

typedef std::shared_ptr<AccountDelegateWrapper> AccountDelegateWrapperPtr;

#endif //_ACCOUNT_DELEGATE_WRAPPER_H_
