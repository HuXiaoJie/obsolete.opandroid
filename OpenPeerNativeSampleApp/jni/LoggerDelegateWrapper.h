#include "openpeer/core/ILogger.h"
#include <jni.h>

#ifndef _LOGGER_DELEGATE_WRAPPER_H_
#define _LOGGER_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class LoggerDelegateWrapper : public ILoggerDelegate
{
private:
	jobject javaDelegate;
public:
	LoggerDelegateWrapper(jobject delegate);
public:

	//ILoggerDelegate implementation
	virtual void onNewSubsystem(
			SubsystemID subsystemUniqueID,
			const char *subsystemName
	);
	virtual void onLog(
			SubsystemID subsystemUniqueID,
			const char *subsystemName,
			Severity severity,
			Level level,
			const char *message,
			const char *function,
			const char *filePath,
			ULONG lineNumber
	);

	virtual ~LoggerDelegateWrapper();
};

typedef std::shared_ptr<LoggerDelegateWrapper> LoggerDelegateWrapperPtr;

#endif //_LOGGER_DELEGATE_WRAPPER_H_
