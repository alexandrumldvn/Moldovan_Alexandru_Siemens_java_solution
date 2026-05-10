package com.moldovan.trainapp.exception;

public class OverbookingException extends RuntimeException {
    public OverbookingException(String msg) { super(msg); }
}
