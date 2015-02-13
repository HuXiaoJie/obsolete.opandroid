/*
 * ******************************************************************************
 *  *
 *  *  Copyright (c) 2014 , Hookflash Inc.
 *  *  All rights reserved.
 *  *
 *  *  Redistribution and use in source and binary forms, with or without
 *  *  modification, are permitted provided that the following conditions are met:
 *  *
 *  *  1. Redistributions of source code must retain the above copyright notice, this
 *  *  list of conditions and the following disclaimer.
 *  *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  *  this list of conditions and the following disclaimer in the documentation
 *  *  and/or other materials provided with the distribution.
 *  *
 *  *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  *
 *  *  The views and conclusions contained in the software and documentation are those
 *  *  of the authors and should not be interpreted as representing official policies,
 *  *  either expressed or implied, of the FreeBSD Project.
 *  ******************************************************************************
 */
package com.openpeer.utils;

import android.text.format.Time;

import com.openpeer.javaapi.OPIdentity;
import com.openpeer.javaapi.OPAccount;
import com.openpeer.javaapi.OPCall;
import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPIdentityContact;
import com.openpeer.javaapi.OPRolodexContact;
import com.openpeer.sdk.model.HOPContact;
import com.openpeer.sdk.model.GroupChatMode;
import com.openpeer.sdk.model.HOPConversation;
import com.openpeer.sdk.model.HOPConversationEvent;

