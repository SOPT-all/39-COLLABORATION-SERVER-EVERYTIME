package org.sopt.everytime.timetable.entity;

import org.sopt.everytime.lecture.entity.LectureSection;
import org.sopt.everytime.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "timetable_entries",
	uniqueConstraints = @UniqueConstraint(name = "uk_timetable_entries_user_section", columnNames = {"user_id",
		"section_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "section_id", nullable = false)
	private LectureSection section;

	public static TimetableEntry of(User user, LectureSection section) {
		TimetableEntry entry = new TimetableEntry();
		entry.user = user;
		entry.section = section;
		return entry;
	}
}
