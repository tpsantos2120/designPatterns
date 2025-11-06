package org.java.repository;

import java.util.List;
import org.java.model.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {

  List<Resident> findByCondoId(Long condoId);

  List<Resident> findByCondoIdAndStatus(Long condoId, Resident.ResidentStatus status);
}
