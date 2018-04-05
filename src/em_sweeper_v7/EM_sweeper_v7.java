package em_sweeper_v7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class EM_sweeper_v7 extends JFrame {

    static Timer timer;
    static ImageIcon[] bombGif = {new ImageIcon("E:\\Java\\EM_sweeper_v7\\src\\em_sweeper_v7\\5.gif"),
        new ImageIcon("E:\\Java\\EM_sweeper_v7\\src\\em_sweeper_v7\\6.gif")};

    public static final int MAX_HORIZONTAL_BUTTON = 10;
    public static final int MAX_VERTICAL_BUTTON = 10;
    public static final int MAX_MINES = 10;

    private static JPanel greatestPanel, greaterPanel, greatePanel, minesPanel, timeAndBombsPanel, timePanel, bombsDisplayPanel;
    private static JMenuBar menubar;
    private static JMenu gameMenu, helpMenu, aboutMenu;
    private static JMenuItem newGameMenuItem, exitMenuItem, getHelpMenuItem, aboutMenuItem;
    public static JButton[][] minesButtons;
    private static JLabel timeLabel, minesLabel;
    public static JLabel time;
    public static int counter = 0;
    public static boolean timerRestart = false;
    public static boolean playAgainBool = false;
    public static ArrayList<String> buttonsStack = new ArrayList<String>();

    static Color[] colors = {Color.BLUE, Color.RED, Color.BLACK, Color.GRAY, Color.MAGENTA, Color.CYAN, Color.GREEN, Color.ORANGE};

    ImageIcon icon = new ImageIcon(getClass().getResource("icon.png"));

    public EM_sweeper_v7() {

        super("EMsweeper");
        setIconImage(icon.getImage());

        bombsDisplayPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        timePanel = new JPanel(new GridLayout(1, 2, 0, 0));
        timeAndBombsPanel = new JPanel(new BorderLayout(5, 5));
        minesPanel = new JPanel(new GridLayout(10, 10, 0, 0));
        greaterPanel = new JPanel(new BorderLayout(60, 60));
        greatestPanel = new JPanel(new BorderLayout(5, 5));
        greatePanel = new JPanel(new BorderLayout(5, 35));

        menubar = new JMenuBar();

        greatestPanel.setBorder(BorderFactory.createTitledBorder(""));
        timePanel.setBorder(BorderFactory.createTitledBorder(""));
        bombsDisplayPanel.setBorder(BorderFactory.createTitledBorder(""));

        gameMenu = new JMenu("Game");
        helpMenu = new JMenu("Help");
        aboutMenu = new JMenu("About");

        gameMenu.setFont(new Font("Century Gothic", Font.BOLD, 13));
        helpMenu.setFont(new Font("Century Gothic", Font.BOLD, 13));
        aboutMenu.setFont(new Font("Century Gothic", Font.BOLD, 13));

        newGameMenuItem = new JMenuItem("New Game", new ImageIcon(getClass().getResource("newgame.png")));
        exitMenuItem = new JMenuItem("Exit", new ImageIcon(getClass().getResource("exit.png")));
        getHelpMenuItem = new JMenuItem("Get Help", new ImageIcon(getClass().getResource("help.png")));
        aboutMenuItem = new JMenuItem("", new ImageIcon(getClass().getResource("EM_Programmers.png")));

        newGameMenuItem.setFont(new Font("Century Gothic", Font.BOLD, 12));
        exitMenuItem.setFont(new Font("Century Gothic", Font.BOLD, 12));
        getHelpMenuItem.setFont(new Font("Century Gothic", Font.BOLD, 12));

        String help = "This is what makes EMsweeper 'hard' for a simple game - the scary numbers. This actually is just a \n"
                + "small on/off-pattern-based game system, only with a timer. A square, unless touching a side/corner,\n"
                + "has 8 other squares neighboring it, therefore: \n\n"
                + "♣ If you click the square and see a '1' in it, it means that one of the other 8 squares around \n"
                + "   it contains the bomb; \n"
                + "♣ If you click the square and see a '2' in it, it means that two of the other 8 squares around \n"
                + "   it contains bombs; and so on. \n"
                + "♣ If you click the square and see the mine in it, you have no choice but to repeat the game \n"
                + "    from the start.\n\n"
                + "To conquer more and more squares successfully, you should also observe \n"
                + "other numbers that is near the square you are focusing to click to. \n\n"
                + "TIP:   The higher the number, the greater amount of bombs are placed around it. \n"
                + "        This may be easy, if and only if you carefully observe other numbers situated near it,\n"
                + "        or that the number itself is '8'.\n\n"
                + "After several trials, you should have had a little grasp on how to beat EMsweeper. \n"
                + "Practice more, and you'll find that your reasoning and observational skills have become a little deeper. \n\n"
                + "                                                                           HAVE FUN; \n";

        newGameMenuItem.addActionListener(e -> {
            newGame();
        });

        exitMenuItem.addActionListener(e -> {
            if (askConfirmation() == 0) {
                System.exit(0);
            }
        });
        getHelpMenuItem.addActionListener(e -> {
            JDialog d = new JDialog();
            d.setTitle("EMsweeper");
            d.setModal(true);
            d.setBounds(100, 100, 790, 485);
            d.setResizable(false);
            d.setLocationRelativeTo(minesPanel);
            JPanel p = new JPanel();
            JTextArea h = new JTextArea(help);
            h.setFont(new Font("Century Gothic", Font.BOLD, 13));
            h.setEditable(false);
            p.add(h);
            d.add(p);
            d.setVisible(true);
        });

        minesButtons = new JButton[MAX_HORIZONTAL_BUTTON][MAX_VERTICAL_BUTTON];

        timeLabel = new JLabel("Time  ");
        time = new JLabel("0");
        minesLabel = new JLabel("10  Mines");

        timeLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        minesLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        time.setFont(new Font("Courier New", Font.BOLD, 18));

        for (int r = 0; r < MAX_HORIZONTAL_BUTTON; r++) {
            for (int c = 0; c < MAX_VERTICAL_BUTTON; c++) {
                minesButtons[r][c] = new JButton();
                minesPanel.add(minesButtons[r][c]);
                minesButtons[r][c].setBackground(Color.green);
                minesButtons[r][c].setForeground(Color.green);
                minesButtons[r][c].setFont(new Font("Courier New", Font.BOLD, 17));
                minesButtons[r][c].addActionListener(new buttonClicks_v7(minesButtons[r][c], r, c));
            }
        }

        gameMenu.add(newGameMenuItem);
        gameMenu.add(exitMenuItem);

        helpMenu.add(getHelpMenuItem);
        aboutMenu.add(aboutMenuItem);

        menubar.add(gameMenu);
        menubar.add(helpMenu);
        menubar.add(aboutMenu);

        timePanel.add(timeLabel);
        timePanel.add(time);

        bombsDisplayPanel.add(minesLabel);

        timeAndBombsPanel.add(timePanel, BorderLayout.WEST);
        timeAndBombsPanel.add(bombsDisplayPanel, BorderLayout.EAST);

        greatePanel.add(minesPanel, BorderLayout.CENTER);
        greatePanel.add(timeAndBombsPanel, BorderLayout.SOUTH);

        greaterPanel.add(new JLabel(" "), BorderLayout.NORTH);
        greaterPanel.add(new JLabel(" "), BorderLayout.WEST);
        greaterPanel.add(greatePanel, BorderLayout.CENTER);
        greaterPanel.add(new JLabel(" "), BorderLayout.EAST);

        greatestPanel.add(menubar, BorderLayout.NORTH);
        greatestPanel.add(greaterPanel, BorderLayout.CENTER);

        add(greatestPanel);
    }

    static JTextArea messageOnJOptionPane(String message) {
        JTextArea Label = new JTextArea(message);
        Label.setEditable(false);
        Label.setFont(new Font("Century Gothic", Font.BOLD, 14));
        return Label;
    }

    static void neghibour(int row, int colum) {
        for (int r = row - 1; r < row + 2; r++) {
            if (r < 0 || r > 9) {
                continue;
            }
            for (int c = colum - 1; c < colum + 2; c++) {
                if (c < 0 || c > 9) {
                    continue;
                } else if (r == row && c == colum) {
                    continue;
                } else {
                    if (checkneghibour(r, c) == true) {
                        if (!(minesButtons[r][c].getText().equals(" ")) && minesButtons[r][c].getText().length() == 1) {
                            colorAssign(r, c);
                        } else if (minesButtons[r][c].getText().length() == 0) {
                            push(r, c);
                        }
                    }
                }
            }
        }
        pop();
    }

    static void colorAssign(int row, int colum) {
        minesButtons[row][colum].setBackground(Color.white);
        int x = Integer.parseInt(minesButtons[row][colum].getText());
        minesButtons[row][colum].setForeground(colors[x - 1].darker());
    }

    static boolean checkneghibour(int row, int colum) {
        String x = "" + row + colum;
        if (minesButtons[row][colum].getBackground() == Color.white) {
            return false;
        } else {
            return buttonsStack.contains(x) != true;
        }
    }

    static void push(int row, int colum) {
        buttonsStack.add("" + row + colum);
    }

    static void pop() {
        for (int r = 0; r < buttonsStack.size(); r++) {
            int row = Integer.parseInt(buttonsStack.get(r).charAt(0) + "");
            int colum = Integer.parseInt(buttonsStack.get(r).charAt(1) + "");
            minesButtons[row][colum].setBackground(Color.white);
            String x = buttonsStack.remove(r);
            row = Integer.parseInt(x.charAt(0) + "");
            colum = Integer.parseInt(x.charAt(1) + "");
            neghibour(row, colum);
        }
    }

    static void setBombs(int row, int colum) {
        int randR, randC;

        for (int r = 0; r < 10; r++) {
            do {
                randR = new Random().nextInt(10);
                randC = new Random().nextInt(10);
            } while ((row == randR && colum == randC) || (minesButtons[randR][randC].getText().length() == 1));
//            System.out.println("" + randR + randC);
            minesButtons[randR][randC].setText(" ");
        }
        Indicator();
    }

    static void Indicator() {

        for (int rr = 0; rr < 10; rr++) {
            for (int cc = 0; cc < 10; cc++) {
                int numOfMine = 0;
                for (int r = rr - 1; r < rr + 2; r++) {
                    if (r < 0 || r > 9) {
                        continue;
                    }
                    for (int c = cc - 1; c < cc + 2; c++) {
                        if (c < 0 || c > 9) {
                            continue;
                        } else if (r == rr && c == cc) {
                            continue;
                        } else {
                            if (minesButtons[r][c].getText().equals(" ")) {
                                numOfMine++;
                            }
                        }
                    }
                }
                if (numOfMine != 0 && minesButtons[rr][cc].getText().length() != 1) {
                    minesButtons[rr][cc].setText(numOfMine + "");
                }
            }
        }
    }

    static void checkLoose(int row, int colum) {
        if (minesButtons[row][colum].getText().equals(" ")) {
            timer.stop();
            minesButtons[row][colum].setBackground(Color.white);
            minesButtons[row][colum].setText("");
            minesButtons[row][colum].setIcon(bombGif[1]);
            bombGif[1].getImage().flush();
            for (int r = 0; r < 10; r++) {
                for (int c = 0; c < 10; c++) {
                    if (minesButtons[r][c].getText().equals(" ")) {
                        minesButtons[r][c].setText("");
                        minesButtons[r][c].setBackground(Color.white);
                        minesButtons[r][c].setIcon(bombGif[0]);
                        bombGif[0].getImage().flush();
                    }
                }
            }

            String message = "Sorry, You Lost This Game. Better Luck Next Time! \n This is Yeshiwas!";
            dialogBox(message, 0);
        }
    }

    static void checkWin() {
        int counter = 0;

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                if (minesButtons[r][c].getBackground() == Color.green) {
                    counter++;
                }
                if (counter > 10) {
                    break;
                }
            }
            if (counter > 10) {
                break;
            }
        }
        if (counter == 10) {
            timer.stop();
            for (int r = 0; r < 10; r++) {
                for (int c = 0; c < 10; c++) {
                    if (minesButtons[r][c].getText().equals(" ")) {
                        minesButtons[r][c].setBackground(Color.ORANGE);
                    }
                }
            }
            String message = "Congratulations, You Won The Game!!\n\nTime : " + time.getText() + " Seconds";
            dialogBox(message, 1);
        }
    }

    static void dialogBox(String message, int x) {
        String exit = "<html><strong> Exit </strong></html>";
        String restart = "<html><strong> Restart This Game </strong></html>";
        String again = "<html><strong> Play Again </strong></html>";
        String[] winButtons = {exit, restart, again};
        int accept = JOptionPane.showOptionDialog(minesPanel, messageOnJOptionPane(message), "EMsweeper", 0, ((x == 0) ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE), null, winButtons, winButtons[2]);
        switch (accept) {
            case 0:
                if (askConfirmation() == 0) {
                    System.exit(0);
                } else {
                    dialogBox(message, x);
                }
                break;
            case 1:
                restart();
                JOptionPane.showMessageDialog(minesPanel, messageOnJOptionPane("Becareful you might lose on the first click!"), "EMsweeper", JOptionPane.WARNING_MESSAGE, null);
                timerRestart = true;
                break;
            default:
                if (x == 1) {
                    newGame();
                } else {
                    playAgainBool = true;
                }
                break;
        }
    }

    public static void newGame() {

        if (!buttonsStack.isEmpty()) {
            buttonsStack.clear();
        }

        for (int r = 0; r < MAX_HORIZONTAL_BUTTON; r++) {
            for (int c = 0; c < MAX_VERTICAL_BUTTON; c++) {
                minesButtons[r][c].setBackground(Color.green);
                minesButtons[r][c].setForeground(Color.green);
                minesButtons[r][c].setText("");
                counter = 0;
                time.setText("0");
                minesButtons[r][c].setIcon(null);
            }
        }
        try {
            if (timer.isRunning() == true) {
                timer.stop();
            }
        } catch (Exception e) {
        }
    }

    static void restart() {
        for (int r = 0; r < MAX_HORIZONTAL_BUTTON; r++) {
            for (int c = 0; c < MAX_VERTICAL_BUTTON; c++) {
                minesButtons[r][c].setBackground(Color.green);
                minesButtons[r][c].setForeground(Color.green);
                if (minesButtons[r][c].getIcon() == bombGif[0] || minesButtons[r][c].getIcon() == bombGif[1]) {
                    minesButtons[r][c].setIcon(null);
                    minesButtons[r][c].setText(" ");
                }
                time.setText("0");
                minesButtons[r][c].setIcon(null);
            }
        }
        try {
            if (timer.isRunning() == true) {
                timer.stop();
            }
        } catch (Exception e) {
        }
    }

    static int askConfirmation() {
        return JOptionPane.showConfirmDialog(minesPanel, messageOnJOptionPane("Are You Sure?"), "EMsweeper", JOptionPane.YES_NO_OPTION);
    }

    static void play(File sound) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000);
        } catch (Exception ee) {

        }
    }

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EM_sweeper_v7.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        try {
            Thread.sleep(1300);
        } catch (Exception e) {
        }

        EM_sweeper_v7 em_sweeper = new EM_sweeper_v7();
        em_sweeper.setResizable(false);
        em_sweeper.setSize(540, 600);
        em_sweeper.setLocationRelativeTo(null);
        em_sweeper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        em_sweeper.setVisible(true);
    }
}

