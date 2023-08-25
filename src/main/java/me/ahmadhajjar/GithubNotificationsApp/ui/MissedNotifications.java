package me.ahmadhajjar.GithubNotificationsApp.ui;

import me.ahmadhajjar.GithubNotificationsApp.service.DiskStorageService;
import me.ahmadhajjar.GithubNotificationsApp.utils.PROpener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

public class MissedNotifications extends JFrame {
    private static final Logger logger = LogManager.getLogger(MissedNotifications.class);
    private static final String ESC_ACTION_MAP_KEY = "ESC";
    private static final String ENTER_ACTION_MAP_KEY = "ENTER";
    private final KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
    private final KeyStroke enterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true);

    private JPanel mainPanel;
    private JScrollPane scroller;
    private JTree reposPRsTRee;

    public MissedNotifications(JFrame parent) {
        setContentPane(mainPanel);

        setTitle("Missed Notifications");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setAlwaysOnTop(true);
        setResizable(false);
        setSize(350, 600);
        setLocation(parent.getX() + parent.getWidth(), parent.getY());
        setType(Type.UTILITY);
        setUndecorated(true);

        initialiseKeyboardHandlers();
        initialiseTree();
    }

    private void initialiseTree() {
        reposPRsTRee.setEditable(false);
        reposPRsTRee.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        populateTree();

        reposPRsTRee.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() < 2) {
                    return;
                }

                JTree jTree = (JTree) e.getSource();

                handleSelectionInteraction(jTree);
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

    private void populateTree() {
        var treeRoot = new DefaultMutableTreeNode("All Currently Open PRs");
        var reposPRs = DiskStorageService.getInstance().loadReposPRList();
        for (String repoName : reposPRs.keySet()) {
            var repoTreeNode = new DefaultMutableTreeNode(repoName);

            for (Integer prNumber : reposPRs.get(repoName)) {
                var child = new DefaultMutableTreeNode(prNumber);
                repoTreeNode.add(child);
            }

            treeRoot.add(repoTreeNode);

        }
        reposPRsTRee.setModel(new DefaultTreeModel(treeRoot));
    }

    private void handleSelectionInteraction(JTree jTree) {
        TreePath selectionPath = jTree.getSelectionPath();
        if (selectionPath == null) {
            return;
        }

        if (selectionPath.getPathCount() != 3) {
            return;
        }

        var prNumber = selectionPath.getLastPathComponent().toString();
        var repoName = selectionPath.getPathComponent(1);

        final URI prURI = URI.create("https://github.com/" + repoName + "/pull/" + prNumber);
        PROpener.open(prURI);
    }

    private void initialiseKeyboardHandlers() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, ESC_ACTION_MAP_KEY);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKeyStroke, ENTER_ACTION_MAP_KEY);

        getRootPane().getActionMap().put(ESC_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Closing missed PRs...");
                dispose();
            }
        });
        getRootPane().getActionMap().put(ENTER_ACTION_MAP_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSelectionInteraction(reposPRsTRee);
            }
        });
    }
}
