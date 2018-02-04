package com.klevleev.eskimo.backend.rest;

import java.io.IOException;
import java.util.List;

import com.klevleev.eskimo.backend.domain.Contest;
import com.klevleev.eskimo.backend.services.ContestService;
import com.klevleev.eskimo.backend.storage.TemporaryFile;
import com.klevleev.eskimo.backend.utils.FileUtils;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("contests")
    public List<Contest> getAllContests() {
        return contestService.getAllContests();
    }

    @PostMapping("contest/create/from/zip")
    public Contest createContest(@RequestParam("file") MultipartFile file) throws IOException {
        @Cleanup TemporaryFile zip = new TemporaryFile(fileUtils.saveFile(file, "contest-", "zip"));
        return contestService.saveContestZip(zip.getFile());
    }

}
