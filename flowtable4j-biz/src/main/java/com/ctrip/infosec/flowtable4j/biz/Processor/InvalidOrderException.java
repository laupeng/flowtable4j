package com.ctrip.infosec.flowtable4j.biz.processor;

/**
 * Created by thyang on 2015-07-06.
 */
public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(String message){
        super(message);
    }
}
