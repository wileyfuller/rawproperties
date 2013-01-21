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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wiley
 */
public class RawPropertiesOutputTest {

    private RawProperties rawProps;

    public RawPropertiesOutputTest() {
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
    public void testSlightlyComplexStuff() throws IOException {
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

        String expectedOutput = "# This is multiline property test file\n" //0
                + "test=test property \\\n" //1
                + "    more property\n" //2
                + "\n" //3
                + "#Another Multi-line property\n" //4
                + "bob.marley = is a legend\\\n" //5
                + "  for \\\n" //6
                + "  real\n"
                + "\n"
                + "\n"; 

        Properties props = new Properties();

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
        } catch (Exception ex) {
            Logger.getLogger(RawPropertiesOutputTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        ArrayList<RawEntry> entryList = rawProps.getEntryList();
        assertEquals("Entry list has wrong number of entries", 7, entryList.size());



        ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
        OutputStreamWriter writer = new OutputStreamWriter(baos);
        rawProps.store(writer);
        writer.close();
        String output = baos.toString("UTF-8");
        
        assertEquals(expectedOutput, output);

    }
    
    @Test
    public void testSimpleStuff() throws IOException {
        String input = "# This is a property test file\n" 
                + "test test property \n"
                + "\n" 
                + "test2:hello\n"
                + "hello := world\n"
                + "  lots   of  spaces\n"
                + "!another  comment\n"; 

        String expectedOutput = "# This is a property test file\n" 
                + "test test property \n"
                + "\n" 
                + "test2:hello\n"
                + "hello := world\n"
                + "  lots   of  spaces\n"
                + "!another  comment\n"; 

        Properties props = new Properties();

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
        } catch (Exception ex) {
            Logger.getLogger(RawPropertiesOutputTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
        OutputStreamWriter writer = new OutputStreamWriter(baos);
        rawProps.store(writer);
        writer.close();
        String output = baos.toString("UTF-8");
        
        assertEquals(expectedOutput, output);

    }
    
}
