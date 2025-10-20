package boyuai.trainsys.datastructure;

import java.io.*;
import java.util.Comparator;

/**
 * B+树实现（持久化存储）
 * @param <KeyType> 键类型
 * @param <ValueType> 值类型
 * @param <M> 内部节点最大子节点数
 * @param <L> 叶子节点最大数据数
 */
public class BPlusTree<KeyType, ValueType> 
        implements StorageSearchTable<KeyType, ValueType> {
    
    private static final int DEFAULT_M = 100;
    private static final int DEFAULT_L = 100;
    
    private RandomAccessFile treeNodeFile;
    private RandomAccessFile leafFile;
    private int rearTreeNode;           // 最后一个树节点的位置
    private int rearLeaf;               // 最后一个叶子节点的位置
    private int sizeData;               // 数据个数
    private final int headerLengthOfTreeNodeFile = 2 * Integer.BYTES;
    private final int headerLengthOfLeafFile = 2 * Integer.BYTES;
    private SeqList<Integer> emptyTreeNode;  // 被删除的树节点位置
    private SeqList<Integer> emptyLeaf;      // 被删除的叶子节点位置
    
    /**
     * B+树内部节点
     */
    private class TreeNode {
        boolean isBottomNode;           // 是否是叶子节点的父节点
        int pos;                        // 节点位置
        int dataCount;                  // 子节点个数
        int[] childrenPos;              // 子节点位置数组
        Pair<KeyType, ValueType>[] septal;  // 分隔关键字数组
        
        @SuppressWarnings("unchecked")
        TreeNode(int m) {
            this.childrenPos = new int[m];
            this.septal = new Pair[m - 1];
            this.isBottomNode = false;
            this.pos = 0;
            this.dataCount = 0;
        }
    }
    
    /**
     * B+树叶子节点
     */
    private class Leaf {
        int nxt;                        // 下一个叶子节点位置
        int pos;                        // 当前叶子节点位置
        int dataCount;                  // 数据个数
        Pair<KeyType, ValueType>[] value;  // 数据数组
        
        @SuppressWarnings("unchecked")
        Leaf(int l) {
            this.value = new Pair[l];
            this.nxt = 0;
            this.pos = 0;
            this.dataCount = 0;
        }
    }
    
    private String treeNodeFileName;
    private String leafFileName;
    private TreeNode root;
    private Comparator<KeyType> comparator;
    private int m, l;  // 实际使用的M和L值
    
    /**
     * 构造函数
     * @param name 文件名前缀
     */
    public BPlusTree(String name) {
        this(name, DEFAULT_M, DEFAULT_L, null);
    }
    
    /**
     * 构造函数
     * @param name 文件名前缀
     * @param m 内部节点最大子节点数
     * @param l 叶子节点最大数据数
     * @param comparator 键比较器
     */
    @SuppressWarnings("unchecked")
    public BPlusTree(String name, int m, int l, Comparator<KeyType> comparator) {
        this.m = m;
        this.l = l;
        this.comparator = comparator;
        this.treeNodeFileName = name + "_treeNodeFile";
        this.leafFileName = name + "_leafFile";
        this.emptyTreeNode = new SeqList<>();
        this.emptyLeaf = new SeqList<>();
        
        try {
            // 打开文件
            this.treeNodeFile = new RandomAccessFile(treeNodeFileName, "rw");
            this.leafFile = new RandomAccessFile(leafFileName, "rw");

            long tlen = treeNodeFile.length();
            long llen = leafFile.length();

            boolean invalidHeader = (tlen < headerLengthOfTreeNodeFile) || (llen < headerLengthOfLeafFile);

            if (tlen == 0 || llen == 0 || invalidHeader) {
                // 文件不存在或头部不完整，重新初始化
                treeNodeFile.setLength(0);
                leafFile.setLength(0);
                initialize();
            } else {
                // 尝试从文件读取信息，若失败则回退到初始化
                try {
                    loadFromFile();
                } catch (Exception ex) {
                    treeNodeFile.setLength(0);
                    leafFile.setLength(0);
                    initialize();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize B+ tree", e);
        }
    }
    
    /**
     * 比较两个键的大小
     */
    @SuppressWarnings("unchecked")
    private int compare(KeyType key1, KeyType key2) {
        if (comparator != null) {
            return comparator.compare(key1, key2);
        } else if (key1 instanceof Comparable) {
            return ((Comparable<KeyType>) key1).compareTo(key2);
        } else {
            throw new IllegalArgumentException("KeyType must implement Comparable or provide a Comparator");
        }
    }
    
    /**
     * 比较两个Pair的大小
     */
    private boolean checkPairLess(Pair<KeyType, ValueType> lhs, Pair<KeyType, ValueType> rhs) {
        int keyCmp = compare(lhs.getKey(), rhs.getKey());
        if (keyCmp != 0) {
            return keyCmp < 0;
        } else {
            // 键相等时比较值
            if (lhs.getValue() instanceof Comparable && rhs.getValue() instanceof Comparable) {
                return ((Comparable<ValueType>) lhs.getValue()).compareTo(rhs.getValue()) < 0;
            }
            return lhs.getValue().hashCode() < rhs.getValue().hashCode();
        }
    }
    
    /**
     * 比较两个Pair是否相等
     */
    private boolean checkPairEqual(Pair<KeyType, ValueType> lhs, Pair<KeyType, ValueType> rhs) {
        return lhs.getKey().equals(rhs.getKey()) && lhs.getValue().equals(rhs.getValue());
    }
    
    /**
     * 初始化B+树
     */
    private void initialize() throws IOException {
        // 创建根节点
        root = new TreeNode(m);
        root.isBottomNode = true;
        root.pos = 1;
        root.dataCount = 1;
        root.childrenPos[0] = 1;
        
        // 创建初始叶子节点
        Leaf initLeaf = new Leaf(l);
        initLeaf.nxt = 0;
        initLeaf.dataCount = 0;
        initLeaf.pos = 1;
        
        // 写入文件
        writeLeaf(initLeaf);
        writeTreeNode(root);
        
        rearLeaf = rearTreeNode = 1;
        sizeData = 0;
    }
    
    /**
     * 从文件加载B+树
     */
    private void loadFromFile() throws IOException {
        // 读取树节点文件头部
        treeNodeFile.seek(0);
        int rootPos = treeNodeFile.readInt();
        rearTreeNode = treeNodeFile.readInt();
        
        // 读取根节点
        root = new TreeNode(m);
        readTreeNode(root, rootPos);
        
        // 读取被删除的树节点
        treeNodeFile.seek(headerLengthOfTreeNodeFile + (rearTreeNode + 1) * getTreeNodeSize());
        int treeNodeEmptySize = treeNodeFile.readInt();
        for (int i = 0; i < treeNodeEmptySize; i++) {
            emptyTreeNode.pushBack(treeNodeFile.readInt());
        }
        
        // 读取叶子节点文件头部
        leafFile.seek(0);
        rearLeaf = leafFile.readInt();
        sizeData = leafFile.readInt();
        
        // 读取被删除的叶子节点
        leafFile.seek(headerLengthOfLeafFile + (rearLeaf + 1) * getLeafSize());
        int leafEmptySize = leafFile.readInt();
        for (int i = 0; i < leafEmptySize; i++) {
            emptyLeaf.pushBack(leafFile.readInt());
        }
    }
    
    /**
     * 获取树节点序列化后的大小
     */
    private int getTreeNodeSize() {
        return 1 + 4 + 4 + m * 4 + (m - 1) * 16; // 简化计算，实际需要根据具体类型调整
    }
    
    /**
     * 获取叶子节点序列化后的大小
     */
    private int getLeafSize() {
        return 4 + 4 + 4 + l * 16; // 简化计算，实际需要根据具体类型调整
    }
    
    /**
     * 写入树节点到文件
     */
    private void writeTreeNode(TreeNode node) throws IOException {
        treeNodeFile.seek(headerLengthOfTreeNodeFile + node.pos * getTreeNodeSize());
        treeNodeFile.writeBoolean(node.isBottomNode);
        treeNodeFile.writeInt(node.pos);
        treeNodeFile.writeInt(node.dataCount);
        for (int i = 0; i < m; i++) {
            treeNodeFile.writeInt(node.childrenPos[i]);
        }
        for (int i = 0; i < m - 1; i++) {
            if (node.septal[i] != null) {
                treeNodeFile.writeUTF(node.septal[i].getKey().toString());
                treeNodeFile.writeUTF(node.septal[i].getValue().toString());
            } else {
                treeNodeFile.writeUTF("");
                treeNodeFile.writeUTF("");
            }
        }
    }
    
    /**
     * 写入叶子节点到文件
     */
    private void writeLeaf(Leaf leaf) throws IOException {
        leafFile.seek(headerLengthOfLeafFile + leaf.pos * getLeafSize());
        leafFile.writeInt(leaf.nxt);
        leafFile.writeInt(leaf.pos);
        leafFile.writeInt(leaf.dataCount);
        for (int i = 0; i < l; i++) {
            if (leaf.value[i] != null) {
                leafFile.writeUTF(leaf.value[i].getKey().toString());
                leafFile.writeUTF(leaf.value[i].getValue().toString());
            } else {
                leafFile.writeUTF("");
                leafFile.writeUTF("");
            }
        }
    }
    
    /**
     * 从文件读取树节点
     */
    private void readTreeNode(TreeNode node, int pos) throws IOException {
        treeNodeFile.seek(headerLengthOfTreeNodeFile + pos * getTreeNodeSize());
        node.isBottomNode = treeNodeFile.readBoolean();
        node.pos = treeNodeFile.readInt();
        node.dataCount = treeNodeFile.readInt();
        for (int i = 0; i < m; i++) {
            node.childrenPos[i] = treeNodeFile.readInt();
        }
        // 注意：这里简化了Pair的读取，实际应用中需要根据具体类型进行反序列化
        for (int i = 0; i < m - 1; i++) {
            String keyStr = treeNodeFile.readUTF();
            String valueStr = treeNodeFile.readUTF();
            if (!keyStr.isEmpty()) {
                // 这里需要根据实际类型进行转换，暂时设为null
                node.septal[i] = null;
            }
        }
    }
    
    /**
     * 从文件读取叶子节点
     */
    private void readLeaf(Leaf leaf, int pos) throws IOException {
        leafFile.seek(headerLengthOfLeafFile + pos * getLeafSize());
        leaf.nxt = leafFile.readInt();
        leaf.pos = leafFile.readInt();
        leaf.dataCount = leafFile.readInt();
        // 注意：这里简化了Pair的读取，实际应用中需要根据具体类型进行反序列化
        for (int i = 0; i < l; i++) {
            String keyStr = leafFile.readUTF();
            String valueStr = leafFile.readUTF();
            if (!keyStr.isEmpty()) {
                // 这里需要根据实际类型进行转换，暂时设为null
                leaf.value[i] = null;
            }
        }
    }
    
    public int size() {
        return sizeData;
    }
    
    @Override
    public SeqList<ValueType> find(KeyType key) {
        SeqList<ValueType> result = new SeqList<>();
        try {
            TreeNode current = root;
            Leaf leaf = new Leaf(l);
            
            if (current.dataCount == 0) {
                return result;
            }
            
            // 找到叶子节点
            while (!current.isBottomNode) {
                int childIndex = binarySearchTreeNode(key, current);
                readTreeNode(current, current.childrenPos[childIndex]);
            }
            
            // 在叶子节点中查找
            readLeaf(leaf, current.childrenPos[binarySearchTreeNode(key, current)]);
            int index = binarySearchLeaf(key, leaf);
            
            // 收集所有匹配的值
            while (index < leaf.dataCount && leaf.value[index] != null && 
                   leaf.value[index].getKey().equals(key)) {
                result.pushBack(leaf.value[index].getValue());
                index++;
            }
            
            // 检查下一个叶子节点
            while (leaf.nxt != 0 && index == leaf.dataCount) {
                readLeaf(leaf, leaf.nxt);
                index = 0;
                while (index < leaf.dataCount && leaf.value[index] != null && 
                       leaf.value[index].getKey().equals(key)) {
                    result.pushBack(leaf.value[index].getValue());
                    index++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to find in B+ tree", e);
        }
        return result;
    }
    
    @Override
    public void insert(KeyType key, ValueType value) {
        try {
            Pair<KeyType, ValueType> pair = new Pair<>(key, value);
            if (insert(pair, root)) {
                // 根节点分裂，创建新的根节点
                TreeNode newRoot = new TreeNode(m);
                TreeNode newNode = new TreeNode(m);
                newNode.pos = getNewTreeNodePos();
                newNode.isBottomNode = root.isBottomNode;
                newNode.dataCount = m / 2;
                
                int mid = m / 2;
                for (int i = 0; i < mid; i++) {
                    newNode.childrenPos[i] = root.childrenPos[mid + i];
                }
                for (int i = 0; i < mid - 1; i++) {
                    newNode.septal[i] = root.septal[mid + i];
                }
                
                root.dataCount = mid;
                writeTreeNode(root);
                writeTreeNode(newNode);
                
                newRoot.dataCount = 2;
                newRoot.pos = getNewTreeNodePos();
                newRoot.isBottomNode = false;
                newRoot.childrenPos[0] = root.pos;
                newRoot.childrenPos[1] = newNode.pos;
                newRoot.septal[0] = root.septal[mid - 1];
                root = newRoot;
                writeTreeNode(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to insert into B+ tree", e);
        }
    }
    
    @Override
    public void remove(KeyType key, ValueType value) {
        try {
            Pair<KeyType, ValueType> pair = new Pair<>(key, value);
            if (remove(pair, root)) {
                if (!root.isBottomNode && root.dataCount == 1) {
                    // 根节点只有一个子节点，将子节点作为新的根
                    TreeNode son = new TreeNode(m);
                    readTreeNode(son, root.childrenPos[0]);
                    emptyTreeNode.pushBack(root.pos);
                    root = son;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to remove from B+ tree", e);
        }
    }
    
    /**
     * 递归插入
     */
    private boolean insert(Pair<KeyType, ValueType> pair, TreeNode currentNode) throws IOException {
        if (currentNode.isBottomNode) {
            // 叶子节点层
            Leaf leaf = new Leaf(l);
            int nodePos = binarySearchTreeNodeValue(pair, currentNode);
            readLeaf(leaf, currentNode.childrenPos[nodePos]);
            int leafPos = binarySearchLeafValue(pair, leaf);
            
            // 插入数据
            leaf.dataCount++;
            sizeData++;
            for (int i = leaf.dataCount - 1; i > leafPos; i--) {
                leaf.value[i] = leaf.value[i - 1];
            }
            leaf.value[leafPos] = pair;
            
            if (leaf.dataCount == l) {
                // 叶子节点满了，需要分裂
                Leaf newLeaf = new Leaf(l);
                newLeaf.pos = getNewLeafPos();
                newLeaf.nxt = leaf.nxt;
                leaf.nxt = newLeaf.pos;
                
                int mid = l / 2;
                for (int i = 0; i < mid; i++) {
                    newLeaf.value[i] = leaf.value[i + mid];
                }
                leaf.dataCount = newLeaf.dataCount = mid;
                
                writeLeaf(leaf);
                writeLeaf(newLeaf);
                
                // 更新父节点
                for (int i = currentNode.dataCount; i > nodePos + 1; i--) {
                    currentNode.childrenPos[i] = currentNode.childrenPos[i - 1];
                }
                currentNode.childrenPos[nodePos + 1] = newLeaf.pos;
                for (int i = currentNode.dataCount - 1; i > nodePos; i--) {
                    currentNode.septal[i] = currentNode.septal[i - 1];
                }
                currentNode.septal[nodePos] = leaf.value[mid - 1];
                currentNode.dataCount++;
                
                if (currentNode.dataCount == m) {
                    return true; // 需要继续分裂
                } else {
                    writeTreeNode(currentNode);
                    return false;
                }
            } else {
                writeLeaf(leaf);
                return false;
            }
        } else {
            // 内部节点层
            TreeNode son = new TreeNode(m);
            int now = binarySearchTreeNodeValue(pair, currentNode);
            readTreeNode(son, currentNode.childrenPos[now]);
            
            if (insert(pair, son)) {
                // 子节点分裂
                TreeNode newNode = new TreeNode(m);
                newNode.pos = getNewTreeNodePos();
                newNode.isBottomNode = son.isBottomNode;
                
                int mid = m / 2;
                for (int i = 0; i < mid; i++) {
                    newNode.childrenPos[i] = son.childrenPos[mid + i];
                }
                for (int i = 0; i < mid - 1; i++) {
                    newNode.septal[i] = son.septal[mid + i];
                }
                newNode.dataCount = son.dataCount = mid;
                
                writeTreeNode(son);
                writeTreeNode(newNode);
                
                // 更新当前节点
                for (int i = currentNode.dataCount; i > now + 1; i--) {
                    currentNode.childrenPos[i] = currentNode.childrenPos[i - 1];
                }
                currentNode.childrenPos[now + 1] = newNode.pos;
                for (int i = currentNode.dataCount - 1; i > now; i--) {
                    currentNode.septal[i] = currentNode.septal[i - 1];
                }
                currentNode.septal[now] = son.septal[mid - 1];
                currentNode.dataCount++;
                
                if (currentNode.dataCount == m) {
                    return true; // 需要继续分裂
                } else {
                    writeTreeNode(currentNode);
                    return false;
                }
            } else {
                return false;
            }
        }
    }
    
    /**
     * 递归删除
     */
    private boolean remove(Pair<KeyType, ValueType> pair, TreeNode currentNode) throws IOException {
        if (currentNode.isBottomNode) {
            // 叶子节点层
            Leaf leaf = new Leaf(l);
            int nodePos = binarySearchTreeNodeValue(pair, currentNode);
            readLeaf(leaf, currentNode.childrenPos[nodePos]);
            int leafPos = binarySearchLeafValue(pair, leaf);
            
            if (leafPos == leaf.dataCount || !checkPairEqual(leaf.value[leafPos], pair)) {
                return false; // 未找到
            }
            
            leaf.dataCount--;
            sizeData--;
            for (int i = leafPos; i < leaf.dataCount; i++) {
                leaf.value[i] = leaf.value[i + 1];
            }
            
            if (leaf.dataCount < l / 2) {
                // 需要合并或借节点
                // 这里简化处理，实际需要实现复杂的合并逻辑
                writeLeaf(leaf);
                return currentNode.dataCount < m / 2;
            } else {
                writeLeaf(leaf);
                return false;
            }
        } else {
            // 内部节点层
            TreeNode son = new TreeNode(m);
            int now = binarySearchTreeNodeValue(pair, currentNode);
            readTreeNode(son, currentNode.childrenPos[now]);
            
            if (remove(pair, son)) {
                // 子节点删除后需要调整
                // 这里简化处理，实际需要实现复杂的合并逻辑
                writeTreeNode(son);
                return currentNode.dataCount < m / 2;
            } else {
                return false;
            }
        }
    }
    
    /**
     * 在树节点中二分查找
     */
    private int binarySearchTreeNode(KeyType key, TreeNode node) {
        int left = 0, right = node.dataCount - 2, ans = node.dataCount - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (node.septal[mid] != null && compare(node.septal[mid].getKey(), key) < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
                ans = mid;
            }
        }
        return ans;
    }
    
    /**
     * 在叶子节点中二分查找
     */
    private int binarySearchLeaf(KeyType key, Leaf leaf) {
        int left = 0, right = leaf.dataCount - 1, ans = leaf.dataCount;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (leaf.value[mid] != null && compare(leaf.value[mid].getKey(), key) < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
                ans = mid;
            }
        }
        return ans;
    }
    
    /**
     * 在树节点中二分查找Pair
     */
    private int binarySearchTreeNodeValue(Pair<KeyType, ValueType> pair, TreeNode node) {
        // 当 dataCount <= 1（仅一个子指针）时，直接返回最后一个子指针位置
        if (node.dataCount <= 1) {
            return Math.max(0, node.dataCount - 1);
        }
        int left = 0;
        int right = node.dataCount - 2; // septal 的合法下标范围 [0, dataCount-2]
        int ans = node.dataCount - 1;   // 默认落到最后一个子指针
        while (left <= right) {
            int mid = (left + right) >>> 1;
            if (node.septal[mid] != null && checkPairLess(node.septal[mid], pair)) {
                left = mid + 1;
            } else {
                right = mid - 1;
                ans = mid;
            }
        }
        return ans;
    }
    
    /**
     * 在叶子节点中二分查找Pair
     */
    private int binarySearchLeafValue(Pair<KeyType, ValueType> pair, Leaf leaf) {
        int left = 0, right = leaf.dataCount - 1, ans = leaf.dataCount;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (leaf.value[mid] != null && checkPairLess(leaf.value[mid], pair)) {
                left = mid + 1;
            } else {
                right = mid - 1;
                ans = mid;
            }
        }
        return ans;
    }
    
    /**
     * 获取新的树节点位置
     */
    private int getNewTreeNodePos() {
        if (emptyTreeNode.Empty()) {
            return ++rearTreeNode;
        } else {
            int pos = emptyTreeNode.back();
            emptyTreeNode.popBack();
            return pos;
        }
    }
    
    /**
     * 获取新的叶子节点位置
     */
    private int getNewLeafPos() {
        if (emptyLeaf.Empty()) {
            return ++rearLeaf;
        } else {
            int pos = emptyLeaf.back();
            emptyLeaf.popBack();
            return pos;
        }
    }
    
    /**
     * 清空B+树
     */
    public void clear() {
        try {
            treeNodeFile.close();
            leafFile.close();
            emptyTreeNode.clear();
            emptyLeaf.clear();
            initialize();
        } catch (IOException e) {
            throw new RuntimeException("Failed to clear B+ tree", e);
        }
    }
    
    /**
     * 关闭文件
     */
    public void close() {
        try {
            if (treeNodeFile != null) {
                // 保存状态到文件
                treeNodeFile.seek(0);
                treeNodeFile.writeInt(root.pos);
                treeNodeFile.writeInt(rearTreeNode);
                writeTreeNode(root);
                
                leafFile.seek(0);
                leafFile.writeInt(rearLeaf);
                leafFile.writeInt(sizeData);
                
                // 保存空闲节点列表
                treeNodeFile.seek(headerLengthOfTreeNodeFile + (rearTreeNode + 1) * getTreeNodeSize());
                treeNodeFile.writeInt(emptyTreeNode.length());
                for (int i = 0; i < emptyTreeNode.length(); i++) {
                    treeNodeFile.writeInt(emptyTreeNode.visit(i));
                }
                
                leafFile.seek(headerLengthOfLeafFile + (rearLeaf + 1) * getLeafSize());
                leafFile.writeInt(emptyLeaf.length());
                for (int i = 0; i < emptyLeaf.length(); i++) {
                    leafFile.writeInt(emptyLeaf.visit(i));
                }
                
                treeNodeFile.close();
                leafFile.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to close B+ tree", e);
        }
    }
}
