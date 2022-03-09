package demo.geo.app.services;

import org.springframework.mock.web.MockMultipartFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Collections;
import java.time.LocalDateTime;

import demo.geo.app.dao.SectionRepository;
import demo.geo.app.entities.Section;
import demo.geo.app.exceptions.NotFoundException;
import demo.geo.app.exceptions.UnprocessableException;
import demo.geo.app.entities.Job;
import demo.geo.app.enums.*;
import demo.geo.app.dao.JobRepository;

@ExtendWith(MockitoExtension.class)
public class FileServiceImplTest {
    
    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private XLSService xlsService;
    
    private FileService fileService;
    
    private static Job job;
    private static Section section;
    private static MockMultipartFile file;

    private static final long JOB_ID = 1L;
    private static final long SECTION_ID = 1L;
    private static final JobType JOB_TYPE = JobType.EXPORT;
    private static final JobStatus JOB_STATUS = JobStatus.IN_PROGRESS;
    private static final JobFormat JOB_FORMAT = JobFormat.XLS;
    private static final String SECTION = "Section 1";
    private static final String CONTENT_TYPE = "multipart/form-data";
    private static final byte[] EMPTY_CONTENT = "".getBytes();

    @BeforeEach
    public void setUp() {
        fileService = new FileServiceImpl(sectionRepository, jobRepository, xlsService);
        
        job = new Job(JOB_TYPE, JOB_STATUS, JOB_FORMAT, LocalDateTime.now());
        job.setId(JOB_ID);
        section = new Section(SECTION, Collections.emptyList());
        section.setId(SECTION_ID);
        file = new MockMultipartFile("filename", "filename.xlsx", CONTENT_TYPE, EMPTY_CONTENT);
    }

    /**
     * Test of startJob method, of class XLSServiceImpl.
     */
    @Test
    public void testStartJob_Export_Success() {
        when(sectionRepository.findAll()).thenReturn(List.of(section));
        
        fileService.startJob(JobType.EXPORT, JOB_FORMAT);
        
        verify(sectionRepository).findAll();
        verify(xlsService).generateXLS(any());
        verify(jobRepository).save(any());
    }
    
    /**
     * Test of startJob method, of class XLSServiceImpl.
     */
    @Test
    public void testStartJob_Export_Failed() {
        when(sectionRepository.findAll()).thenReturn(Collections.emptyList());
        
        assertThatThrownBy(() -> fileService.startJob(JobType.EXPORT, JOB_FORMAT))
                                  .isInstanceOf(NotFoundException.class);
        
        verify(sectionRepository).findAll();
        verify(xlsService, times(0)).generateXLS(any());
        verify(jobRepository, times(0)).save(any());
    }
    
    /**
     * Test of startJob method, of class XLSServiceImpl.
     */
    @Test
    public void testStartJob_Import_Success() {
        fileService.startJob(JobType.IMPORT, JOB_FORMAT);
        
        verify(sectionRepository, times(0)).findAll();
        verify(xlsService, times(0)).generateXLS(any());
        verify(jobRepository).save(any());
    }

    /**
     * Test of getJobStatus method, of class XLSServiceImpl.
     */
    @Test
    public void testGetJobStatus_Export_Success() {
        job.setType(JobType.EXPORT);
        when(jobRepository.getById(JOB_ID)).thenReturn(job);
        
        fileService.getJobStatus(JobType.EXPORT, JOB_ID);
        verify(jobRepository).getById(JOB_ID);
    }

    /**
     * Test of getJobStatus method, of class XLSServiceImpl.
     */
    @Test
    public void testGetJobStatus_Export_Failed_DoesntExists() {
        when(jobRepository.getById(JOB_ID)).thenReturn(null);
        
        assertThatThrownBy(() -> fileService.getJobStatus(JobType.EXPORT, JOB_ID))
                                  .isInstanceOf(NotFoundException.class);
        verify(jobRepository).getById(JOB_ID);
    }
    
    /**
     * Test of getJobStatus method, of class XLSServiceImpl.
     */
    @Test
    public void testGetJobStatus_Export_Failed_NotThisType() {
        job.setType(JobType.IMPORT);
        when(jobRepository.getById(JOB_ID)).thenReturn(job);
        
        assertThatThrownBy(() -> fileService.getJobStatus(JobType.EXPORT, JOB_ID))
                                  .isInstanceOf(UnprocessableException.class);
        verify(jobRepository).getById(JOB_ID);
    }
    
    /**
     * Test of getJobStatus method, of class XLSServiceImpl.
     */
    @Test
    public void testGetJobStatus_Import_Success() {
        job.setType(JobType.IMPORT);
        when(jobRepository.getById(JOB_ID)).thenReturn(job);
        
        fileService.getJobStatus(JobType.IMPORT, JOB_ID);
        verify(jobRepository).getById(JOB_ID);
    }

    /**
     * Test of getJobStatus method, of class XLSServiceImpl.
     */
    @Test
    public void testGetJobStatus_Import_Failed_DoesntExists() {
        when(jobRepository.getById(JOB_ID)).thenReturn(null);
        
        assertThatThrownBy(() -> fileService.getJobStatus(JobType.IMPORT, JOB_ID))
                                  .isInstanceOf(NotFoundException.class);
        verify(jobRepository).getById(JOB_ID);
    }
    
    /**
     * Test of getJobStatus method, of class XLSServiceImpl.
     */
    @Test
    public void testGetJobStatus_Import_Failed_NotThisType() {
        job.setType(JobType.EXPORT);
        when(jobRepository.getById(JOB_ID)).thenReturn(job);
        
        assertThatThrownBy(() -> fileService.getJobStatus(JobType.IMPORT, JOB_ID))
                                  .isInstanceOf(UnprocessableException.class);
        verify(jobRepository).getById(JOB_ID);
    }
    
    /**
     * Test of exportXLS method, of class XLSServiceImpl.
     */
    @Test
    public void testExportXLS_Failed_Type() {
        job.setType(JobType.IMPORT);

        when(jobRepository.getById(JOB_ID)).thenReturn(job);
        assertThatThrownBy(() -> fileService.exportXLS(JOB_ID))
                                  .isInstanceOf(UnprocessableException.class);
        verify(jobRepository).getById(JOB_ID);
    }
    
    /**
     * Test of exportXLS method, of class XLSServiceImpl.
     */
    @Test
    public void testExportXLS_Failed_Status() {
        job.setStatus(JobStatus.IN_PROGRESS);
        
        when(jobRepository.getById(JOB_ID)).thenReturn(job);
        assertThatThrownBy(() -> fileService.exportXLS(JOB_ID))
                                  .isInstanceOf(UnprocessableException.class);
        verify(jobRepository).getById(JOB_ID);
    }

    /**
     * Test of importXLS method, of class XLSServiceImpl.
     */
    @Test
    public void testImportXLS_Failed_EmptyFile() {
        job.setStatus(JobStatus.IN_PROGRESS);

        assertThatThrownBy(() -> fileService.importXLS(job, file))
                .isInstanceOf(UnprocessableException.class);
        verify(xlsService, times(0)).parseXLS(any(), any());
    }
    
}
