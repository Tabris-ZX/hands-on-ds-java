package boyuai.trainsys.datastructure;

import java.util.Comparator;

/**
 * 红黑树实现
 * @param <KeyType> 键类型
 * @param <ValueType> 值类型
 */
public class RedBlackTree<KeyType, ValueType> implements DynamicSearchTable<KeyType, ValueType> {
    
    /**
     * 红黑树节点颜色枚举
     */
    private enum Color { RED, BLACK }
    
    /**
     * 红黑树节点类
     */
    private class RedBlackNode {
        Color color;                    // 节点颜色
        RedBlackNode parent;           // 父节点
        RedBlackNode left;             // 左子节点
        RedBlackNode right;            // 右子节点
        DataType<KeyType, ValueType> data;  // 数据
        
        public RedBlackNode() {}
        
        public RedBlackNode(Color color, DataType<KeyType, ValueType> element, 
                           RedBlackNode parent, RedBlackNode left, RedBlackNode right) {
            this.color = color;
            this.data = element;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }
        
        public RedBlackNode(Color color, DataType<KeyType, ValueType> element, RedBlackNode parent) {
            this.color = color;
            this.data = element;
            this.parent = parent;
            this.left = null;
            this.right = null;
        }
    }
    
    private RedBlackNode root;
    private Comparator<KeyType> comparator;
    
    /**
     * 构造函数（使用默认比较器）
     */
    public RedBlackTree() {
        this.root = null;
        this.comparator = null;
    }
    
    /**
     * 构造函数（指定比较器）
     * @param comparator 键比较器
     */
    public RedBlackTree(Comparator<KeyType> comparator) {
        this.root = null;
        this.comparator = comparator;
    }
    
    /**
     * 比较两个键的大小
     * @param key1 第一个键
     * @param key2 第二个键
     * @return 比较结果
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
     * 递归销毁子树
     * @param node 当前节点
     */
    private void makeEmpty(RedBlackNode node) {
        if (node == null) return;
        makeEmpty(node.left);
        makeEmpty(node.right);
        // Java 自动垃圾回收，无需手动 delete
    }
    
