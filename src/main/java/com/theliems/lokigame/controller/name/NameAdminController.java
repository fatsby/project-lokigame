package com.theliems.lokigame.controller.name;

import com.theliems.lokigame.model.dto.name.NameCreateRequest;
import com.theliems.lokigame.model.dto.name.NameDTO;
import com.theliems.lokigame.model.dto.name.NameUpdateRequest;
import com.theliems.lokigame.model.enums.NameType;
import com.theliems.lokigame.service.system.NameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/names")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class NameAdminController {

    private final NameService nameService;

    @GetMapping
    public ResponseEntity<List<NameDTO>> getAllNames(@RequestParam(required = false) NameType type) {
        return ResponseEntity.ok(nameService.getAllNames(type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NameDTO> getNameById(@PathVariable UUID id) {
        return ResponseEntity.ok(nameService.getNameById(id));
    }

    @PostMapping
    public ResponseEntity<NameDTO> createName(@Valid @RequestBody NameCreateRequest request) {
        return ResponseEntity.ok(nameService.createName(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NameDTO> updateName(
            @PathVariable UUID id,
            @Valid @RequestBody NameUpdateRequest request) {
        return ResponseEntity.ok(nameService.updateName(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteName(@PathVariable UUID id) {
        nameService.deleteName(id);
        return ResponseEntity.noContent().build();
    }
}
