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
