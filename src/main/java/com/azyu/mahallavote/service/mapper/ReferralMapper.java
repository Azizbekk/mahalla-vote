package com.azyu.mahallavote.service.mapper;

import com.azyu.mahallavote.domain.Referral;
import com.azyu.mahallavote.service.dto.ReferralDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ReferralMapper extends EntityMapper<ReferralDTO, Referral> {
    @Override
    @Mapping(source = "referrer.id", target = "referrerId")
    @Mapping(source = "referred.id", target = "referredId")
    ReferralDTO toDto(Referral entity);

    @Override
    @Mapping(source = "referrerId", target = "referrer.id")
    @Mapping(source = "referredId", target = "referred.id")
    Referral toEntity(ReferralDTO dto);

    @Override
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "referrer", ignore = true)
    @Mapping(target = "referred", ignore = true)
    void partialUpdate(@MappingTarget Referral entity, ReferralDTO dto);
}
