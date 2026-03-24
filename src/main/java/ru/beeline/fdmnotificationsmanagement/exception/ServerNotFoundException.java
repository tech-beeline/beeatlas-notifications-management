/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.fdmnotificationsmanagement.exception;

public class ServerNotFoundException extends RuntimeException {
    public ServerNotFoundException(String message) {
        super(message);
    }
}
