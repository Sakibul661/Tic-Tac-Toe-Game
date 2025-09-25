package Game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class TicTacToeGUI {
    private static final String HUMAN = "Mr. X";
    private static final String AI = "AI";
    private final JFrame frame;
    private final JButton[] buttons = new JButton[9];
    private final JLabel statusLabel;
    private final JButton resetButton;
    private String[] board = new String[9];

    public TicTacToeGUI() {
        Arrays.fill(board, " ");

        frame = new JFrame("Tic-Tac-Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.ORANGE);
        frame.setLayout(new BorderLayout(0, 10));

        // Center: 3x3 grid of buttons
        JPanel grid = new JPanel(new GridLayout(3,3,4,4));
        grid.setBackground(Color.ORANGE);
        for (int i = 0; i < 9; i++) {
            final int idx = i;
            JButton btn = new JButton("");
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 40)); // large font like Python version
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.WHITE);
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createRaisedBevelBorder());
            btn.setPreferredSize(new Dimension(170, 120));
            btn.addActionListener(e -> humanMove(idx));
            buttons[i] = btn;
            grid.add(btn);
        }
        frame.add(grid, BorderLayout.CENTER);

        // South: status label and reset button (stacked)
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.setBackground(Color.BLACK);

        statusLabel = new JLabel("Your turn (Mr. X)", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 25));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        south.add(statusLabel);

        resetButton = new JButton("New Game");
        resetButton.setFont(new Font("Arial", Font.BOLD, 25));
        resetButton.setBackground(Color.GRAY);
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createRaisedBevelBorder());
        resetButton.setPreferredSize(new Dimension(300, 50));
        resetButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.addActionListener(e -> reset());
        south.add(Box.createRigidArea(new Dimension(0,10)));
        south.add(resetButton);

        frame.add(south, BorderLayout.SOUTH);

        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void humanMove(int idx) {
        if (" ".equals(board[idx]) && checkWinner(board) == null) {
            board[idx] = HUMAN;
            buttons[idx].setText(HUMAN);
            buttons[idx].setEnabled(false);
            buttons[idx].setForeground(Color.RED); 
            if (checkWinner(board) == null) {
                // Delay AI move by 200 ms to mimic your original root.after
                Timer t = new Timer(200, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ((Timer)e.getSource()).stop();
                        aiMove();
                    }
                });
                t.setRepeats(false);
                t.start();
            } else {
                updateStatus();
            }
        }
    }

    private void aiMove() {
        if (checkWinner(board) != null) return;
        Integer move = bestMove(board);
        if (move != null) {
            board[move] = AI;
            buttons[move].setText(AI);
            buttons[move].setEnabled(false);
            buttons[move].setForeground(new Color(0,128,0)); // green
        }
        updateStatus();
    }

    private void updateStatus() {
        String winner = checkWinner(board);
        if (winner != null) {
            if ("Tie".equals(winner)) {
                statusLabel.setText("It's a Tie!");
                statusLabel.setForeground(Color.YELLOW);
            } else {
                // color = green if winner == HUMAN else red
                if (HUMAN.equals(winner)) {
                    statusLabel.setText(winner + " Wins!");
                    statusLabel.setForeground(new Color(0,128,0)); // green
                } else {
                    statusLabel.setText(winner + " Wins!");
                    statusLabel.setForeground(Color.RED);
                }
            }
            for (JButton btn : buttons) {
                btn.setEnabled(false);
            }
        } else {
            statusLabel.setText("Your turn (Mr. X)");
            statusLabel.setForeground(Color.WHITE);
        }
    }

    private void reset() {
        board = new String[9];
        Arrays.fill(board, " ");
        for (JButton btn : buttons) {
            btn.setText("");
            btn.setEnabled(true);
            btn.setForeground(Color.WHITE);
        }
        statusLabel.setText("Your turn (Mr. X)");
        statusLabel.setForeground(Color.WHITE);
    }

    
    private String checkWinner(String[] b) {
        int[][] wins = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
        };
        for (int[] w : wins) {
            int a = w[0], c = w[2], m = w[1];
            if (!" ".equals(b[a]) && b[a].equals(b[m]) && b[a].equals(b[c])) {
                return b[a];
            }
        }
        boolean anyEmpty = false;
        for (String s : b) {
            if (" ".equals(s)) {
                anyEmpty = true;
                break;
            }
        }
        if (!anyEmpty) return "Tie";
        return null;
    }

    private int minimax(String[] b, String player) {
        String winner = checkWinner(b);
        if (AI.equals(winner)) return 1;
        if (HUMAN.equals(winner)) return -1;
        if ("Tie".equals(winner)) return 0;

        if (AI.equals(player)) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (" ".equals(b[i])) {
                    b[i] = AI;
                    int score = minimax(b, HUMAN);
                    b[i] = " ";
                    best = Math.max(best, score);
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (" ".equals(b[i])) {
                    b[i] = HUMAN;
                    int score = minimax(b, AI);
                    b[i] = " ";
                    best = Math.min(best, score);
                }
            }
            return best;
        }
    }

    private Integer bestMove(String[] b) {
        int best = Integer.MIN_VALUE;
        Integer move = null;
        for (int i = 0; i < 9; i++) {
            if (" ".equals(b[i])) {
                b[i] = AI;
                int score = minimax(b, HUMAN);
                b[i] = " ";
                if (score > best) {
                    best = score;
                    move = i;
                }
            }
        }
        return move;
    }

    // -------------------- Main --------------------
    public static void main(String[] args) {
        // Ensure UI runs on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new TicTacToeGUI());
    }
}
