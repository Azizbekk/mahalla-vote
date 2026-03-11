package com.azyu.mahallavote.repository;

import com.azyu.mahallavote.domain.Referral;
import com.azyu.mahallavote.domain.enumeration.ReferralStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Optional<Referral> findByReferredId(Long referredId);

    List<Referral> findByReferrerId(Long referrerId);

    long countByReferrerId(Long referrerId);

    long countByReferrerIdAndStatus(Long referrerId, ReferralStatus status);

    boolean existsByReferredId(Long referredId);
}
