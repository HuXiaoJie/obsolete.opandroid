package com.openpeer.sample.login;

import com.openpeer.sdk.model.HOPAccount;
import com.openpeer.sdk.model.HOPAccountIdentity;

public interface LoginViewHandler {
    public abstract void processIdentityMessageForInnerBrowserWindowFrame(HOPAccountIdentity identity,String message);

    public abstract void processAccountMessageForInnerBrowserWindowFrame(HOPAccount account,String message);

    public void onAccountLoginComplete();

    public void showIdentityLoginWebView(HOPAccountIdentity identity);

    public void showAccountLoginWebView();

    public void closeIdentityLoginWebView(HOPAccountIdentity identity);

    public void onIdentityReady(HOPAccountIdentity identity);

    public void closeAccountLoginWebView();
    public void loadAccountLoginUrl(String url);
    public void loadIdentityLoginUrl(HOPAccountIdentity identity,String url);

    /**
     * @param identity
     */
    public void onIdentityShutdown(HOPAccountIdentity identity);
    //    public void onSignoutComplete();
    public void onAccountShutdown();
}
