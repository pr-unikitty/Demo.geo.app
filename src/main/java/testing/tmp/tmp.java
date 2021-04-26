/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing.tmp;

/**
 *
 * @author Unikitty
 */
public class tmp {
    
}

/*

package testing.xml;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import testing.dao.*;


@RunWith(SpringRunner.class)
@WebMvcTest(Application.class)
@ContextConfiguration(classes = XmlController.class)
@AutoConfigureMockMvc
//@SpringBootTest

public class XmlControllerTest {
    
    @MockBean
    private SectionService sectionService;
    
    @MockBean
    private JobRepository jobRepository;

    @MockBean
    private XmlService xmlService;
    
    @MockBean
    private SectionRepository sectionRepository;
    
    @Autowired
    private MockMvc mockMvc;

    public XmlControllerTest() {
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
    public void findAllJobs() throws Exception {
        
        Job newJob = new Job(JType.EXPORT);
        newJob.setStatus(JStatus.IN_PROGRESS);
        newJob.setDateTime(LocalDateTime.now());
        jobRepository.save(newJob);
        System.out.println(jobRepository.findAll());
        
        List<Job> jobs = Arrays.asList(newJob);
        given(jobRepository.findAll()).willReturn(jobs);
        mockMvc.perform(get("/export/1")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andReturn().getResponse().getContentAsString();
        
    }

}

*/