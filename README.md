# REST CRUD API for Sections and GeologicalClasses + importing/exporting XML files  
Technology stack: Spring, Hipernate, SpringBoot, SpringData, ApachePOI, Gradle

## Features  
  * The project was developed using Gradle Build Tool  

  * Port was chanched to 8086 (in testing.dao.CustomContainer class)  

  * Files import-export used folder with path {C:\\Users\\Unikitty\\Documents\\NetBeansProjects\\SpringBootAppWithGradle}   
  (in testing.xml.XmlDataBase class)  
  
## Data  

  Data structure is a set of records such as:  
	{  
  “name”: “Section 1”,  
  “geologicalClasses”: [  
    { “name”: “Geo Class 11”, ”code”: “GC11” },  
    { “name”: “Geo Class 12”, ”code”: “GC12” }, ...]  
	}  
	XML file structure contains header and list of sections with it’s geological classes as:  
| Section name | Class 1 name | Class 1 code | Class 2 name | Class 2 code | 
|--------------|:------------:|:------------:|:------------:|:------------:|
|   Section 1  | Geo Class 11 |     GC11     | Geo Class 12 |    GC12      |
|   Section 2  | Geo Class 21 |     GC21     | Geo Class 22 |    GC22      |
|   Section 3  | Geo Class 31 |     GC31     |              |              |

## Realised APIs
   APIs is presented by the following possible requests "localhost:8086/...":  
  1.1. POST "/add" with required fields _section_, _geoClassName_ & _geoClassCode_  
       - create new record with the specified values of Section name and 1 geoClass  

  1.2. GET "/sections/{id}"   
       - show section with specified ID   

  1.3. GET "/sections"   
       - show all records in DB   

  1.4. DEL "/delete" with required field _id_   
       - delete one record from DB  

  1.5. DEL "/deleteAll"  
       - delete all records in DB  

  1.6. PUT "/addGeoclass" with required fields _section_, _geoClassName_ and _geoClassCode_   
       - add new geoClass with the specified name and code to section   

  2.1. GET "/sections/by-code" with required field _code_   
       - show a list of all Sections that have geologicalClasses with the specified code   

  3.1. POST "/import" with required field _file_   
       - returns ID of the Async Job and launches importing   

  3.2. GET "/import/{id}"   
       - returns result of importing by Job ID ("DONE", "IN PROGRESS", "ERROR")  

  3.3. GET "/export"   
       - returns ID of the Async Job and launches exporting   

  3.4. GET "/export/{id}"  
     - returns result of parsed file by Job ID ("DONE", "IN PROGRESS", "ERROR")    

  3.5. GET "/export/{id}/file" 
     - returns a file by Job ID (throw an exception if exporting is in process)  
  
