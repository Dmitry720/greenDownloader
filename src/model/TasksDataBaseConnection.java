package model;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.LinkedList;
import java.time.LocalDateTime;
import java.io.File;
import java.nio.file.*;

/**
 * connection to Tasks database
 * (wraps java.sql.Connection)
 * @author L1ttl3S1st3r
 * @version 1.0
 * 
 * 
 * @// TODO: 16.08.2018 update documentation, refactor, test 
 * */
public class TasksDataBaseConnection implements AutoCloseable {
    private final Connection _connection;

    /**
     * Default constructor. Uses default database url
     * @throws ClassNotFoundException
     * @throws SQLException
     * */
    public TasksDataBaseConnection() throws ClassNotFoundException, SQLException {
        this("jdbc:sqlite:.." + File.separator
                + ".." + File.separator
                + "resourses"
                + File.separator + "db.db"
        );
    }

    /**
     * Constructor with custom database url
     * @param url url for database, example \"jdbc:sqlite:../../resourses/db.db\"
     * @throws ClassNotFoundException
     * @throws SQLException
     * */
    public TasksDataBaseConnection(String url) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        _connection = DriverManager.getConnection(url);
        _connection.setAutoCommit(false);
    }

    @Override
    protected void finalize() throws Throwable {
        _connection.close();
    }

    @Override
    public void close() throws SQLException{
        _connection.close();
    }

    /**
     * sets downloading status of path with id
     * @param id
     * @param value new downloading status
     * @throws SQLException
     * */
    public setDownloading(int id, boolean value) throws SQLException {
        Statement statement = _connetion.createStatement();
        statement.executeUpdate(
                "UPDATE TASKS set DOWNLOADING = " + Boolean.toString(value)
                        + " WHERE ID = " + Integer.toString(id) + ";"
        );
        _connection.commit();
    }

    /**
     * gets downloading status of path with id
     * @param id
     * @throws SQLException
     * */
    public getDownloading(int id) {
        // get result set with needed result
        Statement statement = _connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT DOWNLOADING FROM TASKS WHERE ID == " + Integer.toString(id) + ";"
        );
        statement.close();

        return resultSet.getBoolean("DOWNLOADING");
    }

    /**
     * @return DownloadTask with your id
     * @param id id of needed DownloadTask
     * @throws SQLException
     * @throws MalformedURLException
     * */
    public DownloadTask peekTask(int id) throws SQLException, MalformedURLException {
        // execute sql query and get result (one node)
        Statement statement = _connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT SCHEDULED, TIME FROM TASKS WHERE ID == " + Integer.toString(id) + ";"
        );
        statement.close();

        // return result or throw an exception "result doesn't exist"
        if(resultSet.next()) {
            // handle if task is scheduled
            if (resultSet.getBoolean("SCHEDULED"))
                return new DownloadTask(
                        id, getTaskUrls(id), getTaskPaths(id), parseTaskTime(resultSet.getString("TIME"))
                );
            else
                return new DownloadTask(id, getTaskUrls(id), getTaskPaths(id));
        }
        else
            throw new SQLDataException("Node with id = " + Integer.toString(id) + " doesn't exist");
    }

    private LocalDateTime parseTaskTime(String time) {
        // make an array of LocalDateTime args
        int[] dateTimeArray = new int[6];
        int counter = 0;
        for (String dateNumberString : time.split(",")) {
            dateTimeArray[counter] = Integer.getInteger(dateNumberString);
            counter++;
        }

        return LocalDateTime.of(
                dateTimeArray[0], dateTimeArray[1], dateTimeArray[2],
                dateTimeArray[3], dateTimeArray[4], dateTimeArray[5]
        );
    }

    private URL[] getTaskUrls(int id) throws SQLException, MalformedURLException {
        // init an array with a length equal count of urls associated with the id
        Statement statement = _connection.createStatement();
        ResultSet urlsCount = statement.executeQuery(
                "SELECT COUNT(*) as count FROM URLS WHERE ID == " + Integer.toString(id) + ";"
        );
        URL[] urlsArray = new URL[urlsCount.getInt("count")];

        // get set of urls associated with the id
        ResultSet urlsSet = statement.executeQuery(
                "SELECT URL FROM URLS WHERE ID == " + Integer.toString(id) + ";"
        );

        statement.close();

        // fill an urls array
        int counter = 0;
        while (urlsSet.next()) {
            urlsArray[counter] = new URL(urlsSet.getString("URL"));
            counter++;
        }

        return urlsArray;
    }

    private Path[] getTaskPaths(int id) throws SQLException, MalformedURLException {
        // init an array with a length equal count of urls associated with the id
        Statement statement = _connection.createStatement();
        ResultSet urlsCount = statement.executeQuery(
                "SELECT COUNT(*) as count FROM URLS WHERE ID == " + Integer.toString(id) + ";"
        );
        Path[] pahtsArray = new Path[urlsCount.getInt("count")];

        // get set of paths associated with the id
        ResultSet pathsSet = statement.executeQuery(
                "SELECT PATH FROM URLS WHERE ID == " + Integer.toString(id) + ";"
        );

        statement.close();

        // fill an urls array
        int counter = 0;
        while (pathsSet.next()) {
            pathsArray[counter] = new Paths.get(pathsSet.getString("PATH"));
            counter++;
        }

        return urlsArray;
    }

    /**
     * removes DownloadTask with your id
     * @param id
     * @throws SQLException
     */
    public void removeTask(int id) throws SQLException{
        Statement statement = _connection.createStatement();
        statement.executeUpdate(
                "DELETE from TASKS WHERE ID = " + Integer.toString(id) + ";"
        );
        _connection.commit();
        statement.close();
    }

    /**
     * @return min time of planned tasks | null if time doesn't exist
     * @throws SQLException
     */
    public LocalDateTime minTime() throws SQLException {
        // get min time string of scheduled task
        Statement statement = _connection.createStatement();
        ResultSet minTimeSet = statement.executeQuery(
                "SELECT MIN(TIME) as minTime FROM TASKS WHERE SCHEDULED = TRUE AND DOWNLOADING = FALSE"
        );
        statement.close();

        // convert min time string to LocalDateTime, if scheduled tasks exist
        if(minTimeSet.next())
            return parseTaskTime(minTimeSet.getString("minTime"));
        else
            return null;
    }

    /**
     * @return DownloadTask with minimum planned time or null if there are no any planned tasks
     * @throws SQLException
     * @throws MalformedURLException
     * */
    public DownloadTask peekPlannedTask() throws SQLException, MalformedURLException {
        Statement statement = _connection.createStatement();
        ResultSet idOfPlannedTask = statement.executeQuery(
                "SELECT ID FROM TASKS WHERE" +
                        " TIME = (SELECT MIN(TIME) FROM TASKS" +
                        " WHERE SCHEDULED = TRUE AND DOWNLOADING = FALSE)"
        );
        statement.close();

        if(idOfPlannedTask.next())
            return peekTask(idOfPlannedTask.getInt("ID"));
        else
            return null;
    }

    /**
     * add new DownloadTask to database
     * @param urls urls of new Download task
     * @param paths paths of downloded files
     * @return id of new DownloadTask
     * @throws SQLException
     * @throws IllegalArgumentException urls.length != paths.length
     * */
    public int addTask(URL[] urls, Path[] paths) throws SQLException, IllegalArgumentException {
        // handle case if urls and paths lengths aren't equal
        if (urls.length != paths.length)
            throw new IllegalArgumentException(
                    "urls and files lenghts aren't equal (" + Integer.toString(urls.length)
                            + " and " + Integer.toString(paths.length) + ")"
            );

        // add new task to Tasks table
        Statement statement = _connection.createStatement();
        statement.executeUpdate("INSERT INTO TASKS(SCHEDULED, DOWNLOADING) VALUES (0, 0);");
        _connection.commit();

        // get new task id
        int id = statement.executeQuery("SELECT last_insert_rowid() as id").getInt("id");

        // add all urls with path to urls table, bind them with new task id
        for (int index; index < urls.length; index++) {
            statement.executeQuery {
                "INSERT INTO URLS(ID, URL, PATH) VALUES (" + Integer.toString(id) + ", " +
                        urls[index].toString() + ", " + paths[index].toString() + ");"
            };
            _connection.commit();
        }

        statement.close();

        return id;
    }

    private String timeString(LocalDateTime time) {
        return Integer.toString(time.getYear()) + ","
                + Integer.toString(time.getMonthValue()) + ","
                + Integer.toString(time.getDayOfMonth()) + ","
                + Integer.toString(time.getHour()) + ","
                + Integer.toString(time.getMinute()) + ","
                + Integer.toString(time.getSecond()) + ",";
    }

    /**
     * add new scheduled DownloadTask to database
     * @param urls urls of new Download task
     * @param paths paths of downloded files
     * @param time time in schedule
     * @return id of newPlannedTask
     * @throws SQLException
     * @throws IllegalArgumentException urls.length != paths.length
     * */
    public int addPlannedTask(URL[] urls, Path[] paths, LocalDateTime time) throws SQLException, IllegalArgumentException {
        // handle case if urls and paths lengths aren't equal
        if (urls.length != paths.length)
            throw new IllegalArgumentException(
                    "urls and files lenghts aren't equal (" + Integer.toString(urls.length)
                            + " and " + Integer.toString(paths.length) + ")"
            );

        // add new planned task to Tasks table
        Statement statement = _connection.createStatement();
        statement.executeUpdate(
                "INSERT INTO TASKS(SCHEDULED, TIME, DOWNLOADING) VALUES (1, " + timeString(time) + ", 0);"
        );
        _connection.commit();

        // get new task id
        int id = statement.executeQuery("SELECT last_insert_rowid() as id").getInt("id");

        // add all urls with path to urls table, bind them with new task id
        for (int index; index < urls.length; index++) {
            statement.executeQuery {
                "INSERT INTO URLS(ID, URL, PATH) VALUES (" + Integer.toString(id) + ", " +
                        urls[index].toString() + ", " + paths[index].toString() + ");"
            };
            _connection.commit();
        }

        statement.close();

        return id;
    }

    /**
     * @return list with all tasks in database
     * @throws SQLException
     * @throws MalformedURLException
     * */
    public List<DownloadTask> tasksList () throws SQLException, MalformedURLException {
        // get set of all tasks in database
        Statement statement = _connection.createStatement();
        ResultSet set = statement.executeQuery("SELECT * FROM TASKS;");

        // init list of download tasks
        List<DownloadTask> resultList = new LinkedList<>();
        while(set.next())
            if(set.getBoolean("SCHEDULED"))
                resultList.add(
                        new DownloadTask(
                                set.getInt("ID"),
                                getTaskUrls(set.getInt("ID")),
                                getTaskPaths(set.getInt("ID")),
                                parseTaskTime(set.getString("TIME"))
                        )
                );
            else
                resultList.add(
                        new DownloadTask(
                                set.getInt("ID"),
                                getTaskUrls(set.getInt("ID")),
                                getTaskPaths(set.getInt("ID"))
                        )
                );

        statement.close();
        return resultList;
    }
}
