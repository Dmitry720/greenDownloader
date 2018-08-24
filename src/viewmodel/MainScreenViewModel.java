package viewmodel;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;
import java.util.PriorityQueue;

import model.DBWatcher;
import model.DownloadTask;
import model.Watcher;

public class MainScreenViewModel {

	
	private DBWatcher dbWatcher;
	private Watcher watcher;
	private PriorityQueue<DownloadTask> queue;
	private List<Boolean> downloadControllerList;
	
	public MainScreenViewModel() throws ClassNotFoundException, MalformedURLException, SQLException {
		queue = new PriorityQueue<>();
		watcher = new Watcher(queue);
		dbWatcher = new DBWatcher(queue);
	}
	
	public static void main(String[] args) {
		
	}
	
	private void downloadController() {
		for(;;) {
			try {
				Thread.sleep(10000);
				
			} catch (Exception e) {
				// TODO
				System.out.println("проблема с проверкой загрузок");
				//
				e.printStackTrace();
			}
		}
	}
	
}
