INSERT INTO courses (id, department_id, category_id, course_code, name, semester, credit, recommended_grade, target_grade, target_text) VALUES
    (31, 5, 5, 'GEN2401', '교내인턴', '2학기', 3, NULL, NULL, '전학년'),
    (32, 5, 5, 'GEN2402', '사진디자인', '2학기', 3, NULL, NULL, '전학년'),
    (33, 5, 5, 'GEN2403', '인적성검사와 취업실전', '2학기', 2, NULL, NULL, '전학년');

INSERT INTO lecture_sections (id, course_id, section_no, professor, capacity, saved_count) VALUES
    (58, 31, '1.0', '한지민', 30, 1),
    (59, 32, '1.0', '류승아', 40, 1),
    (60, 33, '1.0', '강민재', 60, 1);

INSERT INTO section_times (id, section_id, day_of_week, start_time, end_time, classroom) VALUES
    (101, 58, 'MON', '10:00:00', '14:00:00', NULL),
    (102, 59, 'WED', '12:00:00', '14:45:00', '조형예술관-306'),
    (103, 60, 'FRI', '12:00:00', '14:45:00', NULL);

UPDATE lecture_sections
SET saved_count = saved_count - 1
WHERE id IN (3, 4, 5, 10, 41)
  AND saved_count > 0;

DELETE
FROM timetable_entries
WHERE user_id = 1;

INSERT INTO timetable_entries (user_id, section_id) VALUES
    (1, 58),
    (1, 59),
    (1, 60);
