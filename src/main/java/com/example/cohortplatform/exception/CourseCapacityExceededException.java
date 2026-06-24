package com.example.cohortplatform.exception;

public class CourseCapacityExceededException extends RuntimeException {
    public CourseCapacityExceededException(String message) {
        super(message);
    }
}
