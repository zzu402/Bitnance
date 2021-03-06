package com.hzz.ui.panel;

import com.hzz.constant.QueryConstant;
import com.hzz.model.*;
import com.hzz.service.AccountService;
import com.hzz.service.PriceService;
import com.hzz.service.TradeService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.*;
import java.util.List;

public class UserInfoPanel extends JPanel {
    public static final Object object = new Object();
    private static final long serialVersionUID = 1L;
    public static boolean isNeedUpdateTradeMethod = false;
    private static Logger logger = LoggerFactory.getLogger(UserInfoPanel.class);
    private TradeService tradeService = new TradeService();
    private AccountService accountService = new AccountService();
    private PriceService priceService = new PriceService();
    private JPanel top;
    private JPanel bottom;
    private JPanel panel_1;
    private JPanel panel_2;

    public UserInfoPanel() {
        isNeedUpdateTradeMethod = true;
        setLayout(null);
        JPanel leftPanel = new JPanel();
        leftPanel.setBounds(0, 0, 390, 635);
        leftPanel.setBorder(BorderFactory.createTitledBorder("用户资产"));
        add(leftPanel);
        leftPanel.setLayout(null);

        top = new JPanel();
        top.setBounds(5, 15, 380, 280);
        top.setBorder(BorderFactory.createTitledBorder("当前资产饼状图"));
        initUserFreeMoney(top);
        leftPanel.add(top);

        bottom = new JPanel();
        bottom.setBounds(5, 300, 380, 330);
        bottom.setBorder(BorderFactory.createTitledBorder("历史资产折线图"));
        initUserAccount(bottom);
        leftPanel.add(bottom);

        panel_1 = new JPanel();
        panel_1.setBounds(400, 0, 370, 295);
        panel_1.setBorder(BorderFactory.createTitledBorder("交易记录"));

        panel_1.setLayout(null);
        add(panel_1);

        initTradeHistory(panel_1);


        panel_2 = new JPanel();
        panel_2.setBounds(400, 295, 370, 340);
        panel_2.setBorder(BorderFactory.createTitledBorder("交易策略"));
        panel_2.setLayout(null);
        initTradeMethod();
        add(panel_2);

    }

