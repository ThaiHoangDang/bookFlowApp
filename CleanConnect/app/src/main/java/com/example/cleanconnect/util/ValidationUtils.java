package com.example.cleanconnect.util;

import android.util.Patterns;

public class ValidationUtils {

    // Validate an email address
    public static boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Validate a password
    public static boolean isValidPassword(CharSequence password) {
        // At least 6 characters
        // You can add more criteria based on your requirements
        return password != null && password.length() >= 6;
    }
}
