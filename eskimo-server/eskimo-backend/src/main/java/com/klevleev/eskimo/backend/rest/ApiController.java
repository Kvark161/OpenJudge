package com.klevleev.eskimo.backend.rest;

import com.google.gson.stream.JsonWriter;
import com.klevleev.eskimo.backend.dao.ContestDao;
import com.klevleev.eskimo.backend.domain.Contest;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by stepank on 05.04.2017.
 */
@RestController
@Slf4j
@RequestMapping("api")
public class ApiController {

    @Autowired
    private ContestDao contestDao;

    @RequestMapping("contests")
    public String getAllContests() {
        List<Contest> contests = contestDao.getAllContests();
        try {
            @Cleanup StringWriter result = new StringWriter();
            @Cleanup JsonWriter jsonWriter = new JsonWriter(result);
            jsonWriter.beginArray();
            for (Contest contest : contests) {
                jsonWriter.beginObject();
                jsonWriter.name("id").value(contest.getId());
                jsonWriter.name("name").value(contest.getName());
                jsonWriter.name("startTime").value(contest.getStartTime().toString());
                jsonWriter.name("duration").value(contest.getDuration());
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.flush();
            result.flush();
            return result.toString();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