import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class CoreTestObjectFactory {
    public static OPAccount getMockAccount() {
        OPAccount account = Mockito.mock(OPAccount.class);
        Mockito.when(account.getPeerUri()).thenReturn("peer://opp" +
                                                          ".me/c745ed4fbd6816d3b9cdb543d8f730bd590798b47349c0f37ea7e40e7d30747f");
        Mockito.when(account.getReloginInformation()).thenReturn
            ("{\\\"peer\\\":{\\\"$version\\\":\\\"1\\\"," +
                                                                     "\\\"sectionBundle\\\":[{\\\"section\\\":{\\\"$id\\\":\\\"A\\\"," +
                                                                     "\\\"algorithm\\\":\\\"https://meta.openpeer" +
                                                                     "" +
                 ".org/2013/07/21/jsonmsg#rsa-sha1-aes-cfb-32-16-16-sha256-md5\\\"," +
                                                                     "\\\"created\\\":1416378300," +
                 "\\\"expires\\\":1479450300," +
                                                                     "\\\"saltBundle\\\":{\\\"salt\\\":{\\\"$id" +
                                                                     "\\\":\\\"e5312679cc05a914710c666a7cfbedbc\\\"," +
                                                                     "\\\"#text\\\":\\\"cCMkhdUDVrvxBnq08sG/KcHqldgBASrQwEmjBMUgSLo=\\\"}," +
                                                                     "\\\"signature\\\":{\\\"reference\\\":\\\"#e5312679cc05a914710c666a7cfbedbc\\\"," +
                                                                     "\\\"algorithm\\\":\\\"http://meta.openpeer.org/2012/12/14/jsonsig#rsa-sha1\\\"," +
                                                                     "\\\"digestValue\\\":\\\"IUe324ko...V5/A8Q38Gj45i4jddX=\\\"," +
                                                                     "\\\"digestSigned\\\":\\\"DEf...GM~C0/Ez=\\\"," +
                                                                     "\\\"key\\\":{\\\"$id\\\":\\\"db144bb314103033cbba7d52e\\\"," +
                                                                     "\\\"domain\\\":\\\"opp" +
                 ".me\\\",\\\"service\\\":\\\"salt\\\"}}}}," +
                                                                     "\\\"signature\\\":{\\\"reference\\\":\\\"#A\\\"," +
                                                                     "\\\"algorithm\\\":\\\"https://meta.openpeer" +
                                                                     "" +
                 ".org/2012/12/14/jsonsig#rsa-sha1\\\"," +
                                                                     "\\\"digestValue\\\":\\\"Qdab+5u/4X7y3KztUeYKN2vwDjI=\\\"," +
                                                                     "\\\"digestSigned\\\":\\\"j4iXKfBtNTAdQJdj1Af5PNRvZJRT+k3S79Vtt36PT5njWBQr6cJ6" +
                                                                     "+s8S/8VTfZ9X9yyshbRN7NxtngdbvZKT/y9nUPXxB1UEq/rJiB" +
                                                                     "/HjJRN5dUIQHNQJeCjCtxz1LF6JmYuF9AAhxTi5rD4iLkOjDuD3rsDH7fKHAbE27S6K0p8Y1wAeKHsDV6xyuFGC+Pz6d5tBcpe4SiK+tAcWVPTIX8DV5zIOqOK0ENxWR6Ksp9CJ1w5TFG3rucbF4o27xU8Ivb2G2wEI0MnOlcjptFZMVyTXCdzpljtXBaiqfNAU8DfdUyYu6T9f4+Shcf607LkTtrTVv1s4sySsbl0TKclNg==\\\",\\\"key\\\":{\\\"x509Data\\\":\\\"MIIBIDANBgkqhkiG9w0BAQEFAAOCAQ0AMIIBCAKCAQEArS+w0To47Pcf+V0HMvwj/RopZ7dcSh99pNsoheiiUXvGNfy7o223bbPkC8ajNJ+9XAPocvYwMkRUXmFsKH80WAv1auL4vvgkjPysG/7A5eTzWXUyS021MkgxLS+2bZblVsd0svVN5E/ldXx+UrX5/4jsGZMI0Q+PU7Av88ywd3IVYxdwhhv3aejfYZdQAQWiT2yBBi3f4A0SFP1AGp8vjUuPnETwESaG5XZIHbboiX2aZXonKfSEJDAIsPtEjJ/0ubfQGqxx7ApR+hKluDd/kuG+4y+L6CiQMszfqqS+AkLHZ3Dppd3t0MVwAWrAFHqgfVsuoLBsH12iORaYCfm+TQIBEQ==\\\"}}},{\\\"section\\\":{\\\"$id\\\":\\\"B\\\",\\\"contact\\\":\\\"peer://opp.me/c745ed4fbd6816d3b9cdb543d8f730bd590798b47349c0f37ea7e40e7d30747f\\\",\\\"findSecret\\\":\\\"Quiw3mxiE0P3uP4bCiQiZ7hobPKmiCZCvD8r20tMsSvchQfiZs81\\\"},\\\"signature\\\":{\\\"reference\\\":\\\"#B\\\",\\\"algorithm\\\":\\\"https://meta.openpeer.org/2012/12/14/jsonsig#rsa-sha1\\\",\\\"digestValue\\\":\\\"xTcSgxmd+B74P5B7YnA43WKpr/4=\\\",\\\"digestSigned\\\":\\\"lKjvEE7Sge7Q1GkUKUMthJpsUBWDJ4mKy0MPgOr0Ryt1OnULv8zqu4OIWweWsFXAmVq+p7ScvlugsmRjIAfoJMbNqZNDwjkOn4rZO1rDSELTJW6w5NlvtGYvGxXGMKsc/lrPT300PljVYxuSa+vcBXZ0wF0qZPLUFuwwkKlPZDhv77ZAiMdVVIAEWS3uD5eApZQ7xxqMGavxPVxLvmzmVZHftLkOPpGP/qU00ZM+FfkRe5iba6jq5eAYKvNi3PguR/MsFmqjpLY8s2Y5Fd2tHddTDOYcB6h72+Latm1if2iqjJKarJ3LDlZnfcJ4y5KAJiEOeIXAIvkWQ3rFebrjSA==\\\",\\\"key\\\":{\\\"uri\\\":\\\"peer://opp.me/c745ed4fbd6816d3b9cdb543d8f730bd590798b47349c0f37ea7e40e7d30747f\\\"}}},{\\\"section\\\":{\\\"$id\\\":\\\"C\\\",\\\"contact\\\":\\\"peer://opp.me/c745ed4fbd6816d3b9cdb543d8f730bd590798b47349c0f37ea7e40e7d30747f\\\"},\\\"signature\\\":{\\\"reference\\\":\\\"#C\\\",\\\"algorithm\\\":\\\"https://meta.openpeer.org/2012/12/14/jsonsig#rsa-sha1\\\",\\\"digestValue\\\":\\\"lepi+locQvmAPICFuC3qjhzZx9A=\\\",\\\"digestSigned\\\":\\\"BgdbxyN4F6BFb4WyG+aami6XKhWMvhIPzUakToPL9KApAcChomTyC3/wtRAMoShEfr0xRUQIz8VJQIu+rLRGsVgw6PDjdXb+riK0dPj0iws9KufSHBUgQhungw+UR+Wa+BfM+I8503EXNWHLHI8Q52N6CPil3b3i++if1BK9Js0mdjMVOYF4yDHdCzq90vaTXzWbwgbcRvV6kWwulzaJufOuYFjSRQ/DF/FBvsHFbEqMHo5dFqCtuea46emWA+5rrA2CyRVe0pbAChyum6Mk5rX2yqZ8nRZnG9nE3eWrPAMuWq5/z5DHP30IyuJnGhVWCekEC1oGl3CsYhWmzjLPvw==\\\",\\\"key\\\":{\\\"uri\\\":\\\"peer://opp.me/c745ed4fbd6816d3b9cdb543d8f730bd590798b47349c0f37ea7e40e7d30747f\\\"}}}]}}");

        Mockito.when(account.getPeerUri()).thenReturn("peer://hcs.io/1234567890");
        return account;
    }

    public static HOPContact getMockHomeUser() {
        List<OPIdentityContact> identityContacts = new ArrayList<OPIdentityContact>();
        OPContact contact = getHomeOPContact();
        identityContacts.add(getCurrentUserIdentityContact());

        HOPContact user = new HOPContact(contact, identityContacts);
        user.setUserId(1);
        return user;
    }

    public static OPIdentity getMockHomeIdentity() {
        OPIdentity identity = Mockito.mock(OPIdentity.class);
        Mockito.when(identity.getIdentityURI()).thenReturn("identity://facebook" +
                                                               ".com/100003952283621");
        return identity;
    }

    static OPContact getHomeOPContact() {
        OPContact contact = Mockito.mock(OPContact.class);
        Mockito.when(contact.getPeerURI()).thenReturn("peer://opp" +
                                                          ".me/c745ed4fbd6816d3b9cdb543d8f730bd590798b47349c0f37ea7e40e7d30747f");
        Mockito.when(contact.getPeerFilePublic()).thenReturn
            ("{\\\"peer\\\":{\\\"$version\\\":\\\"1\\\"," +
                 "\\\"sectionBundle\\\":[{\\\"section\\\":{\\\"$id\\\":\\\"A\\\"," +
                 "\\\"algorithm\\\":\\\"https://meta.openpeer" +
                 ".org/2013/07/21/jsonmsg#rsa-sha1-aes-cfb-32-16-16-sha256-md5\\\"," +
                 "\\\"created\\\":1416378300,\\\"expires\\\":1479450300," +
                 "\\\"saltBundle\\\":{\\\"salt\\\":{\\\"$id" +
                 "\\\":\\\"e5312679cc05a914710c666a7cfbedbc\\\"," +
                 "\\\"#text\\\":\\\"cCMkhdUDVrvxBnq08sG/KcHqldgBASrQwEmjBMUgSLo=\\\"}," +
                 "\\\"signature\\\":{\\\"reference\\\":\\\"#e5312679cc05a914710c666a7cfbedbc\\\"," +
                 "\\\"algorithm\\\":\\\"http://meta.openpeer.org/2012/12/14/jsonsig#rsa-sha1\\\"," +
                 "\\\"digestValue\\\":\\\"IUe324ko...V5/A8Q38Gj45i4jddX=\\\"," +
                 "\\\"digestSigned\\\":\\\"DEf...GM~C0/Ez=\\\"," +
                 "\\\"key\\\":{\\\"$id\\\":\\\"db144bb314103033cbba7d52e\\\"," +
                 "\\\"domain\\\":\\\"opp.me\\\",\\\"service\\\":\\\"salt\\\"}}}}," +
                 "\\\"signature\\\":{\\\"reference\\\":\\\"#A\\\"," +
                 "\\\"algorithm\\\":\\\"https://meta.openpeer" +
                 ".org/2012/12/14/jsonsig#rsa-sha1\\\"," +
                 "\\\"digestValue\\\":\\\"Qdab+5u/4X7y3KztUeYKN2vwDjI=\\\"," +
                 "\\\"digestSigned\\\":\\\"j4iXKfBtNTAdQJdj1Af5PNRvZJRT+k3S79Vtt36PT5njWBQr6cJ6" +
                 "+s8S/8VTfZ9X9yyshbRN7NxtngdbvZKT/y9nUPXxB1UEq/rJiB" +
                 "/HjJRN5dUIQHNQJeCjCtxz1LF6JmYuF9AAhxTi5rD4iLkOjDuD3rsDH7fKHAbE27S6K0p8Y1wAeKHsDV6xyuFGC+Pz6d5tBcpe4SiK+tAcWVPTIX8DV5zIOqOK0ENxWR6Ksp9CJ1w5TFG3rucbF4o27xU8Ivb2G2wEI0MnOlcjptFZMVyTXCdzpljtXBaiqfNAU8DfdUyYu6T9f4+Shcf607LkTtrTVv1s4sySsbl0TKclNg==\\\",\\\"key\\\":{\\\"x509Data\\\":\\\"MIIBIDANBgkqhkiG9w0BAQEFAAOCAQ0AMIIBCAKCAQEArS+w0To47Pcf+V0HMvwj/RopZ7dcSh99pNsoheiiUXvGNfy7o223bbPkC8ajNJ+9XAPocvYwMkRUXmFsKH80WAv1auL4vvgkjPysG/7A5eTzWXUyS021MkgxLS+2bZblVsd0svVN5E/ldXx+UrX5/4jsGZMI0Q+PU7Av88ywd3IVYxdwhhv3aejfYZdQAQWiT2yBBi3f4A0SFP1AGp8vjUuPnETwESaG5XZIHbboiX2aZXonKfSEJDAIsPtEjJ/0ubfQGqxx7ApR+hKluDd/kuG+4y+L6CiQMszfqqS+AkLHZ3Dppd3t0MVwAWrAFHqgfVsuoLBsH12iORaYCfm+TQIBEQ==\\\"}}},{\\\"section\\\":{\\\"$id\\\":\\\"B\\\",\\\"contact\\\":\\\"peer://opp.me/c745ed4fbd6816d3b9cdb543d8f730bd590798b47349c0f37ea7e40e7d30747f\\\",\\\"findSecret\\\":\\\"Quiw3mxiE0P3uP4bCiQiZ7hobPKmiCZCvD8r20tMsSvchQfiZs81\\\"},\\\"signature\\\":{\\\"reference\\\":\\\"#B\\\",\\\"algorithm\\\":\\\"https://meta.openpeer.org/2012/12/14/jsonsig#rsa-sha1\\\",\\\"digestValue\\\":\\\"xTcSgxmd+B74P5B7YnA43WKpr/4=\\\",\\\"digestSigned\\\":\\\"lKjvEE7Sge7Q1GkUKUMthJpsUBWDJ4mKy0MPgOr0Ryt1OnULv8zqu4OIWweWsFXAmVq+p7ScvlugsmRjIAfoJMbNqZNDwjkOn4rZO1rDSELTJW6w5NlvtGYvGxXGMKsc/lrPT300PljVYxuSa+vcBXZ0wF0qZPLUFuwwkKlPZDhv77ZAiMdVVIAEWS3uD5eApZQ7xxqMGavxPVxLvmzmVZHftLkOPpGP/qU00ZM+FfkRe5iba6jq5eAYKvNi3PguR/MsFmqjpLY8s2Y5Fd2tHddTDOYcB6h72+Latm1if2iqjJKarJ3LDlZnfcJ4y5KAJiEOeIXAIvkWQ3rFebrjSA==\\\",\\\"key\\\":{\\\"uri\\\":\\\"peer://opp.me/c745ed4fbd6816d3b9cdb543d8f730bd590798b47349c0f37ea7e40e7d30747f\\\"}}},{\\\"section\\\":{\\\"$id\\\":\\\"C\\\",\\\"contact\\\":\\\"peer://opp.me/c745ed4fbd6816d3b9cdb543d8f730bd590798b47349c0f37ea7e40e7d30747f\\\"},\\\"signature\\\":{\\\"reference\\\":\\\"#C\\\",\\\"algorithm\\\":\\\"https://meta.openpeer.org/2012/12/14/jsonsig#rsa-sha1\\\",\\\"digestValue\\\":\\\"lepi+locQvmAPICFuC3qjhzZx9A=\\\",\\\"digestSigned\\\":\\\"BgdbxyN4F6BFb4WyG+aami6XKhWMvhIPzUakToPL9KApAcChomTyC3/wtRAMoShEfr0xRUQIz8VJQIu+rLRGsVgw6PDjdXb+riK0dPj0iws9KufSHBUgQhungw+UR+Wa+BfM+I8503EXNWHLHI8Q52N6CPil3b3i++if1BK9Js0mdjMVOYF4yDHdCzq90vaTXzWbwgbcRvV6kWwulzaJufOuYFjSRQ/DF/FBvsHFbEqMHo5dFqCtuea46emWA+5rrA2CyRVe0pbAChyum6Mk5rX2yqZ8nRZnG9nE3eWrPAMuWq5/z5DHP30IyuJnGhVWCekEC1oGl3CsYhWmzjLPvw==\\\",\\\"key\\\":{\\\"uri\\\":\\\"peer://opp.me/c745ed4fbd6816d3b9cdb543d8f730bd590798b47349c0f37ea7e40e7d30747f\\\"}}}]}}");
        return contact;
    }

    static OPIdentityContact getCurrentUserIdentityContact() {
        OPIdentityContact contact = new OPIdentityContact();
        contact.setIdentityURI("identity://facebook.com/100003952283621");
        contact.setExpires(new Time());
        contact.setLastUpdated(new Time());
        contact.setIdentityProofBundle("");
        contact.setStableID("0f0b9c8bf0a8978da553ab1f41fd15da35a12e2f");
        contact.setDisposition(OPRolodexContact.Dispositions.Disposition_NA);
        contact.setName("Test Namefour");

        return contact;
    }

    public static OPContact getMockContact() {
        OPContact contact = Mockito.mock(OPContact.class);
        Mockito.when(contact.getPeerURI()).thenReturn("peer://opp" +
                                                          ".me/38757c7768fedec48bc31073ccffe828f3cbaa7b4810631a8b19d616627936c5");
        Mockito.when(contact.getPeerFilePublic()).thenReturn
            ("\\\"peer\\\":{\\\"$version\\\":\\\"1\\\"," +
                 "\\\"sectionBundle\\\":[{\\\"section\\\":{\\\"$id\\\":\\\"A\\\"," +
                 "\\\"algorithm\\\":\\\"https://meta.openpeer" +
                 ".org/2013/07/21/jsonmsg#rsa-sha1-aes-cfb-32-16-16-sha256-md5\\\"," +
                 "\\\"created\\\":1414948125,\\\"expires\\\":1478020125," +
                 "\\\"saltBundle\\\":{\\\"salt\\\":{\\\"$id" +
                 "\\\":\\\"f48327a306683f5961a50de637da6fa0\\\"," +
                 "\\\"#text\\\":\\\"ot4EtoimtrdYraxhYSeN2Kr5y/jkbz906yhxkE5RWPU=\\\"}," +
                 "\\\"signature\\\":{\\\"algorithm\\\":\\\"http://meta.openpeer" +
                 ".org/2012/12/14/jsonsig#rsa-sha1\\\",\\\"digestSigned\\\":\\\"DEf.." +
                 ".GM~C0/Ez=\\\",\\\"digestValue\\\":\\\"IUe324ko...V5/A8Q38Gj45i4jddX=\\\"," +
                 "\\\"key\\\":{\\\"$id\\\":\\\"db144bb314103033cbba7d52e\\\"," +
                 "\\\"domain\\\":\\\"opp.me\\\",\\\"service\\\":\\\"salt\\\"}," +
                 "\\\"reference\\\":\\\"#f48327a306683f5961a50de637da6fa0\\\"}}}," +
                 "\\\"signature\\\":{\\\"algorithm\\\":\\\"https://meta.openpeer" +
                 ".org/2012/12/14/jsonsig#rsa-sha1\\\"," +
                 "\\\"digestSigned\\\":\\\"QvYuSHndX8eraeXJNr8pbkRLezm7FZzCyR0LdrH4I4a52jJ" +
                 "/BOdZtafE8sWPiDvHiOy9dnBelKOVEbX8ktpKEoWtgD" +
                 "/pnU0UzE0EJQ93lmTM21VTx3s8oWIGMlCK8OIjfB/xwi3K9IVVnVCEqjdvyluILoZ5y31" +
                 "+xdZ64vH9ktOsRqOV1Jf/q0jVC/cqc7rDUce8yxB" +
                 "/XiRwGf6bMDeYRKr6XOqhxyocfU7hSTzOvKMwCrvgvjkou0kvVvsisJTz/v2MPwmOHXe6cd16sgf" +
                 "+heTevHk89k90Q+7S2A4gS68LP7tThCIkmjttr92CZNDGgzIyQ1nGJCNPYDS8X+nuWg==\\\"," +
                 "\\\"digestValue\\\":\\\"1MbWT3gygcj89TonI5kuUv5DqTM=\\\"," +
                 "\\\"key\\\":{\\\"x509Data" +
                 "\\\":\\\"MIIBIDANBgkqhkiG9w0BAQEFAAOCAQ0AMIIBCAKCAQEApqYwv7ZWCNt2P7oybyEOw2v3LtWRoloaJ4+W8+v04fKQF5EMTvRJ/zpIwcB/RZ/lS6Bg0IRvaMKv0fINH6d0sU1vYAUyqdugL+zfKwhnjm1OSbOhZhSoj48TUim/VzzjA/Y3gWCWVfHTz68Nh424Svuu2C4R8fSgijqv1G3urcv3ZPNM8unrYR2WOGunGdxImxQIYh91XLZ/LKfw79MZ845TH9tRLjRfXabuTr5/CH+/J+j0rbNHK+WerXv7GZ3UFY2Ejwel8bvE8jTNymCJCUZ6R36Cuuc6FWHxzs6FPpUtMWKJyDBg0SJxz8meCtTXuc2XQVmz8nCSqeGJ5ySkWQIBEQ==\\\"},\\\"reference\\\":\\\"#A\\\"}},{\\\"section\\\":{\\\"$id\\\":\\\"B\\\",\\\"contact\\\":\\\"peer://opp.me/38757c7768fedec48bc31073ccffe828f3cbaa7b4810631a8b19d616627936c5\\\",\\\"findSecret\\\":\\\"FmvGr9DdtEPV5TIOB8Nfs3rwoZfSCz6zblMaQ0O23LwgoJJ5qwCQ\\\"},\\\"signature\\\":{\\\"algorithm\\\":\\\"https://meta.openpeer.org/2012/12/14/jsonsig#rsa-sha1\\\",\\\"digestSigned\\\":\\\"TtsJyeHa8c3JXD6rdjGrHde9ERdELfL7thFFZDFRHTPFKucwYPSg5qJWoFSLLa5hz7pL12OyblQNTNGPqzwv+aXDasG6eCOPx6p1RI6sfhRZLTIwTY1HyvCWYJ0gnWsVEwznLNhyeqnhchjFL2FF+aXy1TQBpNWOvmWhaNnlFoop5hAiO88E5McAO1NTxK+hzTl79UR1hgLmGuqDhQcHdGvIBCDrCy/scsvqTDD3mDr/hBW62xWJyOISCY/SabPBEV6khay6VBNcrj61cWqjWoOvm++O34r0gGqSKu67HCOecU9Rft0lZAfUzeI47uLmgge7i++89ImVLvMvygjtgA==\\\",\\\"digestValue\\\":\\\"QWAccd5yPNPMDxjhjQnpGakNWS0=\\\",\\\"key\\\":{\\\"uri\\\":\\\"peer://opp.me/38757c7768fedec48bc31073ccffe828f3cbaa7b4810631a8b19d616627936c5\\\"},\\\"reference\\\":\\\"#B\\\"}},{\\\"section\\\":{\\\"$id\\\":\\\"C\\\",\\\"contact\\\":\\\"peer://opp.me/38757c7768fedec48bc31073ccffe828f3cbaa7b4810631a8b19d616627936c5\\\"},\\\"signature\\\":{\\\"algorithm\\\":\\\"https://meta.openpeer.org/2012/12/14/jsonsig#rsa-sha1\\\",\\\"digestSigned\\\":\\\"Vg9fg3RUVcCZP35sHu3wyAWTNCsB+5YESNR0apjTWHXL8EhKOd91XL4uN4knXRFChHq7i0n1fm1V6eRkUmG/rFOA4kumcSHbziFLgw2hBhZgF9c37XAqRuS8fVhv7NQ2hhsx04pNNeYwtl5f5/UIL6Ai/uV9867PbbjAn7nPQ5tr37hF1CCsuLlx0JQVnw8AuXCCZBoC/iaRVwU9Xmdmn2iwSOXPa1khp3ZSprftVrTrTSVJYg4AtVXVSSO+U7RrajXotY2nDXYqIBuk+HVZgSO/pXI01pTfepH5o9nhMgfyVPS16lu/zqtc5PYrGA9NTgSJpNIBULEFpYnaQJKFhw==\\\",\\\"digestValue\\\":\\\"my0WmcckrHZuN5IKPYksLwQI3VM=\\\",\\\"key\\\":{\\\"uri\\\":\\\"peer://opp.me/38757c7768fedec48bc31073ccffe828f3cbaa7b4810631a8b19d616627936c5\\\"},\\\"reference\\\":\\\"#C\\\"}}]}}" +
                 "");
        return contact;
    }

    public static OPRolodexContact getRolodexContact1() {
        OPRolodexContact contact = new OPRolodexContact();

        contact.setDisposition(OPRolodexContact.Dispositions.Disposition_NA);
        contact.setName("Test Nametwo");
        contact.setIdentityURI("identity://facebook.com/100003823387069");
        contact.setIdentityProvider("facebook.com");
        contact.setDisposition(OPRolodexContact.Dispositions.Disposition_NA);

        return contact;
    }

    public static OPIdentityContact getIdentityContact() {
        OPIdentityContact contact = new OPIdentityContact(getRolodexContact1());
        Time expires = new Time();
        expires.set(System.currentTimeMillis() + 1000000000000l);
        contact.setExpires(expires);
        contact.setLastUpdated(new Time());
        contact.setIdentityProofBundle("");
        contact.setStableID("2a43d7c9331a4f3e0fd3cc7178356217e6cd63db");
        return contact;
    }

    public static HOPContact getUser1() {
        List<OPIdentityContact> identityContacts = new ArrayList<OPIdentityContact>();

        OPContact contact = getMockContact();
        identityContacts.add(getIdentityContact());
        HOPContact user = new HOPContact(contact, identityContacts);
        user.setUserId(2);
        return user;
    }

    public static HOPContact getUser2() {
        List<OPIdentityContact> identityContacts = new ArrayList<OPIdentityContact>();

        OPContact contact = getMockContact1();
        identityContacts.add(getIdentityContact1());
        HOPContact user = new HOPContact(contact, identityContacts);
        user.setUserId(3);
        return user;
    }

    public static OPRolodexContact getRolodexContact2() {
        OPRolodexContact contact = new OPRolodexContact();

        contact.setDisposition(OPRolodexContact.Dispositions.Disposition_NA);
        contact.setName("Test Namethree");
        contact.setIdentityURI("identity://facebook.com/100003823387069");
        contact.setIdentityProvider("facebook.com");
        contact.setDisposition(OPRolodexContact.Dispositions.Disposition_NA);

        return contact;
    }

    public static OPIdentityContact getIdentityContact1() {
        OPIdentityContact contact = new OPIdentityContact(getRolodexContact1());
        Time expires = new Time();
        expires.set(System.currentTimeMillis() + 1000000000000l);
        contact.setExpires(expires);
        contact.setLastUpdated(new Time());
        contact.setIdentityProofBundle("");
        contact.setStableID("84e6b751d3f5ff6fbf159911498b6e210b531a2c");
        return contact;
    }

    public static OPContact getMockContact1() {
        OPContact contact1 = Mockito.mock(OPContact.class);
        Mockito.when(contact1.getPeerURI()).thenReturn("peer://opp" +
                                                           ".me/d980982255a2975c49af9390cf31c059fe5bc0b946c1b95f5d0a587c82abd5ec");
        Mockito.when(contact1.getPeerFilePublic()).thenReturn
            ("{\\\"peer\\\":{\\\"$version\\\":\\\"1\\\"," +
                 "\\\"sectionBundle\\\":[{\\\"section\\\":{\\\"$id\\\":\\\"A\\\"," +
                 "\\\"algorithm\\\":\\\"https://meta.openpeer" +
                 ".org/2013/07/21/jsonmsg#rsa-sha1-aes-cfb-32-16-16-sha256-md5\\\"," +
                 "\\\"created\\\":1414948689,\\\"expires\\\":1478020689," +
                 "\\\"saltBundle\\\":{\\\"salt\\\":{\\\"$id" +
                 "\\\":\\\"8b599827d079301976f63f7a0c41d9bd\\\"," +
                 "\\\"#text\\\":\\\"HCmsfjtC6CKSowkd9i6bMH/OGTrXiB2igGcjbgXIHwk=\\\"}," +
                 "\\\"signature\\\":{\\\"algorithm\\\":\\\"http://meta.openpeer" +
                 ".org/2012/12/14/jsonsig#rsa-sha1\\\",\\\"digestSigned\\\":\\\"DEf.." +
                 ".GM~C0/Ez=\\\",\\\"digestValue\\\":\\\"IUe324ko...V5/A8Q38Gj45i4jddX=\\\"," +
                 "\\\"key\\\":{\\\"$id\\\":\\\"db144bb314103033cbba7d52e\\\"," +
                 "\\\"domain\\\":\\\"opp.me\\\",\\\"service\\\":\\\"salt\\\"}," +
                 "\\\"reference\\\":\\\"#8b599827d079301976f63f7a0c41d9bd\\\"}}}," +
                 "\\\"signature\\\":{\\\"algorithm\\\":\\\"https://meta.openpeer" +
                 ".org/2012/12/14/jsonsig#rsa-sha1\\\"," +
                 "\\\"digestSigned\\\":\\\"GzQluhLyXHvg3YO" +
                 "/0ydt1fPTqpMXKA5uOiXfpjL3Ek7jmmPYmwHw4a2Xd24uuEa1n8yGh2NvGyW24NZbD49hj8s" +
                 "/izxeg1VnzWxUdjziYfdRHy+ehvI7eSdaZVVUab7W0FrE/mD77hl11k4WWA8id3t8pFfNDZ9gZt8sCt" +
                 "+bxY3IwDH1cXvjoQEhWTufxcmmEK6LmDZWwCmY6mIP/33U1TQtGjEfLgCGeAvhucxEjXZIF" +
                 "+gPtmjboMGGHM9vEcnaE3c2eqzDOUZiTWQIs0OIdneGFK7VR2sE6caDiakW7X6NteUatjPHqeUUOBPnGX2e6X5qaSzQu5D4Z/AryBq+Pw==\\\",\\\"digestValue\\\":\\\"NmJ2VA64ttPTxkXPW7o6maccEgY=\\\",\\\"key\\\":{\\\"x509Data\\\":\\\"MIIBIDANBgkqhkiG9w0BAQEFAAOCAQ0AMIIBCAKCAQEApAAvJaIAIfTZ8x9pJVzJdKeCaQerH59X2rfVmWLUHEu/ko6OWawMZF+Y6t1Bz6YNSzra5FFCmd/+AIx4LOzIVzaKlco4XdiAkSI23GrNnswQKCYRuHbJ/yWMR7YqSMz4IGYyS+Ukq4UXgl49kpMeZPCG2cHk7t0qJdcoQPc32+I9fNMQku+le7kqZGu2sk151ybzXxQi0MoEz2h8/zuE9Tv4t4D+ndvKFZZI8NPbUmYjUPWlpqPNoL5mflSMX0Cz+Z14YPteh+/rybqBGL2UjkJy+EdFAcrAcL2IAfj7luixhNet4goAUSO26oztwgnRd2cqZvjBqGCVeBXOpa9QswIBEQ==\\\"},\\\"reference\\\":\\\"#A\\\"}},{\\\"section\\\":{\\\"$id\\\":\\\"B\\\",\\\"contact\\\":\\\"peer://opp.me/d980982255a2975c49af9390cf31c059fe5bc0b946c1b95f5d0a587c82abd5ec\\\",\\\"findSecret\\\":\\\"65g3OaSgIX90Nqjbg5c7TjFngtOm2Xu6UhdgZ0eSbaP29Ugp0wpu\\\"},\\\"signature\\\":{\\\"algorithm\\\":\\\"https://meta.openpeer.org/2012/12/14/jsonsig#rsa-sha1\\\",\\\"digestSigned\\\":\\\"nh8JLrRL90/U89SfaJtQ6II5Gxncsxy5+cjDVBBwp+cAXLXtHaHc687S3s0fGnOG5To2/izwQZWootXacKyc87kDnXGDylkwBy6vUcbf1R7ry9ChOOiUbm+CnSCSJyokIz4bXFbsY8p8r67/ftHnUoYucvvxzvu+EPKnAAzQJcoZ9WD3EsjqN8RJKR4SDVLer7WIVuk3iQ6L2xg6szRWXsyDNGw8Kk3CLDrCt72pZ4U0qoADUdCMNMsapNM6wFIV8Rkdi6DaqMZmXCLcsNBKVjUUV627EdJreyn2MNkAr/AUuutW43G7k885ZwTV6WMCMg58xz62hcWFhmK4N1tcxw==\\\",\\\"digestValue\\\":\\\"gk5bmenHnzzS5Bh/W+CanZ6iwwY=\\\",\\\"key\\\":{\\\"uri\\\":\\\"peer://opp.me/d980982255a2975c49af9390cf31c059fe5bc0b946c1b95f5d0a587c82abd5ec\\\"},\\\"reference\\\":\\\"#B\\\"}},{\\\"section\\\":{\\\"$id\\\":\\\"C\\\",\\\"contact\\\":\\\"peer://opp.me/d980982255a2975c49af9390cf31c059fe5bc0b946c1b95f5d0a587c82abd5ec\\\"},\\\"signature\\\":{\\\"algorithm\\\":\\\"https://meta.openpeer.org/2012/12/14/jsonsig#rsa-sha1\\\",\\\"digestSigned\\\":\\\"IYe7nhZCMDcooKLf6Xdk9suhYzD2CFoyWhgcbGg1oTNDkeRY5lazQNm96p2zJe2XzfH9JJyrC3curMlRcsq7vVJ1C+7QmvXrFRSOD+ZMzx0nQqX+jiZsuWmjuN0sxLoS5xyIjxT4N4r8cCexwCYwoI15HI5RYYxBdFQYbr226nAMHFee/S3nRp4SvHzYfe7+0OxDLUxR5zAnejlniInVf2/MMc3R6Rw400VvQq9O4bZ0//2igBsN/GDYRYkfUCRdchAVV3Fdn7yU1gxQmjXfbuHF7eJljQ93qNUTx6/2eMITSqGAxWkXINu6N0IvZU43ToIYuZJNq7tDWdDCjcezZQ==\\\",\\\"digestValue\\\":\\\"oIgl5pOtYwwhUdBdwfHoKjnCeBs=\\\",\\\"key\\\":{\\\"uri\\\":\\\"peer://opp.me/d980982255a2975c49af9390cf31c059fe5bc0b946c1b95f5d0a587c82abd5ec\\\"},\\\"reference\\\":\\\"#C\\\"}}]}}" +
                 "");

        return contact1;
    }

    public static HOPConversation getMockConversation() {
        List<HOPContact> users = new ArrayList<HOPContact>();
        HOPContact user1 = getUser1();

        HOPContact user2 = getUser2();
        users.add(user1);
        users.add(user2);

        HOPConversation conversation = Mockito.mock(HOPConversation.class);
        Mockito.when(conversation.getParticipants()).thenReturn(users);
        Mockito.when(conversation.getCurrentCbcId()).thenReturn(2530l);
        Mockito.when(conversation.getType()).thenReturn(GroupChatMode.contact);
        Mockito.when(conversation.getConversationId()).thenReturn("contextId-01");
        HOPConversationEvent lastEvent = new HOPConversationEvent(conversation, HOPConversationEvent
            .EventTypes.ContactsChange, "no description");
        Mockito.when(conversation.getLastEvent()).thenReturn(lastEvent);
        return conversation;
    }

    public static OPCall getMockCall() {
        OPCall call = Mockito.mock(OPCall.class);
        Mockito.when(call.getCallID()).thenReturn("callId-123455");
        OPContact contact = getMockContact();
        OPContact contactSelf = getHomeOPContact();
        Mockito.when(call.getCaller()).thenReturn(contact);
        Mockito.when(call.getCallee()).thenReturn(contactSelf);
        List<OPIdentityContact> identityContacts = new ArrayList<OPIdentityContact>();
        HOPContact user = getUser1();
        Mockito.when(call.getPeerUser()).thenReturn(user);
        return call;
    }

}
