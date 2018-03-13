import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kejw
 * @version V1.0
 * @Project ud-spring-flow-predict
 * @Description: TODO
 * @date 2018/1/29
 */
public class TestFlow {

    static String selectsql = null;
    static ResultSet retsult = null;

    public static final String url = "jdbc:mysql://127.0.0.1/jtt?serverTimezone=UTC&characterEncoding=utf8&useUnicode=true&useSSL=false";
    public static final String name = "com.mysql.jdbc.Driver";
    public static final String user = "root";
    public static final String password = "root";

    public static Connection conn = null;
    public static PreparedStatement pst = null;

    public static void main(String[] args) {
        String s2013 = selectsql = "SELECT outDate, sum(count) FROM t_out_list_count_for_predict_pre where outDate between 20130126 and 20130208 GROUP BY outDate ORDER BY outDate ASC";//SQL语句
        List<double[]> d2013 = readLastDayData2(s2013);
        String s2014 = selectsql = "SELECT outDate, sum(count) FROM t_out_list_count_for_predict_pre where outDate between 20140116 and  20140129  GROUP BY outDate ORDER BY outDate ASC";//SQL语句
        List<double[]> d2014 = readLastDayData2(s2014);
        String s2015 = selectsql = "SELECT outDate, sum(count) FROM t_out_list_count_for_predict_pre where outDate between 20150204 and 20150217 GROUP BY outDate ORDER BY outDate ASC";//SQL语句
        List<double[]> d2015 = readLastDayData2(s2015);
        String s2016 = selectsql = "SELECT outDate, sum(count) FROM t_out_list_count_for_predict_pre where outDate between 20160124 and 20160206 GROUP BY outDate ORDER BY outDate ASC";//SQL语句
        List<double[]> d2016 = readLastDayData2(s2016);
        String s2017 = selectsql = "SELECT outDate, sum(count) FROM t_out_list_count_for_predict_pre where outDate between 20170113 and 20170126 GROUP BY outDate ORDER BY outDate ASC";//SQL语句
        List<double[]> d2017 = readLastDayData2(s2017);

        double[] a2013 = new double[d2013.size()];
        for (int i = 0; i < d2013.size(); i++) {
            a2013[i] = d2013.get(i)[0];
        }
        double[] a2014 = new double[d2014.size()];
        for (int i = 0; i < d2014.size(); i++) {
            a2014[i] = d2014.get(i)[0];
        }
        double[] a2015 = new double[d2015.size()];
        for (int i = 0; i < d2015.size(); i++) {
            a2015[i] = d2015.get(i)[0];
        }
        double[] a2016 = new double[d2016.size()];
        for (int i = 0; i < d2016.size(); i++) {
            a2016[i] = d2016.get(i)[0];
        }
        double[] a2017 = new double[d2017.size()];
        for (int i = 0; i < d2017.size(); i++) {
            a2017[i] = d2017.get(i)[0];
        }
        plot(a2013, a2014, a2015, a2016, a2017, "dd");
    }


    public static List<double[]> readLastDayData2(String selectsql) {
        int paraCount = 1; //读取参数数量
        try {
            Class.forName(name);//指定连接类型
            conn = DriverManager.getConnection(url, user, password);//获取连接
            pst = conn.prepareStatement(selectsql);//准备执行语句
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<double[]> list = new ArrayList<>();
        try {
            retsult = pst.executeQuery();//执行语句，得到结果集
            while (retsult.next()) {
                double[] paras = new double[paraCount];
                for (int i = 0; i < paraCount; i++) {
                    paras[i] = retsult.getDouble(i + 2);
                }
                //System.out.println(Arrays.toString(paras));
                list.add(paras);
            }//显示数据
            // System.out.println(Arrays.toString(list.toArray()));
            retsult.close();
            conn.close();//关闭连接
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void plot(double[] d2013, double[] d2014, double[] d2015, double[] d2016, double[] d2017, String name) {
        double[] index = new double[d2013.length];
        for (int i = 0; i < d2013.length; i++)
            index[i] = i;
        int min = minValue(d2013, d2014, d2015, d2016, d2017);
        int max = maxValue(d2013, d2014, d2015, d2016, d2017);
        final XYSeriesCollection dataSet = new XYSeriesCollection();
        addSeries(dataSet, index, d2013, "d2013");
        addSeries(dataSet, index, d2014, "d2014");
        addSeries(dataSet, index, d2015, "d2015");
        addSeries(dataSet, index, d2016, "d2016");
        addSeries(dataSet, index, d2017, "d2017");
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "ex_list flow", // chart title
                "year", // x axis label
                name, // y axis label
                dataSet, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );
        XYPlot xyPlot = chart.getXYPlot();
        // X-axis
        final NumberAxis domainAxis = (NumberAxis) xyPlot.getDomainAxis();
        domainAxis.setRange((int) index[0], (int) (index[index.length - 1] + 2));
        domainAxis.setTickUnit(new NumberTickUnit(20));
        domainAxis.setVerticalTickLabels(true);
        // Y-axis
        final NumberAxis rangeAxis = (NumberAxis) xyPlot.getRangeAxis();
        rangeAxis.setRange(min, max);
        rangeAxis.setTickUnit(new NumberTickUnit(50));
        final ChartPanel panel = new ChartPanel(chart);
        final JFrame f = new JFrame();
        f.add(panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    private static void addSeries(final XYSeriesCollection dataSet, double[] x, double[] y, final String label) {
        final XYSeries s = new XYSeries(label);
        for (int j = 0; j < x.length; j++) s.add(x[j], y[j]);
        dataSet.addSeries(s);
    }

    private static int minValue(double[] d2013, double[] d2014, double[] d2015, double[] d2016, double[] d2017) {
        double min = Integer.MAX_VALUE;
        for (int i = 0; i < d2013.length; i++) {
            if (min > d2013[i]) min = d2013[i];
            if (min > d2014[i]) min = d2014[i];
            if (min > d2015[i]) min = d2015[i];
            if (min > d2016[i]) min = d2016[i];
            if (min > d2017[i]) min = d2017[i];
        }
        return (int) (min * 0.98);
    }

    private static int maxValue(double[] d2013, double[] d2014, double[] d2015, double[] d2016, double[] d2017) {
        double max = Integer.MIN_VALUE;
        for (int i = 0; i < d2013.length; i++) {
            if (max < d2013[i]) max = d2013[i];
            if (max < d2014[i]) max = d2014[i];
            if (max < d2015[i]) max = d2015[i];
            if (max < d2016[i]) max = d2016[i];
            if (max < d2017[i]) max = d2017[i];
        }
        return (int) (max * 1.02);
    }
}