class buttonClicks_v7 implements ActionListener {

    private JButton b1;
    private int row;
    private int colum;

    public buttonClicks_v7(JButton b1, int r, int c) {
        this.b1 = b1;
        this.row = r;
        this.colum = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        b1.setBackground(Color.white);
        EM_sweeper_v7.counter++;
        EM_sweeper_v7.checkLoose(row, colum);
        if (EM_sweeper_v7.counter == 1) {
            EM_sweeper_v7.setBombs(row, colum);
            EM_sweeper_v7.timerRestart = true;
            EM_sweeper_v7.timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    change();
                }
            });
        }
        if (EM_sweeper_v7.timerRestart == true) {
            EM_sweeper_v7.timer.start();
            EM_sweeper_v7.timerRestart = false;
        }
        if (b1.getText().length() == 1 && !b1.getText().equals(" ")) {
            int x = Integer.parseInt(b1.getText());
            b1.setForeground(EM_sweeper_v7.colors[x - 1].darker());
        }
        if (EM_sweeper_v7.minesButtons[row][colum].getText().length() == 0) {
            EM_sweeper_v7.neghibour(row, colum);
        }
        if (EM_sweeper_v7.playAgainBool == true) {
            EM_sweeper_v7.newGame();
            EM_sweeper_v7.playAgainBool = false;
        }
        EM_sweeper_v7.checkWin();
    }

    void change() {
        int temp;
        temp = Integer.parseInt(EM_sweeper_v7.time.getText());
        temp++;
        EM_sweeper_v7.time.setText(temp + "");
    }
}
