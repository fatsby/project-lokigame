package com.theliems.lokigame.service.system;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.dto.name.NameCreateRequest;
import com.theliems.lokigame.model.dto.name.NameDTO;
import com.theliems.lokigame.model.dto.name.NameUpdateRequest;
import com.theliems.lokigame.model.entity.name.Name;
import com.theliems.lokigame.model.enums.NameType;
import com.theliems.lokigame.repository.system.NameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NameService {

    private final NameRepository nameRepository;
    private final ExceptionFactory exceptionFactory;

    @Transactional(readOnly = true)
    public List<NameDTO> getAllNames(NameType type) {
        List<Name> names;
        if (type != null) {
            names = nameRepository.findByType(type);
        } else {
            names = nameRepository.findAll();
        }
        return names.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NameDTO getNameById(UUID id) {
        Name name = nameRepository.findById(id)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Name", id));
        return mapToDTO(name);
    }

    @Transactional
    public NameDTO createName(NameCreateRequest request) {
        if (nameRepository.existsByNameAndType(request.getName(), request.getType())) {
            throw exceptionFactory.validationError(
                    "Name '" + request.getName() + "' with type " + request.getType() + " already exists");
        }

        Name name = Name.builder()
                .name(request.getName())
                .type(request.getType())
                .build();

        name = nameRepository.save(name);
        log.info("Created new name: {} (Type: {})", name.getName(), name.getType());
        return mapToDTO(name);
    }

    @Transactional
    public NameDTO updateName(UUID id, NameUpdateRequest request) {
        Name name = nameRepository.findById(id)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Name", id));

        if (request.getName() != null && !request.getName().isBlank()) {
            if (!request.getName().equals(name.getName()) &&
                    request.getType() != null &&
                    nameRepository.existsByNameAndType(request.getName(), request.getType())) {
                throw exceptionFactory.validationError(
                        "Name '" + request.getName() + "' with type " + request.getType() + " already exists");
            }
            name.setName(request.getName());
        }

        if (request.getType() != null) {
            name.setType(request.getType());
        }

        name = nameRepository.save(name);
        log.info("Updated name ID {}: {} (Type: {})", id, name.getName(), name.getType());
        return mapToDTO(name);
    }

    @Transactional
    public void deleteName(UUID id) {
        if (!nameRepository.existsById(id)) {
            throw exceptionFactory.resourceNotFound("Name", id);
        }
        nameRepository.deleteById(id);
        log.info("Deleted name ID: {}", id);
    }

    private NameDTO mapToDTO(Name name) {
        return NameDTO.builder()
                .id(name.getId())
                .name(name.getName())
                .type(name.getType())
                .build();
    }
}
