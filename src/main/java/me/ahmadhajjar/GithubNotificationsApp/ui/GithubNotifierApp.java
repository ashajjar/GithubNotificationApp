package me.ahmadhajjar.GithubNotificationsApp.ui;

import me.ahmadhajjar.GithubNotificationsApp.service.DiskStorageService;
import me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner.AddRepoButtonListener;
import me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner.HelpButtonListener;
import me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner.RemoveRepoButtonListener;
import me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner.WatchedReposListFocusListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GithubNotifierApp extends JFrame {
    public static final String CTRL_ENTER_ACTION_MAP_KEY = "ENTER";
    public static final String CTRL_D_ACTION_MAP_KEY = "CTRL_D";
    public static final String F1_ACTION_MAP_KEY = "F1";
    private static final String CTRL_S_ACTION_MAP_KEY = "CTRL_S";

    private final KeyStroke ctrlenterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK, true);
    private final KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK, true);
    private final KeyStroke f1KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, true);
    private final KeyStroke ctrlSKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, true);

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
        TrayAdapter.getInstance().addPopupMenuItem("Reload", e -> JOptionPane.showMessageDialog(null, "UI Says Hi"));
        TrayAdapter.getInstance().addPopupMenuItem("Exit", e -> exitApplication(this));
    }

    private void exitApplication(Window window) {
        System.out.println("Exiting ...");
        saveWatchedRepos();
        window.dispose();
        System.exit(0);
    }

    private void initialiseWatchedReposList() {
        populateWatchedReposList();
        watchedReposList.addFocusListener(new WatchedReposListFocusListener());
    }

    private void populateWatchedReposList() {
        System.out.println("Loading saved repos into UI ...");
        DefaultListModel<String> model = new DefaultListModel<>();

        DiskStorageService.getInstance().loadReposList().forEach(model::addElement);

        watchedReposList.setModel(model);
    }

    private void initialiseWindow() {
        System.out.println("Initialising UI ...");
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
        setIconImage(appIcon.getImage());
    }

    private void initializeKeyboardHandlers() {
        System.out.println("Initialising keyboard handling ...");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlenterKeyStroke, CTRL_ENTER_ACTION_MAP_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlDKeyStroke, CTRL_D_ACTION_MAP_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f1KeyStroke, F1_ACTION_MAP_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlSKeyStroke, CTRL_S_ACTION_MAP_KEY);
        getRootPane().getActionMap().put(CTRL_ENTER_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Adding new repo to watch list ...");
                addRepo.doClick();
            }
        });
        getRootPane().getActionMap().put(CTRL_D_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Removing selected repo from watched list ...");
                removeRepoButton.doClick();
            }
        });
        getRootPane().getActionMap().put(F1_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Calling for help ...");
                helpButton.doClick();
            }
        });
        getRootPane().getActionMap().put(CTRL_S_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Saving watched repos to disk ...");
                saveWatchedRepos();
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
        System.out.println("Saved repos!");
    }
}
