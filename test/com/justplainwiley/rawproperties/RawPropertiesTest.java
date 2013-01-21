/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.justplainwiley.rawproperties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wiley
 */
public class RawPropertiesTest {

    private RawProperties rawProps;

    public RawPropertiesTest() {
    }

    @Before
    public void setUp() {
        rawProps = new RawProperties();
    }

    @After
    public void tearDown() {
        rawProps = null;
    }

    @Test
    public void testLoadFromInputStream() {
        String input = "# This is a test file\n"
                + "test=test property\n   ";

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
        } catch (Exception ex) {
            Logger.getLogger(RawPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        RawEntry re = rawProps.getEntry("test");
        assertNotNull(re);
        assertEquals("test property", re.getValue());

        ArrayList<RawEntry> entryList = rawProps.getEntryList();
        assertEquals("Entry list should have 3 entries", 3, entryList.size());

    }

    @Test
    public void testPropertyTypes() {
        String input = "# This is a test file\n"
                + "test=test property\n   ";

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
        } catch (Exception ex) {
            Logger.getLogger(RawPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        ArrayList<RawEntry> entryList = rawProps.getEntryList();
        assertEquals("Entry list should have 3 entries", 3, entryList.size());

        assertEquals("First Entry should be a Comment", EntryType.COMMENT, entryList.get(0).getType());
        assertEquals("Second Entry should be a Property", EntryType.PROPERTY, entryList.get(1).getType());
        assertEquals("Third Entry should be a Blank Line", EntryType.BLANK_LINE, entryList.get(2).getType());

    }

    @Test
    public void testMultiLineProperty() {
        String input = "# This is multiline property test file\n"
                + "test=test property \\\n"
                + "    more property";

        Properties props = new Properties();

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
        } catch (Exception ex) {
            Logger.getLogger(RawPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        ArrayList<RawEntry> entryList = rawProps.getEntryList();
        assertEquals("Entry list should have 2 entries", 2, entryList.size());

        assertEquals("First Entry should be a Comment", EntryType.COMMENT, entryList.get(0).getType());
        assertEquals("Second Entry should be a Property", EntryType.PROPERTY, entryList.get(1).getType());
    }

    @Test
    public void testMultiLineValue() {
        String input = "# This is multiline property test file\n"
                + "test=test property \\\n"
                + "    more property";

        Properties props = new Properties();

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
        } catch (Exception ex) {
            Logger.getLogger(RawPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        ArrayList<RawEntry> entryList = rawProps.getEntryList();
        assertEquals("Entry list should have 2 entries", 2, entryList.size());

        RawEntry multiLineProp = entryList.get(1);
        assertEquals("Incorrect multi-line value", "test property more property", multiLineProp.getValue());

        assertEquals(2, multiLineProp.getLines().size());
        assertEquals("test property \\", multiLineProp.getLines().get(0));
        assertEquals("    more property", multiLineProp.getLines().get(1));
    }

    @Test
    public void testLineNumbers() throws IOException {
        String input = "# This is multiline property test file\n" //0
                + "test=test property \\\n" //1
                + "    more property\n" //2
                + "\n" //3
                + "#Another Multi-line property\n" //4
                + "bob.marley = is a legend\\\n" //5
                + "  for \\\n" //6
                + "  real\n" //7
                + "\n" //8
                + "\n"; //9

        Properties props = new Properties();

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
        } catch (Exception ex) {
            Logger.getLogger(RawPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        ArrayList<RawEntry> entryList = rawProps.getEntryList();
        assertEquals("Entry list has wrong number of entries", 7, entryList.size());

        assertEquals(EntryType.COMMENT, entryList.get(0).getType());
        assertEquals(EntryType.PROPERTY, entryList.get(1).getType());
        assertEquals(EntryType.BLANK_LINE, entryList.get(2).getType());
        assertEquals(EntryType.COMMENT, entryList.get(3).getType());
        assertEquals(EntryType.PROPERTY, entryList.get(4).getType());
        assertEquals(EntryType.BLANK_LINE, entryList.get(5).getType());
        assertEquals(EntryType.BLANK_LINE, entryList.get(6).getType());

        assertEquals(0, entryList.get(0).getLineNum());
        assertEquals(1, entryList.get(1).getLineNum());
        assertEquals(3, entryList.get(2).getLineNum());
        assertEquals(4, entryList.get(3).getLineNum());
        assertEquals(5, entryList.get(4).getLineNum());
        assertEquals(8, entryList.get(5).getLineNum());
        assertEquals(9, entryList.get(6).getLineNum());
        

    }

    @Test
    public void testSingleLineProperties() {
        String input = "# This is a single-line property test file\n" //0
                + "prop=test property \\\\\n" //1
                + "prop1 = test value\n"
                + "prop2= test value 2\n"
                + "prop3 = test value 3 \\\n";


        Properties props = new Properties();

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
        } catch (Exception ex) {
            Logger.getLogger(RawPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        ArrayList<RawEntry> entryList = rawProps.getEntryList();
        assertEquals("Entry list has wrong number of entries", 5, entryList.size());


        assertEquals("test property \\\\", entryList.get(1).getValue());
        assertEquals("test value", entryList.get(2).getValue());
        assertEquals("test value 2", entryList.get(3).getValue());
        assertEquals("test value 3 ", entryList.get(4).getValue());
    }

    @Test
    public void testSingleLinePropertyRetrievalByKey() {
        String input = "# This is single-line property test file\n" //0
                + "prop=test property \\\\\n" //1
                + "prop1 = test value\n"
                + "prop2= test value 2\n"
                + "prop3 = test value 3 \\\n";


        Properties props = new Properties();

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
        } catch (Exception ex) {
            Logger.getLogger(RawPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        ArrayList<RawEntry> entryList = rawProps.getEntryList();
        assertEquals("Entry list has wrong number of entries", 5, entryList.size());

        assertEquals("test property \\\\", rawProps.getEntry("prop").getValue());
        assertEquals("test value", rawProps.getEntry("prop1").getValue());
        assertEquals("test value 2", rawProps.getEntry("prop2").getValue());
        assertEquals("test value 3 ", rawProps.getEntry("prop3").getValue());

    }

    @Test
    public void testFinalContinuation() {
        String input = "# This is a single-line property test file\n" //0
                + "prop3 = test value 3 \\\n";


        Properties props = new Properties();

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
        } catch (Exception ex) {
            Logger.getLogger(RawPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        ArrayList<RawEntry> entryList = rawProps.getEntryList();
        assertEquals("Entry list has wrong number of entries", 2, entryList.size());


        assertEquals("test value 3 ", entryList.get(1).getValue());
    }
    
    @Test
    public void testNonFinalContinuation() {
        String input = "# This is a single-line property test file\n" //0
                + "prop3 = test value 3 \\\n"
                + "\n";


        Properties props = new Properties();

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
        } catch (Exception ex) {
            Logger.getLogger(RawPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        ArrayList<RawEntry> entryList = rawProps.getEntryList();
        assertEquals("Entry list has wrong number of entries", 2, entryList.size());


        assertEquals("test value 3 ", entryList.get(1).getValue());
    }

    @Test
    public void testComments() {
        String input = "# This is a single-line property test file\n"
                + "  property := blah\n"
                + "!This is another comment\n" 
                + "prop3 = test value 3 \\\n"
                + "\n";


        Properties props = new Properties();

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
        } catch (Exception ex) {
            Logger.getLogger(RawPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        ArrayList<RawEntry> entryList = rawProps.getEntryList();
        assertEquals("Entry list has wrong number of entries", 4, entryList.size());

        assertEquals("Wrong number of properties", 2, rawProps.size());
//        assertEquals("test value 3 ", entryList.get(1).getValue());
    }
}
