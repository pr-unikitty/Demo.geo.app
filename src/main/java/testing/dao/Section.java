package testing.dao;

import java.io.Serializable;
import javax.persistence.*;
import com.google.gson.annotations.Expose;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.*;

// Main class
@Entity
public class Section implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "section_id")
    private Integer id;
    
    @Expose
    private String name;
    
    @Expose
    @JsonBackReference
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GeologicalClass> geologicalClasses;
    
    // Default constructor 'cause JPA
    protected Section() {}
    // Own constructor
    public Section(String secName) {
        this.name = secName;
        this.geologicalClasses = new ArrayList<GeologicalClass>();
    }
    
    // Getters
    public Integer getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public List<GeologicalClass> getGeologicalClasses() {
        return this.geologicalClasses;
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
    
    //toDEBUG
    public void print() {
        System.out.println(this.name);
        System.out.println("size = " + this.geologicalClasses.size());
        for (GeologicalClass row : this.geologicalClasses) {
            System.out.println(row.toString());
        }
    }
}
