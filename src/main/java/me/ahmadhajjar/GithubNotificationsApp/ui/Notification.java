package me.ahmadhajjar.GithubNotificationsApp.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Notification extends JPanel {
    private static final Logger logger = LogManager.getLogger(Notification.class);
    private JButton btnOpen;
    private JButton btnDismiss;
    private JLabel lblIcon;
    private JPanel mainPanel;
    private JPanel panelActions;
    private JPanel panelTexts;
    private JTextArea txtMessage;

    public Notification(String repoName, int newPRNumber, ActionListener onOpen, ActionListener onClose) {
        add(mainPanel);
        setSize(350, 75);
        panelActions.setMaximumSize(new Dimension(75, 75));
        panelTexts.setMaximumSize(new Dimension(200, 75));

        btnOpen.addActionListener(onOpen);
        btnDismiss.addActionListener(onClose);

        txtMessage.setBackground(new Color(0,0,0,0));
        txtMessage.setBorder(new LineBorder(new Color(0,0,0,0),0));
        txtMessage.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        txtMessage.setText("\nNew PR #" + newPRNumber + " in " + repoName);

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

        setVisible(true);
        setOpaque(false);

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onOpen.actionPerformed(new ActionEvent(e, 1, ""));
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(20, 20); //Border corners arcs {width,height}, change this to whatever you want
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Draws the rounded panel with borders.
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);//paint background
        graphics.setColor(getForeground());
        graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);//paint border
    }
}
