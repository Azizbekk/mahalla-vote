package com.azyu.mahallavote.service.mapper;

import com.azyu.mahallavote.domain.ProjectSetting;
import com.azyu.mahallavote.service.dto.ProjectSettingDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectSettingMapper extends EntityMapper<ProjectSettingDTO, ProjectSetting> {}
