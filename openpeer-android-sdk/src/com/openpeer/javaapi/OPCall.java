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

import android.text.format.Time;

import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.model.OPUser;

public class OPCall {
    public static final int DIRECTION_OUTGOING = 0;
    public static final int DIRECTION_INCOMING = 1;

    public long getCbcId() {
        return mCbcId;
    }

    public void setCbcId(long cbcId) {
        this.mCbcId = cbcId;
    }

    long mCbcId;
    /**
     * Helper function. Get the caller/callee OPContact.
     * 
     * @return
     */
    public OPContact getPeer() {
        OPContact peer = getCaller();
        if (peer.isSelf()) {
            return getCallee();
        } else {
            return peer;
        }
    }

    /**
     * Helper function to retrieve the peer user information.
     * 
     * @return
     */
    public OPUser getPeerUser() {
        OPContact contact = getPeer();
        return OPDataManager.getInstance().getUser(contact,
                getIdentityContactList(contact));
    }

    /**
     * Helper function. Retrieve identity contact list of the peer
     * 
     * @param contact
     * @return
     */
    public List<OPIdentityContact> getIdentityContactList(OPContact contact) {
        // TODO Auto-generated method stub
        return getConversationThread().getIdentityContactList(contact);
    }

    public OPCall() {
    }

    // BEGINNING OF JNI -- BE CAREFUL OF ANY SIGNATURE CHANGES
    private long nativeClassPointer;
    private OPUser mUser;

    public static native String toString(CallStates state);

    public static native String toString(CallClosedReasons reason);

    public static native String toDebugString(OPCall call,
            boolean includeCommaPrefix);

    public static native OPCall placeCall(
            OPConversationThread conversationThread, OPContact toContact,
            boolean includeAudio,
            boolean includeVideo);

    public native long getStableID();

    public native String getCallID();

    public native OPConversationThread getConversationThread();

    public native OPContact getCaller();

    public native OPContact getCallee();

    public native boolean hasAudio();

    public native boolean hasVideo();

    public native CallStates getState(); // state is always relative to "this" location, be is caller or callee

    public native int getClosedReason();

    public native Time getCreationTime();

    public native Time getRingTime();

    public native Time getAnswerTime();

    public native Time getClosedTime();

    public native void ring(); // tell the caller that the call is ringing

    public native void answer(); // answer the call

    public native void hold(boolean hold); // place the call on hold (or remove from hold)

    public native void hangup(int reason); // end the call

    private native void releaseCoreObjects();

    protected void finalize() throws Throwable {

        if (nativeClassPointer != 0)
        {
            releaseCoreObjects();
        }

        super.finalize();
    }

}
