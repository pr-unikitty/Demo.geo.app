package testing.dao;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class SectionTest {
    
    public SectionTest() {
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
     * Test of getName method, of class Section.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        Section instance = new Section("section1");
        String expResult = "section1";
        String result = instance.getName();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of addGeoClass and getGeologicalClasses methods, of class Section.
     */
    @Test
    public void testAddGeoClassAndGetGeologicalClasses() {
        System.out.println("addGeoClass & getGeologicalClasses");
        Section sec = new Section("section2");
        GeologicalClass geo = new GeologicalClass(sec,"geoName1","geoCode1");
        List<GeologicalClass> expResult = new ArrayList();
        expResult.add(geo);
        sec.addGeoClass(geo);
        List<GeologicalClass> result = sec.getGeologicalClasses();
        assertEquals(expResult, result);
    }

    /**
     * Test of addListOfGeoClasses method, of class Section.
     */
    @Test
    public void testAddListOfGeoClasses() {
        System.out.println("addListOfGeoClasses");
        Section sec = new Section("section3");
        GeologicalClass geo1 = new GeologicalClass(sec,"geoName1","geoCode1");
        GeologicalClass geo2 = new GeologicalClass(sec,"geoName2","geoCode2");
        List<GeologicalClass> geos = new ArrayList();
        geos.add(geo1);
        geos.add(geo2);
        sec.addListOfGeoClasses(geos);
        List<GeologicalClass> result = sec.getGeologicalClasses();
        assertEquals(geos, result);
    }
    
}
