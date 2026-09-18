package com.vts.vtsapproot.vm;

public class ProcessResult {
    public enum Status {SUCCESS, ERROR, LOADING, TOKENEXPIRED}

    public enum StatusAdv {NONE, ISFILTERED, ISSORTED, ISREFRESHED}

    public final Status status;
    public final StatusAdv statusAdv;
    public final String message;

    public long timestamp;

    private ProcessResult(Status status) {
        this.status = status;
        this.message = null;
        this.statusAdv = StatusAdv.NONE;
        this.timestamp = System.currentTimeMillis();
    }

    private ProcessResult(String message) {
        this.status = Status.ERROR;
        this.message = message;
        this.statusAdv = StatusAdv.NONE;
        this.timestamp = System.currentTimeMillis();
    }

    private ProcessResult(Status status, String message) {
        this.status = status;
        this.message = message;
        this.statusAdv = StatusAdv.NONE;
        this.timestamp = System.currentTimeMillis();
    }

    private ProcessResult() {
        this.status = Status.SUCCESS;
        this.message = null;
        this.statusAdv = StatusAdv.NONE;
        this.timestamp = System.currentTimeMillis();
    }

    private ProcessResult(StatusAdv statusadv) {
        this.status = Status.SUCCESS;
        this.message = null;
        this.statusAdv = statusadv;
        this.timestamp = System.currentTimeMillis();
    }

    public static ProcessResult loading() {
        return new ProcessResult(Status.LOADING);
    }

    public static ProcessResult success() {
        return new ProcessResult();
    }
    public static ProcessResult success(String msg) {
        return new ProcessResult(Status.SUCCESS, msg);
    }

    public static ProcessResult success(StatusAdv statusadv) {
        return new ProcessResult(statusadv);
    }

    public static ProcessResult error(String msg) {
        return new ProcessResult(msg);
    }

    public static ProcessResult tokenexpired() {
        return new ProcessResult(Status.TOKENEXPIRED);
    }
}
