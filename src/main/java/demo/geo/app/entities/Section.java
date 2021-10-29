package demo.geo.app.entities;


import java.io.Serializable;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="sections")
public class Section implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "section_id")
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GeologicalClass> geologicalClasses;
    
    public Section() {
    }
    
    public Section(String secName) {
        this.name = secName;
        this.geologicalClasses = new ArrayList<>();
    }

    public Section(String name, List<GeologicalClass> geologicalClasses) {
        this.name = name;
        this.geologicalClasses = geologicalClasses;
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

    public List<GeologicalClass> getGeologicalClasses() {
        return geologicalClasses;
    }

    public void setGeologicalClasses(List<GeologicalClass> geologicalClasses) {
        this.geologicalClasses = geologicalClasses;
    }

    // Add GeoClass to existing Section
    public Section addGeoClass(GeologicalClass geo) {
        this.geologicalClasses.add(geo);
        return this;
    }
    
    // Add GeoClass to existing Section
    public Section addListOfGeoClasses(List<GeologicalClass> geos) {
        for (GeologicalClass geoClass : geos) {
            this.geologicalClasses.add(
                    geoClass);
        }
        return this;
    }

}
