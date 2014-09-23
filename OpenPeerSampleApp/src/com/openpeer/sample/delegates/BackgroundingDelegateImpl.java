package com.openpeer.sample.delegates;

import android.util.Log;

import com.openpeer.javaapi.OPBackgroundingDelegate;
import com.openpeer.javaapi.OPBackgroundingNotifier;
import com.openpeer.javaapi.OPBackgroundingSubscription;

/**
 * Copyright (c) 2013, SMB Phone Inc. / Hookflash Inc.
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
public class BackgroundingDelegateImpl extends OPBackgroundingDelegate {
    private static final String TAG = BackgroundingDelegateImpl.class.getSimpleName();

    private static BackgroundingDelegateImpl instance;
    public static BackgroundingDelegateImpl getInstance(){
        if(instance==null){
            instance=new BackgroundingDelegateImpl();
        }
        return instance;
    }
    @Override
    public void onBackgroundingGoingToBackground(OPBackgroundingSubscription opBackgroundingSubscription, OPBackgroundingNotifier opBackgroundingNotifier) {
        Log.d(TAG, "onBackgroundingGoingToBackground"+opBackgroundingSubscription + opBackgroundingNotifier);
    }

    @Override
    public void onBackgroundingGoingToBackgroundNow(OPBackgroundingSubscription opBackgroundingSubscription) {
        Log.d(TAG, "onBackgroundingGoingToBackgroundNow "+opBackgroundingSubscription);

    }

    @Override
    public void onBackgroundingReturningFromBackground(OPBackgroundingSubscription opBackgroundingSubscription) {
        Log.d(TAG, "onBackgroundingReturningFromBackground" + opBackgroundingSubscription);

    }

    @Override
    public void onBackgroundingApplicationWillQuit(OPBackgroundingSubscription opBackgroundingSubscription) {
        Log.d(TAG, "onBackgroundingApplicationWillQuit" + opBackgroundingSubscription);

    }
}
