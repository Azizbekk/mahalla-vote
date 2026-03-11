package com.azyu.mahallavote.repository;

import com.azyu.mahallavote.domain.Captcha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaRepository extends JpaRepository<Captcha, Long> {}
