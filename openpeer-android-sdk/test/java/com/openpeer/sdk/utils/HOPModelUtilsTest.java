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
package com.openpeer.sdk.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.app.Application;

import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.model.HOPContact;

/**
 *
 */
@RunWith(RobolectricTestRunner.class)
public class HOPModelUtilsTest {

    @Before
    public void setup() {
        Application application = Robolectric.application;
        OPDatastoreDelegateImpl delegate = OPDatastoreDelegateImpl.getInstance();
        delegate.init(application);
        delegate.setupForTest();
        HOPDataManager.getInstance().init(delegate);
    }

    @After
    public void destroy() {
    }

    @Test
    public void testGetWindowIdArrayInput() {
        long[] userIds = { 1l, 2l, 3l };
        long[] userIds1 = { 1l, 3l, 2l };
        Assert.assertEquals(HOPModelUtils.getWindowId(userIds), HOPModelUtils.getWindowId(userIds1));
    }

    @Test
    public void testGetWindowIdListInput() {
        long[] userIds = { 1l, 3l, 2l };

        List<HOPContact> users = new ArrayList<HOPContact>();
        HOPContact user1 = new HOPContact();
        HOPContact user2 = new HOPContact();
        HOPContact user3 = new HOPContact();
        user1.setUserId(1);
        user2.setUserId(2);
        user3.setUserId(3);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        Assert.assertEquals(HOPModelUtils.getWindowId(users), HOPModelUtils.getWindowId(userIds));
    }
}
