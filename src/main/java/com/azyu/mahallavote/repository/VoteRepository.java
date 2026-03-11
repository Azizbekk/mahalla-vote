package com.azyu.mahallavote.repository;

import com.azyu.mahallavote.domain.Vote;
import com.azyu.mahallavote.domain.enumeration.VoteStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    List<Vote> findByVoterId(Long voterId);

    List<Vote> findByStatus(VoteStatus status);

    Page<Vote> findByStatus(VoteStatus status, Pageable pageable);

    long countByVoterIdAndStatus(Long voterId, VoteStatus status);
}
