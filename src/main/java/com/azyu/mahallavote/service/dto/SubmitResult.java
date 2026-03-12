package com.azyu.mahallavote.service.dto;

public class SubmitResult {

    public enum Status {
        SMS_SENT,
        ACCEPTED,
        ERROR,
    }

    private final Status status;
    private final String message;

    private SubmitResult(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public static SubmitResult smsSent() {
        return new SubmitResult(Status.SMS_SENT, null);
    }

    public static SubmitResult accepted(String message) {
        return new SubmitResult(Status.ACCEPTED, message);
    }

    public static SubmitResult error(String message) {
        return new SubmitResult(Status.ERROR, message);
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
