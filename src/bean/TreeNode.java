package bean;

import java.util.*;

public class TreeNode {
    public GeoPoint value;
    public TreeNode left;
    public TreeNode right;
    public int deepth;
    public int axis;

    public TreeNode(GeoPoint value, TreeNode left, TreeNode right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }

    public List<TreeNode> leaves() {
        List<TreeNode> lst = new ArrayList<TreeNode>();
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.offer(this);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node.isLeaf()) {
                lst.add(node);
            } else {
                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }
        }
        return lst;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    public void breadFirstPrint() {
        Queue<TreeNode> q = new LinkedList<TreeNode>();
        // 首先根节点入队
        q.offer(this);
        while (!q.isEmpty()) {
            // 出队，拿到出队元素
            TreeNode t = q.poll();
            System.out.println(t.value);
            //　判断左节点是否为空，不空入队
            if (t.left != null)
                // 入队
                q.offer(t.left);
            // 判断右节点是否为空
            if (t.right != null)
                q.offer(t.right);
        }

    }

    public void deepFirstPrint() {
        //　创建栈
        Stack<TreeNode> s = new Stack<TreeNode>();
        // 首先根节点入栈
        s.push(this);
        while (!s.empty()) {
            // 出栈
            TreeNode t = s.pop();
            System.out.println(t.value);
            // 判断右节点是否为空，不空入栈
            if (t.right != null)
                s.push(t.right);
            // 判断左节点是否为空，不空入栈
            if (t.left != null)
                s.push(t.left);
        }
    }

    public static void main(String args[]) {
        /*TreeNode n1 = new TreeNode(new GeoPoint(1.0f,2.0f), null, null);
        TreeNode n2 = new TreeNode(new GeoPoint(3.0f,4.0f), null, null);
        TreeNode n3 = new TreeNode(new GeoPoint(5.0f,6.0f), null, null);
        TreeNode n4 = new TreeNode(new GeoPoint(7.0f,8.0f), null, null);
        TreeNode n5 = new TreeNode(new GeoPoint(9.0f,10.0f), null, null);
        TreeNode n6 = new TreeNode(new GeoPoint(11.0f,12.0f), null, null);

        n1.left = n2;
        n1.right = n4;
        n2.left = n3;
        n2.right = n5;
        n3.left = n6;

        n1.breadFirstPrint();
        n1.deepFirstPrint();
*/
    }
}
