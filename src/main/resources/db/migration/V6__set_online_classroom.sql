UPDATE section_times
SET classroom = '온라인'
WHERE id IN (14, 69, 70, 97)
  AND classroom IS NULL;
