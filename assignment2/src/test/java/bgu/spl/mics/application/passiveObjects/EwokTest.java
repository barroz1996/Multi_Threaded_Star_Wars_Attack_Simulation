
package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {
    private Ewok e;

    @BeforeEach
    void setUp() {
        e=new Ewok(1);
    }

    @Test
    void testAcquire() {
        assertTrue(e.isAvailable());
        e.acquire();
        assertFalse(e.isAvailable());
    }

    @Test
    void testRelease() {
        e.available=false;
        assertFalse(e.isAvailable());
        e.release();
        assertTrue(e.isAvailable());
    }
}
