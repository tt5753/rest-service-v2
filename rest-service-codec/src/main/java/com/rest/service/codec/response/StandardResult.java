package com.rest.service.codec.response;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by lins on 16-2-24.
 */
public class StandardResult<T> implements Serializable {
    private T result;
    private String errorMsg;
    private Integer errorCode = 200;

    public StandardResult(){}

    public StandardResult(T result){
        this(result,null,null);
    }

    public StandardResult(Integer errorCode,String errorMsg){
        this(null,errorCode,errorMsg);
    }

    public StandardResult(T result,Integer errorCode,String errorMsg){
        this.result = result;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
