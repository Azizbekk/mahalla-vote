package com.azyu.mahallavote.repository;

import com.azyu.mahallavote.domain.VoteLot;
import com.azyu.mahallavote.domain.enumeration.VoteLotStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteLotRepository extends JpaRepository<VoteLot, Long> {
    Optional<VoteLot> findFirstByStatusOrderByIdDesc(VoteLotStatus status);

    boolean existsByStatus(VoteLotStatus status);
}
