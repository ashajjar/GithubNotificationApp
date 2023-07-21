package me.ahmadhajjar.GithubNotificationsApp.ui;

import me.ahmadhajjar.GithubNotificationsApp.service.DiskStorageService;
import me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner.AddRepoButtonListener;
import me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner.HelpButtonListener;
import me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner.RemoveRepoButtonListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.*;

public class GithubNotifierApp extends JFrame {
    public static final String CTRL_ENTER_ACTION_MAP_KEY = "ENTER";
    public static final String CTRL_D_ACTION_MAP_KEY = "CTRL_D";
    public static final String F1_ACTION_MAP_KEY = "F1";
    private static final String F5_ACTION_MAP_KEY = "F5";

    private final KeyStroke ctrlenterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK, true);
    private final KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK, true);
    private final KeyStroke f1KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, true);
    private final KeyStroke f5KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, true);

    private JTextField newRepoName;
    private JButton addRepo;
    private JList<String> watchedReposList;
    private JPanel mainPanel;
    private JButton removeRepoButton;
    private JButton helpButton;

    public GithubNotifierApp() {
        initialiseWindow();

        addRepo.addActionListener(new AddRepoButtonListener(newRepoName, watchedReposList));
        removeRepoButton.addActionListener(new RemoveRepoButtonListener(watchedReposList));
        helpButton.addActionListener(new HelpButtonListener());

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                saveWatchedRepos();
                e.getWindow().dispose();
            }
        });

        initializeKeyboardHandlers();
        initialiseWatchedReposList();
    }

    private void initialiseWatchedReposList() {
        DefaultListModel<String> model = new DefaultListModel<>();

        DiskStorageService.getInstance().loadReposList().forEach(model::addElement);

        watchedReposList.setModel(model);
    }

    private void initialiseWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("GitHub Repo Notifier");
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        setWindowIcon();
        pack();
        setVisible(true);
    }

    private void setWindowIcon() {
        ImageIcon appIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon.png")));
        setIconImage(appIcon.getImage());
    }

    private void initializeKeyboardHandlers() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlenterKeyStroke, CTRL_ENTER_ACTION_MAP_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlDKeyStroke, CTRL_D_ACTION_MAP_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f1KeyStroke, F1_ACTION_MAP_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f5KeyStroke, F5_ACTION_MAP_KEY);
        getRootPane().getActionMap().put(CTRL_ENTER_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRepo.doClick();
            }
        });
        getRootPane().getActionMap().put(CTRL_D_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeRepoButton.doClick();
            }
        });
        getRootPane().getActionMap().put(F1_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                helpButton.doClick();
            }
        });
        getRootPane().getActionMap().put(F5_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("reload is not implemented yet");
            }
        });
    }

    private void saveWatchedRepos() {
        List<String> result = new ArrayList<>();
        ListModel<String> model = watchedReposList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            result.add(model.getElementAt(i));
        }
        DiskStorageService.getInstance().saveReposList(result);
        System.out.println("Saved repos");
    }
}
