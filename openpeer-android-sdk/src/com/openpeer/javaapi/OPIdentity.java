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

import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import com.openpeer.sdk.app.HOPSettingsHelper;
import com.openpeer.sdk.model.IdentityData;

public class OPIdentity {
    /**
     * Start identity login. This is handled automatically in {@link com.openpeer.sdk.model.HOPLoginManager}. Application should not use this
     * method directly
     *
     * @param identityUri
     *            Pass null for initial login. Pass in the stored identity uri if you need to manually relogin this identity.
     * @param account
     * @param delegate
     * @return
     */

    public static OPIdentity login(String identityUri, OPAccount account,
            OPIdentityDelegate delegate) {
        HOPSettingsHelper config = HOPSettingsHelper.getInstance();
        if (TextUtils.isEmpty(identityUri)) {
            identityUri = config.getIdentityBaseUri();
        }
        return login(account,
                delegate,
                config.getIdentityProviderDomain(),// "identity-v1-rel-lespaul-i.hcs.io",
                identityUri,// "identity://identity-v1-rel-lespaul-i.hcs.io/",
                config.getRedirectUponCompleteUrl());
    }

    // BEGINNING OF JNI -- BE careful of any interface changes!!!
    private long nativeClassPointer;

    private long nativeDelegatePointer;

    public static native String toString(IdentityStates state);

    public static native String toDebugString(OPIdentity identity,
            Boolean includeCommaPrefix);

    public static native OPIdentity login(
            OPAccount account,
            OPIdentityDelegate delegate,
            String identityProviderDomain, // used when identity URI is of legacy or oauth-type
            String identityURI_or_identityBaseURI,
            String outerFrameURLUponReload
            );

    public static native OPIdentity loginWithIdentityPreauthorized(
            OPAccount account,
            OPIdentityDelegate delegate,
            String identityProviderDomain, // used when identity URI is of legacy or oauth-type
            String identityURI,
            String identityAccessToken,
            String identityAccessSecret,
            Time identityAccessSecretExpires
            );

    public native IdentityStates getState(
            int outLastErrorCode,
            String outLastErrorReason
            );

    public long getStableID() {
        return getIdentityURI().hashCode();
    }

    public native long getID();

    public native boolean isDelegateAttached();

    public native void attachDelegate(
            OPIdentityDelegate delegate,
            String outerFrameURLUponReload
            );

    public native void attachDelegateAndPreauthorizedLogin(
            OPIdentityDelegate delegate,
            String identityAccessToken,
            String identityAccessSecret,
            Time identityAccessSecretExpires
            );

    public native String getIdentityURI();

    public native String getIdentityProviderDomain();

    public native OPIdentityContact getSelfIdentityContact();

    public native String getInnerBrowserWindowFrameURL();

    public native void notifyBrowserWindowVisible();

    public native void notifyBrowserWindowClosed();

    public native String getNextMessageForInnerBrowerWindowFrame();

    public native void handleMessageFromInnerBrowserWindowFrame(String message);

    /**
     * Download contacts from Rolodex server. This method is executed asynchrnously and the result will be handled in
     * {@link com.openpeer.javaapi.OPIdentityDelegate#onIdentityRolodexContactsDownloaded(OPIdentity)}
     *
     * @param inLastDownloadedVersion
     *            if a previous version of the rolodex was downloaded/stored, pass in the version of the last information downloaded to
     *            prevent redownloading infomration again
     */
    public native void startRolodexDownload(String inLastDownloadedVersion); //

    /**
     * force a refresh of the contact list This method is executed asynchrnously and the result will be handled in
     * {@link com.openpeer.javaapi.OPIdentityDelegate#onIdentityRolodexContactsDownloaded(OPIdentity)}
     */
    public native void refreshRolodexContacts();

    /**
     * This method should be called ONLY once
     *
     * @return
     */
    public native OPDownloadedRolodexContacts getDownloadedRolodexContacts();

    /**
     * This function needs to be called upon signout. This cancels the identity instance synchronouly
     */
    public native void cancel();

    private native void releaseCoreObjects();

    protected void finalize() throws Throwable {

        if (nativeClassPointer != 0 || nativeDelegatePointer != 0)
        {
            Log.d("output", "Cleaning identity core objects");
            releaseCoreObjects();
        }

        super.finalize();
    }

    /**
     * @return
     */
    public IdentityStates getState() {
        // TODO Auto-generated method stub
        return getState(0, "");
    }
}
