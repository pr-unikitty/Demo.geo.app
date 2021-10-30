package demo.geo.app.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import demo.geo.app.xls.enums.JStatus;
import demo.geo.app.xls.enums.JType;

@Entity
public class Job implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private JType type;
    
    @Enumerated(EnumType.STRING)
    private JStatus status;

    private LocalDateTime dateTime;

    public Job() {
    }

    public Job(JType type) {
        this.type = type;
    }
    
    public Job(JType type, JStatus status, LocalDateTime dateTime) {
        this.type = type;
        this.status = status;
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JType getType() {
        return type;
    }

    public void setType(JType type) {
        this.type = type;
    }

    public JStatus getStatus() {
        return status;
    }

    public void setStatus(JStatus status) {
        this.status = status;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

}
