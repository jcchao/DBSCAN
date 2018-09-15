package bean;

public class Rectangle {
    public double x1;
    public double x2;
    public double y1;
    public double y2;

    public Rectangle(double x1, double x2, double y1, double y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public boolean contain(GeoPoint p) {
        return p.x >= this.x1 && p.x <= this.x2 && p.y >= this.y1 && p.y <= this.y2;
    }

    /**
     * 计算点到矩形的最短距离，如果在矩形内，则距离为０
     * @param p
     * @return
     */
    public double minDistance(GeoPoint p) {
        double d1 = this.x1 - p.x;
        double d2 = this.y1 - p.y;
        double d3 = p.x - this.x2;
        double d4 = p.y - this.y2;
        double max = Math.max(d1, d2);
        max = Math.max(max, d3);
        max = Math.max(max, d4);
        return Math.max(0, max);
    }

    public double maxDistance(GeoPoint p) {
        double d1 = this.x2 - p.x;
        double d2 = this.y2 - p.y;
        double d3 = p.x - this.x1;
        double d4 = p.y - this.y1;
        double max = Math.max(d1, d2);
        max = Math.max(max, d3);
        max = Math.max(max, d4);
        return Math.max(0, max);
    }


    /**
     * 节点分裂（每个节点代表一个矩形区域（矩形分裂））
     * @param split
     * @param axis
     * @return
     */
    public Rectangle[] split(double split, int axis) {
        Rectangle[] rectangles = new Rectangle[2];
        if (axis == 0) {
            rectangles[0] = new Rectangle(this.x1, split, this.y1, this.y2);
            rectangles[1] = new Rectangle(split, this.x2, this.y1, this.y2);
        } else {
            rectangles[0] = new Rectangle(this.x1, this.x2, this.y1, split);
            rectangles[1] = new Rectangle(this.x1, this.x2, split, this.y2);
        }
        return rectangles;
    }
}
