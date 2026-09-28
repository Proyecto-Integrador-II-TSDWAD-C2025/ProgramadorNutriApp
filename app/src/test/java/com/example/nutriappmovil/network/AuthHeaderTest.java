package com.example.nutriappmovil.network;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AuthHeaderTest {

    @Test
    public void fromToken_returnsNullForMissingOrBlankTokens() {
        assertNull(AuthHeader.fromToken(null));
        assertNull(AuthHeader.fromToken(""));
        assertNull(AuthHeader.fromToken("   "));
    }

    @Test
    public void fromToken_trimsAndBuildsDjangoTokenHeader() {
        assertEquals("Token abc123", AuthHeader.fromToken("  abc123  "));
    }
}
