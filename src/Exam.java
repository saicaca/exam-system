import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class Exam extends Application {
    private String title;
    private int time;
    private ArrayList<Question> questions = new ArrayList<>();
    private boolean isFinished;
    private QuesPane quesPane;
    private HBox hBox;

    @Override
    public void start(Stage stage) {
        // 读取试题
        try {
            File file = new File("test.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String[] info = reader.readLine().split("#");
            title = info[0];
            time = Integer.parseInt(info[1]);

            String quesStr;
            int number = 0;
            while ((quesStr = reader.readLine()) != null)
                questions.add(new Question(quesStr));
            Collections.shuffle(questions);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        // 显示考试基本信息
        Label lbTitle = new Label(title);
        lbTitle.setFont(Font.font(20));
        lbTitle.setAlignment(Pos.CENTER);

        // 创建题目选择按钮
        FlowPane buttonPane = new FlowPane();
        buttonPane.setMinWidth(5 * QuesBt.size);
        buttonPane.setMaxWidth(5 * QuesBt.size);

        QuesBt[] btList = new QuesBt[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            QuesBt button = new QuesBt(i);
            buttonPane.getChildren().add(button);
        }

        quesPane = new QuesPane(questions.get(0));

        // 布局排版
        VBox leftBox = new VBox();
        leftBox.getChildren().addAll(lbTitle, buttonPane);

        hBox = new HBox();
        hBox.getChildren().addAll(leftBox, quesPane);

        Scene scene = new Scene(hBox);
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    // 试题面板
    class QuesPane extends VBox {
        QuesPane(Question question) {
            setPadding(new Insets(20,20,20,20));
            Label lbQuestion = new Label(question.getQues());
            lbQuestion.setFont(Font.font(20));
            getChildren().add(lbQuestion);

            ArrayList<String> selectionList = question.getSelection();
            for (int i = 0; i < selectionList.size(); i++) {
                final int pos = i;
                CheckBox cb = new CheckBox(selectionList.get(pos));
                cb.setSelected(question.getAnswer()[pos]);    // 恢复选择状态
                cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        question.getAnswer()[pos] = newValue;
                    }
                });
                getChildren().add(cb);
            }
        }
    }

    // 题目选择按钮
    class QuesBt extends Button {
        final static int size = 50;
        Question question;
        QuesBt(int num) {
            setText(""+(num+1));
            setMinSize(size,size);
            question = questions.get(num);
            setOnAction(event -> {
                hBox.getChildren().remove(quesPane);
                quesPane = new QuesPane(question);
                hBox.getChildren().add(quesPane);
            });
        }
    }
}

