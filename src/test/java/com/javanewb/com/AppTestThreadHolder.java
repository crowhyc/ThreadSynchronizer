package com.javanewb.com;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTestThreadHolder
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTestThreadHolder(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTestThreadHolder.class );
    }

    /**
     * Rigourous TestThreadHolder :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
