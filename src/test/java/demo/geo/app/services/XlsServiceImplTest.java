package demo.geo.app.services;

import demo.geo.app.dao.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import java.util.Collections;
import java.time.LocalDateTime;

import demo.geo.app.dao.SectionRepository;
import demo.geo.app.entities.Section;
import demo.geo.app.exceptions.NotFoundException;
import demo.geo.app.exceptions.UnprocessableException;
import demo.geo.app.entities.Job;
import demo.geo.app.enums.*;

@ExtendWith(MockitoExtension.class)
public class XlsServiceImplTest {
    
    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private FileService fileService;
    
    private XlsService XlsService;
    
    private static Job job;
    private static Section section1;
    
    private static final long JOB_ID = 1L;
    private static final long SECTION_ID = 1L;
    private static final JType JOB_TYPE = JType.EXPORT;
    private static final JStatus JOB_STATUS = JStatus.IN_PROGRESS;
    private static final String SECTION = "Section 1";
        
    @BeforeEach
    public void setUp() {
        XlsService = new XlsServiceImpl(sectionRepository, jobRepository, fileService);
        
        job = new Job(JOB_TYPE, JOB_STATUS, LocalDateTime.now());
        job.setId(JOB_ID);
        section1 = new Section(SECTION, Collections.emptyList());
        section1.setId(SECTION_ID);
    }

    /**
     * Test of startJob method, of class XlsServiceImpl.
     */
    @Test
    public void testStartJob_Export_Success() {
        when(sectionRepository.findAll()).thenReturn(List.of(section1));
        
        XlsService.startJob(JType.EXPORT);
        
        verify(sectionRepository).findAll();
        verify(fileService).generateXLS(any());
        verify(jobRepository).save(any());
    }
    
    /**
     * Test of startJob method, of class XlsServiceImpl.
     */
    @Test
    public void testStartJob_Export_Failed() {
        when(sectionRepository.findAll()).thenReturn(Collections.emptyList());
        
        assertThatThrownBy(() -> XlsService.startJob(JType.EXPORT))
                                  .isInstanceOf(NotFoundException.class);
        
        verify(sectionRepository).findAll();
        verify(fileService, times(0)).generateXLS(any());
        verify(jobRepository, times(0)).save(any());
    }
    
    /**
     * Test of startJob method, of class XlsServiceImpl.
     */
    @Test
    public void testStartJob_Import_Success() {
        XlsService.startJob(JType.IMPORT);
        
        verify(sectionRepository, times(0)).findAll();
        verify(fileService, times(0)).generateXLS(any());
        verify(jobRepository).save(any());
    }

    /**
     * Test of getJobStatus method, of class XlsServiceImpl.
     */
    @Test
    public void testGetJobStatus_Export_Success() {
        job.setType(JType.EXPORT);
        when(jobRepository.getById(JOB_ID)).thenReturn(job);
        
        XlsService.getJobStatus(JType.EXPORT, JOB_ID);
        verify(jobRepository).getById(JOB_ID);
    }

    /**
     * Test of getJobStatus method, of class XlsServiceImpl.
     */
    @Test
    public void testGetJobStatus_Export_Failed_DoesntExists() {
        when(jobRepository.getById(JOB_ID)).thenReturn(null);
        
        assertThatThrownBy(() -> XlsService.getJobStatus(JType.EXPORT, JOB_ID))
                                  .isInstanceOf(NotFoundException.class);
        verify(jobRepository).getById(JOB_ID);
    }
    
    /**
     * Test of getJobStatus method, of class XlsServiceImpl.
     */
    @Test
    public void testGetJobStatus_Export_Failed_NotThistype() {
        job.setType(JType.IMPORT);
        when(jobRepository.getById(JOB_ID)).thenReturn(job);
        
        assertThatThrownBy(() -> XlsService.getJobStatus(JType.EXPORT, JOB_ID))
                                  .isInstanceOf(UnprocessableException.class);
        verify(jobRepository).getById(JOB_ID);
    }
    
    /**
     * Test of getJobStatus method, of class XlsServiceImpl.
     */
    @Test
    public void testGetJobStatus_Import_Success() {
        job.setType(JType.IMPORT);
        when(jobRepository.getById(JOB_ID)).thenReturn(job);
        
        XlsService.getJobStatus(JType.IMPORT, JOB_ID);
        verify(jobRepository).getById(JOB_ID);
    }

    /**
     * Test of getJobStatus method, of class XlsServiceImpl.
     */
    @Test
    public void testGetJobStatus_Import_Failed_DoesntExists() {
        when(jobRepository.getById(JOB_ID)).thenReturn(null);
        
        assertThatThrownBy(() -> XlsService.getJobStatus(JType.IMPORT, JOB_ID))
                                  .isInstanceOf(NotFoundException.class);
        verify(jobRepository).getById(JOB_ID);
    }
    
    /**
     * Test of getJobStatus method, of class XlsServiceImpl.
     */
    @Test
    public void testGetJobStatus_Import_Failed_NotThistype() {
        job.setType(JType.EXPORT);
        when(jobRepository.getById(JOB_ID)).thenReturn(job);
        
        assertThatThrownBy(() -> XlsService.getJobStatus(JType.IMPORT, JOB_ID))
                                  .isInstanceOf(UnprocessableException.class);
        verify(jobRepository).getById(JOB_ID);
    }
    
    /**
     * Test of exportXLS method, of class XlsServiceImpl.
     */
    @Test
    public void testExportXLS_Failed_Type() {
        job.setType(JType.IMPORT);

        when(jobRepository.getById(JOB_ID)).thenReturn(job);
        assertThatThrownBy(() -> XlsService.exportXLS(JOB_ID))
                                  .isInstanceOf(UnprocessableException.class);
        verify(jobRepository).getById(JOB_ID);
    }
    
    /**
     * Test of exportXLS method, of class XlsServiceImpl.
     */
    @Test
    public void testExportXLS_Failed_Status() {
        job.setStatus(JStatus.IN_PROGRESS);
        
        when(jobRepository.getById(JOB_ID)).thenReturn(job);
        assertThatThrownBy(() -> XlsService.exportXLS(JOB_ID))
                                  .isInstanceOf(UnprocessableException.class);
        verify(jobRepository).getById(JOB_ID);
    }
    
}
