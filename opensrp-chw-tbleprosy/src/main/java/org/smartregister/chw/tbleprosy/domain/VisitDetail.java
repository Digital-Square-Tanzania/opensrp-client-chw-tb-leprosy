package org.smartregister.chw.tbleprosy.domain;

import java.util.Date;

public class VisitDetail {
    private String visitDetailsId;
    private String visitId;
    private String baseEntityId;
    private String visitKey;
    private String parentCode;
    private String details;
    private String humanReadable;
    private String jsonDetails;
    private String preProcessedJson;
    private String preProcessedType;
    private Boolean processed;
    private Date updatedAt;
    private Date createdAt;

    public String getVisitDetailsId() {
        return visitDetailsId;
    }

    public void setVisitDetailsId(String visitDetailsId) {
        this.visitDetailsId = visitDetailsId;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getVisitKey() {
        return visitKey;
    }

    public void setVisitKey(String visitKey) {
        this.visitKey = visitKey;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getJsonDetails() {
        return jsonDetails;
    }

    public void setJsonDetails(String jsonDetails) {
        this.jsonDetails = jsonDetails;
    }

    public String getPreProcessedJson() {
        return preProcessedJson;
    }

    public void setPreProcessedJson(String preProcessedJson) {
        this.preProcessedJson = preProcessedJson;
    }

    public String getPreProcessedType() {
        return preProcessedType;
    }

    public void setPreProcessedType(String preProcessedType) {
        this.preProcessedType = preProcessedType;
    }

    public String getHumanReadable() {
        return humanReadable;
    }

    public void setHumanReadable(String humanReadable) {
        this.humanReadable = humanReadable;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}