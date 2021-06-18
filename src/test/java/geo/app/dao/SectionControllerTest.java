package geo.app.dao;

import geo.app.dao.SectionRepository;
import geo.app.dao.SectionController;
import geo.app.model.GeologicalClass;
import geo.app.model.Section;
import geo.app.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.*;
import org.springframework.test.context.ContextConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {SectionController.class, Application.class})
public class SectionControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SectionRepository repository;
    
    @Before
    public void setDb() {
        Section section = new Section("Section1");
        GeologicalClass geo = new GeologicalClass(section, "GeoClass11", "GC11");
        section.addGeoClass(geo);
        repository.save(section);
    }
    
    @After
    public void resetDb() {
        repository.deleteAll();
    }
        
    @Test
    public void whenCreateSection() {

        Section section = new Section();
        
        String resp = "/sections?section=Section2&geoClassName=GeoClass11&geoClassCode=GC11";
        ResponseEntity<Section> response = restTemplate.postForEntity(resp, section, Section.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getName(), is("Section2"));
        System.out.println("POST sections");
    }
    
    @Test
    public void whenBadCreateSimilarSection() {

        Section section = new Section();
        
        String resp = "/sections?section=Section1&geoClassName=GeoClass11&geoClassCode=GC11";
        ResponseEntity<Section> response = restTemplate.postForEntity(resp, section, Section.class);
        assertThat(response.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
        System.out.println("POST sections");
    }
    
    @Test
    public void whenBadCreateSectionWithoutParams() {

        System.out.println("BAD POST sections");
        Section section = new Section();
        
        String resp = "/sections";
        ResponseEntity<Section> response = restTemplate.postForEntity(resp, section, Section.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        
        resp = "/sections?section=Section1";
        response = restTemplate.postForEntity(resp, section, Section.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        
        resp = "/sections?section=Section1&geoClassName=GeoClass11";
        response = restTemplate.postForEntity(resp, section, Section.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        
        resp = "/sections?section=Section1&geoClassCode=GC11";
        response = restTemplate.postForEntity(resp, section, Section.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }
    
    @Test
    public void whenGetSections() {

        System.out.println("GET sections");
                
        ResponseEntity<Section> response = restTemplate.getForEntity("/sections", Section.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getName(), is("Section1"));
    }
    
    @Test
    public void whenBadGetSection() {

        System.out.println("BAD GET sections");
        String resp = "/sections/{id}";
        ResponseEntity<Section> response = restTemplate.getForEntity(resp, Section.class,2);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }
    
    @Test
    public void whenDeleteSections() {

        System.out.println("DEL sections");
        ResponseEntity<Section> response = restTemplate.exchange("/sections", HttpMethod.DELETE, null, Section.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        
        response = restTemplate.getForEntity("/sections", Section.class);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }
    
    @Test
    public void whenGetSectionsByCode() {

        System.out.println("GET by-code");
                
        String resp = "/sections/by-code?code=GC11";
        ResponseEntity<Section> response = restTemplate.getForEntity(resp, Section.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getName(), is("Section1"));
    }
}
