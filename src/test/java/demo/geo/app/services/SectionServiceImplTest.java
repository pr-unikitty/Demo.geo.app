package demo.geo.app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Collections;

import demo.geo.app.dao.SectionRepository;
import demo.geo.app.entities.GeologicalClass;
import demo.geo.app.entities.Section;
import demo.geo.app.exceptions.NotFoundException;
import demo.geo.app.exceptions.UnprocessableException;

@ExtendWith(MockitoExtension.class)
public class SectionServiceImplTest {
    
    @Mock
    private SectionRepository sectionRepository;
    
    private SectionService sectionService;

    private Section section;
    private GeologicalClass geoClass;
    
    private static final long SECTION_ID = 1L;
    private static final String SECTION_NAME = "Section 1";
    private static final long GEOCLASS_ID = 2L;
    private static final String GEOCLASS_NAME = "Geological class 1";
    private static final String GEOCLASS_CODE = "GC1";
    
    
    @BeforeEach
    public void setUp() {
        sectionService = new SectionServiceImpl(sectionRepository);
        geoClass = new GeologicalClass(SECTION_ID, GEOCLASS_NAME, GEOCLASS_CODE);
        section = new Section(SECTION_NAME, List.of(geoClass));
        section.setId(SECTION_ID);
        geoClass.setId(GEOCLASS_ID);
    }
    
    /**
     * Test of findAll method, of class SectionServiceImpl.
     */
    @Test
    public void testFindAll_Success() {
        when(sectionRepository.findAll()).thenReturn(List.of(section));
        List<Section> gotSectionList = sectionService.findAll();
        
        verify(sectionRepository).findAll();
        assertEquals(gotSectionList, List.of(section));
    }

    /**
     * Test of findOne method, of class SectionServiceImpl.
     */
    @Test
    public void testFindOne_Success() {
        when(sectionRepository.findById(SECTION_ID)).thenReturn(section);
        Section gotSection = sectionService.findOne(SECTION_ID);
        
        verify(sectionRepository).findById(SECTION_ID);
        assertEquals(gotSection, section);
    }

    /**
     * Test of findOne method, of class SectionServiceImpl.
     */
    @Test
    public void testFindOne_Failed() {
        final long WRONG_SESSION_ID = SECTION_ID + 1L;
        
        when(sectionRepository.findById(WRONG_SESSION_ID)).thenReturn(null);
        assertThatThrownBy(() -> sectionService.findOne(WRONG_SESSION_ID))
                                  .isInstanceOf(NotFoundException.class);
        verify(sectionRepository).findById(WRONG_SESSION_ID);
    }
    
    /**
     * Test of createSection method, of class SectionServiceImpl.
     */
    @Test
    public void testCreateSection_Success() {
        final long NEW_SESSION_ID = SECTION_ID + 1L;
        final String NEW_SECTION_NAME = SECTION_NAME + "2";
        
        GeologicalClass newGeoClass = new GeologicalClass(NEW_SESSION_ID, GEOCLASS_NAME, GEOCLASS_CODE);
        Section newSection = new Section(NEW_SECTION_NAME, List.of(newGeoClass));
        newSection.setId(NEW_SESSION_ID);
        
        when(sectionRepository.findByName(NEW_SECTION_NAME)).thenReturn(null);
        sectionService.createSection(newSection);
        verify(sectionRepository, times(2)).save(any());
    }
    
    /**
     * Test of createSection method, of class SectionServiceImpl.
     */
    @Test
    public void testCreateSection_Failed_TheSameName() {
        final long NEW_SESSION_ID = SECTION_ID + 1L;
        
        GeologicalClass newGeoClass = new GeologicalClass(NEW_SESSION_ID, GEOCLASS_NAME, GEOCLASS_CODE);
        Section newSection = new Section(SECTION_NAME, List.of(newGeoClass));
        newSection.setId(NEW_SESSION_ID);
        
        when(sectionRepository.findByName(SECTION_NAME)).thenReturn(section);
        assertThatThrownBy(() -> sectionService.createSection(newSection))
                                  .isInstanceOf(UnprocessableException.class);
        verify(sectionRepository, times(0)).save(any());
    }

