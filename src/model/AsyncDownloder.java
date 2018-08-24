package model;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 * Class for asynchronous downloading of files.
 */
public class AsyncDownloder {
	
	private TasksDataBaseConnection dataBase;
	private ExecutorService downloadThreadPool;
	private Map<Integer, List<Future<?>>> downloadTaskList;
	
	AsyncDownloder() {
		// TODO написать статический метод во VM для получения БД 
		downloadThreadPool = Executors.newCachedThreadPool();
		downloadTaskList = new ConcurrentHashMap<>();
	}
	
	public static void main(String[] args) {
		
		
		
	}
	
	/**
	 * Begins the asynchronous downloading of files (each in its own thread) according to the specified URL array.
	 * 
	 * @param task - container containing download parameters
	 */
	public void addDownloadProcess(DownloadTask task) {
		List<Future<?>> downloadFileList;
		try {
			downloadFileList = Collections.synchronizedList(new ArrayList<>(task.getUrls().length));
			downloadTaskList.put(task.getId(), downloadFileList);
			for(int i = 0; i < task.getUrls().length; i++) {
				final int index = i;
				Future<?> future = downloadThreadPool.submit(() -> {this.startFileDownload(task, index);});
				downloadFileList.add(future);
			}
			dataBase.setDownloading(task.getId(), true);
		}
		catch (NullPointerException e) {
			//TODO
			System.out.println("в таске где-то нули");
			//
			e.printStackTrace();
		}
		catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	/**
	 * cancels the download for a given id
	 * 
	 * @param id - id of download to be canceled
	 * @throws NullPointerException
	 */
	public void cancelFileDownload(int id) throws NullPointerException {
		int index = 0;
		for(Future<?> future : downloadTaskList.get(id)) {
			future.cancel(true);
			deleteTask(id, index++);
		}
	}
	
	private void startFileDownload(DownloadTask task, int index) {
		boolean fileDeleteFlag = false;
		BufferedInputStream bis = null;
		OutputStream file = null;
        try {

        	bis = new BufferedInputStream(task.getUrls()[index].openStream());
        	file = Files.newOutputStream(task.getPaths()[index]);
        	
        	byte[] buffer = new byte[1024];
        	int count = 0;
        	
        	while((count = bis.read(buffer, 0, 1024)) != -1)
        	{
        		file.write(buffer, 0, count);
        		fileDeleteFlag = true;
        	}
        	
        }
        catch(FileNotFoundException e) {
        	//TODO
        	System.out.println("файл не может быть создан");
        	//
        	e.printStackTrace();
        }
        catch(IOException e) {      	
        	//TODO
        	System.out.println("проблема с соединением");
        	//
        	if(fileDeleteFlag)
        		deleteFile(task.getPaths()[index]);
        	e.printStackTrace();
        }
        finally {
        	
        	//TODO
        	System.out.println("загрузка завершена");
        	//
        	
        	//TODO
        	try {bis.close();} catch(Exception e) {System.out.println("K");}
        	try {file.close();} catch(Exception e) {System.out.println("K");}	
        	//
        	
        	deleteTask(task.getId(), index);

        }
	}

	private void deleteFile(Path path) {
		try { 
			Files.delete(path);
		} 
		catch(InvalidPathException e) {
		}
		catch(IOException e) {
			//TODO
			System.out.println("файл не может быть удалён");
			//
		}
	}
	
	//TODO подумать над оптимизацией
	private void deleteTask(int id, int index) {
		List<Future<?>> taskList = downloadTaskList.get(id); 
		taskList.remove(index);
    	if(taskList.isEmpty())
    		downloadTaskList.remove(id);
	}
	
	//TODO
	private void test(DownloadTask task, URL url) {
		try {
			Thread.sleep(2000);
			System.out.println("*");
			for(int i : downloadTaskList.keySet()) {
				System.out.println(downloadTaskList.get(i).size());
			}
			System.out.println("*");
		} catch (InterruptedException e) {
			System.out.println("нагрузка паламалася");
		}
	}
	//
	
}