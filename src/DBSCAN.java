import bean.GeoPoint;
import bean.GeoPointSet;
import tools.ClusterTools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class DBSCAN {
    public static void main(String args[]){
        long startTime = System.currentTimeMillis();

        try {
            String inputFile = "/home/chaochao/Desktop/bikeGPS.csv";
            String outputFile = "/home/chaochao/Desktop/cluster_res.csv";

            int count = 0;


            List<GeoPoint> points = new ArrayList<GeoPoint>();
            HashSet<String> hashSet = new HashSet<String>();
            GeoPointSet dataSet = new GeoPointSet(points);

            FileReader fr = new FileReader(inputFile);
            BufferedReader br = new BufferedReader(fr);
            String str = null;
            //去除重复行
            while ((str = br.readLine()) != null) {
                hashSet.add(str);
                count += 1;
            }

            System.out.println("filter finished! original data size: " + count + ", filtered data size: " + hashSet.size());
            for (String value: hashSet) {
                Double longitude = Double.parseDouble(value.split(",")[0]);
                Double lantitude = Double.parseDouble(value.split(",")[1]);
                GeoPoint p = new GeoPoint(longitude, lantitude);
                dataSet.append(p);
            }
            System.out.println("total points: " + dataSet.length);
            ClusterTools clt = new ClusterTools();
            clt.dbScan(dataSet, 0.5, 50);
            System.out.println("clustering finished!");
            FileWriter fw = new FileWriter(outputFile);
            for (GeoPoint p: dataSet.value) {
                Double longitude = p.x;
                Double lantitude = p.y;
                int label = p.label;
                int clusterID = p.clusterID;
                fw.write(longitude + "," + lantitude + "," + label +", " + clusterID +"\n");
            }
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("finished!" + ((endTime - startTime) / 1000));

    }
}
