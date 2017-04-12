package com.klevleev.eskimo.backend.rest;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.klevleev.eskimo.backend.domain.Contest;
import com.klevleev.eskimo.backend.services.ContestService;
import com.klevleev.eskimo.backend.utils.FileUtils;
import com.klevleev.eskimo.backend.utils.TemplateFile;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    private ContestService contestService;

    @Autowired
    private FileUtils fileUtils;

    private Gson gson = new Gson();

    @RequestMapping("contests")
    public String getAllContests() throws IOException {
        List<Contest> contests = contestService.getAllContests();
        return toJson(contests);
    }

    @PostMapping("contest/create/from/zip")
    public String createContest(@RequestParam("file") MultipartFile file) throws IOException {
        @Cleanup TemplateFile zip = new TemplateFile(fileUtils.saveFile(file, "contest-", "zip"));
        Contest newContest = contestService.saveContestZip(zip.getFile());
        return toJson(newContest);
    }

    private String toJson(List<Contest> contests) throws IOException {
        @Cleanup StringWriter result = new StringWriter();
        @Cleanup JsonWriter jsonWriter = new JsonWriter(result);
        jsonWriter.beginArray();
        for (Contest contest : contests) {
            writeJson(jsonWriter, contest);
        }
        jsonWriter.endArray();
        jsonWriter.flush();
        result.flush();
        return result.toString();
    }

    private String toJson(Contest contest) throws IOException {
        @Cleanup StringWriter result = new StringWriter();
        @Cleanup JsonWriter jsonWriter = new JsonWriter(result);
        writeJson(jsonWriter, contest);
        jsonWriter.flush();
        result.flush();
        return result.toString();
    }

    private void writeJson(JsonWriter jsonWriter, Contest contest) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("id").value(contest.getId());
        jsonWriter.name("name").value(contest.getName());
        jsonWriter.name("startTime").value(contest.getStartTime() != null ? contest.getStartTime().toString() : null);
        jsonWriter.name("duration").value(contest.getDuration());
        jsonWriter.endObject();
    }

}
