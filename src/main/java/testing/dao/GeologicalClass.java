package testing.dao;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.*;
import com.google.gson.annotations.Expose;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name="geoclasses")
public class GeologicalClass implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    
    @Expose
    private String name;
    
    @Expose
    @Column(name="code")
    private String code;
    
    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "section_id")
    protected Section section;

    // Default constructor 'cause JPA
    protected GeologicalClass() {}
    // Own constructor
    public GeologicalClass(Section secName, String geoName, String geoCode) {
        this.section = secName;
        this.name = geoName;
        this.code = geoCode;
    }

    // Getters
    public String getName() {
        return this.name;
    }
    public String getCode() {
        return this.code;
    }
    public Section getSection() {
        return this.section;
    }
    
}
