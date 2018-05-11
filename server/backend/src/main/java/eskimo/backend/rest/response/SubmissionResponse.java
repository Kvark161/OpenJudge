package eskimo.backend.rest.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import eskimo.backend.entity.Submission;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Getter @Setter
public class SubmissionResponse {
    private long submissionId;
    private Instant sendingTime;
    private String userName;
    private String problemName;
    private Submission.Status status;
    private long usedTime;
    private long usedMemory;
    private int firstFailTest;

    public SubmissionResponse() {
    }

    public SubmissionResponse(Submission submission, String userName, String problemName) {
        submissionId = submission.getId();
        sendingTime = submission.getSendingTime();
        this.userName = userName;
        this.problemName = problemName;
        status = submission.getStatus();
        usedTime = submission.getUsedTime();
        usedMemory = submission.getUsedMemory();
        firstFailTest = submission.getFirstFailTest();
    }

    @JsonGetter("sendingTime")
    public String getSendingTimeJson() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(sendingTime.atZone(TimeZone.getDefault().toZoneId()));
    }
}
