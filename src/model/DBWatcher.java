package model;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Queue;

/**
 * ѕоток демон, извлекающий задачи из базы данных и добавл€ющий их в очередь
 */
public class DBWatcher {

	private Thread daemon;
	private Queue<DownloadTask> taskQueue;
	private TasksDataBaseConnection dataBase;
	private LocalDateTime minTaskTime;
	
	/**
	 * @param taskQueue - очередь, в которую будут добавл€тьс€ задачи
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws MalformedURLException
	 */
	public DBWatcher(Queue<DownloadTask> taskQueue) throws ClassNotFoundException, SQLException, MalformedURLException {
		dataBase = new TasksDataBaseConnection();
		this.taskQueue = taskQueue;
		minTaskTime = dataBase.getMinTime();
		
		this.daemon = new Thread(() -> {this.daemonStart();});
		daemon.setDaemon(true);
		daemon.start();
	}
	
	public static void main(String[] args) {
		try {
			//new DBWatcher();
		} catch (Exception e) {
				System.out.println("ѕроблемы с ƒЅ демоном");
				e.printStackTrace();
		}
	}
	
	private void daemonStart() {
		for(;;) {
			
			try {
				Thread.sleep(500);
				// TODO
				System.out.println("&_&");
				//
			} catch (InterruptedException e) {
				// TODO
				System.out.println("демону мешают спать");
				//
			}
			
			if(LocalDateTime.now().equals(minTaskTime)) {
				try {
					DownloadTask task = dataBase.peekPlannedTask();
					if(task != null)
						taskQueue.add(task);
					minTaskTime = dataBase.getMinTime();
				} catch (MalformedURLException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
	}
	
}
