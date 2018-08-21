package viewmodel;

import model.DownloadTask;
import model.TasksDataBaseConnection;

import java.util.PriorityQueue;

public class TasksViewModel {
    private PriorityQueue<DownloadTask> TasksQueue;
    private Thread DownloadThread;
    private Thread ScheduleThread;
    private TasksDataBaseConnection connection;
}
