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
package com.openpeer.sample;

import java.util.Timer;
import java.util.TimerTask;

import com.openpeer.javaapi.OPBackgrounding;
import com.openpeer.sample.delegates.BackgroundingCompletionDelegateImpl;
import com.openpeer.sdk.model.HOPCallManager;

/**
 *
 */
public class BackgroundingManager {
    private static final long TIMER_BACKGROUNDING = 20 * 1000;
    private static Timer mBackgroundingTimer;
    private static boolean mInBackground;

    private static boolean mBackgroundingPending;

    public static boolean isInBackground() {
        // TODO Auto-generated method stub
        return mInBackground;
    }

    public static void onEnteringForeground() {
        mBackgroundingPending = false;

        if (mBackgroundingTimer != null) {
            mBackgroundingTimer.cancel();
            mBackgroundingTimer = null;
        } else if (mInBackground) {
            OPBackgrounding.notifyReturningFromBackground();
            mInBackground = false;
        }
    }

    public static void onEnteringBackground() {
        if (HOPCallManager.getInstance().hasCalls()) {
            mBackgroundingPending = true;
        } else {
            mBackgroundingPending = false;

            if (mBackgroundingTimer == null) {
                mBackgroundingTimer = new Timer();
                mBackgroundingTimer.schedule(new TimerTask() {
                    public void run() {
                        OPBackgrounding
                                .notifyGoingToBackground(BackgroundingCompletionDelegateImpl
                                        .getInstance());
                        mInBackground = true;
                        mBackgroundingTimer = null;
                    }
                }, TIMER_BACKGROUNDING);
            }
        }
    }
    
    public static void onAppShutdown(){
        
    }

    /**
     * @return
     */
    public static boolean isBackgroundingPending() {
        // TODO Auto-generated method stub
        return mBackgroundingPending;
    }
}
