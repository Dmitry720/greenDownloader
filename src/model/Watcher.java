package model;

import java.util.Queue;

/**
 * A class that retrieves tasks from a specified queue and passes them to the addDownloadProcess method of the AsyncDownloder class
 */
public class Watcher {

	private Thread daemon;
	private Queue<DownloadTask> queue;
	private AsyncDownloder downloder;
	
	// TODO
	public AsyncDownloder getDownloder() {return downloder;}
	
	/**
	 * @param queue - the queue with tasks to perform
	 */
	public Watcher(Queue<DownloadTask> queue) {
		this.queue = queue;
		this.downloder = new AsyncDownloder();
		this.daemon = new Thread(() -> {this.daemonStart();});
		daemon.setDaemon(true);
		daemon.start();
	}
		
	private void daemonStart() {
		for(;;) {
			try {
				Thread.sleep(500);
				if(!queue.isEmpty()) {
					downloder.addDownloadProcess(queue.remove());
					System.out.println("$_$");
				}
			}
			catch (InterruptedException e) {
				// TODO
				System.out.println("������ ������ �����!");
				//
				e.printStackTrace();
			}
			catch (Exception e) {
				// TODO
				System.out.println("���-�� ����� �� ���");
				//
				e.printStackTrace();
			}
		}
	}
	
}
