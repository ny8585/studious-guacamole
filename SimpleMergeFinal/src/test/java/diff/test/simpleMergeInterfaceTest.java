package diff.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class simpleMergeInterfaceTest
	extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
	public simpleMergeInterfaceTest( String testName )
	{
		super( testName );
	}

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( simpleMergeInterfaceTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	assertTrue( true );
//        assertFalse( true );
    }
	
	
}