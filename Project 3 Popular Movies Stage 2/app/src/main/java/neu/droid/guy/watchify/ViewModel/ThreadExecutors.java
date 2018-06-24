package neu.droid.guy.watchify.ViewModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A singleton class to execute All Threads and safeguard against race conditions
 */
public class ThreadExecutors {

    private static ThreadExecutors threadExecutorsInstance;
    private static final Object LOCK = new Object();
    private final Executor diskExecutor;

    private ThreadExecutors(Executor diskExecutor) {
        this.diskExecutor = diskExecutor;
    }


    public static ThreadExecutors getThreadExecutorsInstance() {
        if (threadExecutorsInstance == null) {
            synchronized (LOCK) {
                threadExecutorsInstance = new ThreadExecutors(Executors.newSingleThreadExecutor());
            }
        }

        return threadExecutorsInstance;
    }

    public Executor getDiskExecutor() {
        return diskExecutor;
    }
}
