package com.example.finalfitnesstracker;

import com.example.finalfitnesstracker.classes.ADD;
import com.example.finalfitnesstracker.dataBase.DatabaseHandler;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class HelloController {
    @FXML
   public Button add;
@FXML
public RadioButton week,month;

@FXML

public Label avgCalorie,avgDuration;


@FXML
public TableView<ADD> view;

    //TableColumn
    @FXML
    public TableColumn<ADD, Integer> VId;
    @FXML
    public TableColumn<ADD, String> VDate;
    @FXML
    public TableColumn<ADD, String> VWorkout;
    @FXML
    public TableColumn<ADD, Float> VDuration;
    @FXML
    public TableColumn<ADD, Integer> VCalorie;



    DatabaseHandler dbHandler= new DatabaseHandler();
    ADD workoutData = new ADD();
    double[] avgLast7Days = dbHandler.getAverageDurationAndCaloriesLast7Days();
    double[] avgLast30Days = dbHandler.getAverageDurationAndCaloriesLast30Days();

    @FXML
    public void initialize() {
        week.setSelected(true);
        setupTableColumns();
        showTableData();
    }

    private void setupTableColumns() {
        // Map the TableColumns to the ADD class properties
        VId.setCellValueFactory(new PropertyValueFactory<>("id"));
        VDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        VWorkout.setCellValueFactory(new PropertyValueFactory<>("workout"));
        VDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        VCalorie.setCellValueFactory(new PropertyValueFactory<>("calorie"));
    }

    public void showTableData() {
        if (week.isSelected()) {
            ShowWeekData();


            // Call to show data for the last 7 days
        } else if (month.isSelected()) {
            ShowMonthData(); // Call to show data for the last 30 days
        } else {
            System.out.println("Something went wrong");
        }
    }

    @FXML
    public void SetSelectedMonth() {
        week.setSelected(false);
        month.setSelected(true);
        showTableData(); // Update table data on selection change
    }

    @FXML
    public void SetSelectedWeek() {
        week.setSelected(true);
        month.setSelected(false);
        showTableData(); // Update table data on selection change
    }

    public void ShowWeekData() {
        ObservableList<ADD> records = dbHandler.getLast7DaysData();
        view.setItems(records);
        avgCalorie.setText(+avgLast30Days[1]+" "+"cal");
        avgDuration.setText(+avgLast30Days[0]+" "+"min");
    }

    public void ShowMonthData() {
        ObservableList<ADD> records = dbHandler.getLast30DaysData();
        view.setItems(records);
        avgCalorie.setText(+avgLast30Days[1]+" "+"cal");
        avgDuration.setText(+avgLast30Days[0]+" "+"min");
    }

    public void handleButton() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("add-data.fxml"));
        Parent root = loader.load();
        Stage window = (Stage) add.getScene().getWindow();
        window.setScene(new Scene(root, 1024, 768));
        window.show();
    }




}