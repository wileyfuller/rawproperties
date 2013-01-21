/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.justplainwiley.rawproperties;

import java.io.ByteArrayInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Wiley Fuller <wiley.fuller@gmail.com>
 */
public class PropertiesBehaviourVerificationTest {

    private RawProperties rawProps;
    private Properties jdkProps;

    public PropertiesBehaviourVerificationTest() {
    }

    @Before
    public void setUp() {
        rawProps = new RawProperties();
        jdkProps = new Properties();
    }

    @After
    public void tearDown() {
        rawProps = null;
    }

    @Test
    public void testStandardProperties() {
        String input = "# This is a test file\n"
                + "prop1=test property\n"
                + "prop2=test property 2\n"
                + "prop3 = test property\n"
                + "prop4= test property\n"
                + "prop5 =: test property\n"
                + "prop6 := test property\n"
                + "prop7 :\n"
                + "prop8\n"
                + "prop 9\n";


        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        ByteArrayInputStream bais2 = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
            jdkProps.load(bais2);
        } catch (Exception ex) {
            Logger.getLogger(PropertiesBehaviourVerificationTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        assertEquals(jdkProps.size(), rawProps.size());
        
        assertEquals(jdkProps.get("prop1"), rawProps.get("prop1"));
        assertEquals(jdkProps.get("prop2"), rawProps.get("prop2"));
        assertEquals(jdkProps.get("prop3"), rawProps.get("prop3"));
        assertEquals(jdkProps.get("prop4"), rawProps.get("prop4"));
        assertEquals(jdkProps.get("prop5"), rawProps.get("prop5"));
        assertEquals(jdkProps.get("prop6"), rawProps.get("prop6"));

    }

    @Test
    public void testSimpleMultiline() {
        String input = "# This is a test file\n"
                + "prop1=test property \\\n"
                + "    some more text\n"
                + "prop3 = blah\\\n"
                + "    blah\n"
                + "# a comment";

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        ByteArrayInputStream bais2 = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
            jdkProps.load(bais2);
        } catch (Exception ex) {
            Logger.getLogger(PropertiesBehaviourVerificationTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        assertEquals(jdkProps.size(), rawProps.size());

        assertEquals(jdkProps.get("prop1"), rawProps.get("prop1"));

    }
    
    @Test
    public void testSpaceSeparatedProps() {
        String input = "# This is a test file\n"
                + "three cats in a tree\n"
                + "two dogs in a tree\n";

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        ByteArrayInputStream bais2 = new ByteArrayInputStream(input.getBytes());
        try {
            rawProps.load(bais);
            jdkProps.load(bais2);
        } catch (Exception ex) {
            Logger.getLogger(PropertiesBehaviourVerificationTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("An exception was thrown while trying to load the properties file");
        }

        assertEquals(jdkProps.size(), rawProps.size());

        assertEquals(jdkProps.get("three"), rawProps.get("three"));
        assertEquals(jdkProps.get("two"), rawProps.get("two"));

    }
    
}
