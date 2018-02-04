package com.klevleev.eskimo.backend.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by Sokirkina Ekaterina on 12-Mar-2017.
 */
@Data
public class Solution {

	private Long id;
	private String name;
	@JsonProperty("source_type")
	private String programmingLanguage;

}
