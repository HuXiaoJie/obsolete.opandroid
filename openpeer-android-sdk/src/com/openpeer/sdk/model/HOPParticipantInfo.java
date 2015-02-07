package com.openpeer.sdk.model;

import java.util.List;

public class HOPParticipantInfo {
    long cbcId;
    List<HOPContact> particpants;

    public HOPParticipantInfo() {
    }

    public HOPParticipantInfo(long cbcId, List<HOPContact> user) {
        this.cbcId = cbcId;
        this.particpants = user;
    }

    public long getCbcId() {
        return cbcId;
    }

    public void setCbcId(long cbcId) {
        this.cbcId = cbcId;
    }

    public List<HOPContact> getParticipants() {
        return particpants;
    }

    public void setUsers(List<HOPContact> user) {
        this.particpants = user;
    }

    public void addUsers(List<HOPContact> users) {
        particpants.addAll(users);
    }

    public void removeUsers(List<HOPContact> users) {
        particpants.removeAll(users);
    }
}
