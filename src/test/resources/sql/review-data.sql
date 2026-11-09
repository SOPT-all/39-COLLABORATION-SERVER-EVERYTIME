INSERT INTO departments (id, name) VALUES (1, '컴퓨터공학과');
INSERT INTO categories (id, name) VALUES (1, '공통교양');
INSERT INTO courses (id, department_id, category_id, course_code, name, semester, credit)
VALUES (1, 1, 1, 'GEN1001', 'AI와창의적문제해결', '2학기', 3);
INSERT INTO lecture_sections (id, course_id, section_no, professor) VALUES (1, 1, '01', '김민수');
INSERT INTO lecture_sections (id, course_id, section_no, professor) VALUES (2, 1, '02', '이지은');

INSERT INTO keywords (id, name) VALUES (1, '교수님'), (2, '과제'), (3, '시험'), (4, '팀 프로젝트');

INSERT INTO reviews (id, section_id, year, like_count, semester, grade_point, content) VALUES
    (101, 1, 2026, 0, '2학기', 3.00, '매주 과제 있기는 한데 수업시간에 실습했던거로 나오는 경우도 있어서 엄청 귀찮지는 않음'),
    (102, 1, 2026, 0, '2학기', 5.00, '매주 과제있긴한데 거의 수업시간에 다하는거고 부담 없어요'),
    (103, 1, 2026, 0, '2학기', 5.00, '교수님께서 좋으셔서 듣기 좋았어요!');

INSERT INTO review_keywords (id, review_id, keyword_id, highlight_start, highlight_end) VALUES
    (1, 103, 1, 0, 3),
    (2, 102, 2, 3, 5),
    (3, 101, 2, 3, 5),
    (4, 101, 3, NULL, NULL);
