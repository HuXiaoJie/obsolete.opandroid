/**
 * Copyright (c) 2014, SMB Phone Inc. / Hookflash Inc.
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p/>
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package com.openpeer.sdk.model;

import com.openpeer.javaapi.IdentityStates;

import java.util.Hashtable;

/**
 *
 */
public class IdentityData {
    boolean mLoggingIn;
    boolean mAssociating;
    IdentityStates pendingState;

    public IdentityStates getPendingState() {
        return pendingState;
    }

    public void setPendingState(IdentityStates pendingState) {
        this.pendingState = pendingState;
    }

    public boolean isLoggingIn() {
        return mLoggingIn;
    }

    public void setLoggingIn(boolean loggingIn) {
        this.mLoggingIn = loggingIn;
    }

    public boolean isAssociating() {
        return mAssociating;
    }

    public void setAssociating(boolean associating) {
        this.mAssociating = associating;
    }

    static Hashtable<Long, IdentityData> instances = new Hashtable<Long, IdentityData>();

    public static IdentityData getInstance(long id) {
        IdentityData data = instances.get(id);
        if (data == null) {
            data = new IdentityData();
            instances.put(id, data);
        }
        return data;
    }

}
