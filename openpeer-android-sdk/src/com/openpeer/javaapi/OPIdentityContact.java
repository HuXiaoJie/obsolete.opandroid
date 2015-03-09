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

public class OPIdentityContact extends OPRolodexContact {

    private String mStableID;

    private OPPeerFilePublic mPeerFilePublic;
    private String mIdentityProofBundleEl;

    private int mPriority;
    private int mWeight;

    private Time mLastUpdated;
    private Time mExpires;

    public OPIdentityContact(long rolodexId,String mIdentityURI, String mIdentityProvider,
            String mName, String mProfileURL, String mVProfileURL,
            List<OPAvatar> mAvatars) {
        super(rolodexId,mIdentityURI, mIdentityProvider,
                mName, mProfileURL, mVProfileURL,
                mAvatars);
    }

    /**
     * @ExcludeFromJavadoc
     * @param mStableID
     * @param peerFileString
     * @param mIdentityProofBundleEl
     * @param mPriority
     * @param mWeight
     * @param lastUpdateTime
     * @param expireTime
     * @param id
     * @return
     */
    public OPIdentityContact setIdentityParams(String mStableID,
            String peerFileString, String mIdentityProofBundleEl,
            int mPriority, int mWeight, long lastUpdateTime, long expireTime
            ) {

        this.mStableID = mStableID;
        this.mPeerFilePublic = new OPPeerFilePublic(peerFileString);
        this.mIdentityProofBundleEl = mIdentityProofBundleEl;
        this.mPriority = mPriority;
        this.mWeight = mWeight;
        this.mLastUpdated = new Time();
        mLastUpdated.set(lastUpdateTime);
        this.mExpires = new Time();
        mExpires.set(expireTime);
        return this;
    }

    public OPIdentityContact() {

    }

    public OPIdentityContact(OPRolodexContact rolodexContact) {
        copy(rolodexContact);
    }

    public boolean hasData() {
        return false;
    }

    public String getStableID() {
        return mStableID;
    }

    public void setStableID(String mStableID) {
        this.mStableID = mStableID;
    }

    public OPPeerFilePublic getPeerFilePublic() {
        return mPeerFilePublic;
    }

    public void setPeerFilePublic(OPPeerFilePublic mPeerFilePublic) {
        this.mPeerFilePublic = mPeerFilePublic;
    }

    public String getIdentityProofBundle() {
        return mIdentityProofBundleEl;
    }

    public void setIdentityProofBundle(String mIdentityProofBundleEl) {
        this.mIdentityProofBundleEl = mIdentityProofBundleEl;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int mPriority) {
        this.mPriority = mPriority;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int mWeight) {
        this.mWeight = mWeight;
    }

    public Time getLastUpdated() {
        return mLastUpdated;
    }

    public void setLastUpdated(Time mLastUpdated) {
        this.mLastUpdated = mLastUpdated;
    }

    public Time getExpires() {
        return mExpires;
    }

    public void setExpires(Time mExpires) {
        this.mExpires = mExpires;
    }

    public String toString() {
        return super.toString() + " identityProofBundle "
                + mIdentityProofBundleEl + " mStableID " + mStableID
                + " mExpires " + mExpires;
    }

}
