module com.example.finalfitnesstracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Export the necessary package for your application
    exports com.example.finalfitnesstracker;

    // Open the package containing ADD class to JavaFX base
    opens com.example.finalfitnesstracker.classes to javafx.base;
}
