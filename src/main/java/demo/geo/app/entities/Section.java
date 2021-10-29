package demo.geo.app.entities;

import com.google.gson.annotations.Expose;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="sections")
public class Section implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "section_id")
    private long id;
    
    //@Expose
    private String name;
    
    //@Expose
    //@JsonBackReference
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GeologicalClass> geologicalClasses;
    
    public Section() {
    }
    
    public Section(String secName) {
        this.name = secName;
        this.geologicalClasses = new ArrayList<GeologicalClass>();
    }

    public Section(String name, List<GeologicalClass> geologicalClasses) {
        this.name = name;
        this.geologicalClasses = geologicalClasses;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
    public Section addGeoClass (GeologicalClass geo) {
        this.geologicalClasses.add(geo);
        return this;
    }
    
    // Add GeoClass to existing Section
    public Section addListOfGeoClasses (List<GeologicalClass> geos) {
        for (GeologicalClass geoClass : geos) {
            this.geologicalClasses.add(geoClass);
        }
        return this;
    }

}
