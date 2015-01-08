#include "openpeer/core/IBackgrounding.h"
#include <jni.h>

#ifndef _BACKGROUNDING_DELEGATE_WRAPPER_H_
#define _BACKGROUNDING_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class BackgroundingDelegateWrapper : public IBackgroundingDelegate
{
private:
	jobject javaDelegate;
public:
	BackgroundingDelegateWrapper(jobject delegate);
public:
	//IBackgroundingDelegate implementation
    virtual void onBackgroundingGoingToBackground(
                                                  IBackgroundingSubscriptionPtr subscription,
                                                  IBackgroundingNotifierPtr notifier
                                                  );

    virtual void onBackgroundingGoingToBackgroundNow(
                                                     IBackgroundingSubscriptionPtr subscription
                                                     );

    virtual void onBackgroundingReturningFromBackground(
                                                        IBackgroundingSubscriptionPtr subscription
                                                        );

    virtual void onBackgroundingApplicationWillQuit(
                                                    IBackgroundingSubscriptionPtr subscription
                                                    );

	virtual ~BackgroundingDelegateWrapper();
};

typedef std::shared_ptr<BackgroundingDelegateWrapper> BackgroundingDelegateWrapperPtr;

#endif //_BACKGROUNDING_DELEGATE_WRAPPER_H_
