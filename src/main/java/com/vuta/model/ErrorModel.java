package com.vuta.model;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class ErrorModel {

    private String message;

    public ErrorModel(String message) {
        this.message = message;
    }

    public ErrorModel() {
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
