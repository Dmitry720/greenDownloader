package main;

import java.net.URL;
import java.io.File;
import java.nio.file.*;
import model.*;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        try {
            model.TasksDataBaseConnection con = new model.TasksDataBaseConnection();
            URL[] urls = {new URL("https://docs.oracle.com/javase/7/docs/api/java/net/URL.html")};
            Path[] paths = {Paths.get("." + File.separator)};
            LocalDateTime now = LocalDateTime.now();

            System.out.println("Connection established");

            int id = con.addTask(urls, paths);

            System.out.println(String.format("insertion ok (%d)", id));

            System.out.println(con.getDownloading(id));

            System.out.println("selection ok");

            DownloadTask task = con.peekTask(id);

            System.out.println(String.format("selection ok (getting task (%d))", task.getId()));

            if(task == null)
                System.out.print("NULL!!!");
            if(task.getUrls() == null)
                System.out.println("URLS ARE NULL!!!");

            System.out.println(task.getUrls()[0]);

            con.setDownloading(id, true);
            System.out.println(con.getDownloading(id));

            con.removeTask(id);

            System.out.println("removing ok");

            // planned

            id = con.addPlannedTask(urls, paths, now);

            System.out.println("insertion ok");

            System.out.println(con.getDownloading(id));

            System.out.println(con.minTime() == null? "null" : con.minTime());

            System.out.println("selection ok");

            task = con.peekPlannedTask();

            for (URL url : task.getUrls()) {
                System.out.println(url);
            }

            con.setDownloading(id, true);
            System.out.println(con.getDownloading(id));

            System.out.println(con.minTime() == null ? "null" : "not null");

            con.removeTask(id);

            System.out.println(con.minTime()  == null ? "null" : "not null" );
        }
        catch (Exception ex) {
            System.out.println("??????????????????????????????????????????????????????????");
            System.out.println(ex);
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            System.out.println(ex.getLocalizedMessage());
            System.out.println("??????????????????????????????????????????????????????????");
        }
    }
}