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
package com.openpeer.sdk.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPConversationThread;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.model.OPUser;

public class OPModelUtils {

    /**
     * Calculate a unique window id for contacts based group chat mode
     * 
     * @param userIds
     *            local User ids array of the conversation participants
     * @return
     */
    public static long getWindowId(long userIds[]) {
        long tmp[] = new long[userIds.length + 1];
        tmp[userIds.length] = OPDataManager.getDatastoreDelegate()
                .getLoggedinUser()
                .getUserId();
        System.arraycopy(userIds, 0, tmp, 0, userIds.length);
        Arrays.sort(tmp);
        String arr[] = new String[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            arr[i] = "" + tmp[i];
        }
        long code = Arrays.deepHashCode(arr);
        Log.d("test",
                " hash code " + code + " array " + Arrays.deepToString(arr));
        return code;
    }

    /**
     * Calculate a unique window id for contacts based group chat mode
     * 
     * @param users
     *            List of participants
     * @return
     */
    public static long getWindowId(List<OPUser> users) {
        long userIds[] = new long[users.size()];
        for (int i = 0; i < userIds.length; i++) {
            OPUser user = users.get(i);
            userIds[i] = user.getUserId();
        }
        // TODO Auto-generated method stub
        return getWindowId(userIds);
    }

    public static long getWindowIdForThread(OPConversationThread mConvThread) {

        List<OPContact> contacts = mConvThread.getContacts();
        List<OPUser> users = new ArrayList<OPUser>();
        for (OPContact contact : contacts) {
            if (!contact.isSelf()) {
                OPUser user = OPDataManager.getDatastoreDelegate().getUser(
                        contact,
                        mConvThread.getIdentityContactList(contact));
                // This function will also set the userId so don't worry
                users.add(user);
            }
        }
        return getWindowId(users);
    }
}
