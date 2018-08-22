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

            int id = con.addTask(urls, paths);

            System.out.println(con.getDownloading(id));

            DownloadTask task = con.peekTask(id);

            for (URL url : task.getUrls()) {
                System.out.print(url);
            }

            con.setDownloading(id, true);
            System.out.println(con.getDownloading(id));

            con.removeTask(id);

            // planned

            id = con.addPlannedTask(urls, paths, now);

            System.out.println(con.getDownloading(id));
            System.out.println(con.minTime());

            task = con.peekPlannedTask();

            for (URL url : task.getUrls()) {
                System.out.print(url);
            }

            con.setDownloading(id, true);
            System.out.println(con.getDownloading(id));

            System.out.println(con.minTime());

            con.removeTask(id);

            System.out.println(con.minTime());
        }
        catch (Exception ex) { System.out.println(ex);}
    }
}