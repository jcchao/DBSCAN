package tools;

import bean.*;

import java.util.*;

public class KDTree {

    public double minx;
    public double maxx;
    public double miny;
    public double maxy;
    private GeoMapTools gmt = new GeoMapTools();

    private double std(double[] arr) {
        double sum = 0;
        for (int i=0; i<arr.length; i++) {
            sum += arr[i];
        }
        double avg = sum / arr.length;
        double std = 0;
        for (int i=0; i<arr.length; i++) {
            std += Math.pow((arr[i]-avg), 2);
        }
        return Math.sqrt(std/(arr.length-1));
    }

    private Double[] distinct(double[] arr) {
        HashSet<Double> hs = new HashSet<Double>();
        for (double i: arr) {
            hs.add(i);
        }

        Double[] d = new Double[hs.size()];
        return hs.toArray(d);
    }

    private double median(double[] arr) {
        Double[] distinctArr= this.distinct(arr);
        if (distinctArr.length % 2 == 0)
            return (arr[distinctArr.length/2-1] + arr[distinctArr.length/2]) / 2.0;
        else
            return arr[distinctArr.length/2];
    }

    private int medianIndex(GeoPointSet dataSet, double median, int axis) {
        int i = 0;
        for (GeoPoint p: dataSet.value) {
            if (p.getValueByAxis(axis) <= median)
                i++;
            else
                break;
        }
        return i-1;
    }

    private List<TreeNode> traverseChecking(TreeNode node, Rectangle rect, GeoPoint p, double r) {
        List<TreeNode> lst = new ArrayList<TreeNode>();
        if (node.isLeaf()) {
            if (gmt.calDistance(p, node.value) <= r)
                lst.add(node);
            return lst;
        } else if (rect.minDistance(p) > r) {
            return lst;
        } else if (rect.maxDistance(p) < r) {
            for (TreeNode leaf: node.leaves()){
                if (gmt.calDistance(p, leaf.value) <= r)
                    lst.add(leaf);
            }
            return lst;
        } else {
            Rectangle[] rectangles = rect.split(node.value.getValueByAxis(node.axis), node.axis);
            List<TreeNode> left = traverseChecking(node.left, rectangles[0], p, r);
            List<TreeNode> right = traverseChecking(node.right, rectangles[1], p, r);
            left.addAll(right);
            return left;
        }
    }

    public TreeNode kdTreeRecursive(GeoPointSet dataSet, int deepth) {
        // 获取数据维度数
        if (dataSet.isEmpty())
            return null;
        int k = dataSet.value.get(0).dim;
        int axis = deepth % k;
        dataSet = dataSet.sort(axis);
        int median = dataSet.length / 2;
        return new TreeNode(dataSet.value.get(median), kdTreeRecursive(dataSet.slice(0, median), deepth+1),
                kdTreeRecursive(dataSet.slice(median+1, dataSet.length), deepth+1));

    }

