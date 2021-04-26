package testing.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.*;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import testing.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;


@RunWith(SpringRunner.class)
@WebMvcTest(Application.class)
@ContextConfiguration(classes = SectionController.class)
public class SectionControllerTest {
    
    @MockBean
    private SectionRepository sectionRepository;

    @MockBean
    private SectionService sectionService;
    
    @Autowired
    private MockMvc mockMvc;

    public SectionControllerTest() {
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

    @Test
    public void findAllSections() throws Exception {
        
        Section sec1 = new Section("section1");
        GeologicalClass geoClass = new GeologicalClass(sec1, "geoName", "geoCode");
        sec1.addGeoClass(geoClass);
        sectionRepository.save(sec1);
        Section sec2 = new Section("section2");
        sec2.addGeoClass(geoClass);
        sectionRepository.save(sec2);
        
        List<Section> secs = new ArrayList();
        secs.add(sec1);
        secs.add(sec2);
        given(sectionRepository.findAll()).willReturn(secs);

        mockMvc.perform(get("/sections")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn().getResponse().getContentAsString().equals(sec1.getName());
    }

    @Test
    public void findOneSection() throws Exception {
        
        Section sec1 = new Section("section1");
        GeologicalClass geoClass = new GeologicalClass(sec1, "geoName", "geoCode");
        sec1.addGeoClass(geoClass);
        sectionRepository.save(sec1);
        List<Section> secs = Arrays.asList(sec1);
        given(sectionRepository.findAll()).willReturn(secs);

        mockMvc.perform(get("/sections/1")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn().getResponse().getContentAsString().equals(sec1.getName());
    }
}
