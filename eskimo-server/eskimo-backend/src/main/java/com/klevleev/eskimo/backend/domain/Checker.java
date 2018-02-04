package com.klevleev.eskimo.backend.domain;


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
@Data
public class Checker implements Serializable {

    private Long id;

    @JsonProperty("type")
    private String programmingLanguage;

    @JsonProperty("name")
    private String fileName;

}
