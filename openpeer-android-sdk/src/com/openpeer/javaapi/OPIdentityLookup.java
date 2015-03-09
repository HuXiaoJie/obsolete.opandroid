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

import com.openpeer.sdk.app.HOPSettingsHelper;

import android.util.Log;

public class OPIdentityLookup {
	/**
	 * Create and execute identity lookup task. This method is executed asynchronously and result is handled in
	 * {@link com.openpeer.javaapi.OPIDentityLookupDelegate#onIdentityLookupCompleted(OPIdentityLookup lookup)}
	 * 
	 * @param account
	 * @param delegate
	 * @param identityLookupInfos
	 * @return
	 */
	public static OPIdentityLookup create(
			OPAccount account,
			OPIdentityLookupDelegate delegate,
			List<OPIdentityLookupInfo> identityLookupInfos
			) {
		return create(
				account,
				delegate,
				identityLookupInfos,
				HOPSettingsHelper.getInstance().getIdentityProviderDomain());
	}

	private long nativeClassPointer;

	private long nativeDelegatePointer;

	public static native String toDebugString(OPIdentityLookup lookup, boolean includeCommaPrefix);

	public static native OPIdentityLookup create(
			OPAccount account,
			OPIdentityLookupDelegate delegate,
			List<OPIdentityLookupInfo> identityLookupInfos,
			String identityServiceDomain
			);

	public native long getStableID();

	public native boolean isComplete();

	public native boolean wasSuccessful(
			int outErrorCode,
			String outErrorReason
			);

	public native void cancel();

	public native List<OPIdentityContact> getUpdatedIdentities();

	public native List<OPIdentityLookupInfo> getUnchangedIdentities();

	public native List<OPIdentityLookupInfo> getInvalidIdentities();

	private native void releaseCoreObjects();

	protected void finalize() throws Throwable {

		if (nativeClassPointer != 0 || nativeDelegatePointer != 0)
		{
			Log.d("output", "Cleaning identity lookup core objects");
			releaseCoreObjects();
		}

		super.finalize();
	}
}
