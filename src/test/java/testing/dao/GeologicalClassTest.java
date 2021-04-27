package testing.dao;

import org.junit.Test;
import static org.junit.Assert.*;

public class GeologicalClassTest {
    
    public GeologicalClassTest() {
    }
    
    /**
     * Test of getName method, of class GeologicalClass.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        Section sec = new Section("section1");
        GeologicalClass geo = new GeologicalClass(sec,"geoName1","geoCode1");
        String expResult = "geoName1";
        String result = geo.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCode method, of class GeologicalClass.
     */
    @Test
    public void testGetCode() {
        System.out.println("getCode");
        Section sec = new Section("section2");
        GeologicalClass geo = new GeologicalClass(sec,"geoName2","geoCode2");        
        String expResult = "geoCode2";
        String result = geo.getCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSection method, of class GeologicalClass.
     */
    @Test
    public void testGetSection() {
        System.out.println("getSection");
        Section sec = new Section("section3");
        GeologicalClass geo = new GeologicalClass(sec,"geoName3","geoCode3");
        Section result = geo.getSection();
        assertEquals(sec, result);
    }
 
}
