ALTER TABLE review_keywords
    ADD CONSTRAINT uk_review_keywords_review_keyword UNIQUE (review_id, keyword_id);
