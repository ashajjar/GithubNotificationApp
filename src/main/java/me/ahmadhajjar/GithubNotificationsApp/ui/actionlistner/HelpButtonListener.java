package me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        Container parentComponent = ((Component) e.getSource()).getParent();
        JOptionPane.showMessageDialog(
                parentComponent,
                """
                        CTRL+ENTER ---------- Add new repo
                        CTRL+S ----------------- Save the new repos list
                        CTRL+D ----------------- Delete selected repo
                        F1 ------------------------- Open this message""",
                "Help",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
