package eskimo.backend.judge.jobs;

import eskimo.backend.judge.Invoker;

public interface JudgeJob {

    void execute(Invoker invoker);

}
