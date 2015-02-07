package com.openpeer.sdk.model;

import android.text.format.Time;

import com.openpeer.javaapi.CallStates;
import com.openpeer.javaapi.OPCall;
import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPConversationThread;
import com.openpeer.javaapi.OPIdentityContact;

import java.util.List;

public class HOPCall {

    public static final int DIRECTION_INCOMING = 1;
    public static final int DIRECTION_OUTGOING = 0;
    private OPCall call;
    private HOPContact peer;
    CallMediaStatus callMediaStatus;

    HOPCall(OPCall call) {
        this.call = call;
        callMediaStatus = new CallMediaStatus();
    }

    public CallMediaStatus getCallMediaStatus() {
        return callMediaStatus;
    }

    public long getCbcId() {
        return call.getCbcId();
    }

    public boolean hasAudio() {
        return call.hasAudio();
    }

    public static String toString(CallStates state) {
        return OPCall.toString(state);
    }

    public CallStates getState() {
        return call.getState();
    }

    public void hold(boolean hold) {
        call.hold(hold);
    }

    public Time getCreationTime() {
        return call.getCreationTime();
    }

    public void setCbcId(long cbcId) {
        call.setCbcId(cbcId);
    }

    public HOPContact getPeerUser() {
        return call.getPeerUser();
    }

    public void ring() {
        call.ring();
    }

    public int getClosedReason() {
        return call.getClosedReason();
    }

    public static HOPCall placeCall(OPConversationThread conversationThread, OPContact toContact,
                                    boolean includeAudio, boolean includeVideo) {
        return new HOPCall(OPCall.placeCall(conversationThread, toContact, includeAudio,
                                            includeVideo));
    }

    public Time getClosedTime() {
        return call.getClosedTime();
    }

    public String getCallID() {
        return call.getCallID();
    }

    public void answer() {
        call.answer();
    }

    public boolean isOutgoing() {
        return call.isOutgoing();
    }

    public int getCallDirection() {
        return isOutgoing() ? 0 : 1;
    }

    public boolean hasVideo() {
        return call.hasVideo();
    }

    public static String toDebugString(OPCall call, boolean includeCommaPrefix) {
        return OPCall.toDebugString(call, includeCommaPrefix);
    }

    public Time getAnswerTime() {
        return call.getAnswerTime();
    }

    public long getAnswerTimeInMillis() {
        return call.getAnswerTime().toMillis(false);
    }

    public List<OPIdentityContact> getIdentityContactList(OPContact contact) {
        return call.getIdentityContactList(contact);
    }

    public void hangup(int reason) {
        call.hangup(reason);
    }

    public long getStableID() {
        return call.getStableID();
    }

    public Time getRingTime() {
        return call.getRingTime();
    }
}
