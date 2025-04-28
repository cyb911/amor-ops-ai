package com.amor.mcpservice.dto;

import lombok.Generated;

public class R<T> {
    private static final long serialVersionUID = 1L;
    private int code;
    private String msg;
    private T data;

    public static <T> R<T> ok() {
        return restResult(null, 0, null);
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, 0, null);
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, 0, msg);
    }

    public static <T> R<T> failed() {
        return restResult(null, 1, null);
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, 1, msg);
    }

    public static <T> R<T> failed(T data) {
        return restResult(data, 1, (String)null);
    }

    public static <T> R<T> failed(T data, String msg) {
        return restResult(data, 1, msg);
    }

    public static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    @Generated
    public String toString() {
        int var10000 = this.getCode();
        return "R(code=" + var10000 + ", msg=" + this.getMsg() + ", data=" + this.getData() + ")";
    }

    @Generated
    public R() {
    }

    @Generated
    public R(final int code, final String msg, final T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @Generated
    public int getCode() {
        return this.code;
    }

    @Generated
    public R<T> setCode(final int code) {
        this.code = code;
        return this;
    }

    @Generated
    public String getMsg() {
        return this.msg;
    }

    @Generated
    public R<T> setMsg(final String msg) {
        this.msg = msg;
        return this;
    }

    @Generated
    public T getData() {
        return this.data;
    }

    @Generated
    public R<T> setData(final T data) {
        this.data = data;
        return this;
    }
}
