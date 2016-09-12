package com.klevleev.eskimo.server.web.forms;

import com.klevleev.eskimo.server.core.domain.Contest;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Created by Ekaterina Sokirkina on 06-Sep-2016.
 */
public class EditContestForm implements Serializable {
	private static final long serialVersionUID = 1510541381125226729L;

	@NotNull
	@NotEmpty
	private String name;

	@NotNull
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private LocalDateTime startTime;

	@NotNull
	@Min(value = 1)
	private Long durationInMinutes;

	public EditContestForm() {
	}

	public EditContestForm(Contest contest) {
		setName(contest.getName(new Locale("en")));
		LocalDateTime startTime = contest.getStartTime();
		if (startTime != null) {
			setStartTime(contest.getStartTime());
		}
		Duration duration = contest.getDuration();
		if (duration != null){
			setDurationInMinutes(duration.getSeconds()/60);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public Long getDurationInMinutes() {
		return durationInMinutes;
	}

	public void setDurationInMinutes(Long durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}
}
