INSERT INTO departments (id, name) VALUES (1, '컴퓨터공학과'), (2, '교양대학');
INSERT INTO categories (id, name) VALUES (1, '전공필수'), (2, '공통교양');
INSERT INTO users (id, name, grade, department_id) VALUES (1, '김도윤', 2, 1);
INSERT INTO courses (id, department_id, category_id, course_code, name, semester, credit, target_grade, target_text)
VALUES (1, 1, 1, 'CSE2101', '자료구조', '2학기', 3, 2, '컴퓨터공학과 2학년'),
       (2, 1, 1, 'CSE3105', '운영체제', '2학기', 3, 3, '컴퓨터공학과 3학년'),
       (3, 2, 2, 'GEN1001', '글쓰기와 소통', '2학기', 2, NULL, '전학년');
INSERT INTO course_histories (id, user_id, course_id, grade) VALUES (1, 1, 1, 'A0');
