package eskimo.backend.judge.jobs;

import eskimo.backend.judge.Invoker;

public abstract class JudgeJob {

    Invoker invoker;

    public void execute(Invoker invoker) {
        this.invoker = invoker;
        execute();
    }

    abstract void execute();

}
