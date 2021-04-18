package testing.dao;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface SectionRepository extends CrudRepository<Section, Integer> {
    List<Section> findByName(String name);
}

