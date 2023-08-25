package me.ahmadhajjar.GithubNotificationsApp.ui;

import me.ahmadhajjar.GithubNotificationsApp.service.DiskStorageService;
import me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GithubNotifierApp extends JFrame {
    private static final Logger logger = LogManager.getLogger(GithubNotifierApp.class);
    public static final String CTRL_ENTER_ACTION_MAP_KEY = "CTRL_ENTER";
    public static final String CTRL_D_ACTION_MAP_KEY = "CTRL_D";
    public static final String F1_ACTION_MAP_KEY = "F1";
    private static final String CTRL_S_ACTION_MAP_KEY = "CTRL_S";
    private static final String CTRL_O_ACTION_MAP_KEY = "CTRL_O";

    private final KeyStroke ctrlenterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK, true);
    private final KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK, true);
    private final KeyStroke f1KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, true);
    private final KeyStroke ctrlSKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, true);
    private final KeyStroke ctrlOKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK, true);

    private JTextField newRepoName;
    private JButton addRepo;
    private JList<String> watchedReposList;
    private JPanel mainPanel;
    private JButton removeRepoButton;
    private JButton helpButton;
    private JButton missedNotificationsButton;

    public GithubNotifierApp() {
        initialiseWindow();

        addRepo.addActionListener(new AddRepoButtonListener(newRepoName, watchedReposList));
        removeRepoButton.addActionListener(new RemoveRepoButtonListener(watchedReposList));
        helpButton.addActionListener(new HelpButtonListener());
        missedNotificationsButton.addActionListener(new MissedNotificationsButtonListener(this));

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication(e.getWindow());
            }
        });

        initializeKeyboardHandlers();
        initialiseWatchedReposList();
        initialiseTrayPopupMenu();
    }

    private void initialiseTrayPopupMenu() {
        TrayAdapter.getInstance().addPopupMenuItem("Reload", e -> logger.warn("Tray reload is not implemented yet"));
        TrayAdapter.getInstance().addPopupMenuItem("Exit", e -> exitApplication(this));
    }

    private void exitApplication(Window window) {
        logger.debug("Exiting ...");
        saveWatchedRepos();
        window.dispose();
        System.exit(0);
    }

    private void initialiseWatchedReposList() {
        populateWatchedReposList();
        watchedReposList.addFocusListener(new WatchedReposListFocusListener());
    }

    private void populateWatchedReposList() {
        logger.debug("Loading saved repos into UI ...");
        DefaultListModel<String> model = new DefaultListModel<>();

        DiskStorageService.getInstance().loadReposList().forEach(model::addElement);

        watchedReposList.setModel(model);
    }

    private void initialiseWindow() {
        logger.debug("Initialising UI ...");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("GitHub Repo Notifier");
        setContentPane(mainPanel);
        setWindowIcon();
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setWindowIcon() {
        ImageIcon appIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon.png")));

        try {
            logger.debug("Setting icon with reflection ...");
            Class<?> taskbar = Class.forName("java.awt.Taskbar");
            Method getTaskbar = taskbar.getDeclaredMethod("getTaskbar");
            Object instance = getTaskbar.invoke(taskbar);
            Method setIconImage = instance.getClass().getDeclaredMethod("setIconImage", Image.class);
            setIconImage.invoke(instance, appIcon.getImage());
        } catch (Throwable t) {
            logger.warn("Setting icon with reflection failed! Falling back to tradition way...");
            setIconImage(appIcon.getImage());
        }

    }

    private void initializeKeyboardHandlers() {
        logger.debug("Initialising keyboard handling ...");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlenterKeyStroke, CTRL_ENTER_ACTION_MAP_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlDKeyStroke, CTRL_D_ACTION_MAP_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f1KeyStroke, F1_ACTION_MAP_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlSKeyStroke, CTRL_S_ACTION_MAP_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlOKeyStroke, CTRL_O_ACTION_MAP_KEY);

        getRootPane().getActionMap().put(CTRL_ENTER_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Adding new repo to watch list ...");
                addRepo.doClick();
            }
        });
        getRootPane().getActionMap().put(CTRL_D_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Removing selected repo from watched list ...");
                removeRepoButton.doClick();
            }
        });
        getRootPane().getActionMap().put(F1_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Calling for help ...");
                helpButton.doClick();
            }
        });
        getRootPane().getActionMap().put(CTRL_S_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Saving watched repos to disk ...");
                saveWatchedRepos();
            }
        });
        getRootPane().getActionMap().put(CTRL_O_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Opening missed PRs ...");
                missedNotificationsButton.doClick();
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
        logger.debug("Saved repos!");
    }
}
