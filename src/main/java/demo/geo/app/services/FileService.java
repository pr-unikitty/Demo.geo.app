package demo.geo.app.services;

import java.io.File;
import demo.geo.app.entities.Job;

public interface FileService {
    
    public void generateXLS(Job job);
    public void parseXLS(File file, Job job);
    
}
