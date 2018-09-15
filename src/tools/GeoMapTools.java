package tools;

import bean.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class GeoMapTools {
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

    /**
     * 给定两个经纬度坐标计算这两点之间的距离，计算方法：Haversine公式
     *  a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
     *  c = 2 ⋅ atan2( √a, √(1−a) )
     *  d = R ⋅ c
     *  where φ is latitude, λ is longitude, R is earth’s radius (mean radius = 6,371km);
     *  The angles need to be in radians to pass to trig functions!
     * @return distance(km)
     */
    public double calDistance(GeoPoint p1, GeoPoint p2) {

        double latDistance = Math.toRadians(p1.y - p2.y);
        double lngDistance = Math.toRadians(p1.x - p2.x);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(p1.y)) * Math.cos(Math.toRadians(p2.y))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c * 10000.0) / 10000.0;
    }

    /*public double calDistance(GeoPoint p1, GeoPoint p2) {
        return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y));
    }
*/
    public double calDistanceByAxis(GeoPoint p1, GeoPoint p2, int axis) {
        double lat1 = p1.y;
        double lng1 = p1.x;
        double lat2 = p2.y;
        double lng2 = p2.x;
        if (axis == 0) {
            lat1 = lat2 = 0.0;
        } else {
            lng1 = lng2 = 0.0;
        }

        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(lng1 - lng2);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c * 10000.0) / 10000.0;
    }


    /**
     * 戈丁经纬度坐标，距离，方位角，返回新的经纬度坐标
     * @param lng　经度
     * @param lat　纬度
     * @param distance　距离
     * @param bearing　方位角
     * @return
     */
    public List<Double> getNewLngAndLat(double lng, double lat, double distance, double bearing) {
        final double Ea = 6378137; //地球赤道半径
        final double Eb = 6356725; //地球极半径
        double dx = distance * 1000 * Math.sin(bearing * Math.PI / 180.0);
        double dy = distance * 1000 * Math.cos(bearing * Math.PI / 180.0);
        double ec = Eb + (Ea - Eb) * (90.0 - lat) / 90.0;
        double ed = ec * Math.cos(lat * Math.PI / 180);
        double newLng = (dx / ed + lng * Math.PI / 180.0) * 180.0 / Math.PI;
        double newLat = (dy / ec + lat * Math.PI / 180.0) * 180.0 / Math.PI;

        List<Double> lngLat = new ArrayList<Double>();
        lngLat.add(newLng);
        lngLat.add(newLat);
        return lngLat;
    }

    /**
     * 给定经纬度和距离，返回该坐标点的最小外包矩形的四个顶点坐标
     * @param lng 经度
     * @param lat 维度
     * @param distance 距离
     * @return 四个顶点坐标
     */
    public List<List<Double>> getRectRange(double lng, double lat, double distance) {
        List<Double> lngLat0 = getNewLngAndLat(lng, lat, distance, 0.0);
        List<Double> lngLat90 = getNewLngAndLat(lng, lat, distance, 90.0);
        List<Double> lngLat180 = getNewLngAndLat(lng, lat, distance, 180.0);
        List<Double> lngLat270 = getNewLngAndLat(lng, lat, distance, 270.0);

        List<List<Double>> points = new ArrayList<List<Double>>();
        points.add(lngLat0);
        points.add(lngLat90);
        points.add(lngLat180);
        points.add(lngLat270);
        return points;
    }

    public static void main(String args[]) {
        /*double lng1 = 117.194967774556;
        double lat1 = 39.0372572932223;
        double lng2 = 117.194966660556;
        double lat2 = 39.0364055632223;*/
        double lng1 = 117.194971654778;
        double lat1 = 39.0346998820001;
        double lng2 = 117.194970572916;
        double lat2 = 39.0338481976366;
        GeoMapTools mp = new GeoMapTools();
        System.out.println(mp.calDistance(new GeoPoint(lng1, lat1), new GeoPoint(lng2, lat2)));
    }
}
