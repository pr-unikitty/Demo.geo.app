package testing.xml;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import testing.exceptions.NotFoundException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.MalformedURLException;



@Service
public class XmlDataBase {

    @Value("${C:\\Users\\Unikitty\\Documents\\NetBeansProjects\\SpringBootAppWithGradle}")
    private Path storage;

    // For TZ#3: /import
    public void storeImportFile(InputStream inputStream, String fileName) throws IOException {
        Files.copy(inputStream, storage.resolve(fileName).normalize());
        inputStream.close();
    }

    // For TZ#3: /export
    public void storeExportFile(HSSFWorkbook hssfWorkbook, String fileName) throws IOException {
        hssfWorkbook.write(new FileOutputStream(fileName));
    }

    // For TZ#3: /export/id/file
    public Resource loadResource(String fileName) {
        try {
            Resource resource = new UrlResource(storage.resolve(fileName).normalize().toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new NotFoundException("File " + fileName + " is not found.");
            }
        } catch (MalformedURLException ex) {
            throw new NotFoundException("File " + fileName + " is not found.", ex);
        }
    }
}
