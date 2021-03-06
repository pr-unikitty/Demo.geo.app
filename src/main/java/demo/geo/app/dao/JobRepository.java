package demo.geo.app.dao;

import demo.geo.app.entities.Job;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import demo.geo.app.enums.JType;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByIdAndType(Integer jobId, JType jobType);
}
