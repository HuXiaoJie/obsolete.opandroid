#include "openpeer/core/IBackgrounding.h"
#include <jni.h>

#ifndef _BACKGROUNDING_COMPLETION_DELEGATE_WRAPPER_H_
#define _BACKGROUNDING_COMPLETION_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class BackgroundingCompletionDelegateWrapper : public IBackgroundingCompletionDelegate
{
private:
	jobject javaDelegate;
public:
	BackgroundingCompletionDelegateWrapper(jobject delegate);
public:
	//IBackgroundingCompletionDelegate implementation
	virtual void onBackgroundingReady(IBackgroundingQueryPtr query);

	virtual ~BackgroundingCompletionDelegateWrapper();
};

typedef std::shared_ptr<BackgroundingCompletionDelegateWrapper> BackgroundingCompletionDelegateWrapperPtr;

#endif //_BACKGROUNDING_COMPLETION_DELEGATE_WRAPPER_H_