    public TreeNode kdTree(GeoPointSet dataSet) {
        /**
         * 为了解决横坐标相似或纵坐标相似这种请款，分裂axis以方差较大的轴为准，分裂点的选择以尽量维持左右平衡为原则，
         * 在保证左子树小于等于，右子树大于的情况下尽量维持左右平衡。
         * 例如：(4,0) (4,2) (1,6) (4,6) (5,6) (0,8)应以y轴为分裂方向，
         * 以x轴排序：(4,0) (4,2) (1,6) (4,6) (5,6) (0,8)
         * 0,2,6,8的中位数是４所以分裂点应选(1,4)，非分裂轴的值取分裂点右边的点对应的非分裂轴的值。
         */
        int count = 0;
        Queue<QueueContext> queue = new LinkedList<QueueContext>();
        //判断分裂方向，选取方差较大的轴作为分裂方向
        int axis = this.std(dataSet.getValuesByAxis(0)) > this.std(dataSet.getValuesByAxis(1)) ? 0:1;
        //按分裂方向排序
        GeoPointSet dataSetSorted = dataSet.sort(axis);
        //设置kd-tree的取值范围
        if (axis == 0) {
            this.minx = dataSetSorted.value.get(0).x;
            this.maxx = dataSetSorted.value.get(dataSetSorted.length-1).x;
            GeoPointSet otherSorted = dataSet.sort(1);
            this.miny = otherSorted.value.get(0).y;
            this.maxy = otherSorted.value.get(otherSorted.length-1).y;
        } else {
            this.miny = dataSetSorted.value.get(0).y;
            this.maxy = dataSetSorted.value.get(dataSetSorted.length-1).y;
            GeoPointSet otherSorted = dataSet.sort(0);
            this.minx = otherSorted.value.get(0).x;
            this.maxx = otherSorted.value.get(otherSorted.length-1).x;
        }
        //获取去重后的中位数
        double median = this.median(dataSetSorted.getValuesByAxis(axis));
        //获取分裂点的index
        int medianIndex = this.medianIndex(dataSetSorted, median, axis);
//        System.out.println(medianIndex);
        //生成分裂点
        GeoPoint medianNode = new GeoPoint(0.0,0.0);
        medianNode.setValueByAxis(median, axis);
        medianNode.setValueByAxis(dataSetSorted.value.get(medianIndex).getValueByAxis(axis==0?1:0), axis==0?1:0);
        TreeNode root = new TreeNode(medianNode, null, null);
        //记住当前节点的分裂方向
        root.axis = axis;
        queue.offer(new QueueContext(root, dataSetSorted.slice(0, medianIndex+1), dataSetSorted.slice(medianIndex+1, dataSetSorted.length)));

        while (!queue.isEmpty()) {
            QueueContext queueContext = queue.poll();
//            System.out.println("出队元素:" + queueContext.center + " axis: " + queueContext.center.axis + " leftset:" + queueContext.leftSet +
//                    " rightset:" + queueContext.rightSet);
//            System.exit(0);
            count += 1;
            if (queueContext.leftSet.length > 1) {
                //判断分裂方向
                int leftAxis = this.std(queueContext.leftSet.getValuesByAxis(0)) > this.std(queueContext.leftSet.getValuesByAxis(1)) ? 0:1;
                //按分裂方向排序
                GeoPointSet leftSorted = queueContext.leftSet.sort(leftAxis);
                double leftMedian = this.median(leftSorted.getValuesByAxis(leftAxis));
                int leftMedianIndex = this.medianIndex(leftSorted, leftMedian, leftAxis);
                //生成分裂点
                GeoPoint leftMedianNode = new GeoPoint(0.0,0.0);
                leftMedianNode.setValueByAxis(leftMedian, leftAxis);
                leftMedianNode.setValueByAxis(leftSorted.value.get(leftMedianIndex).getValueByAxis(leftAxis==0?1:0), leftAxis==0?1:0);
                TreeNode node = new TreeNode(leftMedianNode, null, null);
                //记住当前节点的分裂方向
                node.axis = leftAxis;
                //构建左子树根节点
                queueContext.center.left = node;
                //左子树根节点及其孩子入队
                queue.offer(new QueueContext(node, leftSorted.slice(0, leftMedianIndex+1), leftSorted.slice(leftMedianIndex+1, leftSorted.length)));
            } else if (queueContext.leftSet.length == 1){
                queueContext.center.left = new TreeNode(queueContext.leftSet.value.get(0), null, null);
            }

            if (queueContext.rightSet.length > 1) {
                //判断分裂方向
                int rightAxis = this.std(queueContext.rightSet.getValuesByAxis(0)) > this.std(queueContext.rightSet.getValuesByAxis(1)) ? 0:1;
                //按分裂方向排序
                GeoPointSet rightSorted = queueContext.rightSet.sort(rightAxis);
                double rightMedian = this.median(rightSorted.getValuesByAxis(rightAxis));
                int rightMedianIndex = this.medianIndex(rightSorted, rightMedian, rightAxis);
                //生成分裂点
                GeoPoint rightMedianNode = new GeoPoint(0.0,0.0);
                rightMedianNode.setValueByAxis(rightMedian, rightAxis);
                rightMedianNode.setValueByAxis(rightSorted.value.get(rightMedianIndex).getValueByAxis(rightAxis==0?1:0), rightAxis==0?1:0);
                TreeNode node = new TreeNode(rightMedianNode, null, null);
                //记住当前节点的分裂方向
                node.axis = rightAxis;
                //构建右子树根节点
                queueContext.center.right = node;
                //右子树根节点及其孩子入队
                queue.offer(new QueueContext(node, rightSorted.slice(0, rightMedianIndex+1), rightSorted.slice(rightMedianIndex+1, rightSorted.length)));
            } else if (queueContext.rightSet.length == 1){
                queueContext.center.right = new TreeNode(queueContext.rightSet.value.get(0), null, null);
            }
            /*if (count % 10000 == 0)
                System.out.println("出队元素:" + queueContext.center + " axis: " + queueContext.center.axis + " leftset:" + queueContext.leftSet +
                    " rightset:" + queueContext.rightSet);*/
        }
        return root;
    }

    public List<TreeNode> queryRange(TreeNode root, GeoPoint p, double r) {
        Rectangle initRange = new Rectangle(this.minx, this.maxx, this.miny, this.maxy);
        return traverseChecking(root, initRange, p, r);
    }
}
