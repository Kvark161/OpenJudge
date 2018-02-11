package com.klevleev.eskimo.invoker.enums;

import java.io.Serializable;

/**
 * Created by Sokirkina Ekaterina on 06-Oct-2016.
 */
public enum TestVerdict implements Serializable {
    OK,
    WRONG_ANSWER,
    PRESENTATION_ERROR,
    FAIL,
    TIME_LIMIT_EXCEED,
    CHECKER_ERROR, INTERNAL_INVOKER_ERROR
}
