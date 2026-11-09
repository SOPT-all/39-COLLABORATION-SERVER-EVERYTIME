package org.sopt.everytime.review.repository;

import org.sopt.everytime.review.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
