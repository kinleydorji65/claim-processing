package com.claim.claim_processing.common.controller.claim;

import com.claim.claim_processing.common.DTO.request.claim.AccountTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.AccountTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.AccountTypeUpdateRequestDto;
import com.claim.claim_processing.common.service.claim.AccountTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim/masters/account-types")
@RequiredArgsConstructor
public class AccountTypeController {

    private final AccountTypeService accountTypeService;

    @GetMapping
    public ResponseEntity<List<AccountTypeResponseDto>> getAllActive() {
        return ResponseEntity.ok(accountTypeService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountTypeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(accountTypeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AccountTypeResponseDto> create(
            @RequestBody AccountTypeCreateRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountTypeService.create(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountTypeResponseDto> update(
            @PathVariable Long id,
            @RequestBody AccountTypeUpdateRequestDto requestDto) {
        return ResponseEntity.ok(accountTypeService.update(id, requestDto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        accountTypeService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}