package com.example.newgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main extends Application {

    private StickHero stickHero;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        Group group = new Group();

        // Load background image
        Image backgroundImage = new Image(new FileInputStream("img/background.png"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        group.getChildren().add(backgroundImageView);

        // Set up the main scene
        Scene mainScene = new Scene(group);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.setWidth(700);
        primaryStage.setHeight(700);
        primaryStage.setTitle("Stick Hero");

        // Set up the welcome stage
        Stage WelcomeStage = createStage(primaryStage, group);
        WelcomeStage.show();
    }

    private Stage createStage(Stage primaryStage, Group group) {
        Stage stage = new Stage();
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.setWidth(370);
        stage.setHeight(150);
        stage.setResizable(false);
        stage.setTitle("Welcome");

        Label label = new Label("Name:              ");
        TextField textField = new TextField();
        Button button = new Button("OK");

        hBox.getChildren().addAll(label, textField);
        vBox.getChildren().addAll(hBox, new Separator(), button);

        // Center the HBox and VBox
        hBox.setAlignment(javafx.geometry.Pos.CENTER);
        vBox.setAlignment(javafx.geometry.Pos.CENTER);

        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                button.fire(); // Simulate button click
            }
        });

        button.setOnAction(e -> {
            if (textField.getText() != null && !textField.getText().isEmpty()) {
                stage.close();
                e.consume();
                primaryStage.show();
                primaryStage.setOnCloseRequest(event -> System.exit(0));

                // Create a new StickHero instance
                stickHero = new StickHero(primaryStage.getScene(), group, textField.getText());
            }
        });

        return stage;
    }

    @Override
    public void stop() throws Exception {
        // This method is called when the application is shutting down
        if (stickHero != null) {
            Platform.runLater(() -> {
                ((Group) stickHero.getScene().getRoot()).getChildren().remove(stickHero.getGroup());
            });
        }
        super.stop();
    }
}
