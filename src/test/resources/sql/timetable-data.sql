INSERT INTO departments (id, name) VALUES (1001, '테스트학과');
INSERT INTO categories (id, name) VALUES (1001, '테스트전공');
INSERT INTO users (id, name, grade, department_id) VALUES
    (1001, '유채원', 3, 1001),
    (1002, '다른 사용자', 2, 1001),
    (1003, '빈 시간표 사용자', 1, 1001);

INSERT INTO courses (id, department_id, category_id, course_code, name, semester, credit) VALUES
    (1001, 1001, 1001, 'TEST001', '운영체제', '2학기', 3),
    (1002, 1001, 1001, 'TEST002', '데이터베이스', '2학기', 3),
    (1003, 1001, 1001, 'TEST003', '자료구조', '2학기', 3);
INSERT INTO lecture_sections (id, course_id, section_no, professor) VALUES
    (1001, 1001, '01', '테스트교수'),
    (1002, 1002, '01', '테스트교수'),
    (1003, 1003, '01', '테스트교수');

-- 입력 순서와 ID 순서를 달리하여 응답 정렬을 검증한다.
INSERT INTO timetable_entries (id, user_id, section_id) VALUES
    (1002, 1001, 1002),
    (1001, 1001, 1001),
    (1003, 1002, 1003);

-- 첫 강의는 월요일 2회와 수요일 1회. 두 번째 강의는 시간 정보가 없다.
INSERT INTO section_times (id, section_id, day_of_week, start_time, end_time) VALUES
    (1001, 1001, 'WED', '13:30:00', '14:45:00'),
    (1002, 1001, 'MON', '13:30:00', '14:45:00'),
    (1003, 1001, 'MON', '09:00:00', '10:15:00'),
    (1004, 1003, 'FRI', '10:00:00', '11:15:00');
