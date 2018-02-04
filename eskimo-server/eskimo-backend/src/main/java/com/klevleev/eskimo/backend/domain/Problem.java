package com.klevleev.eskimo.backend.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by Sokirkina Ekaterina on 06-Feb-2017.
 */
@Data
public class Problem implements Serializable {
    private static final long serialVersionUID = 304023522081325148L;

    private Long id;

    @JsonIgnore
    private Long index;

    private String name;

    @JsonProperty("time-limit")
    private Long timeLimit;

    @JsonProperty("memory-limit")
    private Long memoryLimit;

    @JsonProperty("tests-count")
    private Long testsCount;

}
