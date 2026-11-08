CREATE TABLE departments
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_departments_name UNIQUE (name)
);

CREATE TABLE categories
(
    id   BIGINT      NOT NULL AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_categories_name UNIQUE (name)
);

CREATE TABLE keywords
(
    id   BIGINT      NOT NULL AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_keywords_name UNIQUE (name)
);

CREATE TABLE users
(
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    name          VARCHAR(50) NOT NULL,
    grade         INT         NOT NULL,
    department_id BIGINT      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_users_department FOREIGN KEY (department_id) REFERENCES departments (id)
);

CREATE TABLE courses
(
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    department_id     BIGINT       NOT NULL,
    category_id       BIGINT       NOT NULL,
    course_code       VARCHAR(30)  NOT NULL,
    name              VARCHAR(150) NOT NULL,
    semester          VARCHAR(10)  NOT NULL,
    credit            INT          NOT NULL,
    recommended_grade INT,
    target_grade      INT,
    target_text       VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_courses_department FOREIGN KEY (department_id) REFERENCES departments (id),
    CONSTRAINT fk_courses_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE course_histories
(
    id        BIGINT     NOT NULL AUTO_INCREMENT,
    user_id   BIGINT     NOT NULL,
    course_id BIGINT     NOT NULL,
    grade     VARCHAR(2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_course_histories_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_course_histories_course FOREIGN KEY (course_id) REFERENCES courses (id)
);

CREATE TABLE lecture_sections
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    course_id   BIGINT       NOT NULL,
    section_no  VARCHAR(10)  NOT NULL,
    professor   VARCHAR(100) NOT NULL,
    capacity    INT,
    saved_count INT,
    PRIMARY KEY (id),
    CONSTRAINT fk_lecture_sections_course FOREIGN KEY (course_id) REFERENCES courses (id)
);

CREATE TABLE section_times
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    section_id  BIGINT      NOT NULL,
    day_of_week VARCHAR(10) NOT NULL,
    start_time  TIME        NOT NULL,
    end_time    TIME        NOT NULL,
    classroom   VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT fk_section_times_section FOREIGN KEY (section_id) REFERENCES lecture_sections (id)
);

CREATE TABLE reviews
(
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    section_id  BIGINT        NOT NULL,
    year        BIGINT        NOT NULL,
    like_count  BIGINT        NOT NULL,
    semester    VARCHAR(10)   NOT NULL,
    grade_point DECIMAL(3, 2) NOT NULL,
    content     TEXT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_reviews_section FOREIGN KEY (section_id) REFERENCES lecture_sections (id)
);

CREATE TABLE review_keywords
(
    id              BIGINT NOT NULL AUTO_INCREMENT,
    review_id       BIGINT NOT NULL,
    keyword_id      BIGINT NOT NULL,
    highlight_start INT,
    highlight_end   INT,
    PRIMARY KEY (id),
    CONSTRAINT fk_review_keywords_review FOREIGN KEY (review_id) REFERENCES reviews (id),
    CONSTRAINT fk_review_keywords_keyword FOREIGN KEY (keyword_id) REFERENCES keywords (id)
);

CREATE TABLE timetable_entries
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    user_id    BIGINT NOT NULL,
    section_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_timetable_entries_user_section UNIQUE (user_id, section_id),
    CONSTRAINT fk_timetable_entries_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_timetable_entries_section FOREIGN KEY (section_id) REFERENCES lecture_sections (id)
);
