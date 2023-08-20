package ru.practicum.shareit.exception;

public class MyValidationException extends RuntimeException {
    public MyValidationException(String message) {
        super(message);
    }
}