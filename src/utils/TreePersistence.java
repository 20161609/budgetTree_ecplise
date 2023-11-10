package utils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TreePersistence {

    // Method to save the tree model
    public static void saveTreeModel(DefaultTreeModel treeModel, String filename) throws IOException {
        if (treeModel == null) {
            throw new IllegalArgumentException("Cannot save a null tree model.");
        }
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty.");
        }
        
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(treeModel);
        }
    }

    // Method to load the tree model
    public static DefaultTreeModel loadTreeModel(String filename) throws IOException, ClassNotFoundException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty.");
        }
        
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("The file " + filename + " does not exist.");
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object object = ois.readObject();
            if (!(object instanceof DefaultTreeModel)) {
                throw new IllegalArgumentException("The file does not contain a valid tree model.");
            }
            return (DefaultTreeModel) object;
        }
    }
    
    // 재귀적으로 경로를 수집하는 보조 메서드입니다.
    private static void dfsPath(DefaultMutableTreeNode node, List<String> paths, String path) {
        // 현재 노드의 사용자 객체를 경로에 추가합니다.
        if (!path.isEmpty()) {
            path += "/";
        }
        path += node.getUserObject().toString();

        // 노드가 리프가 아니라면 자식들에 대해서 재귀적으로 수행합니다.
        if (!node.isLeaf() || node.isRoot()) {
            paths.add(path);
            for (int i = 0; i < node.getChildCount(); i++) {
            	dfsPath((DefaultMutableTreeNode) node.getChildAt(i), paths, path);
            }
        } else {
            // 노드가 리프라면 경로를 리스트에 추가합니다.
            paths.add(path);
        }
    }    

    public static List<String> collectPathsNewTree(DefaultTreeModel treeModel){
    	List<String> paths = new ArrayList<>();
    	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)treeModel.getRoot();
    	dfsPath(rootNode, paths, "");            
    	return paths;
    }
    
    public static List<String> collectPaths() {
    	List<String> paths = new ArrayList<>();
        try {
        	DefaultTreeModel treeModel = TreePersistence.loadTreeModel("TreeData.tree");
        	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)treeModel.getRoot();
        	dfsPath(rootNode, paths, "");            
        } catch (IOException | ClassNotFoundException ex) {

        }
        return paths;
    }    
}
