package com.openpeer.sdk.model;

/**
 * Created by brucexia on 2015-01-20.
 */
public class ContactsRemovedSystemMessage {
    String[] contactsRemoved;//removed peer uris
    public ContactsRemovedSystemMessage(){}

    public ContactsRemovedSystemMessage(String[] contactsRemoved) {
        this.contactsRemoved = contactsRemoved;
    }

    public String[] getContactsRemoved() {
        return contactsRemoved;
    }
}
