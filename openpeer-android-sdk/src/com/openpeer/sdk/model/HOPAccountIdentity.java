package com.openpeer.sdk.model;

import android.text.format.Time;

import com.openpeer.javaapi.*;
import com.openpeer.javaapi.OPIdentity;

public class HOPAccountIdentity {
    private OPIdentity identity;

    public OPIdentity getIdentity() {
        return identity;
    }

    HOPAccountIdentity(OPIdentity identity){
        this.identity = identity;
    }
    public static HOPAccountIdentity login(String identityUri, OPAccount account, OPIdentityDelegate
        delegate) {
        return new HOPAccountIdentity(OPIdentity.login(identityUri, account, delegate));
    }

    public static OPIdentity loginWithIdentityPreauthorized(OPAccount account, OPIdentityDelegate
        delegate, String identityProviderDomain, String identityURI, String identityAccessToken,
                                                            String identityAccessSecret,
                                                            Time identityAccessSecretExpires) {
        return OPIdentity.loginWithIdentityPreauthorized(account, delegate,
                                                         identityProviderDomain, identityURI,
                                                         identityAccessToken,
                                                         identityAccessSecret,
                                                         identityAccessSecretExpires);
    }

    public void handleMessageFromInnerBrowserWindowFrame(String message) {
        identity.handleMessageFromInnerBrowserWindowFrame(message);
    }

    public long getStableID() {
        return identity.getStableID();
    }

    public void refreshContacts() {
        identity.refreshRolodexContacts();
    }

    public IdentityStates getState() {
        return identity.getState();
    }

    public String getIdentityProviderDomain() {
        return identity.getIdentityProviderDomain();
    }

    public boolean isDelegateAttached() {
        return identity.isDelegateAttached();
    }

    public long getID() {
        return identity.getID();
    }

    public void startRolodexDownload(String inLastDownloadedVersion) {
        identity.startRolodexDownload(inLastDownloadedVersion);
    }

    public String getInnerBrowserWindowFrameURL() {
        return identity.getInnerBrowserWindowFrameURL();
    }

    public HOPIdentity getSelfIdentityContact() {
        return new HOPIdentity(identity.getSelfIdentityContact());
    }

    public static String toString(IdentityStates state) {
        return OPIdentity.toString(state);
    }

    public OPDownloadedRolodexContacts getDownloadedRolodexContacts() {
        return identity.getDownloadedRolodexContacts();
    }

    public void attachDelegateAndPreauthorizedLogin(OPIdentityDelegate delegate, String identityAccessToken, String identityAccessSecret, Time identityAccessSecretExpires) {
        identity.attachDelegateAndPreauthorizedLogin(delegate, identityAccessToken,
                                                     identityAccessSecret,
                                                     identityAccessSecretExpires);
    }

    public static String toDebugString(OPIdentity identity, Boolean includeCommaPrefix) {
        return OPIdentity.toDebugString(identity, includeCommaPrefix);
    }

    public String getIdentityURI() {
        return identity.getIdentityURI();
    }

    public IdentityStates getState(int outLastErrorCode, String outLastErrorReason) {
        return identity.getState(outLastErrorCode, outLastErrorReason);
    }

    public void attachDelegate(OPIdentityDelegate delegate, String outerFrameURLUponReload) {
        identity.attachDelegate(delegate, outerFrameURLUponReload);
    }

    public void notifyBrowserWindowVisible() {
        identity.notifyBrowserWindowVisible();
    }

    public static OPIdentity login(OPAccount account, OPIdentityDelegate delegate, String identityProviderDomain, String identityURI_or_identityBaseURI, String outerFrameURLUponReload) {
        return OPIdentity.login(account, delegate, identityProviderDomain,
                                identityURI_or_identityBaseURI, outerFrameURLUponReload);
    }

    public void notifyBrowserWindowClosed() {
        identity.notifyBrowserWindowClosed();
    }

    public void cancel() {
        identity.cancel();
    }

    public String getNextMessageForInnerBrowerWindowFrame() {
        return identity.getNextMessageForInnerBrowerWindowFrame();
    }

    boolean mLoggingIn;
    boolean mAssociating;
    IdentityStates pendingState;
    String pendingCommand;

    public IdentityStates getPendingState() {
        return pendingState;
    }

    public void setPendingState(IdentityStates pendingState) {
        this.pendingState = pendingState;
    }

    public boolean isLoggingIn() {
        return mLoggingIn;
    }

    public void setLoggingIn(boolean loggingIn) {
        this.mLoggingIn = loggingIn;
    }

    public boolean isAssociating() {
        return mAssociating;
    }

    public void setAssociating(boolean associating) {
        this.mAssociating = associating;
    }

    public String getPendingCommand() {
        return pendingCommand;
    }

    public void setPendingCommand(String pendingCommand) {
        this.pendingCommand = pendingCommand;
    }

}
