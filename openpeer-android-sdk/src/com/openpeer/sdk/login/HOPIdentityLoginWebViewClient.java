/*******************************************************************************
 *
 *  Copyright (c) 2014 , Hookflash Inc.
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *  The views and conclusions contained in the software and documentation are those
 *  of the authors and should not be interpreted as representing official policies,
 *  either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
package com.openpeer.sdk.login;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.openpeer.javaapi.OPIdentity;
import com.openpeer.sdk.app.HOPSettingsHelper;
import com.openpeer.sdk.utils.StringUtils;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HOPIdentityLoginWebViewClient extends WebViewClient {

    OPIdentity mIdentity;

    public void setIdentity(OPIdentity identity) {
        mIdentity = identity;
    }

    boolean mInnerFrameLoaded;

    public HOPIdentityLoginWebViewClient(OPIdentity identity) {
        mIdentity = identity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        // view.loadUrl(url);
        if (url.contains("datapass")) {
            int i = 1;
            i++;
        } else {
            super.onLoadResource(view, url);
        }

    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (url.contains("datapass")) {
            Log.w("login", "url to intercept " + url);
            String data = url.substring(url.lastIndexOf("data="));
            data = data.substring(5);
            // Log.w("JNI", data);
            try {
                data = StringUtils
                        .hexToString(URLDecoder.decode(data, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Log.w("login", "Identity Received from JS: " + data);
            mIdentity.handleMessageFromInnerBrowserWindowFrame(data);

            return null;
        } else if (url.contains("?reload=true")) {
            int i = 1;
            i++;
            return null;
        } else {
            return super.shouldInterceptRequest(view, url);
        }

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d("login", "IdentityLoginWebview onPageFinished " + url);
        if (url.contains(HOPSettingsHelper.getInstance().getOuterFrameUrl())) {
            Log.d("login",
                    "IdentityLoginWebview onPageFinished setting data encoding to hex");
            view.loadUrl("javascript:window.init({datapassEncoding: \"hex\"});");
        }
        if (!mInnerFrameLoaded) {

            mInnerFrameLoaded = true;
            String cmd = String.format("javascript:initInnerFrame(\'%s\')",
                    mIdentity.getInnerBrowserWindowFrameURL());
            Log.w("login", "INIT INNER FRAME: " + cmd);
            view.loadUrl(cmd);
        } else {
            super.onPageFinished(view, url);
        }

    }

    @Override
    public void onReceivedError(WebView view, int errorCode,
            String description, String failingUrl) {
        int i = 1;
        i++;
    }

}
