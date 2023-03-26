package io.lazydog.javakillerfx;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavaProcessKiller extends Application {

    // A list view to display the java processes
    private ListView<String> listView;

    // A list to store the process ids and names
    private ObservableList<String> processList;

    @Override
    public void start(Stage primaryStage) {
        // Set the font size and scale factor for the application
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("glass.win.uiScale", "1.5");
        System.setProperty("prism.text", "t2k");

        // Create a list view and set its cell factory to allow editing
        listView = new ListView<>();
        listView.setCellFactory(TextFieldListCell.forListView());

        // Set the font size for the list view
        listView.setStyle("-fx-font-size: 20px;");

        // Create a list to store the process ids and names
        processList = FXCollections.observableArrayList();

        // Populate the list with the current java processes
        populateProcessList();

        // Set the list as the items of the list view
        listView.setItems(processList);

        // Create a button to refresh the list view
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> populateProcessList());

        // Create a button to kill the selected process
        Button killButton = new Button("Kill");
        killButton.setOnAction(e -> killSelectedProcess());

        // Set the style sheet for the buttons
//        refreshButton.getStylesheets().add(getClass().getResource("mac-style.css").toExternalForm());
//        killButton.getStylesheets().add(getClass().getResource("flat-style.css").toExternalForm());

        // Create a horizontal box to hold the buttons
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(refreshButton, killButton);

        // Create a vertical box to hold the list view and the buttons
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(listView, buttonBox);

        // Create a scene and set it to the stage
        Scene scene = new Scene(root, 800, 450);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Java Process Killer");
        primaryStage.show();
    }

    // A method to populate the process list with the current java processes
    private void populateProcessList() {
        // Clear the previous list
        processList.clear();

        try {
            // Execute the command "jps -l"
            Process process = Runtime.getRuntime().exec("jps -l");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Read each line of output and add it to the list
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // remove leading and trailing spaces
                if (!line.isEmpty()) { // skip empty lines
                    processList.add(line); // add the line to the list
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // A method to kill the selected process in the list view
    private void killSelectedProcess() {
        // Get the selected item in the list view
        String selectedItem = listView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) { // if there is a selection
            try {
                // Get the process id from the first token of the item
                String[] tokens = selectedItem.split("\\s+");
                String pid = tokens[0];

                // Execute the command "taskkill /F /PID pid"
                Process process = Runtime.getRuntime().exec("taskkill /F /PID " + pid);
                process.waitFor(); // wait for the process to terminate

                // Remove the item from the list view
                listView.getItems().remove(selectedItem);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}