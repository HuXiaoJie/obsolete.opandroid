package com.openpeer.sdk.model;

import android.text.format.Time;

import com.openpeer.javaapi.CallStates;
import com.openpeer.javaapi.OPCall;
import com.openpeer.javaapi.OPContact;
import com.openpeer.javaapi.OPConversationThread;
import com.openpeer.javaapi.OPIdentityContact;
import com.openpeer.sdk.app.HOPDataManager;

import java.util.List;

public class HOPCall {

    public static final int DIRECTION_INCOMING = 1;
    public static final int DIRECTION_OUTGOING = 0;
    private OPCall call;
    CallMediaStatus callMediaStatus;
    HOPConversation conversation;

    public void setConversation(HOPConversation conversation) {
        this.conversation = conversation;
    }

    HOPCall(OPCall call) {
        this.call = call;
        callMediaStatus = new CallMediaStatus();
        conversation = HOPConversationManager.getInstance().getConversation(call.getConversationThread(), true);
    }

    public CallMediaStatus getCallMediaStatus() {
        return callMediaStatus;
    }

    public long getCbcId() {
        return conversation.getCurrentCbcId();
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

    public HOPContact getPeer() {
        OPContact contact = call.getCaller();
        if (contact.isSelf()) {
            contact = call.getCallee();
        }
        return HOPDataManager.getInstance().getUser(contact,
                                                    getIdentityContactList(contact));
    }

    public void ring() {
        call.ring();
    }

    public int getClosedReason() {
        return call.getClosedReason();
    }

    public static HOPCall placeCall(HOPConversation conversation, OPContact toContact,
                                    boolean includeAudio, boolean includeVideo) {
        return new HOPCall(OPCall.placeCall(conversation.getThread(true), toContact, includeAudio,
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
        return call.getCaller().isSelf();
    }

    public int getCallDirection() {
        return isOutgoing() ? DIRECTION_OUTGOING : DIRECTION_INCOMING;
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

    private List<OPIdentityContact> getIdentityContactList(OPContact contact) {
        OPConversationThread thread = call.getConversationThread();

        return thread.getIdentityContactList(contact);
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

    public HOPConversation getConversation() {
        return conversation;
    }
}
