package interfaces;

public interface LoadDataTask {
    public abstract void before();
    public abstract void doing()throws Exception;
    public abstract void done();
    public abstract void failed();
    public abstract void cancelled();
}
