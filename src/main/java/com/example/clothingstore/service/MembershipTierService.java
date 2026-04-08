package com.example.clothingstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.membershiptier.MembershipTierRequestDTO;
import com.example.clothingstore.dto.membershiptier.MembershipTierResponseDTO;
import com.example.clothingstore.exception.business.NotFoundException;
// import com.example.clothingstore.mapper.mapstruct.MembershipTierMapper;
import com.example.clothingstore.mapper.MembershipTierMapper;

import com.example.clothingstore.model.MembershipTier;
import com.example.clothingstore.repository.MembershipTierRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembershipTierService {

        private final MembershipTierMapper membershipTierMapper;
        private final MembershipTierRepository membershipTierRepository;

        @Transactional
        public Page<MembershipTierResponseDTO> getAllMembershipTier(Pageable pageable) {
                Page<MembershipTier> membershipTiers = membershipTierRepository.findAll(pageable);
                return membershipTiers.map(membershipTierMapper::convertModelToMembershipTierResponseDTO);
        }

        @Transactional
        public MembershipTierResponseDTO createMembershipTier(MembershipTierRequestDTO membershipTierRequestDTO) {
                if (membershipTierRepository.existsByTierName(membershipTierRequestDTO.getTierName())) {
                        throw new NotFoundException("Membership tier with the same name already exists");
                }
                MembershipTier membershipTier = new MembershipTier();
                membershipTier = membershipTierMapper.convertMembershipTierRequestDTOToModel(membershipTierRequestDTO,
                                membershipTier);
                membershipTierRepository.save(membershipTier);
                return membershipTierMapper.convertModelToMembershipTierResponseDTO(membershipTier);
        }

        @Transactional
        public MembershipTierResponseDTO deleteMembershipTier(Integer membershipTieId) {
                MembershipTier membershipTier = membershipTierRepository.findById(membershipTieId)
                                .orElseThrow(() -> new NotFoundException("Invalid membershipTie code"));
                MembershipTierResponseDTO membershipTierResponseDTO = membershipTierMapper
                                .convertModelToMembershipTierResponseDTO(membershipTier);
                membershipTierRepository.delete(membershipTier);
                return membershipTierResponseDTO;
        }

        @Transactional
        public MembershipTierResponseDTO updateMembershipTier(Integer membershipTieId,
                        MembershipTierRequestDTO membershipTierRequestDTO) {
                if (membershipTierRepository.existsByTierName(membershipTierRequestDTO.getTierName())) {
                        throw new NotFoundException("Membership tier with the same name already exists");
                }
                MembershipTier membershipTier = membershipTierRepository.findById(membershipTieId)
                                .orElseThrow(() -> new NotFoundException("Invalid membershipTie code"));
                membershipTier = membershipTierMapper.convertMembershipTierRequestDTOToModel(membershipTierRequestDTO,
                                membershipTier);
                membershipTierRepository.save(membershipTier);
                return membershipTierMapper.convertModelToMembershipTierResponseDTO(membershipTier);
        }

        @Transactional
        public MembershipTierResponseDTO getCurrentMembershipTier(
                        Integer customerId) {
                MembershipTier membershipTier = membershipTierRepository.findByCustomers_UserId(customerId);
                if (membershipTier == null) {
                        throw new NotFoundException("Membership tier not found for customer ID: " + customerId);
                }
                return membershipTierMapper.convertModelToMembershipTierResponseDTO(membershipTier);
        }
}
