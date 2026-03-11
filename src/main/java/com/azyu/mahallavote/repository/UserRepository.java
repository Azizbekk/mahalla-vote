package com.azyu.mahallavote.repository;

import com.azyu.mahallavote.domain.AppUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link AppUser} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findOneByActivationKey(String activationKey);
    List<AppUser> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(LocalDateTime dateTime);
    Optional<AppUser> findOneByResetKey(String resetKey);
    Optional<AppUser> findOneByEmailIgnoreCase(String email);
    Optional<AppUser> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<AppUser> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<AppUser> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<AppUser> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
