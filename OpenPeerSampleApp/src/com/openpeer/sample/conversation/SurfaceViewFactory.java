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
package com.openpeer.sample.conversation;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

import org.webrtc.videoengine.ViERenderer;

import android.content.Context;
import android.view.SurfaceView;

public class SurfaceViewFactory {
	private static Hashtable<String, SurfaceView> mRemoteViews = new Hashtable<String, SurfaceView>();
	private static WeakReference<SurfaceView> mLocalView = new WeakReference<SurfaceView>(null);

	public static SurfaceView getLocalView(Context context) {
		SurfaceView view = mLocalView.get();
		if (view == null) {
			view = ViERenderer.CreateRenderer(context, true);
			mLocalView = new WeakReference<SurfaceView>(view);
		}
		return view;
	}

	public static SurfaceView getRemoteSurfaceView(Context context, String peerId) {
		SurfaceView view = mRemoteViews.get(peerId);
		if (view != null) {
			return view;
		} else {
			view = ViERenderer.CreateRenderer(context, true);
			mRemoteViews.put(peerId, view);
			return view;
		}
	}

}