    /**
     * 左旋操作
     * @param node 旋转中心节点
     */
    private void leftRotate(RedBlackNode node) {
        RedBlackNode rightChild = node.right;
        node.right = rightChild.left;
        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }
        rightChild.parent = node.parent;
        if (node.parent == null) {
            root = rightChild;
        } else if (node == node.parent.left) {
            node.parent.left = rightChild;
        } else {
            node.parent.right = rightChild;
        }
        rightChild.left = node;
        node.parent = rightChild;
    }
    
    /**
     * 右旋操作
     * @param node 旋转中心节点
     */
    private void rightRotate(RedBlackNode node) {
        RedBlackNode leftChild = node.left;
        node.left = leftChild.right;
        if (leftChild.right != null) {
            leftChild.right.parent = node;
        }
        leftChild.parent = node.parent;
        if (node.parent == null) {
            root = leftChild;
        } else if (node == node.parent.left) {
            node.parent.left = leftChild;
        } else {
            node.parent.right = leftChild;
        }
        leftChild.right = node;
        node.parent = leftChild;
    }
    
    /**
     * 插入后重新平衡
     * @param node 新插入的节点
     */
    private void insertionRebalance(RedBlackNode node) {
        if (node == null) return;
        
        while (node != root) {
            RedBlackNode parent = node.parent;
            if (parent.color == Color.BLACK) break; // 父节点为黑色，无需调整
            
            RedBlackNode grandparent = parent.parent;
            RedBlackNode uncle = (parent == grandparent.left) ? grandparent.right : grandparent.left;
            
            // 情况1：叔节点为红色
            if (uncle != null && uncle.color == Color.RED) {
                parent.color = Color.BLACK;
                uncle.color = Color.BLACK;
                grandparent.color = Color.RED;
                node = grandparent;
            }
            // 情况2：叔节点为黑色，需要旋转
            else {
                if (parent == grandparent.left && node == parent.left) {
                    parent.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    rightRotate(grandparent);
                } else if (parent == grandparent.left && node == parent.right) {
                    node.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    leftRotate(parent);
                    rightRotate(grandparent);
                } else if (parent == grandparent.right && node == parent.left) {
                    node.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    rightRotate(parent);
                    leftRotate(grandparent);
                } else if (parent == grandparent.right && node == parent.right) {
                    parent.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    leftRotate(grandparent);
                }
                break;
            }
        }
        root.color = Color.BLACK;
    }
    
    /**
     * 删除后重新平衡
     * @param node 被删除节点的子节点
     */
    private void removalRebalance(RedBlackNode node) {
        if (node == null) return;
        
        while (node != root) {
            RedBlackNode parent = node.parent;
            RedBlackNode sibling = (node == parent.left) ? parent.right : parent.left;
            
            // 情况1：兄弟节点为红色
            if (sibling.color == Color.RED) {
                parent.color = Color.RED;
                sibling.color = Color.BLACK;
                if (sibling == parent.left) {
                    rightRotate(parent);
                } else {
                    leftRotate(parent);
                }
            } else {
                // 检查兄弟节点的子节点颜色
                boolean leftChildBlack = sibling.left == null || sibling.left.color == Color.BLACK;
                boolean rightChildBlack = sibling.right == null || sibling.right.color == Color.BLACK;
                
                // 情况2：兄弟节点为黑色，其子节点都是黑色
                if (leftChildBlack && rightChildBlack) {
                    if (parent.color == Color.RED) {
                        sibling.color = Color.RED;
                        parent.color = Color.BLACK;
                        break;
                    } else {
                        sibling.color = Color.RED;
                        node = parent;
                    }
                } else {
                    // 情况3和4：兄弟节点为黑色，其特定子节点为红色
                    if (node == parent.left) {
                        if (rightChildBlack) {
                            sibling.left.color = Color.BLACK;
                            sibling.color = Color.RED;
                            rightRotate(sibling);
                        } else {
                            sibling.color = parent.color;
                            parent.color = Color.BLACK;
                            sibling.right.color = Color.BLACK;
                            leftRotate(parent);
                            break;
                        }
                    } else {
                        if (leftChildBlack) {
                            sibling.right.color = Color.BLACK;
                            sibling.color = Color.RED;
                            leftRotate(sibling);
                        } else {
                            sibling.color = parent.color;
                            parent.color = Color.BLACK;
                            sibling.left.color = Color.BLACK;
                            rightRotate(parent);
                            break;
                        }
                    }
                }
            }
        }
        root.color = Color.BLACK;
    }
    
    @Override
    public DataType<KeyType, ValueType> find(KeyType key) {
        RedBlackNode current = root;
        while (current != null) {
            int cmp = compare(key, current.data.key);
            if (cmp == 0) {
                return current.data;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }
    
    @Override
    public void insert(DataType<KeyType, ValueType> data) {
        insert(data, null, root);
    }
    
    /**
     * 递归插入节点
     * @param data 要插入的数据
     * @param parent 父节点
     * @param current 当前节点
     */
    private void insert(DataType<KeyType, ValueType> data, RedBlackNode parent, RedBlackNode current) {
        if (current == null) {
            RedBlackNode newNode = new RedBlackNode(Color.RED, data, parent);
            if (parent == null) {
                root = newNode;
            } else if (compare(data.key, parent.data.key) < 0) {
                parent.left = newNode;
            } else {
                parent.right = newNode;
            }
            insertionRebalance(newNode);
        } else {
            int cmp = compare(data.key, current.data.key);
            if (cmp < 0) {
                insert(data, current, current.left);
            } else if (cmp > 0) {
                insert(data, current, current.right);
            }
            // 相等时不插入（根据需求可以更新值）
        }
    }
    
    @Override
    public void remove(KeyType key) {
        remove(key, root);
    }
    
    /**
     * 递归删除节点
     * @param key 要删除的键
     * @param current 当前节点
     */
    private void remove(KeyType key, RedBlackNode current) {
        if (current == null) return;
        
        int cmp = compare(key, current.data.key);
        if (cmp == 0) {
            // 找到要删除的节点
            if (current.left != null && current.right != null) {
                // 有两个子节点，用后继节点替换
                RedBlackNode successor = current.right;
                while (successor.left != null) {
                    successor = successor.left;
                }
                current.data = successor.data;
                remove(successor.data.key, current.right);
            } else {
                // 最多有一个子节点
                RedBlackNode child = (current.left != null) ? current.left : current.right;
                
                if (current.color == Color.BLACK) {
                    if (child != null && child.color == Color.RED) {
                        child.color = Color.BLACK;
                    } else {
                        removalRebalance(current);
                    }
                }
                
                if (child != null) {
                    child.parent = current.parent;
                }
                
                if (current.parent == null) {
                    root = child;
                } else if (current == current.parent.left) {
                    current.parent.left = child;
                } else {
                    current.parent.right = child;
                }
            }
        } else if (cmp < 0) {
            remove(key, current.left);
        } else {
            remove(key, current.right);
        }
    }
}
