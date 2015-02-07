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
package com.openpeer.sample.util;

import java.util.List;

import com.openpeer.sdk.app.HOPDataManager;
import com.openpeer.sdk.model.HOPContact;

/**
 *
 */
public class ModelUtil {
    public static String getUserNamesFromIDsString(String rawContent) {
        String[] ids = rawContent.split(",");
        if (ids.length > 0) {
            long[] lIds = new long[ids.length];
            for (int i = 0; i < ids.length; i++) {
                lIds[i] = Long.parseLong(ids[i]);
            }
            List<HOPContact> users = HOPDataManager.getInstance()
                    .getUsers(lIds);
            StringBuilder sb = new StringBuilder();
            for (HOPContact user : users) {
                sb.append(user.getName() + ",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        return null;
    }
    public static String getNamesStringFromUsers(List<HOPContact> users){
        StringBuilder sb = new StringBuilder();
        for (HOPContact user : users) {
            sb.append(user.getName() + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
