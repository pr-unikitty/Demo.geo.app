package testing.xls;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;


@Repository
public interface JobRepository extends CrudRepository<Job, Integer> {

    List<Job> findByIdAndType(Integer jobId, JType jobType);
}
