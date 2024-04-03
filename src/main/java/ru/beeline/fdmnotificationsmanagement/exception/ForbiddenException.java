package ru.beeline.fdmnotificationsmanagement.exception;


public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}