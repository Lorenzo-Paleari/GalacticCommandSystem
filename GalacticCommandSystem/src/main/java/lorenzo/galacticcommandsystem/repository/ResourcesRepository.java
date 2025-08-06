package lorenzo.galacticcommandsystem.repository;

import lorenzo.galacticcommandsystem.model.Resource;
import org.springframework.data.repository.CrudRepository;

public interface ResourcesRepository extends CrudRepository<Resource, Long> {
}
