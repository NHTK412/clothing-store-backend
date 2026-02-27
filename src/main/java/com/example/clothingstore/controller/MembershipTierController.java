package com.example.clothingstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.membershiptier.MembershipTierRequestDTO;
import com.example.clothingstore.dto.membershiptier.MembershipTierResponseDTO;
import com.example.clothingstore.service.MembershipTierService;
import com.example.clothingstore.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("v1/membership-tiers")
@RequiredArgsConstructor
public class MembershipTierController {

        // @Autowired
        // private MembershipTierService membershipTierService;

        private final MembershipTierService membershipTierService;

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping
        public ResponseEntity<ApiResponse<Page<MembershipTierResponseDTO>>> getAllMembershipTier(
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer size) {

                Pageable pageable = PageRequest.of(page - 1, size);

                Page<MembershipTierResponseDTO> membershipTierResponseDTOs = membershipTierService
                                .getAllMembershipTier(pageable);

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully retrieved membership tiers",
                                                membershipTierResponseDTOs));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ResponseEntity<ApiResponse<MembershipTierResponseDTO>> createMembershipTier(
                        @Valid @RequestBody MembershipTierRequestDTO membershipTierRequestDTO) {

                MembershipTierResponseDTO membershipTierResponseDTO = membershipTierService
                                .createMembershipTier(membershipTierRequestDTO);

                // return ResponseEntity
                // .ok(new ApiResponse<MembershipTierResponseDTO>(true, null,
                // membershipTierResponseDTO));

                return ResponseEntity.ok(
                                ApiResponse.created("Successfully created membership tier", membershipTierResponseDTO));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{membershipTieId}")
        public ResponseEntity<ApiResponse<MembershipTierResponseDTO>> deleteMembershipTier(
                        @PathVariable Integer membershipTieId) {

                MembershipTierResponseDTO membershipTierResponseDTO = membershipTierService
                                .deleteMembershipTier(membershipTieId);

                // return ResponseEntity
                // .ok(new ApiResponse<MembershipTierResponseDTO>(true, null,
                // membershipTierResponseDTO));

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully deleted membership tier", membershipTierResponseDTO));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{membershipTieId}")
        public ResponseEntity<ApiResponse<MembershipTierResponseDTO>> updateMembershipTier(
                        @PathVariable Integer membershipTieId,
                        @Valid @RequestBody MembershipTierRequestDTO membershipTierRequestDTO) {

                MembershipTierResponseDTO membershipTierResponseDTO = membershipTierService
                                .updateMembershipTier(membershipTieId, membershipTierRequestDTO);

                // return ResponseEntity
                // .ok(new ApiResponse<MembershipTierResponseDTO>(true, null,
                // membershipTierResponseDTO));

                return ResponseEntity.ok(
                                ApiResponse.success("Successfully updated membership tier", membershipTierResponseDTO));
        }

}
