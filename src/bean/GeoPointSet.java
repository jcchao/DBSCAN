package bean;

import java.util.*;

public class GeoPointSet {
    public List<GeoPoint> value;
    public int length;

    public GeoPointSet(List<GeoPoint> value) {

        this.value = value;
        this.length = value.size();
    }

    public GeoPointSet slice(int begin, int end) {
        List<GeoPoint> subList = this.value.subList(begin, end);
        return new GeoPointSet(subList);
    }

    public void append(GeoPoint p) {
        this.value.add(p);
        this.length += 1;
    }

    public GeoPointSet extend(GeoPointSet pointSet) {
        List<GeoPoint> lst = new ArrayList<GeoPoint>();
        lst.addAll(this.value);
        lst.addAll(pointSet.value);
        return new GeoPointSet(lst);
    }

    public double[] getValuesByAxis(int axis) {
        double[] v = new double[this.length];
        for (int i=0; i<this.length; i++) {
            v[i] = this.value.get(i).getValueByAxis(axis);
        }
        return v;
    }

    public boolean isEmpty() {
        if (this.value.size() == 0)
            return true;
        else
            return false;
    }

    private void quickSortRecursive(int low, int high, int axis, GeoPoint[] sortedArr) {
        if (low >= high)
            return;
        GeoPoint flag = sortedArr[low];
        int i = low;
        int j = high+1;

        while (true) {
            while (sortedArr[++i].getValueByAxis(axis) <= flag.getValueByAxis(axis)) if (i == high) break;
            while (sortedArr[--j].getValueByAxis(axis) >= flag.getValueByAxis(axis)) if (j == low) break;
            if (i >= j) break;
            GeoPoint tmp = sortedArr[i];
            sortedArr[i] = sortedArr[j];
            sortedArr[j] = tmp;
        }
        sortedArr[low] = sortedArr[j];
        sortedArr[j] = flag;
        quickSortRecursive(low, j-1, axis, sortedArr);
        quickSortRecursive(j+1, high, axis, sortedArr);
    }

    public GeoPointSet sortRecursive(int axis) {
        GeoPoint[] sortedArr = this.value.toArray(new GeoPoint[this.length]);
        this.quickSortRecursive(0, this.length - 1, axis, sortedArr);
        return new GeoPointSet(Arrays.asList(sortedArr));
    }

    private int quickSort(int low, int high, int axis, GeoPoint[] sortedArr) {
        GeoPoint flag = sortedArr[low];
        int i = low;
        int j = high+1;

        while (true) {
            while (sortedArr[++i].getValueByAxis(axis) <= flag.getValueByAxis(axis)) if (i == high) break;
            while (sortedArr[--j].getValueByAxis(axis) >= flag.getValueByAxis(axis)) if (j == low) break;
            if (i >= j) break;
            GeoPoint tmp = sortedArr[i];
            sortedArr[i] = sortedArr[j];
            sortedArr[j] = tmp;
        }
        sortedArr[low] = sortedArr[j];
        sortedArr[j] = flag;
        return j;
    }

    public GeoPointSet sort(int axis) {
        Queue<List<Integer>> queue = new LinkedList<List<Integer>>();
        GeoPoint[] sortedArr = this.value.toArray(new GeoPoint[this.length]);
        int low = 0;
        int high = this.length - 1;
        if (low >= high)
            return this;
        int j = this.quickSort(low, high, axis, sortedArr);
        int leftHigh = j - 1;
        int rightLow = j + 1;
        if (leftHigh > low) {
            List<Integer> queueContext = new ArrayList<Integer>();
            queueContext.add(low);
            queueContext.add(leftHigh);
            queue.offer(queueContext);
        }
        if (rightLow < high) {
            List<Integer> queueContext = new ArrayList<Integer>();
            queueContext.add(rightLow);
            queueContext.add(high);
            queue.offer(queueContext);
        }
        while (!queue.isEmpty()) {
            List<Integer> k = queue.poll();
            int partition = this.quickSort(k.get(0), k.get(1), axis, sortedArr);
            if (partition-1 > k.get(0)) {
                List<Integer> queueContext = new ArrayList<Integer>();
                queueContext.add(k.get(0));
                queueContext.add(partition-1);
                queue.offer(queueContext);
            }
            if (partition+1 < k.get(1)) {
                List<Integer> queueContext = new ArrayList<Integer>();
                queueContext.add(partition+1);
                queueContext.add(k.get(1));
                queue.offer(queueContext);
            }

        }
        return new GeoPointSet(Arrays.asList(sortedArr));
    }

    @Override
    public String toString() {
        String s = "";
        for (GeoPoint p: this.value) {
            s += p;
        }
        return s;
    }

    public static void main(String args[]) {
        GeoPoint p1 = new GeoPoint(3.0f, 2.0f);
        GeoPoint p2 = new GeoPoint(7.0f, 9.0f);
        GeoPoint p3 = new GeoPoint(4.0f, 6.0f);
        GeoPoint p4 = new GeoPoint(5.0f, 8.0f);
        GeoPoint p5 = new GeoPoint(9.0f, 10.0f);
        GeoPoint p6 = new GeoPoint(10.0f, 3.0f);
        GeoPoint p7 = new GeoPoint(2.0f, 4.0f);
        GeoPoint p8 = new GeoPoint(6.0f, 7.0f);

        List<GeoPoint> a = new ArrayList<GeoPoint>();
        a.add(p1);
        a.add(p2);
        a.add(p3);
        a.add(p4);
        a.add(p5);
        a.add(p6);
        a.add(p7);
        a.add(p8);

        GeoPointSet set = new GeoPointSet(a);

        System.out.println(set.sort(0));
        System.out.println(set.sort(1));

    }

}
