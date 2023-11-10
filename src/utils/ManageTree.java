package utils;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import transactionController.Database;

@SuppressWarnings("serial")
public class ManageTree extends JFrame {

    static List<String> paths = TreePersistence.collectPaths();
	private DefaultTreeModel treeModel;
    private JTree tree;

    public ManageTree() {
    	paths = TreePersistence.collectPaths();

    	System.out.println(paths);
        // Initialize the tree model with a root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(root);

        // Initialize the JTree with the model
        tree = new JTree(treeModel);
        loadTree();

        // Basic window settings
        setTitle("Manage Tree");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Panel setup
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // Add buttons
        JButton btnAddBranch = new JButton("Add Branch");
        JButton btnDeleteBranch = new JButton("Delete Branch");
        JButton btnSave = new JButton("Save");
        JButton btnLoad = new JButton("Load");

        // Add action listeners for buttons
        btnAddBranch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBranch();
            }
        });

        btnDeleteBranch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBranch();
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTree();
            }
        });

        btnLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTree();
            }
        });

        // Add buttons to panel
        panel.add(btnAddBranch);
        panel.add(btnDeleteBranch);
        panel.add(btnSave);
        panel.add(btnLoad);

        // Add panel to frame
        this.add(panel, BorderLayout.SOUTH);
        this.add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    private void addBranch() {
        // Get the selected node
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        System.out.println(selectedNode);
        // If no node is selected, select the root node by default
        if (selectedNode == null) {
            selectedNode = (DefaultMutableTreeNode) treeModel.getRoot();
        }

        // Ask the user for the name of the new node
        String nodeName = JOptionPane.showInputDialog(this, "Enter the name of the new branch:");

        // If the input is not null or empty, add the branch
        if (nodeName != null && !nodeName.trim().isEmpty()) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeName.trim());
            
            // Add the new node as a child to the selected node
            treeModel.insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
            
            // Ensure the user can see the new node and expand the path to the new node
            tree.expandPath(new TreePath(selectedNode.getPath()));
            tree.scrollPathToVisible(new TreePath(newNode.getPath()));

            // Notify the JTree component to refresh its display
            treeModel.reload(selectedNode);

            System.out.println("Branch '" + nodeName + "' added.");
        } else {
            System.out.println("Add branch cancelled.");
        }
    }

    private void deleteBranch() {
        // Get the selected node
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        System.out.println(selectedNode);
        
        // If no node is selected or if the selected node is the root, alert the user
        if (selectedNode == null || selectedNode.isRoot()) {
            JOptionPane.showMessageDialog(this, "No valid branch selected for deletion.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm with the user that they want to delete the node
        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected branch?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            // Remove the node from its parent
            treeModel.removeNodeFromParent(selectedNode);

            // Notify the JTree component to refresh its display
            // No need to call reload here because removeNodeFromParent will notify the model listeners
            System.out.println("Branch deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void saveTree() {
        try {
          TreePersistence.saveTreeModel(treeModel, "TreeData.tree");
          JOptionPane.showMessageDialog(this, "Tree saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
          
          List<String> new_paths = TreePersistence.collectPathsNewTree(treeModel);
          HashSet<String> new_paths_hash = new HashSet<>(new_paths);
          
          String sql = "";

          for(int i = 0; i < paths.size(); i++) {
        	  if(new_paths_hash.contains(paths.get(i))==false) {
        		  if(sql.length() == 0) {
        			  sql = "DELETE FROM transactions WHERE branch = '" + paths.get(i) + "'";
        		  }else {
        			  sql += "or branch = '" + paths.get(i) + "'";
        		  }
        	  }
          }
          if(sql.length()>0) {
              Statement stmt = null;
              Database db = new Database();
              Connection con = db.getConnection();
              stmt = con.createStatement();
              stmt.executeUpdate(sql);
          }

          System.out.println(sql);
          
        } catch (/*IOException ex*/Exception e) {
        	System.out.println(e);
//            ex.printStackTrace();
  //          JOptionPane.showMessageDialog(this, "Failed to save the tree.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadTree() {
        try {
        	DefaultTreeModel loadedTreeModel = TreePersistence.loadTreeModel("TreeData.tree");

            // Swing utilities to ensure EDT is utilized for UI changes
            EventQueue.invokeLater(() -> {
            	treeModel = loadedTreeModel;
                tree.setModel(loadedTreeModel);

                // Refresh the entire tree structure from the root
                ((DefaultMutableTreeNode)loadedTreeModel.getRoot()).setUserObject("Root");
                loadedTreeModel.reload();
                System.out.println("Tree loaded successfully!");
            });
            
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load the tree.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ManageTree().setVisible(true);
            }
        });
    }    
}
