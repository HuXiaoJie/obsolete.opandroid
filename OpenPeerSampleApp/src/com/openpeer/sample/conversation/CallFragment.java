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

import android.media.Ringtone;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.openpeer.javaapi.CallStates;
import com.openpeer.javaapi.CameraTypes;
import com.openpeer.javaapi.OPCall;
import com.openpeer.javaapi.OPCaptureCapability;
import com.openpeer.javaapi.OPMediaEngine;
import com.openpeer.javaapi.VideoOrientations;
import com.openpeer.sample.BaseFragment;
import com.openpeer.sample.IntentData;
import com.openpeer.sample.OPNotificationBuilder;
import com.openpeer.sample.R;
import com.openpeer.sample.events.CallStateChangeEvent;
import com.openpeer.sample.util.CallUtil;
import com.openpeer.sample.util.SettingsHelper;
import com.openpeer.sample.util.ViewUtils;
import com.openpeer.sdk.app.OPDataManager;
import com.openpeer.sdk.model.CallManager;
import com.openpeer.sdk.model.CallStatus;
import com.openpeer.sdk.model.ConversationManager;
import com.openpeer.sdk.model.OPConversation;
import com.openpeer.sdk.model.OPUser;
import com.squareup.picasso.Picasso;

import org.webrtc.videoengine.ViERenderer;

import java.util.List;

import de.greenrobot.event.EventBus;

public class CallFragment extends BaseFragment {
    public static final String TAG = CallFragment.class.getSimpleName();
    // TextView mNameView;
    private ImageView mPeerAvatarView;

    private OPCall mCall;
    private SurfaceView mLocalSurface;
    private SurfaceView mRemoteSurface;
    private RelativeLayout mVideoView;
    private boolean mVideo;
    private long[] userIDs;
    OPConversation mConversation;

