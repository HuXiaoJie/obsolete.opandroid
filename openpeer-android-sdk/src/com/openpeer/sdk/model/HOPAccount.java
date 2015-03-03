package com.openpeer.sdk.model;

import com.openpeer.javaapi.*;
import com.openpeer.javaapi.OPIdentity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class HOPAccount {
    private OPAccount account;

    private long mAccountId;
    private long selfContactId;
    private HOPContact selfContact;

    public static long selfContactId() {

        return currentAccount() == null ? HOPDataManager.getInstance().getCurrentUserId() :
            currentAccount().selfContactId;
    }

    public long getSelfContactId() {
        return selfContactId;
    }

    public void setSelfContactId(long selfContactId) {
        this.selfContactId = selfContactId;
    }

    public void setSelfContact(HOPContact selfContact) {
        this.selfContact = selfContact;
    }

    public static HOPContact selfContact() {
        return currentAccount() == null ? null : currentAccount().getSelfContact();
    }

    public HOPContact getSelfContact() {
//        if (selfContact == null && selfContactId != 0) {
        selfContact = HOPDataManager.getInstance().getUserById(HOPDataManager.getInstance()
                                                                   .getCurrentUserId());
//        }
        return selfContact;

    }

    HashMap<Long, HOPAccountIdentity> associatedIdentities = new HashMap<>();

    HOPAccount(OPAccount account) {
        this.account = account;
    }

    public static HOPAccount currentAccount() {
        return HOPLoginManager.getInstance().mAccount;
    }

    public static boolean isAccountReady() {
        return currentAccount() != null &&
            currentAccount().getState() == AccountStates.AccountState_Ready;
    }

    public OPAccount getAccount() {
        return account;
    }

    public long getAccountId() {
        return mAccountId;
    }

    public void setAccountId(long id) {
        this.mAccountId = id;
    }

    public void handleMessageFromInnerBrowserWindowFrame(String unparsedMessage) {
        account.handleMessageFromInnerBrowserWindowFrame(unparsedMessage);
    }

    public String getReloginInformation() {
        return account.getReloginInformation();
    }

    public String getStableID() {
        return account.getStableID();
    }

    public long getID() {
        return account.getID();
    }

    public static HOPAccount login(OPAccountDelegate accountDelegate, OPConversationThreadDelegate
        conversationThreadDelegate, OPCallDelegate callDelegate) {
        HOPAccount account = new HOPAccount(OPAccount.login(accountDelegate,
                                                            conversationThreadDelegate,
                                                            callDelegate));
        account.setAccountId(1);
        return account;
    }

    public void notifyBrowserWindowClosed() {
        account.notifyBrowserWindowClosed();
    }

    public static String toString(AccountStates state) {
        return OPAccount.toString(state);
    }

    public static String toDebugString(OPAccount account, boolean includeCommaPrefix) {
        return OPAccount.toDebugString(account, includeCommaPrefix);
    }

    public void notifyBrowserWindowVisible() {
        account.notifyBrowserWindowVisible();
    }

    public void shutdown() {
        for (HOPAccountIdentity identity : associatedIdentities.values()) {
            identity.cancel();
        }
        account.shutdown();
    }

    public void removeIdentities(List<OPIdentity> identitiesToRemove) {
        account.removeIdentities(identitiesToRemove);
    }

    public String getNextMessageForInnerBrowerWindowFrame() {
        return account.getNextMessageForInnerBrowerWindowFrame();
    }

    public AccountStates getState() {
        return account.getState();
    }

    public static HOPAccount relogin(OPAccountDelegate accountDelegate,
                                     OPConversationThreadDelegate conversationThreadDelegate,
                                     OPCallDelegate callDelegate, String reloginInformation) {
        HOPAccount account1 = new HOPAccount(OPAccount.relogin(accountDelegate,
                                                               conversationThreadDelegate,
                                                               callDelegate,
                                                               reloginInformation));
        account1.setSelfContactId(HOPDataManager.getInstance().getCurrentUserId());
        account1.setAccountId(HOPDataManager.getInstance().getCurrentAccountId());
        return account1;
    }

    public AccountStates getState(int outErrorCode, String outErrorReason) {
        return account.getState(outErrorCode, outErrorReason);
    }

    public String getInnerBrowserWindowFrameURL() {
        return account.getInnerBrowserWindowFrameURL();
    }

    public String getLocationID() {
        return account.getLocationID();
    }

    HOPAccountIdentity getIdentity(long id) {
        return associatedIdentities.get(id);
    }

    public List<HOPAccountIdentity> getAssociatedIdentities() {
        return new ArrayList(associatedIdentities.values());
    }

    public void addIdentity(HOPAccountIdentity identity) {
        associatedIdentities.put(identity.getID(), identity);
    }

    public List<HOPIdentity> getSelfContacts() {
        Collection<HOPAccountIdentity> identities = associatedIdentities.values();
        List<HOPIdentity> mSelfContacts = new ArrayList<>();
        for (HOPAccountIdentity identity : identities) {
            mSelfContacts.add(identity.getSelfIdentityContact());
        }
        return mSelfContacts;
    }

    public List<OPIdentityContact> identityContacts() {
        Collection<HOPAccountIdentity> identities = associatedIdentities.values();
        List<OPIdentityContact> mSelfContacts = new ArrayList<>();
        for (HOPAccountIdentity identity : identities) {
            mSelfContacts.add((OPIdentityContact) identity.getSelfIdentityContact().getContact());
        }
        return mSelfContacts;
    }

    public void refreshContacts() {
        for (HOPAccountIdentity identity : associatedIdentities.values()) {
            identity.refreshContacts();
        }
    }
}
