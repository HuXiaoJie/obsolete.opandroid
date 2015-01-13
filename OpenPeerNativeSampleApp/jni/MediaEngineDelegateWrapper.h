#include "openpeer/core/IMediaEngine.h"
#include <jni.h>

#ifndef _MEDIA_ENGINE_DELEGATE_WRAPPER_H_
#define _MEDIA_ENGINE_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class MediaEngineDelegateWrapper : public IMediaEngineDelegate
{
private:
	jobject javaDelegate;
public:
	MediaEngineDelegateWrapper(jobject delegate);
public:

	//IMediaEngineDelegate implementation
	virtual void onMediaEngineAudioRouteChanged(openpeer::core::IMediaEngine::OutputAudioRoutes audioRoute);
	virtual void onMediaEngineAudioSessionInterruptionBegan();
	virtual void onMediaEngineAudioSessionInterruptionEnded();
	virtual void onMediaEngineFaceDetected();
	virtual void onMediaEngineVideoCaptureRecordStopped();

	virtual ~MediaEngineDelegateWrapper();
};

typedef std::shared_ptr<MediaEngineDelegateWrapper> MediaEngineDelegateWrapperPtr;

#endif //_MEDIA_ENGINE_DELEGATE_WRAPPER_H_
