package com.klevleev.eskimo.backend.dao;

import java.time.LocalDateTime;

import com.klevleev.eskimo.backend.domain.Contest;

/**
 * Created by Sokirkina Ekaterina on 06-Feb-2017.
 */
class LazyContest extends Contest {

	private boolean isNameSet;
	private boolean isStartTimeSet;
	private boolean isDurationSet;

	LazyContest(Long id) {
		setId(id);
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		isNameSet = true;
	}

	@Override
	public String getName() {
		if (!isNameSet) {
			setContestInfo(DaoFactory.getContestDao().getContestInfo(super.getId()));
		}
		return super.getName();
	}

	@Override
	public void setStartTime(LocalDateTime startTime) {
		super.setStartTime(startTime);
		isStartTimeSet = true;
	}

	@Override
	public LocalDateTime getStartTime() {
		if (!isStartTimeSet) {
			setContestInfo(DaoFactory.getContestDao().getContestInfo(super.getId()));
		}
		return super.getStartTime();
	}

	@Override
	public void setDuration(Integer duration) {
		super.setDuration(duration);
		isDurationSet = true;
	}

	@Override
	public Integer getDuration() {
		if (!isDurationSet) {
			setContestInfo(DaoFactory.getContestDao().getContestInfo(super.getId()));
		}
		return super.getDuration();
	}

	private void setContestInfo(Contest other) {
		setId(other.getId());
		setName(other.getName());
		setStartTime(other.getStartTime());
		setDuration(other.getDuration());
	}

}
