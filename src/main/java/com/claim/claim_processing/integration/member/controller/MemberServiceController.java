package com.claim.claim_processing.integration.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claim.claim_processing.integration.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/claim-processing/members")
public class MemberServiceController {
    private final MemberService memberService;

    @GetMapping("/get-member-details/{nppfNumber}")
    public ResponseEntity<?> getMemberDetails(@PathVariable("nppfNumber") String nppfNumber) {
        return ResponseEntity.ok(memberService.getMemberDetails(nppfNumber));
    }
}
