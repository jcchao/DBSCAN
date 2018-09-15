package tools;

import bean.GeoPoint;
import bean.GeoPointSet;
import bean.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ClusterTools {

    public void dbScan(GeoPointSet dataSet, double eps, int minPts) {
        //构建KD-Tree
        KDTree KDTree = new KDTree();
        TreeNode kdTree = KDTree.kdTree(dataSet);
        System.out.println("KD-Tree constructed finished!");
        int clusterId = 0;
        int count = 0;

        for (TreeNode node: kdTree.leaves()){
            count += 1;
            if (count % 1000 == 0)
                System.out.println("total " + count + " points have been visited!");
            if (node.value.label == 0) {
                List<TreeNode> neighbors = KDTree.queryRange(kdTree, node.value, eps);
                if (neighbors.size() >= minPts) {
                    clusterId += 1;
                    node.value.label = 3; //标记为cluster内部点
                    node.value.clusterID = clusterId;
                    // 遍历node的邻居
                    for (int i=0; i<neighbors.size(); i++) {
                        if (neighbors.get(i).value.label == 0) {
                            neighbors.get(i).value.label = 3; //标记为cluster内部点
                            neighbors.get(i).value.clusterID = clusterId;
                            List<TreeNode> n1 = KDTree.queryRange(kdTree, neighbors.get(i).value, eps);
                            if (n1.size() >= minPts)
                                neighbors.addAll(n1);
                        } else if (neighbors.get(i).value.label == 2) {
                            neighbors.get(i).value.label = 1; //标记为边界点
                            neighbors.get(i).value.clusterID = clusterId;
                        }
                    }
                } else {
                    node.value.label = 2; // 标记为噪声点
                }
            }
        }
    }


    public static void main(String args[]) {

        int  pointNum = 10;
        List<GeoPoint> lst = new ArrayList<GeoPoint>();
        for (int i = 0; i < pointNum; i++) {
            Random random = new Random(i);
            GeoPoint p = new GeoPoint(random.nextInt(10), random.nextInt(10));
            lst.add(p);
        }

        GeoMapTools gmt = new GeoMapTools();
        GeoPoint target = new GeoPoint(4.0, 6.0);
        for (GeoPoint p: lst) {
            System.out.print(p);
            System.out.println(gmt.calDistance(target, p));
        }

        GeoPointSet set = new GeoPointSet(lst);
        System.out.println("original set: " + set);
        System.out.println("sorted x set: " + set.sort(0));
        System.out.println("sorted y set: " + set.sort(1));
        KDTree KDTree = new KDTree();

        System.out.println("kd-tree begin...");
        long startTime = System.currentTimeMillis();
        TreeNode kdtree = KDTree.kdTree(set);
        System.out.println("leaves: " + kdtree.leaves());
        System.out.println(KDTree.queryRange(kdtree, target, 5.0));
        long endTime = System.currentTimeMillis();
        System.out.println("finished! " + ((endTime - startTime) / 1000));

    }
}
