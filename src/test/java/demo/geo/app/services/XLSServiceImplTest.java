package demo.geo.app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import demo.geo.app.dao.SectionRepository;
import demo.geo.app.entities.GeologicalClass;
import demo.geo.app.entities.Job;
import demo.geo.app.entities.Section;
import demo.geo.app.dao.JobRepository;
import demo.geo.app.enums.JobFormat;
import demo.geo.app.enums.JobStatus;
import demo.geo.app.enums.JobType;

@ExtendWith(MockitoExtension.class)
public class XLSServiceImplTest {
    
    @Mock
    private SectionRepository sectionRepository;
    
    @Mock
    private JobRepository jobRepository;
    
    @Mock
    private SectionService sectionService;
    
    private XLSService xlsService;
    
    private static File file;
    private static Job job;
    
    private static Section section1;
    private static Section section2;
    private static GeologicalClass geoClass1;
    private static GeologicalClass geoClass2;
    private static final long SECTION_ID_1 = 1L;
    private static final long SECTION_ID_2 = 2L;
    
    private static final String FILE_NAME = "workbook.xls";
    private static final String PATH = "src" + File.separator + "test" + File.separator + "resources";
    private static final String FULL_FILE_NAME = PATH + File.separator + FILE_NAME;
    
    private static final String SECTION_HEADER = "Section name";
    private static final String SECTION_1 = "Section 1";
    private static final String SECTION_2 = "Section 2";
    private static final String GEO_NAME_HEADER = "Class 1 name";
    private static final String GEO_NAME_1 = "Geo Class 11";
    private static final String GEO_NAME_2 = "Geo Class 21";
    private static final String GEO_CODE_HEADER = "Class 1 code";
    private static final String GEO_CODE_1 = "GC11";
    private static final String GEO_CODE_2 = "GC21";

    
    @BeforeAll
    public static void setUpClass() throws FileNotFoundException, IOException {
        // create file table
        HSSFWorkbook workbook = new HSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        HSSFSheet sheet = workbook.createSheet();
        
        buildRow(sheet.createRow(0), createHelper, SECTION_HEADER, GEO_NAME_HEADER, GEO_CODE_HEADER);
        buildRow(sheet.createRow(1), createHelper, SECTION_1, GEO_NAME_1, GEO_CODE_1);
        buildRow(sheet.createRow(2), createHelper, SECTION_2, GEO_NAME_2, GEO_CODE_2);
        
        // 'resources' folder
        if (!Files.exists(Path.of(PATH))) {
            Files.createDirectory(Path.of(PATH));
        }
        
        // write excel-fiction to the file
        OutputStream testFile = new FileOutputStream(FULL_FILE_NAME);
        workbook.write(testFile);
        workbook.close();
        testFile.close();
    }
    
    private static void buildRow(Row row, CreationHelper createHelper, String section,
                                 String geoclass, String geocode) {
        row.createCell(0).setCellValue(createHelper.createRichTextString(section));
        row.createCell(1).setCellValue(createHelper.createRichTextString(geoclass));
        row.createCell(2).setCellValue(createHelper.createRichTextString(geocode));
    }
    
    @BeforeEach
    public void setUp() throws FileNotFoundException {
        xlsService = new XLSServiceImpl(sectionRepository, jobRepository, sectionService);
        file = new File(FULL_FILE_NAME);
        job = new Job(JobType.EXPORT, JobStatus.IN_PROGRESS, JobFormat.XLS, LocalDateTime.now());
        job.setId(1L);
        
        geoClass1 = new GeologicalClass(SECTION_ID_1, GEO_NAME_1, GEO_CODE_1);
        section1 = new Section(SECTION_1, List.of(geoClass1));
        section1.setId(SECTION_ID_1);
        geoClass2 = new GeologicalClass(SECTION_ID_2, GEO_NAME_2, GEO_CODE_2);
        section2 = new Section(SECTION_2, List.of(geoClass2));
        section2.setId(SECTION_ID_2);
    }
    
    @AfterAll
    public static void tearDownClass() throws IOException {
        file.delete();
    }

    /**
     * Test of generateXLS method, of class FileServiceImpl.
     */
    @Test
    public void testGenerateXLS_Success() {
        job.setType(JobType.EXPORT);
        when(sectionRepository.findAll()).thenReturn(List.of(section1, section2));
        
        xlsService.generateXLS(job);
        
        verify(sectionRepository).findAll();
        verify(jobRepository).save(any());
    }

    /**
     * Test of parseXLS method, of class FileServiceImpl.
     */
    @Test
    public void testParseXLS_Success() {
        job.setType(JobType.IMPORT);
        section2.setId(SECTION_ID_2+1L);
        sectionRepository.delete(section2);
        
        when(sectionRepository.findByName(SECTION_1)).thenReturn(section1);
        when(sectionRepository.findByName(SECTION_2)).thenReturn(null);
        when(sectionRepository.save(any())).thenReturn(section2);
        
        xlsService.parseXLS(file, job);
        
        verify(sectionRepository, times(2)).findByName(any());
        verify(sectionRepository, times(2)).save(any());
        verify(jobRepository).save(any());
    }
    
}
