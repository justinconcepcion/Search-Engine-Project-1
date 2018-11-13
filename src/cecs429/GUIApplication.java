package cecs429;

import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

public class GUIApplication extends JFrame{

    static final int MILESTONE_1 = 1;
    static final int MILESTONE_2 = 2;
    static final int MILESTONE_3 = 3;

    private GUIApplication() {

        initlizeMainContent();
    }

    private void initlizeMainContent() {

        setTitle("Search Engine");
        setBounds(new Rectangle(0, 0, 900, 900));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel firstPanel = new JPanel();
        firstPanel.setLayout(new GridLayout(3, 1, 10, 10));
        firstPanel.setMaximumSize(new Dimension(600, 200));

        JButton btnMilestone1 = new JButton();
        btnMilestone1.setText("Milestone 1");
        btnMilestone1.add(Box.createRigidArea(new Dimension(5, 0)));
        firstPanel.add(btnMilestone1);

        btnMilestone1.addActionListener(e -> {

            setVisible(false);
            new MilestoneJFrame(MILESTONE_1,GUIApplication.this);
        });

        JButton btnMilestone2 = new JButton();
        btnMilestone2.setText("Milestone 2");
        btnMilestone2.add(Box.createRigidArea(new Dimension(5, 0)));
        firstPanel.add(btnMilestone2);

        btnMilestone2.addActionListener(e -> {

            setVisible(false);
            new MilestoneJFrame(MILESTONE_2, GUIApplication.this);
        });

        JButton btnMilestone3 = new JButton();
        btnMilestone3.setText("Milestone 3");
        btnMilestone3.add(Box.createRigidArea(new Dimension(5, 0)));
        btnMilestone3.setEnabled(false);
        firstPanel.add(btnMilestone3);

        mainPanel.add(firstPanel);

        setContentPane(mainPanel);

        setSize(520, 600);
        setMinimumSize(new Dimension(520, 600));
        setVisible(true);


    }

    public static void main(String[] args) {
        new GUIApplication();
    }
}
