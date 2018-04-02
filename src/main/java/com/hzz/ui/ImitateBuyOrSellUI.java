package com.hzz.ui;

import com.hzz.model.Price;
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
import java.util.Date;
import java.util.List;

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
                if(type==1)
                    valueMarker.setPaint(Color.GREEN);   // 值标记线颜色
                else
                    valueMarker.setPaint(Color.RED);
                valueMarker.setAlpha(0.9F);         // 值标记线透明度
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss");
                System.out.println(simpleDateFormat.format(new Date(currentPrice.get(i).getCreateTime() * 1000)));
                plot.addDomainMarker(valueMarker);
//                plot.addRangeMarker(valueMarker); // 竖直线
            }
        }
        return localChart;
    }





}
