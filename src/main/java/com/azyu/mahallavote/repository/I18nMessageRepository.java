package com.azyu.mahallavote.repository;

import com.azyu.mahallavote.domain.I18nMessage;
import com.azyu.mahallavote.domain.enumeration.I18nMessageStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface I18nMessageRepository extends JpaRepository<I18nMessage, Long> {
    List<I18nMessage> findAllByStatusOrderBySortOrderAsc(I18nMessageStatus status);

    Optional<I18nMessage> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);
}
