package com.example.finalfitnesstracker;

import com.example.finalfitnesstracker.classes.ADD;
import com.example.finalfitnesstracker.dataBase.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AddDataController {
    @FXML
    public Button dash,add,update,del;
    @FXML
    public ChoiceBox<String> workout;
    @FXML
    public DatePicker date;
    @FXML
    public TextField duration;
    @FXML
    public  TextField calorie;

// Table
@FXML
public TableView<ADD> tdata;

//TableColumn
    @FXML
    public TableColumn<ADD, Integer> colId;
    @FXML
    public TableColumn<ADD, String> colDate;
    @FXML
    public TableColumn<ADD, String> colWorkout;
    @FXML
    public TableColumn<ADD, Float> colDuration;
    @FXML
    public TableColumn<ADD, Integer> colCalorie;

//    Database
    DatabaseHandler dbHandler= new DatabaseHandler();
    ADD workoutData = new ADD();


    @FXML
    public void initialize() {
        // Initialize the ChoiceBox with items
        workout.setItems(FXCollections.observableArrayList("Running", "Cycling", "Other"));
        // Set default selection
        workout.getSelectionModel().select("Running");

        // Set today's date
        date.setValue(LocalDate.now());
        ShowTableData();
        update.setDisable(true);


    }


    public void ShowTableData() {
        // Fetch data from the database
        ObservableList<ADD> records = dbHandler.ViewData();


//        // Print each record to the console
//        System.out.println("Data from Database:");
//        for (ADD record : records) {
//            System.out.println("ID: " + record.getId());
//            System.out.println("Date: " + record.getDate());
//            System.out.println("Workout: " + record.getWorkout());
//            System.out.println("Duration: " + record.getDuration());
//            System.out.println("Calories: " + record.getCalorie());
//            System.out.println("------------------------------");
//        }

//         Set the data in the TableView
        tdata.setItems(records);


        // Map the TableColumns to the ADD class properties
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colWorkout.setCellValueFactory(new PropertyValueFactory<>("workout"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colCalorie.setCellValueFactory(new PropertyValueFactory<>("calorie"));
    }

    @FXML
    public void SelectData(MouseEvent event) {
        ADD selectedData = tdata.getSelectionModel().getSelectedItem();

        // Check if any data is selected
        if (selectedData != null) {
            // Create an alert with Update, Delete, and Cancel options
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Fitness");
            alert.setHeaderText("You have selected the following record:");
            alert.setContentText(
                    "ID: " + selectedData.getId() + "\n" +
                            "Date: " + selectedData.getDate() + "\n" +
                            "Workout: " + selectedData.getWorkout() + "\n" +
                            "Duration: " + selectedData.getDuration() + "\n" +
                            "Calories: " + selectedData.getCalorie() + "\n\n" +
                            "What would you like to do?"
            );

            // Add buttons for Update, Delete, and Cancel
            ButtonType updateButton = new ButtonType("Update");
            ButtonType deleteButton = new ButtonType("Delete");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(updateButton, deleteButton, cancelButton);

            // Show the alert and wait for a response
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent()) {
                if (result.get() == updateButton) {

                    String originalDate = selectedData.getDate();
                    DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("dd:MM:yyyy");
                    LocalDate dateObject = LocalDate.parse(originalDate, originalFormatter);
                    DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("MM:dd:yyyy");
                    String formattedDate = dateObject.format(targetFormatter);

                    // Handle update action
                    workout.setValue(selectedData.getWorkout());
                 date.setValue(dateObject);
                    duration.setText(String.valueOf(selectedData.getDuration()));
                    calorie.setText(String.valueOf(selectedData.getCalorie()));

                    update.setDisable(false);

                } else if (result.get() == deleteButton) {
                    // Handle delete action

                    dbHandler.deleteRecord(selectedData.getId());
                    ShowTableData();  showAlert("Record Deleted successfully!");

                } // Cancel is handled automatically by not doing anything
            }
        } else {
            // Show a warning if no data is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a record from the table.");
            alert.showAndWait();
        }
    }

    // Update
    @FXML
    public void handleUpdate() {
        // Get the selected record from the TableView
        ADD selectedRecord = tdata.getSelectionModel().getSelectedItem();

        // Check if a record is selected
        if (selectedRecord == null) {
            showAlert("No record selected. Please select a record to update.");
            return;
        }

        // Retrieve updated values from the form
        String selectedWorkout = workout.getSelectionModel().getSelectedItem();
        String workoutDuration = duration.getText();
        String workoutCalories = calorie.getText();
        LocalDate workoutDate = date.getValue();

        // Format the date to the required format (dd:MM:yyyy)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy");
        String formattedDate = workoutDate.format(formatter);

        // Validate that the necessary fields are not empty
        if (workoutDuration.isEmpty() || workoutCalories.isEmpty()) {
            showAlert("Please enter required values for Duration and Calories.");
            return;
        }

        // Set the updated values to the selected record
        selectedRecord.setWorkout(selectedWorkout);
        selectedRecord.setDuration(Float.parseFloat(workoutDuration));
        selectedRecord.setCalorie(Integer.parseInt(workoutCalories));
        selectedRecord.setDate(formattedDate);

        // Call the updateRecord method to update the data in the database
        dbHandler.updateRecord(selectedRecord);


        ShowTableData();

        // Clear the input fields after successful update
//        workout.getSelectionModel().clearSelection();
        duration.setText("");
        calorie.setText("");
        date.setValue(LocalDate.now());

        // Show success alert
        showAlert("Record updated successfully!");
        update.setDisable(true);

    }




    @FXML
    public void handleAdd() {
        // Retrieve user input
        String selectedWorkout = workout.getSelectionModel().getSelectedItem();
        String workoutDuration = duration.getText();
        String workoutCalories = calorie.getText();
        LocalDate workoutDate = date.getValue();

//        Formate date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy");
        String formattedDate = workoutDate.format(formatter);


        // Check for required fields
        if (workoutDuration.isEmpty() || workoutCalories.isEmpty()) {
            showAlert("Please enter required values for Duration and Calories.");
            return;
        }
//        class


        workoutData.setDate(formattedDate);
        workoutData.setWorkout(selectedWorkout);
        workoutData.setDuration(Float.parseFloat(workoutDuration));
        workoutData.setCalorie(Integer.parseInt(workoutCalories));



//insert to db
        try {
            dbHandler.AddRecord(workoutData); // Pass the ADD object
            ShowTableData();
            showAlert("Data added successfully!");
            duration.setText("");
            calorie.setText("");
        } catch (Exception e) {
            showAlert("Error adding data: " + e.getMessage()); // Show error message
        }


//        System.out.println("Adding Workout:");
//        System.out.println("Date"+formattedDate);
//        System.out.println("Workout Type: " + selectedWorkout);
//        System.out.println("Duration: " + Float.parseFloat(workoutDuration));
//        System.out.println("Calories: " + workoutCalories);


        // Here you can add logic to store this data in a database or a list
        // Example: myWorkoutList.add(new Workout(selectedWorkout, workoutDuration, workoutCalories, workoutDate));
    }



    // Method to show an alert dialog
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
@FXML
public void handleClear() {
    duration.setText("");
    calorie.setText("");
}

    public void handleButton () throws  Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();
        Stage window = (Stage) dash.getScene().getWindow();
        window.setScene(new Scene(root, 1024, 768));
        window.show();

    }
}
