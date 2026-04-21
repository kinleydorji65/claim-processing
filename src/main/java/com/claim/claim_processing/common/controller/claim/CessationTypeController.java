package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.CessationTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.CessationTypeUpdateRequestDto;
import com.claim.claim_processing.common.service.claim.CessationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/cessation-types")
@RequiredArgsConstructor
public class CessationTypeController {

    private final CessationTypeService cessationTypeService;

    @GetMapping
    public ResponseEntity<List<CessationTypeResponseDto>> getAllActive() {
        return ResponseEntity.ok(cessationTypeService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CessationTypeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cessationTypeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CessationTypeResponseDto> create(
            @RequestBody CessationTypeCreateRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cessationTypeService.create(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CessationTypeResponseDto> update(
            @PathVariable Long id,
            @RequestBody CessationTypeUpdateRequestDto requestDto) {
        return ResponseEntity.ok(cessationTypeService.update(id, requestDto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        cessationTypeService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
