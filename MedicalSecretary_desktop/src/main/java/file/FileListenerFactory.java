package file;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import util.Constant;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class FileListenerFactory {
    long interval = TimeUnit.SECONDS.toMillis(Constant.MONITOR_SECOND);
    public FileAlterationMonitor getMonitor(Object controller, String monitorDir) {
        // 创建过滤器
        System.out.println("===in FileAlterationMonitor=== ");
        IOFileFilter directories = FileFilterUtils.and(FileFilterUtils.directoryFileFilter());
        UploadFileFilter fileFilter = new UploadFileFilter();
        IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),fileFilter);

        IOFileFilter filter = FileFilterUtils.or(directories, files);

        // 装配过滤器
//         FileAlterationObserver observer = new FileAlterationObserver(new File(monitorDir));
        FileAlterationObserver observer = new FileAlterationObserver(new File(monitorDir), filter);
        System.out.println("observer= "+observer);
        observer.addListener(new FileChangeListener(controller));
        return new FileAlterationMonitor(interval, observer);
    }

}
