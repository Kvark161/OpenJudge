package com.klevleev.eskimo.backend.domain;

import lombok.Data;

import java.io.File;

/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
@Data
public class Test {

    private long index;
    private File inputFile;
    private File answerFile;

}
