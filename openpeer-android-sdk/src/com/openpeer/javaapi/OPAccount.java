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

package com.openpeer.javaapi;

import java.util.List;

import com.openpeer.sdk.app.OPSdkConfig;

public class OPAccount {
    /*
     * Database record id
     */
    private long mAccountId;
    private long mSelfContactId;

    public long getSelfContactId() {
        return mSelfContactId;
    }

    public void setSelfContactId(long id) {
        this.mSelfContactId = id;
    }

    public long getAccountId() {
        return mAccountId;
    }

    public void setAccountId(long id) {
        this.mAccountId = id;
    }

    /**
     * 
     * @return Current account state
     */
    public AccountStates getState() {
        return getState(0, "");
    }

    /**
     * Convenience method to get the peerURI of the account
     * 
     * @return
     */
    public String getPeerUri() {
        return OPContact.getForSelf(this).getPeerURI();
    }

    /**
     * Log in and return a valid OPAccount instance
     * 
     * @param accountDelegate
     *            Account delegate instance. This instance need to be kept valid throughout the app lifecycle.
     * @param conversationThreadDelegate
     *            Call delegate instance. This instance need to be kept valid throughout the app lifecycle.
     * @param callDelegate
     *            Call delegate instance. This instance need to be kept valid throughout the app lifecycle.
     * @return
     */
    public static OPAccount login(OPAccountDelegate accountDelegate,
            OPConversationThreadDelegate conversationThreadDelegate,
            OPCallDelegate callDelegate) {
        return login(accountDelegate,
                conversationThreadDelegate,
                callDelegate,
                OPSdkConfig.getInstance().getOuterFrameUrl(),
                OPSdkConfig.getInstance().getGrantId(),
                OPSdkConfig.getInstance().getIdentityProviderDomain(),
                false);
    }

    /**
     * Relogin and return a valid OPAccount instance
     * 
     * @param accountDelegate
     *            Account delegate instance. This instance need to be kept valid throughout the app lifecycel.
     * @param conversationThreadDelegate
     *            Call delegate instance. This instance need to be kept valid throughout the app lifecycel.
     * @param callDelegate
     *            Call delegate instance. This instance need to be kept valid throughout the app lifecycel.
     * @param reloginInformation
     *            valid relogin string cached from last session
     * @return
     */
    public static OPAccount relogin(OPAccountDelegate accountDelegate,
            OPConversationThreadDelegate conversationThreadDelegate,
            OPCallDelegate callDelegate, String reloginInformation) {
        return relogin(accountDelegate,
                conversationThreadDelegate,
                callDelegate,
                OPSdkConfig.getInstance().getOuterFrameUrl(),
                reloginInformation);

    }

    // Beginning of JNI -- BE CAREFUL WITH ANY SIGNATUR CHANGES!!!!
    private long nativeClassPointer;
    private long nativeDelegatePointer;

    public static native String toString(AccountStates state);

    public static native String toDebugString(OPAccount account,
            boolean includeCommaPrefix);

    private static native OPAccount login(OPAccountDelegate delegate,
            OPConversationThreadDelegate conversationThreadDelegate,
            OPCallDelegate callDelegate,
            String namespaceGrantOuterFrameURLUponReload, 
            String grantID,
            String lockboxServiceDomain,
            boolean forceCreateNewLockboxAccount);

    private static native OPAccount relogin(OPAccountDelegate delegate,
            OPConversationThreadDelegate conversationThreadDelegate,
            OPCallDelegate callDelegate,
            String namespaceGrantOuterFrameURLUponReload,
            String reloginInformation);

    /**
     * Get the core object PUID
     * 
     * @return object PUID
     */
    public native long getID();

    /**
     * Get account's lockbox key
     * 
     * @return
     */
    public native String getStableID();

    public native AccountStates getState(int outErrorCode, String outErrorReason);

    public native String getReloginInformation(); // NOTE: will return ElementPtr() is relogin information is not available yet

    public native String getLocationID();

    /**
     * This function should be called during logout. This is Asynchronorous call, application should handle the SHUTDOWN state change
     * {@link com.openpeer.javaapi.OPAccountDelegate}
     */
    public native void shutdown();

    private native String getPeerFilePrivate();

    private native byte[] getPeerFilePrivateSecret();

    /**
     * 
     * @return Identity list associated with this account
     */

    public native List<OPIdentity> getAssociatedIdentities();

    /**
     * TODO Clarify its usage
     * 
     * @param identitiesToRemove
     */
    public native void removeIdentities(List<OPIdentity> identitiesToRemove);

    public native String getInnerBrowserWindowFrameURL();

    public native void notifyBrowserWindowVisible();

    public native void notifyBrowserWindowClosed();

    public native String getNextMessageForInnerBrowerWindowFrame();

    public native void handleMessageFromInnerBrowserWindowFrame(
            String unparsedMessage);

    private native void releaseCoreObjects();

    protected void finalize() throws Throwable {

        if (nativeClassPointer != 0 || nativeDelegatePointer != 0) {
            releaseCoreObjects();
        }

        super.finalize();
    }
    // END of JNI
}
