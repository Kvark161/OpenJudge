package com.klevleev.eskimo.backend.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
@Data
public class Validator {

    private Long index;
    @JsonProperty("name")
    private String fileName;
    @JsonProperty("type")
    private String programmingLanguage;

}
