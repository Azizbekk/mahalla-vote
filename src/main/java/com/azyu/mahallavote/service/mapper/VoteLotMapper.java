package com.azyu.mahallavote.service.mapper;

import com.azyu.mahallavote.domain.VoteLot;
import com.azyu.mahallavote.service.dto.VoteLotDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoteLotMapper extends EntityMapper<VoteLotDTO, VoteLot> {}