    /**
     * Test of createSection method call, of class SectionServiceImpl.
     */
    @Test
    public void testCreateSection_Captor() {
        final long NEW_SESSION_ID = SECTION_ID + 1L;
        final String NEW_SECTION_NAME = SECTION_NAME + "2";
        
        GeologicalClass newGeoClass = new GeologicalClass(NEW_SESSION_ID, GEOCLASS_NAME, GEOCLASS_CODE);
        Section newSection = new Section(NEW_SECTION_NAME, List.of(newGeoClass));
        newSection.setId(NEW_SESSION_ID);
        
        ArgumentCaptor<Section> captor = ArgumentCaptor.forClass(Section.class);
        
        sectionService.createSection(newSection);
        verify(sectionRepository, times(2)).save(captor.capture());
        Section gotSection = captor.getValue();
        assertEquals(NEW_SECTION_NAME, gotSection.getName());
        assertEquals(List.of(newGeoClass), gotSection.getGeologicalClasses());
    }
    
    /**
     * Test of deleteById method, of class SectionServiceImpl.
     */
    @Test
    public void testDeleteById_Success() {
        when(sectionRepository.findById(SECTION_ID)).thenReturn(section);
        sectionService.deleteById(SECTION_ID);
        verify(sectionRepository).deleteById(SECTION_ID);
    }
    
    /**
     * Test of deleteById method, of class SectionServiceImpl.
     */
    @Test
    public void testDeleteById_Failed_WrongId() {
        final long WRONG_SESSION_ID = SECTION_ID + 1L;
        
        when(sectionRepository.findById(WRONG_SESSION_ID)).thenReturn(null);
        assertThatThrownBy(() -> sectionService.deleteById(WRONG_SESSION_ID))
                                  .isInstanceOf(NotFoundException.class);
        verify(sectionRepository, times(0)).deleteById(WRONG_SESSION_ID);
    }

    /**
     * Test of deleteAll method, of class SectionServiceImpl.
     */
    @Test
    public void testDeleteAll_Success() {
        when(sectionRepository.findAll()).thenReturn(List.of(section));
        sectionService.deleteAll();
        verify(sectionRepository).findAll();
        verify(sectionRepository).deleteById(any());
    }

    /**
     * Test of findSectionsByGeologicalCode method, of class SectionServiceImpl.
     */
    @Test
    public void testFindSectionsByGeologicalCode_Success() {
        when(sectionRepository.findSectionsByGeoCode(GEOCLASS_CODE)).thenReturn(List.of(section));
        List<Section> gotSections = sectionService.findSectionsByGeologicalCode(GEOCLASS_CODE);
        
        verify(sectionRepository).findSectionsByGeoCode(GEOCLASS_CODE);
        assertEquals(gotSections, List.of(section));
    }
    
    /**
     * Test of findSectionsByGeologicalCode method, of class SectionServiceImpl.
     */
    @Test
    public void testFindSectionsByGeologicalCode_Failed_NoMatch() {
        final String WRONG_GEOCLASS_CODE = "GC2";
        
        when(sectionRepository.findSectionsByGeoCode(WRONG_GEOCLASS_CODE)).thenReturn(Collections.emptyList());
        List<Section> gotSections = sectionService.findSectionsByGeologicalCode(WRONG_GEOCLASS_CODE);
        
        verify(sectionRepository).findSectionsByGeoCode(WRONG_GEOCLASS_CODE);
        assertEquals(gotSections, Collections.emptyList());
    }

    /**
     * Test of addGeoClassIfAbsent method, of class SectionServiceImpl.
     */
    @Test
    public void testAddGeoClassIfAbsent_Success() {
            GeologicalClass newGeoClass = new GeologicalClass(SECTION_ID, "other name", "other code");

            boolean result = sectionService.addGeoClassIfAbsent(section, newGeoClass);
            assertEquals(result, false);
    }
        
    /**
     * Test of addGeoClassIfAbsent method, of class SectionServiceImpl.
     */
    @Test
    public void testAddGeoClassIfAbsent_Failed_TheSameName() {
        GeologicalClass newGeoClass = new GeologicalClass(SECTION_ID, GEOCLASS_NAME, "other code");
        
        boolean result = sectionService.addGeoClassIfAbsent(section, newGeoClass);
        assertEquals(result, true);
    }
    
    /**
     * Test of addGeoClassIfAbsent method, of class SectionServiceImpl.
     */
    @Test
    public void testAddGeoClassIfAbsent_Failed_TheSameCode() {
        GeologicalClass newGeoClass = new GeologicalClass(SECTION_ID, "othen name", GEOCLASS_CODE);
        
        boolean result = sectionService.addGeoClassIfAbsent(section, newGeoClass);
        assertEquals(result, true);
    }
    
}
