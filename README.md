# REST CRUD API for Sections and GeologicalClasses + importing/exporting XLS files  
Technology stack: JDK 11, Spring Boot 2.5.6, Hipernate, SpringData, ApachePOI, Gradle 7.2

## Features  
  * JSON-format
     
  * Open-source RDBMS H2   
     
  * Port 8086 ("Hello, first Intel")   
     
  * Files import-export uses temporary folder of Apache Tomcat with path like (for Windows): [C:\Users\\_user_\AppData\Local\Temp\tomcat.8086.2443894020857019464]
  
## Data  

  Data structure is a set of records such as:  
  ```json
{
    "id": 1,
    "name": "Section 1",
    "geologicalClasses": [
        {
            "id": 2,
            "name": "GeoClass 11",
            "code": "GC11",
            "sectionId": 1
        },
        {
            "id": 3,
            "name": "GeoClass 12",
            "code": "GC12",
            "sectionId": 1
        }
    ]
}
  
  ```   
    
	XLS file structure contains header and list of sections with its geological classes as:  
| Section name | Class 1 name | Class 1 code | Class 2 name | Class 2 code | 
|--------------|:------------:|:------------:|:------------:|:------------:|
|   Section 1  | Geo Class 11 |     GC11     | Geo Class 12 |    GC12      |
|   Section 2  | Geo Class 21 |     GC21     | Geo Class 22 |    GC22      |
|   Section 3  | Geo Class 31 |     GC31     |              |              |

## Realised APIs
   APIs is presented by the following possible requests "localhost:8086/...":  
  
	  1.1. GET "/sections"   
	       - show all _Sections_ and their _GeologicalClases_  in DB   

	  1.2. GET "/sections/{id}"   
	       - show _Sections_ with specified ID   

	  1.3. POST "/sections"   
	       - create new Section with the specified values of name and any count of _GeologicalClasses_  

	  1.4. POST "/sections/{id}/geoclasses"   
	       - add new _GeologicalClass_ with the specified _name_ and _code_ to existing _Sections_  

	  1.5. DEL "/sections/{id}"   
	       - delete one _Sections_ from DB  

	  1.6. DEL "/sections"  
	       - delete all _Sections_ and _GeologicalClasses_ in DB  

	  2.1. GET "/sections/by-code" with required field _code_   
	       - show a list of all _Sections_ that have _GeologicalClasses_ with the specified code   

	  3.1. POST "/import" with required body _file_ contains xls file   
	       - returns _Job_ with info about _ID_ and _status_ of the Async Job and launches importing   

	  3.2. GET "/import/{id}"   
	       - returns result of importing by JobID ("DONE", "IN PROGRESS", "ERROR")  

	  3.3. GET "/export"   
	       - returns _Job_ with info about _ID_ and _status_ of the Async Job and launches exporting   

	  3.4. GET "/export/{id}"  
	     - returns result of parsed file by JobID ("DONE", "IN PROGRESS", "ERROR")    

	  3.5. GET "/export/{id}/file" 
	     - returns the file by JobID (throw an exception if exporting is in process)  

   You also can use Swagger to read more information about these APIs:  
   ```
	  4.1 GET "/swagger-ui/#/"
	     - shows Api documentation with JSON body examples, parameters, etc	  
   ```
