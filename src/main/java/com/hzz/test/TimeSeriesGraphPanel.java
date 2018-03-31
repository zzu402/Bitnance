//package com.hzz.test;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Font;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import java.awt.geom.Ellipse2D;
//import java.sql.Timestamp;
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//import java.util.Vector;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.DateAxis;
//import org.jfree.chart.axis.DateTickUnit;
//import org.jfree.chart.axis.NumberAxis;
//import org.jfree.chart.axis.NumberTickUnit;
//import org.jfree.chart.labels.StandardXYToolTipGenerator;
//import org.jfree.chart.plot.Marker;
//import org.jfree.chart.plot.ValueMarker;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
//import org.jfree.data.time.Day;
//import org.jfree.data.time.Hour;
//import org.jfree.data.time.Minute;
//import org.jfree.data.time.Second;
//import org.jfree.data.time.TimeSeries;
//import org.jfree.data.time.TimeSeriesCollection;
//import org.jfree.ui.RectangleAnchor;
//import org.jfree.ui.TextAnchor;
//
//public class TimeSeriesGraphPanel extends JPanel {
//    private static final long serialVersionUID = 1L;
//    private TimeSeriesCollection dataCollection = null;
//    private TimeSeries series = null;
//    private JFreeChart chart = null;
//    private XYPlot plot = null;
//    private ChartPanel chartPanel = null;
//
//    private String pvType = "";
//    private String fieldName = "";
//
//    private int minutes_interval_of_X_axis = 10;  //X轴 最大值和最小值之间的距离为minutes_interval_of_X_axis分钟
//    private int tickUnit_Minute_Count = 1; //X轴 每隔tickUnit_Minute_Count分钟画一个刻度
//    private Marker vMarker = null;
//    private Marker hMarker = null;
//
//    public TimeSeriesGraphPanel() {
//        series = new TimeSeries("");
//        series.setMaximumItemCount(100);
//        dataCollection = new TimeSeriesCollection(series);
////	dataCollection.addSeries(series);
//        createChart(dataCollection);
//        chartPanel = new ChartPanel(chart);
//        this.setLayout(new BorderLayout());
//        this.add(chartPanel, BorderLayout.CENTER);
//    }
//    private void createChart(TimeSeriesCollection dataCollection) {
//        chart = ChartFactory.createTimeSeriesChart(
//                "",
//                "",
//                "",
//                dataCollection,
//                false, // 是否显示图例(对于简单的柱状图必须是false)
//                true, // 是否生成工具
//                false // 是否生成URL链接
//        );
//
//        chart.getTitle().setFont(new Font("Dialog", Font.PLAIN, 10));
//        chart.setTitle("No Data");
//        plot = chart.getXYPlot();
//
////设置X轴的刻度配置 + 初始坐标
//        DateAxis domainAxis = (DateAxis)plot.getDomainAxis();
//
//        domainAxis.setTickUnit(new DateTickUnit(DateTickUnit.MINUTE, tickUnit_Minute_Count,
//                new SimpleDateFormat("HH:mm")));
////domainAxis.setVerticalTickLabels(true);	 //垂直显示坐标
//
//        GregorianCalendar gc = new GregorianCalendar();
//        int year = gc.get(Calendar.YEAR);
//        int month = gc.get(Calendar.MONTH);
//        int day = gc.get(Calendar.DATE);
//        int hour = gc.get(Calendar.HOUR_OF_DAY);
//        int miniute = gc.get(Calendar.MINUTE);
//        int second = gc.get(Calendar.SECOND);
//
//        domainAxis = (DateAxis) plot.getDomainAxis();
//// 设置X轴最大值
//        gc = new java.util.GregorianCalendar(year, month, day, hour,
//                miniute + 1, second);
//        domainAxis.setMaximumDate(new Date(gc.getTimeInMillis()));
//// 设置X轴最小值， 最大值和最小值之间的距离为minutes_interval_of_X_axis分钟
//        gc.add(Calendar.MINUTE, - minutes_interval_of_X_axis);
//        domainAxis.setMinimumDate(new Date(gc.getTimeInMillis()));
//        domainAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 10));
//
//
//// 1. 设置Y轴特性
//        NumberAxis vValueAxis = (NumberAxis) plot.getRangeAxis();
//        vValueAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 10));
//        vValueAxis.setAutoTickUnitSelection(false);
//
//
//        NumberTickUnit nt = new NumberTickUnit(0.2d); // 设置刻度
//        vValueAxis.setTickUnit(nt);
//        vValueAxis.setUpperBound(1); // 设置Y轴最大值
//        vValueAxis.setLowerBound(0); // 设置Y轴最小值
//
//    }
//
//    // 初始化图表的相关配置
//    public void setGraphNameAndInit(String pvType, String fieldName) {
//        this.pvType = pvType;
//        this.fieldName = fieldName;
//
//        chart.setTitle(getGraphName()); // 设置表头
//
//// 找到fieldName的位置
//        String[] DB_Fields = PVDataDef.pvType_DBFields_Map.get(pvType);
//        int index = 1;
//
////System.out.println("****************************");
////System.out.println("****************************");
////System.out.println(fieldName);
////System.out.println(pvType);
////System.out.println(index);
//        double maxValue = PVDataDef.pvType_MaxValue_Map.get(pvType)[index];
//        maxValue = maxValue + maxValue/10;
//        double minValue = PVDataDef.pvType_MinValue_Map.get(pvType)[index];
//
//        double upperAlarm = PVDataDef.pvType_UpperAlarmThreshold_Map
//                .get(pvType)[index];
//        double lowerAlarm = PVDataDef.pvType_LowerAlarmThreshold_Map
//                .get(pvType)[index];
//
//        System.out.println(maxValue + "  " +minValue );
//
//
//// 1. 设置Y轴特性
//        NumberAxis vValueAxis = (NumberAxis) plot.getRangeAxis();
//        vValueAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 10));
//        vValueAxis.setAutoTickUnitSelection(false);
//
//        NumberTickUnit nt = new NumberTickUnit((maxValue-minValue)/10.0); // 设置刻度
//        vValueAxis.setTickUnit(nt);
//
//// 增加横向的标注线
//        hMarker = new ValueMarker(upperAlarm);
//        hMarker.setLabelFont(new Font("Dialog", Font.PLAIN, 10));
//        hMarker.setPaint(Color.RED);
//        hMarker.setLabel("upperAlarm");
//        hMarker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
//        hMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
//        plot.addRangeMarker(hMarker);
//
//// 增加纵向的标注线.
//        vMarker = new ValueMarker(lowerAlarm);
//        vMarker.setLabelFont(new Font("Dialog", Font.PLAIN, 10));
//        vMarker.setPaint(Color.YELLOW);
//        vMarker.setLabel("lowerAlarm");
//        vMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
//        vMarker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
//
//        plot.addRangeMarker(vMarker);
//
//// 1. 设置Y轴特性
//        vValueAxis = (NumberAxis) plot.getRangeAxis();
//        vValueAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 10));
//        vValueAxis.setAutoTickUnitSelection(false);
//        vValueAxis.setUpperBound(maxValue); // 设置Y轴最大值
//        vValueAxis.setLowerBound(minValue); // 设置Y轴最小值
//
//// 3. 设置曲线颜色
//        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//        renderer.setSeriesShapesVisible(0, true);
//        renderer.setSeriesPaint(0, Color.BLUE);
//        renderer.setSeriesShape(0,
//                new Ellipse2D.Float(-3.0f, -3.0f, 6.0f, 6.0f)); // circle
//        plot.setRenderer(renderer);
//
//// 4. 设置点击出现数据点值的提示信息
//        plot.setDomainCrosshairVisible(true);
//        plot.setRangeCrosshairVisible(true);
//        plot.getRenderer().setSeriesToolTipGenerator(
//                0,
//                new StandardXYToolTipGenerator("({1}, {2})",
//                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
//                        new DecimalFormat("#,##0.00")));
//        chartPanel.validate();
//    }
//
//    public String getGraphName() {
//        return this.pvType + "." + this.fieldName;
//    }
//
//    public void releaseResource() {
//        this.pvType = "";
//        this.fieldName = "";
//        chart.setTitle("No Data"); // 设置表头为'.'
//        series.clear();
//        plot.removeRangeMarker(hMarker);
//        plot.removeRangeMarker(vMarker);
//// 1. 设置Y轴特性
//        NumberAxis vValueAxis = (NumberAxis) plot.getRangeAxis();
//        vValueAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 10));
//        vValueAxis.setAutoTickUnitSelection(false);
//        NumberTickUnit nt = new NumberTickUnit(0.2d); // 设置刻度
//        vValueAxis.setTickUnit(nt);
//        vValueAxis.setUpperBound(1); // 设置Y轴最大值
//        vValueAxis.setLowerBound(0); // 设置Y轴最小值
//    }
//
//
//    public void showingSeries(Timestamp ts, String pvType, String fieldName,
//                              double fieldValue) {
//        if (this.pvType.equals("") || this.fieldName.equals("")) { // 此图是空的
////setGraphNameAndInit(pvType, ConfigUtil.getConfig(fieldName));
//            setGraphNameAndInit(pvType, "P1");
//        }
//
//////System.out.println("Showing " + this.getGraphName()  + " " + fieldValue);
//        GregorianCalendar gc = DateUtil.getGregorianCalendarFrameTimestap(ts);
//
//        int year = gc.get(Calendar.YEAR);
//        int month = gc.get(Calendar.MONTH);
//        int day = gc.get(Calendar.DATE);
//        int hour = gc.get(Calendar.HOUR_OF_DAY);
//        int miniute = gc.get(Calendar.MINUTE);
//        int second = gc.get(Calendar.SECOND);
//
//        final Hour h = new Hour(hour, new Day(day, month + 1, year));
//        series.addOrUpdate(new Second(second, new Minute(miniute, h)),
//                fieldValue);
////	series.addOrUpdate(new Millisecond(), fieldValue);
//
//        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
//
//// 设置X轴最大值
//        gc = new java.util.GregorianCalendar(year, month, day, hour,
//                miniute + 1, second);
//        domainAxis.setMaximumDate(new Date(gc.getTimeInMillis()));
//
//// 设置X轴最小值， 最大值和最小值之间的距离为minutes_interval_of_X_axis分钟
//        gc.add(Calendar.MINUTE, -minutes_interval_of_X_axis);
//
//        domainAxis.setMinimumDate(new Date(gc.getTimeInMillis()));
//
//    }
//
//    private static long randomNum() {
//        System.out.println((Math.random() * 20 + 150));
//        return (long) (Math.random() * 20 + 150);
//    }
//
//    public static void main(final String[] args) {
//        //测试用
//        JPanel panel = new JPanel();
//        panel.setLayout(new GridBagLayout());
//        TimeSeriesGraphPanel p1 = new TimeSeriesGraphPanel();
//        TimeSeriesGraphPanel p2 = new TimeSeriesGraphPanel();
//        TimeSeriesGraphPanel p3 = new TimeSeriesGraphPanel();
//        TimeSeriesGraphPanel p4 = new TimeSeriesGraphPanel();
//
//        LayoutUtil.add(panel, GridBagConstraints.BOTH,
//                GridBagConstraints.CENTER, 100, 100, 0, 0, 1, 1, p1);
//        LayoutUtil.add(panel, GridBagConstraints.BOTH,
//                GridBagConstraints.CENTER, 100, 100, 1, 0, 1, 1, p2);
//        LayoutUtil.add(panel, GridBagConstraints.BOTH,
//                GridBagConstraints.CENTER, 100, 100, 0, 1, 1, 1, p3);
//        LayoutUtil.add(panel, GridBagConstraints.BOTH,
//                GridBagConstraints.CENTER, 100, 100, 1, 1, 1, 1, p4);
//
//        JFrame f = new JFrame();
//        f.addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });
//        f.getContentPane().add(panel);
//        f.setSize(600, 600);
//        f.setVisible(true);
//
//        Map<String, TimeSeriesGraphPanel> usedGraphPanels = new HashMap<String, TimeSeriesGraphPanel>();
//        Vector<TimeSeriesGraphPanel> emptyGraphPanel = new Vector<TimeSeriesGraphPanel>();
//        emptyGraphPanel.add(p1);
//        emptyGraphPanel.add(p2);
//        emptyGraphPanel.add(p3);
//        emptyGraphPanel.add(p4);
//
//        Random r = new Random();
//
//        String pvType = "Pressure";
//        String fieldName = "P1";
//
//        TimeSeriesGraphPanel foundAPanel = usedGraphPanels.get(pvType + "."+ fieldName);
//
//        if (foundAPanel == null){
//            foundAPanel = emptyGraphPanel.elementAt(0);
//            foundAPanel.setGraphNameAndInit(pvType, fieldName);
//            usedGraphPanels.put(pvType + "."+ fieldName, foundAPanel);
//        }
//
//        for(int i=0;i<200;i++){
//            System.out.println("Time:"+i);
//            double value = r.nextDouble();
//            GregorianCalendar gc = new GregorianCalendar();
//            Timestamp ts = DateUtil.getTimestampFromGregorianCalendar(gc);
//            foundAPanel.showingSeries(ts, pvType, fieldName, randomNum());
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e1) {
//// TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
//        }
//    }
//}