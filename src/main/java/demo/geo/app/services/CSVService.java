package demo.geo.app.services;

import demo.geo.app.entities.Job;

import java.io.File;

public interface CSVService {
    
    public void generateCSV(Job job);
    public void parseCSV(File file, Job job);
    
}
