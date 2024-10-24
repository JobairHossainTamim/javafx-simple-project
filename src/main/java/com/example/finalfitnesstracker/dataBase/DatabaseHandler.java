package com.example.finalfitnesstracker.dataBase;

import com.example.finalfitnesstracker.classes.ADD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DatabaseHandler extends Configs {
    private Connection dbConnection;
    private static Statement statement;

    public Connection getDbConnection() throws SQLException {
        if (dbConnection == null || dbConnection.isClosed()) {
            String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?useSSL=false"+"&allowPublicKeyRetrieval=true"; // You can modify SSL settings as needed
            dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPassword);
            System.out.println(dbConnection != null ? "Connection Established" : "Connection Not Established");
        }
        return dbConnection;
    }

    public static Statement getStatement() throws SQLException {
        if (statement == null || statement.isClosed()) {
            Connection conn = new DatabaseHandler().getDbConnection();
            statement = conn.createStatement();
        }
        return statement;
    }

    public void AddRecord(ADD workoutData) {
        String insertQuery = "INSERT INTO record (date, workout, duration, calorie) VALUES (?, ?, ?, ?)";

        try (Connection conn = getDbConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            // Set the values for the placeholders
            pstmt.setString(1, workoutData.getDate());
            pstmt.setString(2, workoutData.getWorkout());
            pstmt.setFloat(3, workoutData.getDuration());
            pstmt.setInt(4, workoutData.getCalorie());


            // Insert Command
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Record added successfully!");
            } else {
                System.out.println("Failed to add record.");
            }

        } catch (SQLException e) {
            System.err.println("Error adding record: " + e.getMessage());
        }
    }

    public ObservableList<ADD> ViewData() {
        ObservableList<ADD> recordList = FXCollections.observableArrayList();
        String query = "SELECT * FROM record ORDER BY id DESC";

        try (Connection conn = getDbConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Iterate through the result set and create ADD objects
            while (rs.next()) {
                ADD record = new ADD();
                record.setId(rs.getInt("id")); // Column name from database
                record.setDate(rs.getString("date"));
                record.setWorkout(rs.getString("workout"));
                record.setDuration(rs.getFloat("duration"));
                record.setCalorie(rs.getInt("calorie"));

                // Add the ADD object to the list
                recordList.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching data: " + e.getMessage());
        }

        return recordList; // Return the list of records
    }


    public void updateRecord(ADD workoutData) {
        String updateQuery = "UPDATE record SET date = ?, workout = ?, duration = ?, calorie = ? WHERE id = ?";

        try (Connection conn = getDbConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            // Set the values for the placeholders
            pstmt.setString(1, workoutData.getDate());
            pstmt.setString(2, workoutData.getWorkout());
            pstmt.setFloat(3, workoutData.getDuration());
            pstmt.setInt(4, workoutData.getCalorie());
            pstmt.setInt(5, workoutData.getId());  // This is the ID of the record to update

            // Execute the update command
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Record updated successfully!");
            } else {
                System.out.println("Failed to update record.");
            }

        } catch (SQLException e) {
            System.err.println("Error updating record: " + e.getMessage());
        }
    }


    public void deleteRecord(int id) {
        String deleteQuery = "DELETE FROM record WHERE id = ?";

        try (Connection conn = getDbConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            // Set the id value for the placeholder
            pstmt.setInt(1, id);

            // Execute the delete command
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Record deleted successfully!");
            } else {
                System.out.println("Failed to delete record.");
            }

        } catch (SQLException e) {
            System.err.println("Error deleting record: " + e.getMessage());
        }
    }



    public ObservableList<ADD> getLast7DaysData() {
        ObservableList<ADD> recordList = FXCollections.observableArrayList();
        String query = "SELECT * FROM record WHERE STR_TO_DATE(date, '%d:%m:%Y') >= CURDATE() - INTERVAL 7 DAY ORDER BY STR_TO_DATE(date, '%d:%m:%Y') DESC";

        try (Connection conn = getDbConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Iterate through the result set and create ADD objects
            while (rs.next()) {
                ADD record = new ADD();
                record.setId(rs.getInt("id"));
                record.setDate(rs.getString("date"));
                record.setWorkout(rs.getString("workout"));
                record.setDuration(rs.getFloat("duration"));
                record.setCalorie(rs.getInt("calorie"));

                // Add the ADD object to the list
                recordList.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching data for last 7 days: " + e.getMessage());
        }

        return recordList; // Return the list of records
    }

    public ObservableList<ADD> getLast30DaysData() {
        ObservableList<ADD> recordList = FXCollections.observableArrayList();
        String query = "SELECT * FROM record WHERE STR_TO_DATE(date, '%d:%m:%Y') >= CURDATE() - INTERVAL 30 DAY ORDER BY STR_TO_DATE(date, '%d:%m:%Y') DESC";

        try (Connection conn = getDbConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Iterate through the result set and create ADD objects
            while (rs.next()) {
                ADD record = new ADD();
                record.setId(rs.getInt("id"));
                record.setDate(rs.getString("date"));
                record.setWorkout(rs.getString("workout"));
                record.setDuration(rs.getFloat("duration"));
                record.setCalorie(rs.getInt("calorie"));

                // Add the ADD object to the list
                recordList.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching data for last 30 days: " + e.getMessage());
        }

        return recordList;
    }

    public double[] getAverageDurationAndCaloriesLast7Days() {
        String query = "SELECT AVG(duration) AS avgDuration, AVG(calorie) AS avgCalorie FROM record WHERE STR_TO_DATE(date, '%d:%m:%Y') >= CURDATE() - INTERVAL 7 DAY";
        double[] averages = new double[2];

        try (Connection conn = getDbConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                averages[0] = rs.getDouble("avgDuration");
                averages[1] = rs.getDouble("avgCalorie");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating averages for last 7 days: " + e.getMessage());
        }

        return averages;
    }

    public double[] getAverageDurationAndCaloriesLast30Days() {
        String query = "SELECT AVG(duration) AS avgDuration, AVG(calorie) AS avgCalorie FROM record WHERE STR_TO_DATE(date, '%d:%m:%Y') >= CURDATE() - INTERVAL 30 DAY";
        double[] averages = new double[2];

        try (Connection conn = getDbConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                averages[0] = rs.getDouble("avgDuration");
                averages[1] = rs.getDouble("avgCalorie");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating averages for last 30 days: " + e.getMessage());
        }

        return averages;
    }

}