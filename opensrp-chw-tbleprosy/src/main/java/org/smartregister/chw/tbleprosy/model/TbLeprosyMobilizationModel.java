package org.smartregister.chw.tbleprosy.model;

public class TbLeprosyMobilizationModel {
    private String sessionDate;
    private String sessionId;
    private String sessionParticipants;
    private String condomsIssued;

    public TbLeprosyMobilizationModel() {
    }

    public TbLeprosyMobilizationModel(String sessionDate, String sessionParticipants, String sessionId, String condomsIssued) {
        this.sessionDate = sessionDate;
        this.sessionParticipants = sessionParticipants;
        this.sessionId = sessionId;
        this.condomsIssued = condomsIssued;
    }

    public String getSessionParticipants() {
        return sessionParticipants;
    }

    public void setSessionParticipants(String sessionParticipants) {
        this.sessionParticipants = sessionParticipants;
    }

    public String getSessionDate() {
        return sessionDate;
    }


    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCondomsIssued() {
        return condomsIssued;
    }

    public void setCondomsIssued(String condomsIssued) {
        this.condomsIssued = condomsIssued;
    }
}
