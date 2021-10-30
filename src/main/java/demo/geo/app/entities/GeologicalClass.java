package demo.geo.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.*;

/**
 * Subclass for Sections
 */
@Entity
@Table(name="geoclasses")
public class GeologicalClass implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    private String name;
    
    @Column(name="code")
    private String code;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    @JsonIgnore
    protected Section section;
    
    @Column(name = "section_id", nullable = false)
    private Long sectionId;

    protected GeologicalClass() {
    }

    public GeologicalClass(Long sectionId, String geoName, String geoCode) {
        this.sectionId = sectionId;
        this.name = geoName;
        this.code = geoCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

}
