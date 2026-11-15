UPDATE reviews
SET grade_point = ROUND(grade_point);

ALTER TABLE reviews
    MODIFY grade_point INT NOT NULL;
