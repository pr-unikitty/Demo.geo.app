package demo.geo.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    private Integer id;
    
    //@Expose
    private String name;
    
    //@Expose
    @Column(name="code")
    private String code;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    @JsonIgnore
    protected Section section;
    
    @Column(name = "section_id")
    private Long sectionId;

    protected GeologicalClass() {
    }

    public GeologicalClass(long sectionId, String geoName, String geoCode) {
        this.name = geoName;
        this.code = geoCode;
        this.sectionId = sectionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
