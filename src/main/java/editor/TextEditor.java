package editor;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;

public class TextEditor extends JFrame {

    private JTextArea editorTextArea;
    private JPanel toolsPanel;
    private JButton saveButton;
    private JButton openButton;
    private JTextField searchField;
    private JButton searchButton;
    private JButton previousMatchButton;
    private JButton nextMatchButton;
    private JCheckBox regexCheckbox;
    private JScrollPane horizontalTextScroller;
    private JScrollPane verticalTextScroller;
    private JMenuBar topMenuBar;
    private JMenu fileMenu;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem exitItem;
    private JMenu searchMenu;
    private JMenuItem startSearchItem;
    private JMenuItem previousMatchItem;
    private JMenuItem nextMatchItem;
    private JMenuItem useRegexItem;
    private static ListIterator<Point> findIterator;
    private JFileChooser fileChooser;

    public TextEditor() throws HeadlessException {
        super("Notepad");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setName("FileChooser");
        add(fileChooser);

        String fileLocation = "src\\main\\resources\\";
        ActionListener openActionListener = e -> {
            fileChooser.setDialogTitle("Choose a directory to save your file: ");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
            fileChooser.setFileFilter(filter);
            int returnValue = fileChooser.showOpenDialog(null);
            File selectedFile;
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                try (FileInputStream inputStream = new FileInputStream(selectedFile)) {
                    byte[] readBytes = inputStream.readAllBytes();
                    String readString = new String(readBytes, StandardCharsets.UTF_8);
                    editorTextArea.setText(readString);
                } catch (NullPointerException nullPointerException) {
                    System.out.println("input filename to load");
                } catch (FileNotFoundException fileNotFoundException) {
                    editorTextArea.setText("");
                    System.out.println("file specified not found! Try again");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        };

        ActionListener saveActionListener = e -> {
            fileChooser.setDialogTitle("Choose a directory to save your file: ");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
            fileChooser.setFileFilter(filter);
            int returnValue = fileChooser.showOpenDialog(null);
            File selectedFile;
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                try (FileOutputStream fileOutputStream = new FileOutputStream(selectedFile)) {
                    byte[] toWrite = editorTextArea.getText().getBytes();
                    fileOutputStream.write(toWrite);
                } catch (FileNotFoundException fileNotFoundException) {
                    System.out.println("Input file not created/found Error!");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        };

        ActionListener searcherListener = e -> startSearch();

        ActionListener nextListener = e -> highlightNext();

        ActionListener previousFindListener = e -> highlightLast();

        toolsPanel = new JPanel();
        toolsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        add(toolsPanel, BorderLayout.PAGE_START);

        ImageIcon saveIcon = new ImageIcon(fileLocation + "saveIcon.png");
        saveButton = new JButton(saveIcon);
        saveButton.setName("SaveButton");
        saveButton.addActionListener(saveActionListener);
        toolsPanel.add(saveButton);

        ImageIcon openIcon = new ImageIcon(fileLocation + "openIcon.png");
        openButton = new JButton(openIcon);
        openButton.setName("OpenButton");
        openButton.addActionListener(openActionListener);
        toolsPanel.add(openButton);

        searchField = new JTextField();
        searchField.setColumns(15);
        searchField.setDragEnabled(true);
        searchField.setName("SearchField");
        toolsPanel.add(searchField);

        ImageIcon searchIcon = new ImageIcon(fileLocation + "searchIcon.png");
        searchButton = new JButton(searchIcon);
        searchButton.setName("StartSearchButton");
        searchButton.addActionListener(searcherListener);
        toolsPanel.add(searchButton);

        ImageIcon leftIcon = new ImageIcon(fileLocation + "leftIcon.png");
        previousMatchButton = new JButton(leftIcon);
        previousMatchButton.setName("PreviousMatchButton");
        previousMatchButton.addActionListener(previousFindListener);
        toolsPanel.add(previousMatchButton);

        ImageIcon rightIcon = new ImageIcon(fileLocation + "rightIcon.png");
        nextMatchButton = new JButton(rightIcon);
        nextMatchButton.setName("NextMatchButton");
        nextMatchButton.addActionListener(nextListener);
        toolsPanel.add(nextMatchButton);

        regexCheckbox = new JCheckBox("Use regex");
        regexCheckbox.setName("UseRegExCheckbox");
        toolsPanel.add(regexCheckbox);

        editorTextArea = new JTextArea();
        editorTextArea.setName("TextArea");
        add(editorTextArea, BorderLayout.AFTER_LINE_ENDS);

        verticalTextScroller = new JScrollPane(editorTextArea);
        verticalTextScroller.setName("ScrollPane");
        horizontalTextScroller = new JScrollPane(editorTextArea);
        add(verticalTextScroller);
        add(horizontalTextScroller);

        topMenuBar = new JMenuBar();
        setJMenuBar(topMenuBar);

        fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        topMenuBar.add(fileMenu);

        openItem = new JMenuItem("Open");
        openItem.setName("MenuOpen");
        openItem.addActionListener(openActionListener);
        fileMenu.add(openItem);

        saveItem = new JMenuItem("Save");
        saveItem.setName("MenuSave");
        saveItem.addActionListener(saveActionListener);
        fileMenu.add(saveItem);

        fileMenu.addSeparator();

        exitItem = new JMenuItem("Exit");
        exitItem.setName("MenuExit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        searchMenu.setMnemonic(KeyEvent.VK_S);
        topMenuBar.add(searchMenu);

        startSearchItem = new JMenuItem("Start Search");
        startSearchItem.setName("MenuStartSearch");
        startSearchItem.addActionListener(searcherListener);
        searchMenu.add(startSearchItem);

        previousMatchItem = new JMenuItem("Previous search");
        previousMatchItem.setName("MenuPreviousMatch");
        previousMatchItem.addActionListener(previousFindListener);
        searchMenu.add(previousMatchItem);

        nextMatchItem = new JMenuItem("Next match");
        nextMatchItem.setName("MenuNextMatch");
        nextMatchItem.addActionListener(nextListener);
        searchMenu.add(nextMatchItem);

        useRegexItem = new JMenuItem("Use regular expressions");
        useRegexItem.setName("MenuUseRegExp");
        useRegexItem.addActionListener(e -> regexCheckbox.setSelected(true));
        searchMenu.add(useRegexItem);

        setVisible(true);
    }

    public void startSearch() {
        SearcherThread searcher = SearcherThread.getInstance(editorTextArea, searchField.getText(),
                regexCheckbox.isSelected());
//        System.out.println("Starting execution");
        searcher.execute();

        try {
            searcher.get();
        } catch (InterruptedException | ExecutionException interruptedException) {
            interruptedException.printStackTrace();
        }
        findIterator = SearcherThread.instance.foundIndexes.listIterator();
        if (searcher.isDone()) {
            highlightNext();
        }
//        System.out.println(findIterator.toString());
    }

    public void highlightNext() {
        while (findIterator == null) {
            System.out.println("isNull!");
//            findIterator = SearcherThread.instance.foundIndexes.listIterator();
        }
        System.out.println("iterator hasNext: " + findIterator.hasNext());
        if (findIterator.hasNext()) {
            Point pointToHighlight = findIterator.next();
            editorTextArea.setCaretPosition(pointToHighlight.x);
            editorTextArea.select(pointToHighlight.x, pointToHighlight.y);
        } else {
            findIterator = SearcherThread.instance.foundIndexes.listIterator();
            Point pointToHighlight = findIterator.next();
            editorTextArea.setCaretPosition(pointToHighlight.x);
            editorTextArea.select(pointToHighlight.x, pointToHighlight.y);
        }
        editorTextArea.grabFocus();
    }

    public void highlightLast() {
        if (findIterator.hasPrevious()) {
            findIterator.previous();
        }
        System.out.println("previous index: " + findIterator.previousIndex());
        if (findIterator.hasPrevious()) {
            System.out.println("is previous");
            Point pointToHighlight = findIterator.previous();
            editorTextArea.setCaretPosition(pointToHighlight.x);
            editorTextArea.select(pointToHighlight.x, pointToHighlight.y);
            editorTextArea.grabFocus();
        } else {
            Point pointToHighlight = null;
            while (findIterator.hasNext()) {
                findIterator.next();
            }
            if (findIterator.hasPrevious()) {
                pointToHighlight = findIterator.previous();
            }
            if (findIterator.hasNext()) {
                pointToHighlight = findIterator.next();
            }
            if (pointToHighlight != null) {
                editorTextArea.setCaretPosition(pointToHighlight.x);
                editorTextArea.select(pointToHighlight.x, pointToHighlight.y);
                editorTextArea.grabFocus();
            }
        }
    }

}
