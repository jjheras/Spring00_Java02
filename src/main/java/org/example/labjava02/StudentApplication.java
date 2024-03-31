package org.example.labjava02;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StudentApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(StudentApplication.class.getResource("student-init.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 200);
        stage.setTitle("Estudiantes Qualentum");
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void init() throws Exception {
        System.out.println("init() method: "+Thread.currentThread().getName());
    }
    @Override
    public void stop() throws Exception {
        System.out.println("stop() method: "+Thread.currentThread().getName());
    }
    public static void main(String[] args) {
        launch();
    }
}
