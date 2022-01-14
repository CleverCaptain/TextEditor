package editor;

import javax.swing.*;

public class AppRunner {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TextEditor::new);
    }
}
