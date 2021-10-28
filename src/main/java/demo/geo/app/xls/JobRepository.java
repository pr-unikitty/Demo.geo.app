package demo.geo.app.xls;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface JobRepository extends CrudRepository<Job, Integer> {

    List<Job> findByIdAndType(Integer jobId, JType jobType);
}
