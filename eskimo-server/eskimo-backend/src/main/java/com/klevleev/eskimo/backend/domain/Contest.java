package com.klevleev.eskimo.backend.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Sokirkina Ekaterina on 06-Feb-2017.
 */
@Data
public class Contest implements Serializable {
    private static final long serialVersionUID = -7614541625538455702L;

    private Long id;

    private String name;

    private LocalDateTime startTime;

    private Integer duration;

    @JsonGetter("startTime")
    public String getStartTimeString() {
        return startTime == null ? null : startTime.toString();
    }
}
