package testing.xml;

import java.time.LocalDateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class JobTest {
    
    public JobTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setStatus & getStatus methods, of class Job.
     */
    @Test
    public void testSetStatusAndGetStatus() {
        System.out.println("setStatus & getStatus");
        Job job = new Job(JType.IMPORT);
        JStatus expResult = JStatus.DONE;
        job.setStatus(JStatus.DONE);
        JStatus result = job.getStatus();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDateTime and getDateTime methods, of class Job.
     */
    @Test
    public void testSetDateTimeAndGetDateTime() {
        System.out.println("setDateTime & getDateTime");
        Job job = new Job(JType.IMPORT);
        LocalDateTime dtime = LocalDateTime.now();
        job.setDateTime(dtime);
        LocalDateTime result = job.getDateTime();
        assertEquals(dtime, result);
    }

    /**
     * Test of getType method, of class Job.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        Job job = new Job(JType.IMPORT);
        JType expResult = JType.IMPORT;
        JType result = job.getType();
        assertEquals(expResult, result);
    }
    
}
