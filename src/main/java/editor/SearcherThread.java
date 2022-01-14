package editor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearcherThread extends SwingWorker<String, Point> {
    JTextArea editorTextArea;
    String toFind;
    boolean isRegex;
    ArrayList<Point> foundIndexes;
    static SearcherThread instance;

    private SearcherThread(JTextArea editorTextArea, String toFind, boolean isRegex) {
        this.editorTextArea = editorTextArea;
        this.toFind = toFind;
        this.isRegex = isRegex;
        foundIndexes = new ArrayList<>();
    }

    @Override
    protected String doInBackground() {
//        Highlighter highlighter = editorTextArea.getHighlighter();
//        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.PINK);
        Pattern toLook;
        if (isRegex) {
            toLook = Pattern.compile(toFind);
        } else {
            toLook = Pattern.compile(toFind, Pattern.LITERAL);
        }
        Matcher matcher = toLook.matcher(editorTextArea.getText());
        System.out.println(editorTextArea.getText());
        System.out.println(toFind);
        System.out.println(isRegex);
        int numFound = 0;
        while (matcher.find()) {
            System.out.println(++numFound);
            int x = matcher.start();
            int y = matcher.end();
//            System.out.println("x = " + x);
//            System.out.println("y = " + y);
            Point point = new Point(x, y);
            foundIndexes.add(point);

        }
        System.out.println(numFound);
//        System.out.println("Search completed!");
//        foundIndexes.forEach(point -> System.out.printf("Point: (%s, %s)", point.getX(), point.getY()));
        return null;
    }

    public static SearcherThread getInstance(JTextArea textArea, String toFind, boolean isRegex) {

        instance = new SearcherThread(textArea, toFind, isRegex);

        return instance;

    }
}
