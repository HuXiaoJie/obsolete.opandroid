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
package com.openpeer.sdk.delegates;

import java.util.Hashtable;

import android.util.Log;

import com.openpeer.javaapi.OPIdentity;
import com.openpeer.javaapi.OPIdentityLookup;
import com.openpeer.javaapi.OPIdentityLookupDelegate;
import com.openpeer.sdk.app.HOPDataManager;

/**
 * Default implementatiion of OPIdentityLookupDelegate. Handles identity lookup result and save it to datastore.
 */
public class OPIdentityLookupDelegateImpl extends OPIdentityLookupDelegate {
	private final static String TAG = OPIdentityLookupDelegateImpl.class.getSimpleName();

	private static Hashtable<String, OPIdentityLookupDelegateImpl> instances = new Hashtable<String, OPIdentityLookupDelegateImpl>();

	public static OPIdentityLookupDelegateImpl getInstance(OPIdentity identity) {
		String url = identity.getIdentityURI();
		OPIdentityLookupDelegateImpl instance = instances.get(url);
		if (instance == null) {
			instance = new OPIdentityLookupDelegateImpl();
			instance.mIdentity = identity;
		}
		instances.put(url, instance);
		return instance;
	}

	private OPIdentity mIdentity;

	private OPIdentityLookupDelegateImpl() {
	}

	public OPIdentityLookupDelegateImpl(OPIdentity identity) {
		mIdentity = identity;
	}

	@Override
	public void onIdentityLookupCompleted(OPIdentityLookup lookup) {
		String url = mIdentity.getIdentityURI();
		Log.d(TAG, "onIdentityLookupCompleted " + lookup);

		HOPDataManager.getInstance().onIdentityLookupCompleted(url, lookup);
		instances.remove(url);

	}

}
