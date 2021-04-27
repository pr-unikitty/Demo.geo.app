package testing.xls;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

enum JType {IMPORT, EXPORT }
enum JStatus { DONE, IN_PROGRESS, ERROR }

@Entity
public class Job implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    
    @Enumerated(EnumType.STRING)
    private JType type;
    
    @Enumerated(EnumType.STRING)
    private JStatus status;

    private LocalDateTime dateTime;

    // Default constructor 'cause JPA
    public Job() {
    }
    // Own constructor
    public Job(JType type) {
        this.type = type;
    }
    public Job(JType type, Integer id) {
        this.type = type;
        this.id = id;
    }
    
    // Setters & Getters
    public JStatus getStatus() {
        return this.status;
    }
    public void setStatus(JStatus status) {
        this.status = status;
    }
    public LocalDateTime getDateTime() {
        return this.dateTime;
    }
    public void setDateTime(LocalDateTime dtime) {
        this.dateTime = dtime;
    }
    public JType getType() {
        return this.type;
    }
    public Integer getId() {
        return this.id;
    }
}