    private ImageView audioButton;
    private ImageView videoButton;
    private ImageView cameraSwitchButton;
    private ImageView speakerButton;
    private ImageView recordButton;
    private CallStatus mCallStatus;
    private View mStatusOverlay;
    private View mCallView;
    private Ringtone mRingtone;
    private long mPeerId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, null);

        setupView(view);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        // obtainCameraRatios();
        userIDs = args.getLongArray(IntentData.ARG_PEER_USER_IDS);
        String callId = args.getString(IntentData.ARG_CALL_ID);
        String conversationId = args.getString(IntentData.ARG_CONVERSATION_ID);

        if(callId!=null){
            mCall=CallManager.getInstance().findCallById(callId);
            mPeerId = mCall.getPeerUser().getUserId();
        } else if (userIDs != null && userIDs.length > 0) {
            mPeerId = userIDs[0];
            mCall = CallManager.getInstance().findCallForPeer(mPeerId);
        } else {
            Log.e(TAG, "no peerUri nor userIDs");
        }
        if (mCall != null) {
            mVideo = mCall.hasVideo();
        } else {
            if(conversationId!=null) {
                mConversation = ConversationManager.getInstance().getConversationById(conversationId);
            }
            mVideo = args.getBoolean(IntentData.ARG_VIDEO, true);
        }
        EventBus.getDefault().register(this);
    }

    private View setupView(View view) {
        mCallView = view.findViewById(R.id.status);
        mVideoView = (RelativeLayout) view.findViewById(R.id.video);

        mStatusOverlay = view.findViewById(R.id.controls);

        mPeerAvatarView = (ImageView) view.findViewById(R.id.peer_image);

        // No point in hiding the overlay for audio call
        if (mVideo) {
            mStatusOverlay.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    hideOverlay();
                }
            });
        }

        CallControlView mCallControlView = (CallControlView) view
                .findViewById(R.id.call_control);

        if (mVideo) {
            mVideoView.setVisibility(View.VISIBLE);
            mVideoView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showOverlay();
                }
            });
        } else {
            mVideoView.setVisibility(View.GONE);
        }

        ViewStub mediaControlViewStub = (ViewStub) mStatusOverlay
                .findViewById(R.id.media_control);
        if (mVideo) {
            mediaControlViewStub
                    .setLayoutResource(R.layout.layout_media_control_video);
            View layout = mediaControlViewStub.inflate();// getActivity(), R.layout.layout_media_control_video, null);
            audioButton = (ImageView) layout.findViewById(R.id.audio);
            speakerButton = (ImageView) layout.findViewById(R.id.speaker);

            videoButton = (ImageView) layout.findViewById(R.id.video);
            cameraSwitchButton = (ImageView) layout.findViewById(R.id.camera);

        } else {
            mediaControlViewStub
                    .setLayoutResource(R.layout.layout_media_control_audio);

            View layout = mediaControlViewStub.inflate();// getActivity(), R.layout.layout_media_control_audio, null);
            audioButton = (ImageView) layout.findViewById(R.id.audio);
            speakerButton = (ImageView) layout.findViewById(R.id.speaker);
            recordButton = (ImageView) layout.findViewById(R.id.record);
        }
        initMedia(view);
        if (mCall == null) {
            OPUser user = OPDataManager.getInstance().getUserById(mPeerId);
            mCall = mConversation.placeCall(user, true, mVideo);

        } else {
            if (mCall.getState() == CallStates.CallState_Incoming
                    || mCall.getState() == CallStates.CallState_Ringing) {

                playRingtone();
            } else if (mCall.getState() == CallStates.CallState_Open) {
                OPNotificationBuilder.cancelNotificationForCall(mCall.getCallID());
                startShowDuration();
                if (mVideo) {
                    mCallView.setVisibility(View.GONE);
                }
            }
        }
        mCallControlView.bindCall(mCall);
        // mNameView.setText(mCall.getPeerUser().getName());
        String peerName = mCall.getPeerUser().getName();
        getActivity().getActionBar().setTitle(peerName);
        String avatarUri = mCall.getPeerUser().getAvatarUri();
        if (avatarUri != null) {
            Picasso.with(getActivity())
                    .load(avatarUri)
                    .into(mPeerAvatarView);
        }

        return view;
    }

    void setupMediaControl() {
        audioButton
                .setImageResource(mCallStatus.isMuted() ? R.drawable.ic_action_mic_muted
                        : R.drawable.ic_action_mic);
        OPMediaEngine.getInstance().setMuteEnabled(mCallStatus.isMuted());

        audioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallStatus.setMuted(!mCallStatus.isMuted());
                audioButton.setImageResource(mCallStatus.isMuted() ? R.drawable.ic_action_mic_muted
                        : R.drawable.ic_action_mic);
                OPMediaEngine.getInstance().setMuteEnabled(
                        mCallStatus.isMuted());
            }
        });

        speakerButton
                .setImageResource(mCallStatus.isSpeakerOn() ? R.drawable.ic_action_speaker_on
                        : R.drawable.ic_action_speaker_off);

        OPMediaEngine.getInstance().setLoudspeakerEnabled(
                mCallStatus.isSpeakerOn());
        speakerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallStatus.setSpeakerOn(!mCallStatus.isSpeakerOn());
                speakerButton.setImageResource(mCallStatus.isSpeakerOn() ? R.drawable.ic_action_speaker_on
                        : R.drawable.ic_action_speaker_off);

                OPMediaEngine.getInstance().setLoudspeakerEnabled(
                        mCallStatus.isSpeakerOn());

            }
        });
        if (mVideo) {
            videoButton
                    .setImageResource(mCallStatus.isCapturing() ? R.drawable.ic_action_video_on
                            : R.drawable.ic_action_video_off);
            videoButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mCallStatus.setCapturing(!mCallStatus.isCapturing());
                    videoButton.setImageResource(mCallStatus.isCapturing() ? R.drawable.ic_action_video_on
                            : R.drawable.ic_action_video_off);
                    if (mCallStatus.isCapturing()) {
                        OPMediaEngine.getInstance().startVideoCapture();
                    } else {
                        OPMediaEngine.getInstance().stopVideoCapture();

                    }
                }
            });
            cameraSwitchButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mCallStatus.setUseFrontCamera(!mCallStatus.useFrontCamera());
                    if (mCallStatus.useFrontCamera()) {
                        OPMediaEngine.getInstance().setCameraType(
                                CameraTypes.CameraType_Front);
                    } else {
                        OPMediaEngine.getInstance().setCameraType(
                                CameraTypes.CameraType_Back);
                    }
                    setPreview(OPMediaEngine.getInstance().getCameraType(), 0);

                }
            });
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (mCall != null) {

            updateCallView(mCall.getState());
        }

    }

    void updateCallView(CallStates state) {
        String callStateString = CallUtil.getCallStateStringResId(state);
        if (null != callStateString) {
            getActivity().getActionBar().setSubtitle(callStateString);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCall != null) {
            CallStates state = mCall.getState();
            switch (state) {
            case CallState_Closing:
            case CallState_Closed:
                break;
            default:
                OPNotificationBuilder.showNotificationForCall(mCall);
                stopRingtone();
                break;

            }
        }
    }

    public void onStop() {
        super.onStop();

    }

    public void onDestroy() {
        super.onDestroy();

        if (mVideo) {
            // mVideoView.removeAllViews();
            // previewLayout.removeAllViews();
            OPMediaEngine.getInstance().setChannelRenderView(null);
            OPMediaEngine.getInstance().setCaptureRenderView(null);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    public void onEvent(CallStateChangeEvent event){
        OPCall call = event.getCall();
        String callId = call.getCallID();
        CallStates state = event.getState();
        if (callId.equals(mCall.getCallID())) {
            onCallStateChanged(mCall, state);
        }
    }

    public void onCallStateChanged(OPCall call, final CallStates state) {
        Log.d(TAG, "onCallStateChanged " + state.toString() + " call "
                + call);
        if (getActivity() == null || isDetached()) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {

            public void run() {
                updateCallView(state);
                switch (state) {
                case CallState_Placed:
                    if (!isDetached()) {
                        // mNameView.setText("" + state);
                    }
                    break;
                case CallState_Incoming:
                    playRingtone();
                    break;
                case CallState_Early:
                    break;
                case CallState_Ringing:
                    playRingtone();
                    break;
                case CallState_Ringback:
                    playRingtone();
                    break;
                case CallState_Open: // call is open
                    onCallAnswered();
                    break;
                case CallState_Active:
                    break;
                case CallState_Inactive:
                    break;
                case CallState_Hold:
                    break;
                case CallState_Closing: // call is hanging up
                    break;
                case CallState_Closed:
                    onCallClosed();
                    break;
                }
            }
        });
    }

    boolean mVideoPreviewSwitched;

    void initMedia(View view) {
        mCallStatus = CallManager.getInstance().getMediaStateForCall(mPeerId);
        OPMediaEngine.getInstance().setEcEnabled(true);
        OPMediaEngine.getInstance().setAgcEnabled(true);
        OPMediaEngine.getInstance().setNsEnabled(false);
        OPMediaEngine.getInstance().setMuteEnabled(false);
        OPMediaEngine.getInstance().setLoudspeakerEnabled(false);
        if (mVideo) {
            if (mCallStatus.useFrontCamera()) {
                OPMediaEngine.getInstance().setCameraType(
                    CameraTypes.CameraType_Front);
            } else {
                OPMediaEngine.getInstance().setCameraType(
                    CameraTypes.CameraType_Back);
            }

            // This makes sure the video capture is stopped after call is stopped.
            OPMediaEngine.getInstance().setContinuousVideoCapture(false);
            OPMediaEngine.getInstance().setDefaultVideoOrientation(
                VideoOrientations.VideoOrientation_Portrait);
            // OPMediaEngine.getInstance().setRecordVideoOrientation(
            // VideoOrientations.VideoOrientation_LandscapeRight);
            OPMediaEngine.getInstance().setFaceDetection(false);

            setPreview(OPMediaEngine.getInstance().getCameraType(), 0);
        }
        setupMediaControl();

    }

    private static final float ASPECT_RATIO_SMALL = 4.0f / 3.0f;

    private void startShowDuration() {
        mCallView.postDelayed(timerThread, 1000);
    }

    private Runnable timerThread = new Runnable() {

        public void run() {
            if (!isAdded()) {
                return;
            }

            long timeInMilliseconds = mCallStatus.getDuration();

            int secs = (int) (timeInMilliseconds / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            getActivity().getActionBar().setSubtitle(
                    mins + ":" + String.format("%02d", secs));
            mCallView.postDelayed(this, 1000);
        }
    };

    private void onCallAnswered() {
        mCallStatus.setAnswerTime(System.currentTimeMillis());
        startShowDuration();
        if (mVideo) {
            mCallView.setVisibility(View.GONE);
            hideOverlay();
        }
        stopRingtone();
    }

    private void onCallClosed() {
        stopRingtone();
        getActivity().finish();
    }

    void playRingtone() {
        if (mRingtone == null) {
            // mRingtone = RingtoneManager.getRingtone(getActivity(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            mRingtone = SettingsHelper.getRingtone();
            Log.d(TAG, "play ringtone " + mRingtone.getTitle(getActivity()));
        }
        mRingtone.play();
    }

    void stopRingtone() {
        if (mRingtone != null) {
            mRingtone.stop();
        }
        mRingtone = null;
    }

    void showOverlay() {
        getActivity().getActionBar().show();
        mStatusOverlay.setVisibility(View.VISIBLE);
    }

    void hideOverlay() {
        getActivity().getActionBar().hide();
        mStatusOverlay.setVisibility(View.GONE);
    }

    // orientation is not used for now
    void setPreview(CameraTypes cameraType, int orientation) {
        float previewRatio;
        int width;
        if (mVideoPreviewSwitched) {
            ViewUtils.measureView(mVideoView);
            previewRatio = (float) mVideoView.getHeight()
                    / (float) mVideoView.getWidth();
            // previewRatio = (float) getActivity().getResources()
            // .getConfiguration().screenHeightDp
            // /(float) getActivity().getResources().getConfiguration().screenWidthDp;
            width = getActivity().getResources().getConfiguration().screenWidthDp;
        } else {
            previewRatio = ASPECT_RATIO_SMALL;
            width = getActivity().getResources().getDimensionPixelSize(
                    R.dimen.width_local_video);
        }
        mVideoView.removeAllViews();

        List<OPCaptureCapability> capabilities = OPMediaEngine.getInstance()
                .getCaptureCapabilities(cameraType);
        int size = capabilities.size();
        if (size != 0) {

            OPCaptureCapability preferredCapability = capabilities.get(0);
            int minDiff = Math.abs(preferredCapability.getWidth() - width);
            float minRatioDiff = Math.abs((float) preferredCapability
                    .getWidth()
                    / (float) preferredCapability.getHeight() - previewRatio);
            // find out the closest resolution
            if (size > 1) {
                for (int i = 1; i < size; i++) {
                    OPCaptureCapability capability = capabilities.get(i);
                    float ratio = Math.abs(((float) capability.getWidth()
                            / (float) capability.getWidth()) - previewRatio);
                    if (ratio < minRatioDiff) {
                        preferredCapability = capabilities.get(i);
                    } else if (ratio == minRatioDiff) {
                        int diff = capability.getWidth() - width;
                        if (diff < minDiff) {
                            minDiff = diff;
                            preferredCapability = capabilities.get(i);
                        }
                    }
                }
            }

            OPMediaEngine.getInstance().setCameraType(cameraType);

            OPMediaEngine.getInstance().setCaptureCapability(
                    preferredCapability, cameraType);

            float aspectRatio = (float) preferredCapability.getWidth()
                    / (float) preferredCapability.getHeight();
            float heightRatio = 1.0f;
            float widthRatio = 1.0f;

            // if the camera aspect ratio is bigger, we need to crop it
            // actual capturing is taller,e.g. 4/3 / 16/9 = 0.75 for height
            if (previewRatio < aspectRatio) {
                heightRatio = previewRatio / aspectRatio;
                OPMediaEngine.getInstance().setCaptureRenderViewCropping(0.0f,
                        0.0f, widthRatio,heightRatio);
            } else if (previewRatio > aspectRatio) {
                // actual capturing is wider, e.g. 10/9 / 4/3 =0.8 for width
                widthRatio = aspectRatio / previewRatio;
                OPMediaEngine.getInstance().setCaptureRenderViewCropping(0.0f,
                        0.0f,widthRatio,heightRatio);
            }

        }

        RelativeLayout.LayoutParams smallLayoutParam = new RelativeLayout.LayoutParams(
                width,
                (int) (width * ASPECT_RATIO_SMALL));
        smallLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        smallLayoutParam.setMargins(10, 0, 0, 10);

        RelativeLayout.LayoutParams fullLayoutParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mLocalSurface = ViERenderer.CreateRenderer(getActivity(), true);
        mRemoteSurface = ViERenderer.CreateRenderer(getActivity(), true);
        OPMediaEngine.getInstance().setChannelRenderView(mRemoteSurface);
        OPMediaEngine.getInstance().setCaptureRenderView(mLocalSurface);
        if (mVideoPreviewSwitched) {
            mRemoteSurface.setZOrderMediaOverlay(true);
            mLocalSurface.setZOrderMediaOverlay(false);
            mVideoView.addView(mLocalSurface, fullLayoutParam);
            mVideoView.addView(mRemoteSurface, smallLayoutParam);
            mRemoteSurface.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mVideoPreviewSwitched = !mVideoPreviewSwitched;
                    setPreview(OPMediaEngine.getInstance().getCameraType(), 0);
                }
            });
        } else {
            mRemoteSurface.setZOrderMediaOverlay(false);
            mLocalSurface.setZOrderMediaOverlay(true);
            mVideoView.addView(mLocalSurface, smallLayoutParam);
            mVideoView.addView(mRemoteSurface, fullLayoutParam);
            mLocalSurface.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mVideoPreviewSwitched = !mVideoPreviewSwitched;
                    setPreview(OPMediaEngine.getInstance().getCameraType(), 0);
                }
            });
        }

    }
}
