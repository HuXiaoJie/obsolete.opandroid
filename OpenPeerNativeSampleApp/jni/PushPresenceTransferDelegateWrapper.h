#include "openpeer/core/IPushPresence.h"
#include <jni.h>

#ifndef _PUSH_PRESENCE_TRANSFER_DELEGATE_WRAPPER_H_
#define _PUSH_PRESENCE_TRANSFER_DELEGATE_WRAPPER_H_

using namespace openpeer::core;

class PushPresenceTransferDelegateWrapper : public IPushPresenceTransferDelegate
{
private:
	jobject javaDelegate;
public:
	PushPresenceTransferDelegateWrapper(jobject delegate);
public:

	//IPushPresenceTransferDelegate implementation
	//-----------------------------------------------------------------------
	// PURPOSE: upload a file to a url
	// NOTES:   - this upload should occur even while the application goes
	//            to the background
	//          - this method is called asynchronously on the application's
	//            thread
	virtual void onPushPresenceTransferUploadFileDataToURL(
			IPushPresencePtr session,
			const char *postURL,
			const char *fileNameContainingData,
			ULONGEST totalFileSizeInBytes,            // the total bytes that exists within the file
			ULONGEST remainingBytesToUpload,          // the file should be seeked to the position of (total size - remaining) and upload the remaining bytes from this position in the file
			IPushPresenceTransferNotifierPtr notifier
	);

	//-----------------------------------------------------------------------
	// PURPOSE: download a file from a URL
	// NOTES:   - this download should occur even while the application goes
	//            to the background
	//          - this method is called asynchronously on the application's
	//            thread
	virtual void onPushPresenceTransferDownloadDataFromURL(
			IPushPresencePtr session,
			const char *getURL,
			const char *fileNameToAppendData,          // the existing file name to open and append
			ULONGEST finalFileSizeInBytes,             // when the download completes the file size will be this size
			ULONGEST remainingBytesToBeDownloaded,     // the downloaded data will be appended to the end of the existing file and this is the total bytes that are to be downloaded
			IPushPresenceTransferNotifierPtr notifier
	);

	virtual ~PushPresenceTransferDelegateWrapper();
};

typedef std::shared_ptr<PushPresenceTransferDelegateWrapper> PushPresenceTransferDelegateWrapperPtr;

#endif //_PUSH_PRESENCE_TRANSFER_DELEGATE_WRAPPER_H_
