package com.hzz.ui.panel;

import com.hzz.common.dao.*;
import com.hzz.exception.CommonException;
import com.hzz.model.Account;
import com.hzz.model.Balance;
import com.hzz.model.MyTrade;
import com.hzz.model.Order;
import com.hzz.service.CommonService;
import com.hzz.utils.DaoUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class UserInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static Logger logger= LoggerFactory.getLogger(UserInfoPanel.class);
	private CommonService commonService=new CommonService();
	private JPanel top;
	private JPanel bottom;
	private JPanel panel_1;
	private JPanel panel_2;
	public static boolean isNeedUpdateTradeMethod=false;
	public static final Object object=new Object();
	public UserInfoPanel() {
		isNeedUpdateTradeMethod=true;
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


	private void initTradeMethod(){
		new Thread(new Runnable() {
			@Override
			public void run() {
					while (true) {

						if(isNeedUpdateTradeMethod) {
							Map<String, String> map = commonService.getTradeMethod();
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
							isNeedUpdateTradeMethod=false;
						}else {
							//让线程陷入阻塞，等到要更新时候再唤醒
							synchronized (UserInfoPanel.object){
								try {
									logger.info("线程准备进入等待状态");
									UserInfoPanel.object.wait();
								} catch (InterruptedException e) {
									logger.error("线程进入等待唤醒异常",e);
								}
							}

						}
					}
			}
		}).start();



	}

	private void initTradeHistory(JPanel panel){

		new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {
					ModelDao modelDao = DaoUtils.getDao(DaoUtils.getTemplate());
					Map<JoinModel, JoinType> joinMap = new LinkedHashMap<>();
					JoinModel joinModel = new JoinModel();
					joinModel.setAliasName("t");
					joinModel.setJoinModel(MyTrade.class);
					joinMap.put(joinModel, JoinType.INNER);

					joinModel = new JoinModel();
					joinModel.setAliasName("o");
					joinModel.setJoinModel(Order.class);
					joinModel.on().add(new ConditionCustom("o.orderId=t.orderId"));
					joinMap.put(joinModel, JoinType.INNER);

					ConditionModel condition = new ConditionModel();
					condition.orderBy("t.time desc");
					condition.limitCount(13);
					condition.columns().addAll(Arrays.asList(new String[]{"t.time", "t.price", "t.qty", "t.isMaker", "t.isBuyer", "o.symbol"}));
					try {
						List<Map<String, Object>> tradeList = modelDao.select(joinMap, condition);
						if (tradeList == null || tradeList.isEmpty())
							return;
						Map<String, Object> trade = null;
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
						int margin = 20;
						panel.removeAll();

						JLabel tlabel = new JLabel("币名");
						tlabel.setBounds(20, 20, 55, 15);
						panel_1.add(tlabel);

						tlabel= new JLabel("时间");
						tlabel.setBounds(100, 20, 55, 15);
						panel_1.add(tlabel);

						tlabel= new JLabel("价格");
						tlabel.setBounds(155, 20, 55, 15);
						panel_1.add(tlabel);

						tlabel= new JLabel("数量");
						tlabel.setBounds(242, 20, 55, 15);
						panel_1.add(tlabel);

						tlabel= new JLabel("进/出");
						tlabel.setBounds(306, 20, 55, 15);
						panel_1.add(tlabel);

						for (int i = 0; i < tradeList.size(); i++) {
							trade = tradeList.get(i);
							margin = 20 * (i + 1);

							String isMakey = (String) trade.get("isMaker");
							boolean isBuyer = !isMakey.equals("true");
							Color color = Color.RED;
							if (isBuyer)
								color = Color.GREEN;

							JLabel label = new JLabel((String) trade.get("symbol"));
							label.setBounds(10, 20 + margin, 75, 15);
							label.setForeground(color);
							panel.add(label);

							Long time = (Long) trade.get("time");
							JLabel label_1 = new JLabel(simpleDateFormat.format(new Date(time * 1000)));
							label_1.setBounds(80, 20 + margin, 85, 15);
							label_1.setForeground(color);
							panel.add(label_1);

							JLabel label_2 = new JLabel((String) trade.get("price"));
							label_2.setBounds(155, 20 + margin, 85, 15);
							label_2.setForeground(color);
							panel.add(label_2);

							JLabel label_3 = new JLabel((String) trade.get("qty"));
							label_3.setBounds(245, 20 + margin, 75, 15);
							label_3.setForeground(color);
							panel.add(label_3);

							JLabel label_4 = new JLabel(isBuyer ? "进" : "出");
							label_4.setBounds(325, 20 + margin, 55, 15);
							label_4.setForeground(color);
							panel.add(label_4);
						}
						panel.repaint();
						try {
							Thread.sleep(10 * 60 * 1000);//交易记录十分钟更新
						} catch (InterruptedException e) {
							logger.error("休眠异常", e);
						}
					} catch (CommonException e) {
						logger.error("用户信息面板数据初始化错误", e);
					}
				}
			}
		}).start();


	}

	private void initUserFreeMoney(JPanel jp){

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Map<String, Balance> balanceMap = commonService.getBalances();
					if (balanceMap == null)
						return;
					Iterator it = balanceMap.entrySet().iterator();
					DefaultPieDataset dataSet = new DefaultPieDataset();
					while (it.hasNext()) {
						Map.Entry<String, Balance> entry = (Map.Entry) it.next();
						String key = entry.getKey();
						Balance value = entry.getValue();
						dataSet.setValue(key, Double.valueOf(value.getFree()) + Double.valueOf(value.getLocked()));
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

	private  void initUserAccount(JPanel jp){


		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Account account = null;
					try {
						java.util.List<Account> list = commonService.getAccount();
						if (list == null || list.isEmpty())
							return;
						// 获取数据集对象
						DefaultCategoryDataset dataset = new DefaultCategoryDataset();
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
						for (int i = 0; i < list.size(); i++) {
							account = list.get(i);
							Date date = new Date(account.getUpdateTime() * 1000);
							dataset.addValue(account.getMoneyCount(), "", simpleDateFormat.format(date));
						}
						JFreeChart chart = ChartFactory.createLineChart(null,
								"date", "money", dataset, PlotOrientation.VERTICAL, false, true,
								false);
						// 获得图表区域对象
						CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
						categoryplot.setBackgroundPaint(Color.lightGray);
						categoryplot.setRangeGridlinesVisible(false);
						// 获显示线条对象
						LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot
								.getRenderer();
						lineandshaperenderer.setBaseShapesVisible(true);
						lineandshaperenderer.setDrawOutlines(true);
						lineandshaperenderer.setUseFillPaint(true);
						lineandshaperenderer.setBaseFillPaint(Color.white);
						// 设置折线加粗
						lineandshaperenderer.setSeriesStroke(0, new BasicStroke(3F));
						lineandshaperenderer.setSeriesOutlineStroke(0, new BasicStroke(2.0F));
						// 设置折线拐点
						lineandshaperenderer.setSeriesShape(0,
								new java.awt.geom.Ellipse2D.Double(-5D, -5D, 10D, 10D));
						ChartPanel panel = new ChartPanel(chart, 360, 295, 300, 200, 1024, 768, true, true, true, true, true, true);
						jp.removeAll();
						jp.add(panel, BorderLayout.CENTER);
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
