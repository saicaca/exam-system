import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class SetupInterface extends Application {
    private File selectedFile;
    private VBox rightBox = new VBox(10);
    private Label lbTitle = new Label("标题：");
    private Label lbTime = new Label("考试时间：");
    private Label lbNum = new Label("题目数量：");

    @Override
    public void start(Stage primaryStage) {

        File[] examList = new File("exams/").listFiles();
        ObservableList<File> data = FXCollections.observableArrayList();
        ListView<File> listView = new ListView<>(data);

        data.addAll(examList);

        listView.setItems(data);
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<File>() {
            @Override
            public void changed(ObservableValue<? extends File> observable, File oldValue, File newValue) {
                selectedFile = newValue;
                setInfo(newValue);
            }
        });

        for (Label label: new Label[]{lbTitle, lbTime, lbNum}) {
            label.setFont(Font.font(16));
        }

        Button btStart = new Button("开始考试");
        btStart.setMinSize(80,40);
        btStart.setOnAction(event -> {
            Exam exam = new Exam();
            exam.start(selectedFile);
            primaryStage.close();
        });

        rightBox.getChildren().addAll(lbTitle, lbTime, lbNum, btStart);

        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(10,10,10,10));
        hBox.getChildren().addAll(listView, rightBox);
        Scene scene = new Scene(hBox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setInfo(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String[] info = reader.readLine().split("#");
            lbTitle.setText("标题：" + info[0]);
            lbTime.setText("考试时间：" + info[1] + "分钟");
            String str;
            int numberOfQuestion = 0;
            while ((str = reader.readLine()) != null)
                numberOfQuestion++;
            lbNum.setText("题目数量：" + numberOfQuestion);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}