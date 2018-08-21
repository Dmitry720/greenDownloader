package model;

import java.net.URL;
import java.util.Collection;
import java.time.LocalDateTime;

/*
* Task container. Main content of TasksQueue, TasksDataBaseConnection and ect
* **/
public class DownloadTask {
    private final int _id;
    private final URL[] _urls;
    private final LocalDateTime _time;
    private final boolean _isScheduled;
    private boolean _isDownloading = false;
    private double _downloadProgress = 0;

    public int getId() {return _id;}

    public URL[] getUrls() {return _urls;}

    public LocalDateTime getTime() {return _time;}

    public boolean isScheduled() {return _isScheduled;}

    public boolean isDownloading() {return _isDownloading;}

    public void setIsDownloading(boolean value) {
        _isDownloading = value;
    }

    public double getDownloadingProgress() {return _downloadProgress;}

    public void setdownloadProgress(double value) throws IllegalArgumentException {
        if (value > 100 || value < 0)
            throw  new IllegalArgumentException("Expected value in range (0, 100), got " + Double.toString(value));

        _downloadProgress = value;
    }


    /**
     * @param id unique id for task
     * @param urls array of urls, which will be downloaded **/
    public DownloadTask(int id, URL[] urls) {
        _id = id;
        _urls = urls;
        _time = null;
        _isScheduled = false;
    }

    /**
     * @param id unique id for task
     * @param urls array of urls, which will be downloaded
     * @param time the time for which the task was scheduled**/
    public DownloadTask(int id, URL[] urls, LocalDateTime time) {
        _id = id;
        _urls = urls;
        _time = time;
        _isScheduled = true;
    }
}
