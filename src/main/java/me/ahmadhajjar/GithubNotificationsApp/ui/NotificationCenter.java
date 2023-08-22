package me.ahmadhajjar.GithubNotificationsApp.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URI;

public class NotificationCenter extends JFrame {
    private static final Logger logger = LogManager.getLogger(NotificationCenter.class);
    private static final int HIDING_DELAY = 5000;
    private JPanel mainPanel;
    private Timer hidingTimer;

    private static NotificationCenter instance = new NotificationCenter();

    public static NotificationCenter getInstance() {
        return instance;
    }

    private NotificationCenter() {
        Border padding = BorderFactory.createEmptyBorder(0, 0, 0, 0);
        mainPanel.setBorder(padding);
        setContentPane(mainPanel);

        setTitle("Notification Center");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setUndecorated(true);
        setAlwaysOnTop(true);
        setResizable(false);
        setShape(new RoundRectangle2D.Double(0, 0, 350, 75, 20, 20));
        setSize(350, 75);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = ((int) screenSize.getWidth());
        setLocation(width - getWidth() - 10, 30);

        hidingTimer = new Timer(HIDING_DELAY, e -> {
            logger.info("Hiding notification center ...");
            hidingTimer.stop();
            getContentPane().removeAll();
            setVisible(false);
        });
        setBackground(new Color(100, 100, 100, 51));
    }

    public void addNotification(String repoName, int newPRNumber) {
        final URI prURI = URI.create("https://github.com/" + repoName + "/pull/" + newPRNumber);
        var notification = new Notification(repoName, newPRNumber, e -> onOK(prURI), e -> onCancel());
        logger.info("Adding notification ...");

        if (getContentPane().getComponentCount() > 4) {
            getContentPane().remove(getContentPane().getComponentCount() - 1);
        }
        getContentPane().add(notification, 0);
        setShape(new RoundRectangle2D.Double(0, 0, 350, 75 * getContentPane().getComponentCount(), 20, 20));
        setSize(350, 75 * getContentPane().getComponentCount());
        repaint();

        hidingTimer.restart();
        setVisible(true);
        logger.info("Showing notification ...");
    }

    private void onOK(URI prURI) {
        logger.warn("Opening PR !!!!!");
        goToPr(prURI);
        validate();
    }

    private void onCancel() {
        logger.warn("Closing notification !!!!!");
        validate();
    }

    private void goToPr(URI prURI) {
        try {
            Desktop.getDesktop().browse(prURI);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
}
