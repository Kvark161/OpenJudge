package com.klevleev.eskimo.backend.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
@Data
public class Statement implements Serializable {
    private static final long serialVersionUID = -5623307237343174281L;

    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DEFAULT_FORMAT = ".pdf";

    private Long id;

    private String language;

    @JsonProperty("name")
    private String fileName;

    private String format;

}
