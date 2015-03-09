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

import java.util.Arrays;
import java.util.List;

public class OPRolodexContact {

    public static class OPAvatar {
        private String mName;
        private String mURL;
        private int mWidth;
        private int mHeight;

        public OPAvatar(String mName, String mURL, int mWidth, int mHeight) {
            super();
            this.mName = mName;
            this.mURL = mURL;
            this.mWidth = mWidth;
            this.mHeight = mHeight;
        }

        OPAvatar() {

        }

        public String getName() {
            return mName;
        }

        public void setName(String mName) {
            this.mName = mName;
        }

        public String getURL() {
            return mURL;
        }

        public void setURL(String mURL) {
            this.mURL = mURL;
        }

        public int getWidth() {
            return mWidth;
        }

        public void setWidth(int mWidth) {
            this.mWidth = mWidth;
        }

        public int getHeight() {
            return mHeight;
        }

        public void setHeight(int mHeight) {
            this.mHeight = mHeight;
        }

        public String toString() {
            return super.toString() + " Name " + mName + " URL " + mURL
                    + " Width " + mWidth + " Height " + mHeight;
        }

    };

    public enum Dispositions {
        Disposition_NA, Disposition_Update, Disposition_Remove;

        Dispositions() {
        }

    };

    private Dispositions mDisposition;
    private String mIdentityURI;
    private String mIdentityProvider;

    private String mName;
    private String mProfileURL;
    private String mVProfileURL;

    private List<OPAvatar> mAvatars;
    private long mAssociatedIdentityId;
    private long openpeerContactId;
    private long mRolodexId;// Database id

    public long getAssociatedIdentityId() {
        return mAssociatedIdentityId;
    }

    public void setmAssociatedIdentityId(long mAssociatedIdentityId) {
        this.mAssociatedIdentityId = mAssociatedIdentityId;
    }

    public OPRolodexContact(long rolodexId,String mIdentityURI, String mIdentityProvider,
            String mName, String mProfileURL, String mVProfileURL,
            List<OPAvatar> mAvatars ) {
        super();
        this.mIdentityURI = mIdentityURI;
        this.mIdentityProvider = mIdentityProvider;
        this.mName = mName;
        this.mProfileURL = mProfileURL;
        this.mVProfileURL = mVProfileURL;
        this.mAvatars = mAvatars;
        this.mRolodexId = rolodexId;
    }

    public long getId() {
        // for now just use the profileURL, may need better algorithm
        return mRolodexId;
    }

    public OPRolodexContact(String name, String profileURL) {
        super();
        this.mName = name;
        this.mProfileURL = profileURL;
    }

    public OPRolodexContact() {

    }

    /**
     * A local ID used to identity a user, it's simply the "_ID" field in database. A user is deemed the same if any of the follow meet: --
     * peerURI -- stableID -- identityURI( TO BE DETERMINED)
     * 
     * @return
     */
    public long getUserId() {
        // TODO Auto-generated method stub
        return openpeerContactId;
    }

    public void setUserId(long userId) {
        this.openpeerContactId = userId;
    }

    public Dispositions getDisposition() {
        if (mDisposition == null) {
            mDisposition = Dispositions.Disposition_NA;
        }
        return mDisposition;
    }

    public void setDisposition(Dispositions mDisposition) {
        this.mDisposition = mDisposition;
    }

    public String getIdentityURI() {
        return mIdentityURI;
    }

    public void setIdentityURI(String mIdentityURI) {
        this.mIdentityURI = mIdentityURI;
    }

    public String getIdentityProvider() {
        return mIdentityProvider;
    }

    public void setIdentityProvider(String mIdentityProvider) {
        this.mIdentityProvider = mIdentityProvider;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getProfileURL() {
        return mProfileURL;
    }

    public void setProfileURL(String mProfileURL) {
        this.mProfileURL = mProfileURL;
    }

    public String getVProfileURL() {
        return mVProfileURL;
    }

    public void setVProfileURL(String mVProfileURL) {
        this.mVProfileURL = mVProfileURL;
    }

    public List<OPAvatar> getAvatars() {
        return mAvatars;
    }

    public void setAvatars(List<OPAvatar> mAvatars) {
        this.mAvatars = mAvatars;
    }

    public String getDefaultAvatarUrl() {
        if (mAvatars != null && mAvatars.size() != 0) {
            return mAvatars.get(0).mURL;
        } else {
            return null;
        }
    }

    OPRolodexContact copy(OPRolodexContact contact) {
        this.mAvatars = contact.mAvatars;
        this.mDisposition = contact.mDisposition;
        this.mIdentityProvider = contact.mIdentityProvider;
        this.mIdentityURI = contact.mIdentityURI;
        this.mName = contact.mName;
        this.mProfileURL = contact.mProfileURL;
        this.mVProfileURL = contact.mVProfileURL;
        this.mAssociatedIdentityId = contact.mAssociatedIdentityId;
        return this;
    }

    public String toString() {
        return super.toString()
                + " ProfileURL "
                + mProfileURL
                + " Name "
                + mName
                + " identityUrl "
                + mIdentityURI
                + " IdentityProvider "
                + mIdentityProvider
                + " Disposition "
                + mDisposition
                + " avatars "
                + (mAvatars != null ? Arrays.deepToString(mAvatars.toArray())
                        : "null");
    }

    /**
     * @param rolodexId
     */
    public void setRolodexId(long rolodexId) {
        mRolodexId =rolodexId;
    }
}
