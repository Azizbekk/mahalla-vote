package com.azyu.mahallavote.service.mapper;

import com.azyu.mahallavote.domain.TelegramUser;
import com.azyu.mahallavote.service.dto.TelegramUserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TelegramUserMapper extends EntityMapper<TelegramUserDTO, TelegramUser> {}
