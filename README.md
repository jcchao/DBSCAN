# DBSCAN　Based KD-Tree
## 项目介绍
该项目是共享单车分布聚类，数据在data目录下，每一行是一条经纬度信息。   
聚类方法采用基于密度的DBSCAN，范围内邻居点查找采用KD-Tree的range search。参考论文[Range Searching using Kd Tree.](http://www.cs.utah.edu/~lifeifei/cs6931/kdtree.pdf)  
初次之外本项目解决了论文中关于构建KD-Tree "The basic assumption is no two point have same x-coordinate, and no two points have same y-coordinate" 该假设的限定，本项目针对拥有相同x或相同y的点做了特殊优化，使得KD-Tree面对这些特殊点也能成功构建。　　
## 代码结构
* `data`目录存放数据
* `src`
   * `bean`
      * `GeoPoint.java` GIS坐标点类
      * `GeoPointSet.java` GIS坐标点集合类
      * `QueueContext.java` 程序中用到的队列context类
      * `Rectangle.java` KD-Tree range search用到的矩形range类
      * `TreeNode.java` KD-Tree节点类
   * `tools`
      * `GeoMapTools.java` GIS地图工具包（包括测量两个经纬度之间的距离）
      *  `KDTree.java` KD-Tree相关操作的工具包（包括构建KD-Tree，KD-Tree的range search等）
      * `ClusterTools.java` DBSCAN聚类相关工具包
 
 * `DBSCAN.java` 程序入口文件
## 项目改进
KD-Tree树虽然可以加快数据的查询，但是如果针对特大数据集，KD-Tree也无能为力。起初本项目KD-Tree的构建采用的是递归的方法，后来由于数据量大，导致递归栈溢出，于是将构建KD-Tree的算法改成了非递归，但是针对百万量级的数据，非递归KD-Tree的构建也非常慢，因为每次节点分裂都需要预先排序，仅仅对百万级数据进行排序就会耗费非常多的时间，所以针对百万级甚至更大量的数据，最好的办法是对数据进行索引，这样range search时能够极大的节省时间。   
之后我会再改进一版采用GeoHash的DBSCAN，解决大数据量的聚类问题。