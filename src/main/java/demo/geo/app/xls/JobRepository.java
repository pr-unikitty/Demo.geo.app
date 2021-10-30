package demo.geo.app.xls;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import demo.geo.app.xls.enums.JType;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByIdAndType(Integer jobId, JType jobType);
}
