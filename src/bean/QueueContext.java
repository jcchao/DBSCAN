package bean;

public class QueueContext {
    public TreeNode center;
    public GeoPointSet leftSet;
    public GeoPointSet rightSet;

    public QueueContext(TreeNode center, GeoPointSet leftSet, GeoPointSet rightSet) {
        this.center = center;
        this.leftSet = leftSet;
        this.rightSet = rightSet;
    }
}
