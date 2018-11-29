import java.util.ArrayList;

public class Question {
    String ques;
    ArrayList<String> selection = new ArrayList<>();
    ArrayList<Integer> key = new ArrayList<>();
    ArrayList<Integer> answer = new ArrayList<>();

    Question(String str) {
        String[] list = str.split("|");
        ques = list[0];
        for (int i = 1; i < list.length; i++) {
            if (list[i].charAt(0) == '^') {
                key.add(i-1);
                selection.add(list[i].substring(1));
            }
            else selection.add(list[i]);
        }
    }
}
