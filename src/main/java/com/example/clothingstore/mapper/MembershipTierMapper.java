package com.example.clothingstore.mapper;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.membershiptier.MembershipTierRequestDTO;
import com.example.clothingstore.dto.membershiptier.MembershipTierResponseDTO;
import com.example.clothingstore.model.MembershipTier;

@Component
public class MembershipTierMapper {

    public MembershipTier convertMembershipTierRequestDTOToModel(
            MembershipTierRequestDTO membershipTierRequestDTO, MembershipTier membershipTier) {

        membershipTier.setTierName(membershipTierRequestDTO.getTierName());

        membershipTier.setColor(membershipTierRequestDTO.getColor());

        membershipTier.setMinimumSpending(membershipTierRequestDTO.getMinimumSpending());

        // membershipTier.setDiscountRate(membershipTierRequestDTO.getDiscountRate());

        // membershipTier.setDescription(membershipTierRequestDTO.getDescription());

        return membershipTier;
    }

    public MembershipTierResponseDTO convertModelToMembershipTierResponseDTO(MembershipTier membershipTier) {

        MembershipTierResponseDTO membershipTierResponseDTO = new MembershipTierResponseDTO();

        membershipTierResponseDTO.setTierName(membershipTier.getTierName());

        membershipTierResponseDTO.setTiedId(membershipTier.getTiedId());

        membershipTierResponseDTO.setMinimumSpending(membershipTier.getMinimumSpending());

        // membershipTierResponseDTO.setDiscountRate(membershipTier.getDiscountRate());

        // membershipTierResponseDTO.setDescription(membershipTier.getDescription());

        membershipTierResponseDTO.setColor(membershipTier.getColor());

        return membershipTierResponseDTO;
    }
}
