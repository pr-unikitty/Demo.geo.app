package testing.xls;

import java.time.LocalDateTime;
import org.junit.Test;
import static org.junit.Assert.*;

public class JobTest {
    
    public JobTest() {
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
