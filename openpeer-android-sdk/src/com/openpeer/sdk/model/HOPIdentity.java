package com.openpeer.sdk.model;

import com.openpeer.javaapi.OPIdentityContact;
import com.openpeer.javaapi.OPRolodexContact;

import java.util.List;

public class HOPIdentity {
    public OPRolodexContact getContact() {
        return contact;
    }

    OPRolodexContact contact;

    public HOPIdentity(OPRolodexContact contact){
        this.contact = contact;
    }

    public long getAssociatedIdentityId() {
        return contact.getAssociatedIdentityId();
    }

    public void setProfileURL(String mProfileURL) {
        contact.setProfileURL(mProfileURL);
    }

    public void setDisposition(OPRolodexContact.Dispositions mDisposition) {
        contact.setDisposition(mDisposition);
    }

    public void setName(String mName) {
        contact.setName(mName);
    }

    public String getDefaultAvatarUrl() {
        return contact.getDefaultAvatarUrl();
    }

    public void setUserId(long userId) {
        contact.setUserId(userId);
    }

    public void setIdentityProvider(String mIdentityProvider) {
        contact.setIdentityProvider(mIdentityProvider);
    }

    public String getVProfileURL() {
        return contact.getVProfileURL();
    }

    public String getProfileURL() {
        return contact.getProfileURL();
    }

    public void setRolodexId(long rolodexId) {
        contact.setRolodexId(rolodexId);
    }

    public String getIdentityProvider() {
        return contact.getIdentityProvider();
    }

    public void setmAssociatedIdentityId(long mAssociatedIdentityId) {
        contact.setmAssociatedIdentityId(mAssociatedIdentityId);
    }

    public List<OPRolodexContact.OPAvatar> getAvatars() {
        return contact.getAvatars();
    }

    public long getId() {
        return contact.getId();
    }

    public String getIdentityURI() {
        return contact.getIdentityURI();
    }

    public void setVProfileURL(String mVProfileURL) {
        contact.setVProfileURL(mVProfileURL);
    }

    public long getUserId() {
        return contact.getUserId();
    }

    public void setAvatars(List<OPRolodexContact.OPAvatar> mAvatars) {
        contact.setAvatars(mAvatars);
    }

    public OPRolodexContact.Dispositions getDisposition() {
        return contact.getDisposition();
    }

    public String getName() {
        return contact.getName();
    }

    public void setIdentityURI(String mIdentityURI) {
        contact.setIdentityURI(mIdentityURI);
    }

    public String getPeerFilePublic(){
        if(contact instanceof OPIdentityContact){
            return ((OPIdentityContact)contact).getPeerFilePublic().getPeerFileString();
        }
        return null;
    }

    public String getStableId(){
        if(contact instanceof OPIdentityContact){
            return ((OPIdentityContact)contact).getStableID();
        }
        return null;
    }

    public long getExpiresInMillis(){
        if(contact instanceof OPIdentityContact){
            return ((OPIdentityContact)contact).getExpires().toMillis(false);
        }
        return 0;
    }

    public int getPriority(){
        if(contact instanceof OPIdentityContact){
            return ((OPIdentityContact)contact).getPriority();
        }
        return 0;
    }

    public int getWeight(){
        if(contact instanceof OPIdentityContact){
            return ((OPIdentityContact)contact).getWeight();
        }
        return 0;
    }

    public long getLastUpdatedInMillis(){
        if(contact instanceof OPIdentityContact){
            return ((OPIdentityContact)contact).getLastUpdated().toMillis(false);
        }
        return 0;
    }

    public boolean isOpenPeerContact(){
        return contact instanceof OPIdentityContact;
    }

    public String getStableID(){
        if(contact instanceof OPIdentityContact) {
            return ((OPIdentityContact) contact).getStableID();
        }
        return null;
    }

}
