package com.openpeer.sample.login;

import com.openpeer.javaapi.OPAccount;
import com.openpeer.javaapi.OPIdentity;

public interface LoginViewHandler {
    public abstract void processIdentityMessageForInnerBrowserWindowFrame(OPIdentity identity,String message);

    public abstract void processAccountMessageForInnerBrowserWindowFrame(OPAccount account,String message);

    public void onAccountLoginComplete();

    public void showIdentityLoginWebView(OPIdentity identity);

    public void showAccountLoginWebView();

    public void closeIdentityLoginWebView(OPIdentity identity);

    public void onIdentityReady(OPIdentity identity);

    public void closeAccountLoginWebView();
    public void loadAccountLoginUrl(String url);
    public void loadIdentityLoginUrl(OPIdentity identity,String url);

    /**
     * @param identity
     */
    public void onIdentityShutdown(OPIdentity identity);
    //    public void onSignoutComplete();
    public void onAccountShutdown();
}
