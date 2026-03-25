package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.Mapper;

import com.example.clothingstore.dto.membershiptier.MembershipTierResponseDTO;
import com.example.clothingstore.model.MembershipTier;

@Mapper(componentModel = "spring")
public interface MembershipTierMapper {

    MembershipTier toEntity(MembershipTier membershipTier);

    MembershipTierResponseDTO toResponseDTO(MembershipTier membershipTier);
}