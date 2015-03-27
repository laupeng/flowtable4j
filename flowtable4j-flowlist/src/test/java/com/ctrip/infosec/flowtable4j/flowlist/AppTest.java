package com.ctrip.infosec.flowtable4j.flowlist;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    private boolean F1()
    {
        System.out.println("F1");
        return false;
    }

    private boolean F2()
    {
        System.out.println("F2");
        return true;
    }
    public void testApp()
    {
        System.out.println(F1()||F2());
        assertTrue( true );

    }
}
