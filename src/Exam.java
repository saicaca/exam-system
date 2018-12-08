import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class Exam {
    private File file;
    private String title;
    private ArrayList<Question> questions = new ArrayList<>();
    private boolean isFinished;
    private QuesPane quesPane;
    private BorderPane rightBox;
    private qtButton[] btList;
    private Button btFinish;

    private ResultPane resultPane;
    private int correctCount;

    private TimerTask timeDisplayTask;
    private int currentSecond;

    Stage stage;


    public void start(File file) {
        this.file = file;
        int totalSecond = 0;

        // 读取试题
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String[] info = reader.readLine().split("#");
            title = info[0];    // 读入考试标题
            totalSecond = 60 * Integer.parseInt(info[1]);   // 读入考试时间

            String quesStr;
            while ((quesStr = reader.readLine()) != null)
                questions.add(new Question(quesStr));
            Collections.shuffle(questions);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


        // 倒计时
        Label lbTimer = new Label();
        lbTimer.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        lbTimer.setTextFill(Color.WHITE);

        currentSecond = totalSecond;

        timeDisplayTask = new TimerTask() {
            @Override
            public void run() {
                currentSecond--;
                Platform.runLater(() -> {
                    if (currentSecond == 0)
                        finish();
                    lbTimer.setText(String.format("%02d:", currentSecond / 60) + String.format("%02d", currentSecond % 60));
                });
            }
        };
        Timer displayTimer = new Timer();
        displayTimer.schedule(timeDisplayTask, 0, 1000);

        StackPane timerPane = new StackPane(lbTimer);
        timerPane.setStyle("-fx-background-color: #3c4043");
        timerPane.setMinHeight(60);

        // 显示考试基本信息
        Label lbTitle = new Label(title);
        lbTitle.setTextFill(Color.WHITE);
        lbTitle.setFont(Font.font("微软雅黑", FontWeight.BOLD, 24));
        VBox infoBox = new VBox(4);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setMinHeight(120);
        infoBox.getChildren().add(lbTitle);
        infoBox.setStyle("-fx-background-color: #3c4043");

        // 创建题目选择按钮
        FlowPane buttonPane = new FlowPane();
        buttonPane.setPadding(new Insets(10,0,0,0));
        buttonPane.setMinWidth(5 * (qtButton.size+2));
        buttonPane.setMaxWidth(5 * (qtButton.size+2));
        buttonPane.setHgap(2);
        buttonPane.setVgap(3);

        btList = new qtButton[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            qtButton button = new qtButton(i);
            btList[i] = button;
            buttonPane.getChildren().add(button);
        }

        // 提交按钮
        btFinish = new Button("提交");
        btFinish.setMinSize(100, 60);
        btFinish.setFont(Font.font(16));
        btFinish.setId("button-blue");

        // 提交前弹出确认窗口
        btFinish.setOnAction(event -> {
            Stage finStage = new Stage();

            Label lbFinish = new Label("确定要交卷吗？");
            lbFinish.setFont(Font.font(16));
            if (!isComplete())
                lbFinish.setText("还有题目未完成，确定要交卷吗？");

            Button btOk = new Button("确定");
            Button btCancel = new Button("取消");
            for (Button button: new Button[]{btOk, btCancel}) {
                button.setMinSize(60, 30);
            }
            btOk.setOnAction(event1 -> {
                finish();
                finStage.close();
            });
            btCancel.setOnAction(event1 -> {
                finStage.close();
            });

            HBox finBtBox = new HBox(10);
            finBtBox.setAlignment(Pos.CENTER);
            finBtBox.getChildren().addAll(btOk, btCancel);
            VBox finBox = new VBox(10);
            finBox.setPadding(new Insets(20,20,20,20));
            finBox.setAlignment(Pos.CENTER);
            finBox.getChildren().addAll(lbFinish, finBtBox);
            Scene finScene = new Scene(finBox);
            finStage.setScene(finScene);
            finStage.show();
        });

        // 右底栏
        BorderPane rightBottomBar = new BorderPane();
        rightBottomBar.setStyle("-fx-background-color: #E0E0E0");
        rightBottomBar.setRight(btFinish);

        quesPane = new QuesPane(questions.get(0));

        // 布局排版
        BorderPane leftBox = new BorderPane();
        leftBox.setStyle("-fx-background-color: #303030");
        leftBox.setTop(infoBox);
        leftBox.setCenter(buttonPane);
        leftBox.setBottom(timerPane);

        rightBox = new BorderPane();
        rightBox.setCenter(quesPane);
        rightBox.setBottom(rightBottomBar);

        HBox hBox = new HBox();
        hBox.setMinSize(800, 600);
        hBox.setPrefSize(800, 600);
        hBox.getChildren().addAll(leftBox, rightBox);

        Scene scene = new Scene(hBox);
        scene.getStylesheets().add(getClass().getResource("skin.css").toExternalForm());   // 设定 css

        rightBox.minWidthProperty().bind(scene.widthProperty().subtract(leftBox.widthProperty()));

        stage = new Stage();

        stage.setScene(scene);
        stage.show();
    }


    // 结束考试
    private void finish() {
        isFinished = true;
        correctCount = 0;

        timeDisplayTask.cancel();

        for (Question question: questions) {
            if (question.isCorrect())
                correctCount++;
        }
        resultPane = new ResultPane();
        rightBox.setCenter(resultPane);

        //
        for (qtButton bt: btList) {
            bt.refresh();
        }

        btFinish.setText("查看成绩");
        btFinish.setOnAction(event -> rightBox.setCenter(resultPane));
    }


    private boolean isComplete() {
        for (Question question: questions) {
            if (question.isEmpty())
                return false;
        }
        return true;
    }


    // 成绩面板
    class ResultPane extends VBox {
        ResultPane() {
            setSpacing(10);
            setAlignment(Pos.CENTER);
            Label lbEnd = new Label("考试结束");
            lbEnd.setFont(Font.font("微软雅黑", FontWeight.BOLD, 30));
            Label lbCorrect = new Label("答对题数：" + correctCount);
            lbCorrect.setFont(Font.font("微软雅黑", FontWeight.BOLD, 30));
            Button btRestart = new Button("重新开始");
            btRestart.setId("button-blue");
            btRestart.setMinSize(80, 60);
            btRestart.setOnAction(event -> {
                Exam newExam = new Exam();
                newExam.start(file);
                stage.close();
            });
            getChildren().addAll(lbEnd, lbCorrect, btRestart);
        }
    }


    // 试题面板
    class QuesPane extends VBox {

        QuesPane(Question question) {
            boolean[] key = question.getKey();
            boolean[] answer = question.getAnswer();

            setMinSize(400, 400);
            setStyle("-fx-background-color: #F5F5F5");

            // 显示题目文本
            Label lbQuestion = new Label("Q" + (questions.indexOf(question) + 1) + "：" + question.getQues());
            lbQuestion.setFont(Font.font("微软雅黑",FontWeight.BOLD,24));
            lbQuestion.setWrapText(true);
            StackPane titlePane = new StackPane();
            titlePane.setMinHeight(120);
            titlePane.setAlignment(Pos.BOTTOM_LEFT);
            titlePane.setPadding(new Insets(20,20,20,20));
            titlePane.setStyle("-fx-background-color: #E0E0E0");
            titlePane.getChildren().add(lbQuestion);
            getChildren().add(titlePane);

            // 创建选项
            VBox selectionPane = new VBox(20);
            getChildren().add(selectionPane);
            selectionPane.setAlignment(Pos.TOP_LEFT);
            selectionPane.setPadding(new Insets(20,20,20,20));
            ArrayList<String> selectionList = question.getSelection();

            if (isFinished) {
                for (int i = 0; i < selectionList.size(); i++) {
                    Label lb;
                    if (answer[i] && key[i]) {
                        lb  = new Label(selectionList.get(i) + " ✔");
                        lb.setTextFill(Color.valueOf("4caf50"));
                    }
                    else if (answer[i] && !key[i]) {
                        lb  = new Label(selectionList.get(i) + " ✖");
                        lb.setTextFill(Color.valueOf("e91e63"));
                    }
                    else if (!answer[i] && key[i]) {
                        lb  = new Label(selectionList.get(i) + " ✔");
                        lb.setTextFill(Color.valueOf("2196f3"));
                    }
                    else
                        lb = new Label(selectionList.get(i));
                    lb.setFont(Font.font(20));

                    selectionPane.getChildren().add(lb);
                }
            }
            else {
                if (question.isMultiple()) {
                    // 是多选题
                    for (int i = 0; i < selectionList.size(); i++) {
                        final int pos = i;
                        CheckBox cb = new CheckBox(selectionList.get(pos));
                        cb.setId("checkBox");
                        cb.setFont(Font.font(20));
                        cb.setSelected(question.getAnswer()[pos]);    // 恢复选择状态
                        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                question.getAnswer()[pos] = newValue;
                                btList[questions.indexOf(question)].refresh();
                            }
                        });
                        selectionPane.getChildren().add(cb);
                    }
                }
                else {
                    // 是单选题
                    ToggleGroup tg = new ToggleGroup();
                    for (int i = 0; i < selectionList.size(); i++) {
                        final int pos = i;
                        RadioButton rb = new RadioButton(selectionList.get(i));
                        rb.setToggleGroup(tg);
                        rb.setId("radioBox");
                        rb.setFont(Font.font(20));
                        rb.setSelected(question.getAnswer()[i]);
                        rb.selectedProperty().addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                for (int j = 0; j < question.getAnswer().length; j++)
                                    question.getAnswer()[j] = false;    // 把其他选项设为 false
                                question.getAnswer()[pos] = newValue;
                                btList[questions.indexOf(question)].refresh();
                            }
                        });
                        selectionPane.getChildren().add(rb);
                    }
                }
            }

            // 创建上下切题按钮
            int index = questions.indexOf(question);
            Button[] btList = new Button[2];
            HBox btBox = new HBox(40);
            btBox.setAlignment(Pos.CENTER);
            if (index != 0) {
                Button btPrev = new Button("上一题");
                btBox.getChildren().add(btPrev);
                btPrev.setMinSize(60, 40);
                btPrev.setId("button-blue");
                btPrev.setOnAction(event -> {
                    quesPane = new QuesPane(questions.get(index-1));
                    rightBox.setCenter(quesPane);
                });
            }
            if (index != questions.size() - 1) {
                Button btNext = new Button("下一题");
                btBox.getChildren().add(btNext);
                btNext.setMinSize(60, 40);
                btNext.setId("button-blue");
                btNext.setOnAction(event -> {
                    quesPane = new QuesPane(questions.get(index+1));
                    rightBox.setCenter(quesPane);
                });
            }
            selectionPane.getChildren().add(btBox);
        }
    }


    // 题目选择按钮
    class qtButton extends Button {
        final static int size = 48;
        Question question;

        qtButton(int num) {
            setText(""+(num+1));
            setMinSize(size,size);
            question = questions.get(num);
            setOnAction(event -> {
                quesPane = new QuesPane(question);
                rightBox.setCenter(quesPane);
            });
            setFont(Font.font(16));
            setId("button");
        }

        public void refresh() {
            if (isFinished && question.isCorrect())
                setId("button-blue");
            else if (isFinished && !question.isCorrect())
                setId("button-red");
            else if (!isFinished && !question.isEmpty())
                setId("button-blue");
            else
                setId("button");
        }
    }
}