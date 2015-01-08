/*******************************************************************************
 *
 *  Copyright (c) 2014 , Hookflash Inc.
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *  The views and conclusions contained in the software and documentation are those
 *  of the authors and should not be interpreted as representing official policies,
 *  either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
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
