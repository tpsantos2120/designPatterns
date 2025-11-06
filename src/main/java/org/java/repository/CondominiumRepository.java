package org.java.repository;

import java.util.List;
import org.java.model.Condominium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CondominiumRepository extends JpaRepository<Condominium, Long> {

  List<Condominium> findByActiveTrue();
}
