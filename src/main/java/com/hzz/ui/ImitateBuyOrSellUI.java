package com.hzz.ui;

import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.Price;
import com.hzz.utils.DaoUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ImitateBuyOrSellUI extends ApplicationFrame
{

    private List<Price>priceList;
    private List<Price> currentPrice;
    private Integer type;
    public ImitateBuyOrSellUI(final String title , List<Price>priceList, List<Price> currentPrice, Integer type)
    {
        super( title );
        this.priceList=priceList;
        this.currentPrice=currentPrice;
        this.type=type;
        final XYDataset dataset = createDataset( );
        final JFreeChart chart = createChart( dataset );
        final ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( new Dimension( 1000 , 800 ) );
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
        Second current =null;
        Double value=null;
        Price price=null;
        for (int i = 0; i <priceList.size() ; i++)
        {
            price=priceList.get(i);
            current=new Second(new Date(price.getCreateTime()*1000));
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
                "Second",
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
                Marker valueMarker=new ValueMarker(new Date(currentPrice.get(i).getCreateTime()*1000).getTime());
                valueMarker.setPaint(Color.blue);   // 值标记线颜色
                valueMarker.setAlpha(0.9F);         // 值标记线透明度
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss");
                System.out.println(simpleDateFormat.format(new Date(currentPrice.get(i).getCreateTime() * 1000)));
                plot.addDomainMarker(valueMarker);
//                plot.addRangeMarker(valueMarker); // 在图表中使用自定义的值标记线
            }
        }
        return localChart;
    }

    public static void main( final String[ ] args )
    {
//        final String title = "模拟";
//        List<Price> priceList=getTestList();
//        List<Price> currentPrice=new ArrayList<>();
//        currentPrice.add(priceList.get(200));
//        currentPrice.add(priceList.get(360));
////        final TimeSeries_AWT demo = new TimeSeries_AWT( title,priceList,currentPrice,1);
//
//        final ImitateBuyOrSellUI demo = new ImitateBuyOrSellUI( title,getPrice("BTCUSDT",0),getPrice("BTCUSDT",1),1);
//        demo.pack( );
//        demo.setLocationRelativeTo(null);
//        demo.setVisible( true );
    }

    public static List<Price> getTestList(){
        List<Price> priceList=new ArrayList<>();
        Price price=null;
        Random random = new Random();
        for(int i=0;i<720;i++){
            price=new Price();
            price.setPrice( String.valueOf(random.nextInt(1100)%(1100-1000+1) + 1000));
            price.setCreateTime(1522460467L+i*10);
            priceList.add(price);
        }
        return priceList;
    }

    public static List<Price> getPrice(String symbol,Integer buyPointType,Integer sellPointType){
        Price price=new Price();
        price.setSymbol(symbol);
        if(buyPointType>0)
             price.setPointType(buyPointType);
        if(sellPointType>0)
            price.setPointSellType(sellPointType);
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
