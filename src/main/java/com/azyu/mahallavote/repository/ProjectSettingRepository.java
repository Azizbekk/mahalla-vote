package com.azyu.mahallavote.repository;

import com.azyu.mahallavote.domain.ProjectSetting;
import com.azyu.mahallavote.domain.enumeration.ProjectSettingStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectSettingRepository extends JpaRepository<ProjectSetting, Long> {
    Optional<ProjectSetting> findBySettingKeyAndStatus(String settingKey, ProjectSettingStatus status);

    Optional<ProjectSetting> findBySettingKey(String settingKey);

    boolean existsBySettingKey(String settingKey);
}
