package com.paulohsmelo.portfolio.exception;

public class TwitterIntegrationException extends RuntimeException {

    public TwitterIntegrationException(String message) {
        super("Twitter integration is unavailable: " + message);
    }
}
