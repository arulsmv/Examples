package com.assignment;
import org.testng.annotations.*;

//import static org.junit.Assert.assertEquals;

import static org.testng.AssertJUnit.*;


/**
 * Created by arulsmv on 26/7/23.
 */
public class SelfDestructiveMapJunitTest {

    SelfDestructingMap<String, String> sDMap;

    @BeforeClass
    public void setUp() {
        sDMap = new SelfDestructingMap<String, String>();
    }

    @AfterClass
    public void tearDown() {
        sDMap.stop();
    }

    // Single test that completes the functionality.
    // As the DS used are native to java no mocks were to be used.
    @Test
    public void testSelfDestrutingMap() throws Throwable {
        sDMap.put("Key1", "Value1", 1000);
        assertEquals(1, sDMap.getStat());
        sDMap.put("Key2", "Value2", 3000);
        sDMap.put("Key3", "Value3", 1000);
        sDMap.put("Key1", "Value1", 1000);
        sDMap.put("Key1", "Value1", 10000);
        sDMap.put("Key2", "Value22", 12000);
        assertEquals( 3, sDMap.getStat());
        sDMap.put("Key3", "Value3", 1000);
        sDMap.put("Key1", "Value1", 1000); // re-insert with less ttl.
        assertEquals(3, sDMap.getStat());
        assertEquals("Value3", sDMap.get("Key3"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        assertEquals(null, sDMap.get("Key3"));
        assertEquals(null,sDMap.get("Key1")); // New ttl should have kicked this out.
        assertEquals("Value22",sDMap.get("Key2")); // ensure getting new value
        assertEquals(1, sDMap.getStat()); // only key2 in the system.
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        assertEquals(null,sDMap.get("Key2")); //ttl-passed it should be null
        try {
            //Sleep for another 1/2 seconds to ensure the key2 also got removed.
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        assertEquals(0, sDMap.getStat());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }
}

