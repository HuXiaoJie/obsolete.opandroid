#include "openpeer/core/ICache.h"
#include <jni.h>

#ifndef _CACHE_DELEGATE_WRAPPER_H_
#define _CACHE_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class CacheDelegateWrapper : public ICacheDelegate
{
private:
	jobject javaDelegate;
public:
	CacheDelegateWrapper(jobject delegate);
public:
	//ICacheDelegate implementation
	virtual zsLib::String fetch(const char *cookieNamePath);
	virtual void store(const char *cookieNamePath,
			Time expires,
			const char *str);
	virtual void clear(const char *cookieNamePath);

	virtual ~CacheDelegateWrapper();
};

typedef std::shared_ptr<CacheDelegateWrapper> CacheDelegateWrapperPtr;

#endif //_CACHE_DELEGATE_WRAPPER_H_
