package me.ahmadhajjar.GithubNotificationsApp.ui;

import me.ahmadhajjar.GithubNotificationsApp.utils.PROpener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URI;

public class NotificationCenter extends JFrame {
    private static final Logger logger = LogManager.getLogger(NotificationCenter.class);
    private static final int HIDING_DELAY = 5000;
    public static final int WIDTH = 350;
    public static final int HEIGHT = 75;
    private JPanel mainPanel;
    private Timer hidingTimer;
    private static final NotificationCenter instance = new NotificationCenter();

    public static NotificationCenter getInstance() {
        return instance;
    }

    private NotificationCenter() {
        Border padding = BorderFactory.createEmptyBorder(0, 0, 0, 0);
        mainPanel.setBorder(padding);
        setContentPane(mainPanel);

        setTitle("Notification Center");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setType(Type.UTILITY);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setResizable(false);
        resetSizeAndShape(HEIGHT);
        setLocationToTopRight();

        hidingTimer = new Timer(HIDING_DELAY, e -> {
            hidingTimer.stop();
            getContentPane().removeAll();
            setVisible(false);
        });
        setBackground(new Color(100, 100, 100, 0));
    }

    private void resetSizeAndShape(int height) {
        setShape(new RoundRectangle2D.Double(0, 0, WIDTH, height, 20, 20));
        setSize(WIDTH, height);
    }

    private void setLocationToTopRight() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = ((int) screenSize.getWidth());
        setLocation(width - getWidth() - 10, 30);
    }

    public void showNotification(String repoName, int newPRNumber) {
        logger.debug("notifying about PR#{} in {}", newPRNumber, repoName);
        final URI prURI = URI.create("https://github.com/" + repoName + "/pull/" + newPRNumber);
        var notification = new Notification(repoName, newPRNumber, e -> onOK(prURI), e -> onCancel());

        if (getContentPane().getComponentCount() > 4) {
            getContentPane().remove(getContentPane().getComponentCount() - 1);
        }
        getContentPane().add(notification, 0);
        resetSizeAndShape(HEIGHT * getContentPane().getComponentCount());
        repaint();

        hidingTimer.restart();
        setVisible(true);
    }

    private void onOK(URI prURI) {
        PROpener.open(prURI);
        validate();
    }

    private void onCancel() {
        validate();
    }
}
