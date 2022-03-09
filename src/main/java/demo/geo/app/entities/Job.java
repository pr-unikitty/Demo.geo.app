package demo.geo.app.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import demo.geo.app.enums.*;

@Entity
public class Job implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private JobType type;
    
    @Enumerated(EnumType.STRING)
    private JobStatus status; 

    @Enumerated(EnumType.STRING)
    private JobFormat format;
    
    private LocalDateTime dateTime;
    
    public Job() {
    }

    public Job(JobType type) {
        this.type = type;
    }
    
    public Job(JobType type, JobStatus status, JobFormat format, LocalDateTime dateTime) {
        this.type = type;
        this.status = status;
        this.format = format;
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public JobFormat getFormat() {
        return format;
    }

    public void setFormat(JobFormat format) {
        this.format = format;
    }

}
