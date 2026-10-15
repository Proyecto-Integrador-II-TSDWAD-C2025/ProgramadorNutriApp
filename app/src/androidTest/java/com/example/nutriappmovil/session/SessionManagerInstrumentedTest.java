package com.example.nutriappmovil.session;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SessionManagerInstrumentedTest {

    private SessionManager sessionManager;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        sessionManager = new SessionManager(context);
        sessionManager.clearSession();
    }

    @After
    public void tearDown() {
        sessionManager.clearSession();
    }

    @Test
    public void saveToken_encryptsAndRestoresNormalizedToken() {
        sessionManager.saveToken("  abc123  ");

        assertEquals("abc123", sessionManager.getToken());
        assertTrue(sessionManager.isLoggedIn());
    }

    @Test
    public void saveToken_rejectsBlankToken() {
        sessionManager.saveToken("   ");

        assertFalse(sessionManager.isLoggedIn());
    }
}
