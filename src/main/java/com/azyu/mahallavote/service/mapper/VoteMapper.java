package com.azyu.mahallavote.service.mapper;

import com.azyu.mahallavote.domain.Vote;
import com.azyu.mahallavote.service.dto.VoteDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface VoteMapper extends EntityMapper<VoteDTO, Vote> {
    @Override
    @Mapping(source = "voter.id", target = "voterId")
    VoteDTO toDto(Vote entity);

    @Override
    @Mapping(source = "voterId", target = "voter.id")
    Vote toEntity(VoteDTO dto);

    @Override
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "voter", ignore = true)
    void partialUpdate(@MappingTarget Vote entity, VoteDTO dto);
}
