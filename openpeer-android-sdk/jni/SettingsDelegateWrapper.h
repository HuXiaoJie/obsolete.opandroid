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
#include "openpeer/core/ISettings.h"
#include <jni.h>

#ifndef _SETTINGS_DELEGATE_WRAPPER_H_
#define _SETTINGS_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class SettingsDelegateWrapper : public ISettingsDelegate
{
private:
	jobject javaDelegate;
public:
	SettingsDelegateWrapper(jobject delegate);
public:
	virtual String getString(const char *key) const;
	virtual LONG getInt(const char *key) const;
	virtual ULONG getUInt(const char *key) const;
	virtual bool getBool(const char *key) const;
	virtual float getFloat(const char *key) const;
	virtual double getDouble(const char *key) const;

	virtual void setString(
			const char *key,
			const char *value
	);
	virtual void setInt(
			const char *key,
			LONG value
	);
	virtual void setUInt(
			const char *key,
			ULONG value
	);
	virtual void setBool(
			const char *key,
			bool value
	);
	virtual void setFloat(
			const char *key,
			float value
	);
	virtual void setDouble(
			const char *key,
			double value
	);

	virtual void clear(const char *key);

	virtual ~SettingsDelegateWrapper();
};

typedef std::shared_ptr<SettingsDelegateWrapper> SettingsDelegateWrapperPtr;

#endif //_SETTINGS_DELEGATE_WRAPPER_H_
