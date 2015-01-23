package com.openpeer.javaapi;

public abstract class OPPushMessagingTransferDelegate {
    //-----------------------------------------------------------------------
    // PURPOSE: upload a file to a url
    // NOTES:   - this upload should occur even while the application goes
    //            to the background
    //          - this method is called asynchronously on the application's
    //            thread
    public abstract void onPushMessagingTransferUploadFileDataToURL(
                                                            OPPushMessaging session,
                                                            String postURL,
                                                            String fileNameContainingData,
                                                            long totalFileSizeInBytes,            // the total bytes that exists within the file
                                                            long remainingBytesToUpload,          // the file should be seeked to the position of (total size - remaining) and upload the remaining bytes from this position in the file
                                                            OPPushMessagingTransferNotifier notifier
                                                            );

    //-----------------------------------------------------------------------
    // PURPOSE: download a file from a URL
    // NOTES:   - this download should occur even while the application goes
    //            to the background
    //          - this method is called asynchronously on the application's
    //            thread
    public abstract void onPushMessagingTransferDownloadDataFromURL(
                                                            OPPushMessaging session,
                                                            String getURL,
                                                            String fileNameToAppendData,          // the existing file name to open and append
                                                            long finalFileSizeInBytes,             // when the download completes the file size will be this size
                                                            long remainingBytesToBeDownloaded,     // the downloaded data will be appended to the end of the existing file and this is the total bytes that are to be downloaded
                                                            OPPushMessagingTransferNotifier notifier
                                                            );
}
