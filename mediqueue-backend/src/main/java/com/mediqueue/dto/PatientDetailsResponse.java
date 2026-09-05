package com.mediqueue.dto;

public class PatientDetailsResponse {
    private String uid;
    private String mqId;
    private String name;
    private String phone;
    private String email;
    private String language;
    private String insuranceStatus;
    private int dependantsCount;

    public PatientDetailsResponse() {}

    public PatientDetailsResponse(String uid, String mqId, String name, String phone, String email, String language, String insuranceStatus, int dependantsCount) {
        this.uid = uid;
        this.mqId = mqId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.language = language;
        this.insuranceStatus = insuranceStatus;
        this.dependantsCount = dependantsCount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMqId() {
        return mqId;
    }

    public void setMqId(String mqId) {
        this.mqId = mqId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getInsuranceStatus() {
        return insuranceStatus;
    }

    public void setInsuranceStatus(String insuranceStatus) {
        this.insuranceStatus = insuranceStatus;
    }

    public int getDependantsCount() {
        return dependantsCount;
    }

    public void setDependantsCount(int dependantsCount) {
        this.dependantsCount = dependantsCount;
    }
}
