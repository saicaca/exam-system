import javafx.scene.control.Button;
import java.util.ArrayList;

public class Question {
    private String ques;
    private ArrayList<String> selection = new ArrayList<>();
    private ArrayList<Integer> key = new ArrayList<>();
    private ArrayList<Integer> answer = new ArrayList<>();

    Question(String str) {
        String[] list = str.split("#");
        ques = list[0];
        for (int i = 1; i < list.length; i++) {
            if (list[i].charAt(0) == '^') {
                key.add(i-1);
                selection.add(list[i].substring(1));
            }
            else selection.add(list[i]);
        }
    }

    public String getQues() {
        return ques;
    }

    public ArrayList<String> getSelection() {
        return selection;
    }

    public ArrayList<Integer> getKey() {
        return key;
    }

    public ArrayList<Integer> getAnswer() {
        return answer;
    }
}