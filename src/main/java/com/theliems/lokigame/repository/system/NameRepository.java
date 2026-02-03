package com.theliems.lokigame.repository.system;

import com.theliems.lokigame.model.entity.name.Name;
import com.theliems.lokigame.model.enums.NameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NameRepository extends JpaRepository<Name, UUID> {
    List<Name> findByType(NameType type);

    boolean existsByNameAndType(String name, NameType type);
}
