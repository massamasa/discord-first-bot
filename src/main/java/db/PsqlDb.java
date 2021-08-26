package db;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PsqlDb {
    private Connection conn;

    public PsqlDb(String postgresqladdress) {
        try {
            conn = DriverManager.getConnection(postgresqladdress);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean userExists(String user_id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT user_id FROM statistics WHERE user_id = ?");
            preparedStatement.setString(1, user_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addUser(String user_id, String username) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO statistics (user_id, username, score) VALUES (?, ?, ?)");
            preparedStatement.setString(1, user_id);
            preparedStatement.setString(2, username);
            preparedStatement.setInt(3, 1);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addOneToScore(String user_id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE statistics SET  score = (score + 1) WHERE user_id = ?");
            preparedStatement.setString(1, user_id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToTimes(String user_id, String username, Timestamp timestamp) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO times (user_id, username, time) VALUES (?, ?, ?)");
            preparedStatement.setString(1,user_id);
            preparedStatement.setString(2, username);
            preparedStatement.setTimestamp(3, timestamp);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getStatisticsList() {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM statistics ORDER BY score DESC");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                list.add(resultSet.getString(2) +":  "+ resultSet.getInt(3)+ "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<String> getTimesList() {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM times ORDER BY time DESC");
            ResultSet resultSet = preparedStatement.executeQuery();
            int laskuri = 0;
            while(resultSet.next() && laskuri != 7){
                list.add(resultSet.getString(2) +":  "+ resultSet.getTimestamp(3)+ "\n");
                laskuri++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addFirst(String user_id, String username, LocalDateTime currentTime) {
        if (!userExists(user_id)) {
            addUser(user_id, username);
        } else {
            addOneToScore(user_id);
        }
        addToTimes(user_id, username, Timestamp.valueOf(currentTime));
    }
}
