package com.example.nutriappmovil;

public class LoginValidator {
    public static boolean isEmailValid(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isPasswordValid(String password) {
        if (password == null || password.trim().isEmpty()) return false;
        return password.length() >= 6;
    }
}