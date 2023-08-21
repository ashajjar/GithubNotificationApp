package me.ahmadhajjar.GithubNotificationsApp.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

public class Notification extends JFrame {
    private static final Logger logger = LogManager.getLogger(Notification.class);
    private JButton btnOpen;
    private JButton btnDismiss;
    private JLabel lblIcon;
    private JPanel mainPanel;
    private JPanel panelActions;
    private JPanel panelTexts;
    private JTextArea txtMessage;

    public Notification(String repoName, int newPRNumber) {
        setContentPane(mainPanel);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setResizable(false);
        setShape(new RoundRectangle2D.Double(0, 0, 350, 75, 20, 20));
        setSize(350, 75);
        panelActions.setMaximumSize(new Dimension(75, 75));
        panelTexts.setMaximumSize(new Dimension(200, 75));

        btnOpen.addActionListener(e -> onOK());
        btnDismiss.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        txtMessage.setBackground(UIManager.getColor("Label.background"));
        txtMessage.setFont(UIManager.getFont("Label.font"));
        txtMessage.setBorder(UIManager.getBorder("Label.border"));
        txtMessage.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        txtMessage.setText("\nNew PR #" + newPRNumber + " in " + repoName);

        new Timer(5000, e -> dispose()).start();
        logger.error("Window is {} x {}", this.getWidth(), this.getHeight());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = ((int) screenSize.getWidth());

        setLocation(width - getWidth() - 10, 30);

        BufferedImage myPicture = null;
        try {
            myPicture = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png")));
        } catch (IOException e) {
            logger.error(e);
        }
        if (myPicture != null) {
            var icon = new ImageIcon();
            icon.setImage(myPicture);

            lblIcon.setIcon(icon);
        }

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                goToPr(repoName, newPRNumber);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void goToPr(String repoName, int newPRNumber) {
        try {
            Desktop.getDesktop().browse(URI.create("https://github.com/" + repoName + "/pull/" + newPRNumber));
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
}
