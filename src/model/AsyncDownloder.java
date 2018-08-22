package model;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 * Class for asynchronous downloading of files.
 */
public class AsyncDownloder {
	
	private ExecutorService downloadThreadPool;
	//TODO ïðîâåðèòü êîððåêòíîñòü ðàçìåðà
	private Map<Integer, List<Future<?>>> downloadTaskList;
	
	AsyncDownloder() {
		downloadThreadPool = Executors.newCachedThreadPool();
		downloadTaskList = new ConcurrentHashMap<>();
	}
	
	public static void main(String[] args) {
		
		URL[] urls = new URL[2];
		try {
			urls[0] = new URL("https://vk.com/doc264626650_470445692?hash=db7a63f4842c5b3503&dl=9ce2cdc17956063655");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		DownloadTask task = new DownloadTask(0, urls);
		task.setFilePath("C:\\Users\\DELL\\Desktop\\file.cdr");
		
		AsyncDownloder ad = new AsyncDownloder();
		ad.addDownloadProcess(task);
		ad.addDownloadProcess(task);
		ad.test(task, null);
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
			task.setIsDownloading(true);
		}
		catch (NullPointerException e) {
			//TODO
			System.out.println("â òàñêå ãäå-òî íóëè");
			//
			e.printStackTrace();
		}
	}
	
	/**
	 * cancels the download for a given id
	 * 
	 * @param id - id of download to be canceled
	 * @throws NullPointerException
	 */
	//TODO çàêîí÷èòü òåñòû
	public void cancelFileDownload(int id) throws NullPointerException {
		int index = 0;
		for(Future<?> future : downloadTaskList.get(id)) {
			future.cancel(true);
			deleteTask(id, index++);
		}
	}
	
	//TODO ðàçîáðàòüñÿ ñ ïðûæêîì ê finally
	private void startFileDownload(DownloadTask task, int index) {
		boolean fileDeleteFlag = false;
		BufferedInputStream bis = null;
		FileOutputStream file = null;
        try {

        	bis = new BufferedInputStream(task.getUrls()[index].openStream());
        	file = new FileOutputStream(task.getFilePath());
        	
        	byte[] buffer = new byte[1024];
        	int count = 0;
        	
        	while((count = bis.read(buffer, 0, 1024)) != -1)
        	{
        		task.setdownloadProgress(task.getDownloadingProgress() + count);
        		file.write(buffer, 0, count);
        		fileDeleteFlag = true;
        	}
        	
        }
        catch(FileNotFoundException e) {
        	//TODO
        	System.out.println("ôàéë íå ìîæåò áûòü ñîçäàí");
        	//
        	e.printStackTrace();
        }
        catch(IOException e) {      	
        	//TODO
        	System.out.println("ïðîáëåìà ñ ñîåäèíåíèåì");
        	//
        	if(fileDeleteFlag)
        		deleteFile(task.getFilePath());
        	e.printStackTrace();
        }
        finally {
        	
        	//TODO
        	System.out.println("çàãðóçêà çàâåðøåíà");
        	//
        	
        	//TODO
        	try {bis.close();} catch(Exception e) {System.out.println("K");}
        	try {file.close();} catch(Exception e) {System.out.println("K");}	
        	//
        	
        	deleteTask(task.getId(), index);

        }
	}

	private void deleteFile(String fileName) {
		try { 
			Path path = Paths.get(fileName);
			Files.delete(path);
		} 
		catch(InvalidPathException e) {
		}
		catch(IOException e) {
			//TODO
			System.out.println("ôàéë íå ìîæåò áûòü óäàë¸í");
			//
		}
	}
	
	//TODO ïîäóìàòü íàä îïòèìèçàöèåé
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
			System.out.println("íàãðóçêà ïàëàìàëàñÿ");
		}
	}
	//
	
}
