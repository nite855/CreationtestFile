package com.creationtime.domain.common;

public final class Required {
    private Required() {
    }

    public static String text(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new DomainException(fieldName + " is required.");
        }
        return value.trim();
    }

    public static int positive(int value, String fieldName) {
        if (value <= 0) {
            throw new DomainException(fieldName + " must be positive.");
        }
        return value;
    }
}

