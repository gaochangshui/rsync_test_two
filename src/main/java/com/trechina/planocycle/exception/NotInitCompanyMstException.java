package com.trechina.planocycle.exception;

public class NotInitCompanyMstException extends Exception{
    public NotInitCompanyMstException() {
        super("not init company mst data exception");
    }

    public NotInitCompanyMstException(String message) {
        super("not init company mst data exception: "+message);
    }
}
