package util;

import interfaces.LoadDataTask;
import javafx.concurrent.Task;

public class LoadingTask extends Task<Integer> {

    private LoadDataTask task;
    public LoadingTask (LoadDataTask task){
        this.task = task;
    }

    public void start(){
        task.before();
        if(!this.runAndReset()){
            this.failed();
        }
    }

    @Override
    protected Integer call() throws Exception {
        task.doing();
        return 0;
    }

    @Override
    protected void succeeded() {
        task.done();
    }
    @Override
    protected void cancelled() {
        task.cancelled();
    }
    @Override
    protected void failed() {
        task.failed();
    }
}
