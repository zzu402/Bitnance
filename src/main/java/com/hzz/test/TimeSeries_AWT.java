package com.hzz.test;

import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.Price;
import com.hzz.utils.DaoUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.peer.ListPeer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class TimeSeries_AWT extends ApplicationFrame
{

    private java.util.List<Price>priceList;
    private java.util.List<Price> currentPrice;
    private Integer type;
    public TimeSeries_AWT( final String title ,java.util.List<Price>priceList,java.util.List<Price> currentPrice,Integer type)
    {
        super( title );
        this.priceList=priceList;
        this.currentPrice=currentPrice;
        this.type=type;
        final XYDataset dataset = createDataset( );
        final JFreeChart chart = createChart( dataset );
        final ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 1000 , 800 ) );
        chartPanel.setMouseZoomable( true , false );
        chartPanel.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (-3 == e.getUnitsToScroll()) {
                    chartPanel.zoomInBoth(1, 10);
                } else if (3 == e.getUnitsToScroll()) {
                    chartPanel.zoomOutBoth(1, 10);
                }
            }
        });

        setContentPane( chartPanel );
    }

    private XYDataset createDataset( )
    {
        if(priceList==null||priceList.size()<1)
            return  new TimeSeriesCollection(new TimeSeries(""));
        final TimeSeries series = new TimeSeries( "Random Data" );
        Minute current =null;
        Double value=null;
        Price price=null;
        for (int i = 0; i <priceList.size() ; i++)
        {
            price=priceList.get(i);
            current=new Minute(new Date(price.getCreateTime()*1000));
            value= Double.valueOf(price.getPrice());
            try
            {
                series.add(current, new Double( value ) );
            }
            catch ( SeriesException e )
            {
                System.err.println("Error adding to series");
            }
        }

        return new TimeSeriesCollection(series);
    }

    private JFreeChart createChart( final XYDataset dataset )
    {
        JFreeChart localChart= ChartFactory.createTimeSeriesChart(
                null,
                "Hour",
                "Value",
                dataset,
                false,
                false,
                false);
        XYPlot plot = (XYPlot)localChart.getPlot();
        plot.setBackgroundPaint(null);
        // 设置值标记线
        if(currentPrice!=null&&currentPrice.size()>1) {
            for(int i=0;i<currentPrice.size();i++) {

                ValueMarker valueMarker = new ValueMarker(Double.valueOf(currentPrice.get(i).getPrice()));
                valueMarker.setPaint(Color.blue);   // 值标记线颜色
                valueMarker.setAlpha(0.9F);         // 值标记线透明度
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss");
                String labelStr = type == 1 ? "买入时间:" + simpleDateFormat.format(new Date(currentPrice.get(i).getCreateTime() * 1000)) : "卖出时间:" + simpleDateFormat.format(new Date(currentPrice.get(i).getCreateTime() * 1000));
                valueMarker.setLabel(labelStr);        // 值标记线显示的文字
                valueMarker.setLabelPaint(Color.BLUE);  // 值标记线显示的文字的颜色
                // 值标记线显示的文字的字体
                valueMarker.setLabelFont(new Font("宋体", Font.PLAIN, 12));
                // 值标记线显示的文字定位到最左端的数据点处
                valueMarker.setLabelAnchor(RectangleAnchor.LEFT);
                // 值标记线在显示的文字的下方左端
                valueMarker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
                plot.addRangeMarker(valueMarker); // 在图表中使用自定义的值标记线
            }
        }
        return localChart;
    }

    public static void main( final String[ ] args )
    {
        final String title = "模拟";
//        List<Price> priceList=getTestList();
//        List<Price> currentPrice=new ArrayList<>();
//        currentPrice.add(priceList.get(200));
//        currentPrice.add(priceList.get(360));

        final TimeSeries_AWT demo = new TimeSeries_AWT( title,getPrice("TFXBTC",0),getPrice("TFXBTC",1),1);
        demo.pack( );
        demo.setLocationRelativeTo(null);
        demo.setVisible( true );
    }

//    public static java.util.List<Price> getTestList(){
//        List<Price> priceList=new ArrayList<>();
//        Price price=null;
//        Random random = new Random();
//        for(int i=0;i<720;i++){
//            price=new Price();
//            price.setPrice( String.valueOf(random.nextInt(1100)%(1100-1000+1) + 1000));
//            price.setCreateTime(1522460467L+i*10);
//            priceList.add(price);
//        }
//        return priceList;
//    }

    public static List<Price> getPrice(String symbol,Integer type){
        Price price=new Price();
        price.setSymbol(symbol);
        if(type>0)
             price.setPointType(type);
        price.groupBy("createTime desc");

        ModelDao modelDao= DaoUtils.getDao(DaoUtils.getTemplate());
        try {
            List<Price> priceList=modelDao.select(price);
            return  priceList;
        } catch (CommonException e) {
            e.printStackTrace();
            return  null;
        }
    }


}
