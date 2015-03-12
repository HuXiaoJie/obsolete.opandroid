package com.openpeer.sample.events;

public class FileUploadEvent extends BaseEvent{
    int progess;
    String fileName;
    String objectId;

    public FileUploadEvent(int progess, String fileName, String objectId) {
        this.progess = progess;
        this.fileName = fileName;
        this.objectId = objectId;
    }

    public int getProgess() {
        return progess;
    }

    public void setProgess(int progess) {
        this.progess = progess;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
