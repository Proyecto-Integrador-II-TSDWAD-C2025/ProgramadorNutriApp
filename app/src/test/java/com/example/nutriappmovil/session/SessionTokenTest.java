package com.example.nutriappmovil.session;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SessionTokenTest {

    @Test
    public void normalize_rejectsNullEmptyAndBlankTokens() {
        assertNull(SessionToken.normalize(null));
        assertNull(SessionToken.normalize(""));
        assertNull(SessionToken.normalize(" \n\t "));
    }

    @Test
    public void normalize_removesSurroundingWhitespace() {
        assertEquals("token-value", SessionToken.normalize("  token-value  "));
    }
}