    public static void UpdateTradeMethod(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (UserInfoPanel.object) {
                    UserInfoPanel.isNeedUpdateTradeMethod = true;
                    UserInfoPanel.object.notify();
                }

            }
        }).start();
    }


    private void initTradeMethod() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    if (isNeedUpdateTradeMethod) {
                        Map<String, String> map = tradeService.getTradeMethod();
                        if (map == null)
                            return;
                        String buy = map.get("buy");
                        String sell = map.get("sell");
                        JLabel label = new JLabel("买入策略");
                        label.setBounds(10, 20, 75, 15);
                        panel_2.removeAll();
                        panel_2.add(label);
                        JScrollPane scrollPane = new JScrollPane();
                        scrollPane.setBounds(10, 40, 340, 120);
                        JTextArea textArea = new JTextArea(buy);
                        textArea.setEnabled(false);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        textArea.setDisabledTextColor(Color.BLUE);
                        scrollPane.setViewportView(textArea);
                        panel_2.add(scrollPane);

                        label = new JLabel("卖出策略");
                        label.setBounds(10, 170, 75, 15);
                        panel_2.add(label);
                        scrollPane = new JScrollPane();
                        scrollPane.setBounds(10, 190, 340, 120);
                        textArea = new JTextArea(sell);
                        textArea.setEnabled(false);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        textArea.setDisabledTextColor(Color.RED);
                        scrollPane.setViewportView(textArea);
                        panel_2.add(scrollPane);
                        panel_2.repaint();
                        isNeedUpdateTradeMethod = false;
                    } else {
                        //让线程陷入阻塞，等到要更新时候再唤醒
                        synchronized (UserInfoPanel.object) {
                            try {
                                logger.info("线程准备进入等待状态");
                                UserInfoPanel.object.wait();
                            } catch (InterruptedException e) {
                                logger.error("线程进入等待唤醒异常", e);
                            }
                        }

                    }
                }
            }
        }).start();


    }

    private void initTradeHistory(JPanel panel) {
        MyTradePanel tradePanel = new MyTradePanel();
        tradePanel.setBounds(5, 15, 360, 270);
        panel.add(tradePanel);
    }

    private void initUserFreeMoney(JPanel jp) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Map<String, Balance> balanceMap = accountService.getBalances();
                    if (balanceMap == null)
                        return;
                    Iterator it = balanceMap.entrySet().iterator();
                    DefaultPieDataset dataSet = new DefaultPieDataset();
                    while (it.hasNext()) {
                        Map.Entry<String, Balance> entry = (Map.Entry) it.next();
                        String key = entry.getKey();
                        Balance value = entry.getValue();
                        Double d = Double.valueOf(value.getFree()) + Double.valueOf(value.getLocked());
                        if (key.equals("BTC"))
                            dataSet.setValue(key, d);
                        else {
                            List<Price> priceList = priceService.getPrices(key + QueryConstant.DEFAULT_TRADE_CONVERT_CON);
                            if (priceList == null)
                                continue;
                            Price price = priceList.get(0);
                            if (price.getPrice() != null)
                                dataSet.setValue(key, d * Double.valueOf(price.getPrice()));
                            else {
                                priceList=priceService.getPrices(QueryConstant.DEFAULT_TRADE_CONVERT_CON + key);
                                if (priceList == null)
                                    continue;
                                price = priceList.get(0);
                                if (price.getPrice() != null) {
                                    dataSet.setValue(key, d / Double.valueOf(price.getPrice()));

                                }
                            }
                        }
                    }
                    //创建饼图
                    JFreeChart chart = ChartFactory.createPieChart(null,
                            dataSet, true, true, false);
                    //用来放置图表
                    ChartPanel panel = new ChartPanel(chart, 360, 250, 300, 200, 1024, 768, true, true, true, true, true, true);
                    jp.removeAll();
                    jp.add(panel, BorderLayout.CENTER);
                    jp.repaint();

                    try {
                        Thread.sleep(1 * 60 * 60 * 1000);//用户资产每隔1小时更新
                    } catch (InterruptedException e) {
                        logger.error("休眠异常", e);
                    }
                }
            }
        }).start();


    }

    private void initUserAccount(JPanel jp) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Account account = null;
                    try {
                        java.util.List<Account> list = accountService.getAccount();
                        if (list == null || list.isEmpty())
                            return;
                        // 获取数据集对象
                        TimeSeries series = new TimeSeries("Account Data");
                        Minute current = null;
                        Double value = null;
                        for (int i = 0; i < list.size(); i++) {
                            account = list.get(i);
                            current = new Minute(new Date(account.getUpdateTime() * 1000));
                            value = Double.valueOf(account.getMoneyCount());
                            try {
                                series.add(current, new Double(value));
                            } catch (SeriesException e) {
                                System.err.println("Error adding to series");
                            }
                        }


                        XYDataset dataset = new TimeSeriesCollection(series);
                        JFreeChart localChart = ChartFactory.createTimeSeriesChart(
                                null,
                                "Hour",
                                "Value",
                                dataset,
                                true,
                                true,
                                false);
                        XYPlot plot = (XYPlot) localChart.getPlot();
                        plot.setBackgroundPaint(null);
                        ChartPanel chartPanel = new ChartPanel(localChart);
                        localChart.setNotify(true);
                        chartPanel.setPreferredSize(new java.awt.Dimension(360, 330));
                        chartPanel.setMouseZoomable(true, false);
                        chartPanel.addMouseWheelListener(new MouseWheelListener() {
                            public void mouseWheelMoved(MouseWheelEvent e) {
                                if (-3 == e.getUnitsToScroll()) {
                                    chartPanel.zoomInBoth(1, 10);
                                } else if (3 == e.getUnitsToScroll()) {
                                    chartPanel.zoomOutBoth(1, 10);
                                }
                            }
                        });
                        jp.removeAll();
                        jp.add(chartPanel, BorderLayout.CENTER);
                        jp.repaint();
                        try {
                            Thread.sleep(1 * 60 * 60 * 1000);//用户资产每隔1小时更新
                        } catch (InterruptedException e) {
                            logger.error("休眠异常", e);
                        }
                    } catch (Exception e) {
                        logger.error("使用jfreeChart出错", e);
                    }
                }
            }
        }).start();

    }

}
