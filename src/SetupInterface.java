import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class SetupInterface extends Application {

    @Override
    public void start(Stage primaryStage) {
        setup();

        File[] examList = new File("exams/").listFiles();
        ComboBox<File> box = new ComboBox<>();
        box.getItems().addAll(examList);

        Button btStart = new Button("开始考试");
        btStart.setOnAction(event -> {
            File file = box.getValue();
            Exam exam = new Exam();
            exam.start(file);
            primaryStage.close();
        });

        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.getChildren().addAll(box, btStart);
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setup() {

    }
}
