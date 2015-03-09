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
package com.openpeer.datastore;

import android.app.Application;

import com.openpeer.javaapi.OPIdentity;
import com.openpeer.javaapi.OPAccount;
import com.openpeer.javaapi.OPCall;
import com.openpeer.javaapi.OPLogger;
import com.openpeer.javaapi.OPRolodexContact;
import com.openpeer.sdk.model.HOPDataManager;
import com.openpeer.sdk.model.HOPContact;
import com.openpeer.sdk.model.GroupChatMode;
import com.openpeer.sdk.model.HOPConversation;
import com.openpeer.sdk.model.HOPParticipantInfo;
import com.openpeer.utils.CoreTestObjectFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RunWith(RobolectricTestRunner.class)
public class DatastoreDelegateImplTest {

    OPDatastoreDelegateImpl delegate;

    @Before
    public void setup() {
        Application application = Robolectric.application;
        HOPContact user = new HOPContact();
        user.setUserId(1);
        delegate = OPDatastoreDelegateImpl.getInstance();
        delegate.init(application);
        delegate.setupForTest();
        OPLogger.setIsUnitTest(true);
        HOPDataManager.getInstance().init(delegate);
    }

    @After
    public void teardown() {

    }

    @Test
    public void testSaveAccount() {
        OPAccount account = Mockito.mock(OPAccount.class);
        Mockito.when(account.getPeerUri()).thenReturn("peer://hcs.io/1234567890");
        Mockito.when(account.getReloginInformation()).thenReturn(
            "{\"reloginInfo\":\"peer://hcs.io/1234567890\"}");
        Mockito.when(account.getPeerUri()).thenReturn("peer://hcs.io/1234567890");
        // delegate.saveAccount(account);

    }

    @Test
    public void testSaveCall() {

        OPCall call = CoreTestObjectFactory.getMockCall();
        HOPConversation conversation = CoreTestObjectFactory.getMockConversation();
        long id = delegate.saveCall(call.getCallID(), conversation.getConversationId(),1 ,0 ,"audio" );
        Assert.assertEquals(1, id);
    }

    @Test
    public void testSaveDownloadedContacts() {
        OPIdentity identity = CoreTestObjectFactory.getMockHomeIdentity();
        List<OPRolodexContact> contacts = new ArrayList<>();
        OPRolodexContact contact1 = CoreTestObjectFactory.getRolodexContact1();
        OPRolodexContact contact2 = CoreTestObjectFactory.getRolodexContact2();
        contacts.add(contact2);

        List<OPRolodexContact> results = delegate.saveDownloadedRolodexContacts(identity,
                                                                                contacts,
                                                                                "abcdefg");
        Assert.assertEquals(contact1, results.get(0));
        Assert.assertEquals(contact2, results.get(1));

    }

    @Test
    public void testSaveConversation() {
        List<HOPContact> users = new ArrayList<HOPContact>();
        HOPContact user1 = CoreTestObjectFactory.getUser1();
        HOPContact user2 = CoreTestObjectFactory.getUser2();
        users.add(user1);
        users.add(user2);
        HOPParticipantInfo info=new HOPParticipantInfo(2530l,users);
        HOPConversation conversation = new HOPConversation(info, "123", GroupChatMode.contact);
        long id = delegate.saveConversation(conversation);
        Assert.assertEquals(id, 1);
    }


}
