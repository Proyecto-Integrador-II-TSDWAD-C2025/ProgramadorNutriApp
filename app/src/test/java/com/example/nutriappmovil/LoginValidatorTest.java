package com.example.nutriappmovil;

import org.junit.Test;
import static org.junit.Assert.*;

public class LoginValidatorTest {
    @Test
    public void emailVacio_retornaFalso() {
        assertFalse(LoginValidator.isEmailValid(""));
    }

    @Test
    public void emailSinArroba_retornaFalso() {
        assertFalse(LoginValidator.isEmailValid("fernando.gmail.com"));
    }

    @Test
    public void passwordMenorA6_retornaFalso() {
        assertFalse(LoginValidator.isPasswordValid("12345"));
    }
}