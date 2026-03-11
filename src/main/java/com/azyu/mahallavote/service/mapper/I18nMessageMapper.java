package com.azyu.mahallavote.service.mapper;

import com.azyu.mahallavote.domain.I18nMessage;
import com.azyu.mahallavote.service.dto.I18nMessageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface I18nMessageMapper extends EntityMapper<I18nMessageDTO, I18nMessage> {}
