package budgetTree;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import utils.ManageTransactions;
import utils.ManageTree;

public class FrameMain {
    static ArrayList<JFrame> openFrames = new ArrayList<>();
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static boolean isValidOpenWindow() {
        return openFrames.stream().noneMatch(JFrame::isVisible);
    }

    private static void addFrame(JFrame frame) {
        openFrames.add(frame);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                openFrames.remove(frame);
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Budget Tree Application");
        JPanel panel = new JPanel();

        // Label 추가
        panel.add(new JLabel("Budget Tree"));

        // 버튼 추가 및 이벤트 핸들러 설정
        addButton(panel, "Modify Transactions", e -> {
            if (isValidOpenWindow()) {
                ManageTransactions manageTransactions = new ManageTransactions();
                manageTransactions.setVisible(true);
                addFrame(manageTransactions);
            }
        });

        addButton(panel, "Modify Tree", e -> {
            if (isValidOpenWindow()) {
                ManageTree manageTree = new ManageTree();
                manageTree.setVisible(true);
                addFrame(manageTree);
            }
        });

        // 프레임 설정
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(400, 300));
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null); // 창을 화면 가운데에 위치시킵니다.
        frame.setVisible(true);
    }

    private static void addButton(JPanel panel, String title, ActionListener actionListener) {
        JButton button = new JButton(title);
        button.addActionListener(actionListener);
        panel.add(button);
    }
}
