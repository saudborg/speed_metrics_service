package de.factorypal.sauloborges.repository;

import de.factorypal.sauloborges.model.Machine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<Machine, String> {

}
