package com.hzz.ui.panel;

import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.main.Api;
import com.hzz.model.Account;
import com.hzz.model.Balance;
import com.hzz.utils.DaoUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class UserInfoPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public UserInfoPanel() {


		setLayout(null);

		JPanel leftPanel = new JPanel();
		leftPanel.setBounds(0, 0, 390, 635);
		leftPanel.setBorder(BorderFactory.createTitledBorder("用户资产"));
		add(leftPanel);
		leftPanel.setLayout(null);

		JPanel top = new JPanel();
		top.setBounds(5, 15, 380, 280);
		top.setBorder(BorderFactory.createTitledBorder("当前资产饼状图"));
		draw(top);
		leftPanel.add(top);

		JPanel bottom = new JPanel();
		bottom.setBounds(5, 300, 380, 330);
		bottom.setBorder(BorderFactory.createTitledBorder("历史资产折线图"));
		draw2(bottom);
		leftPanel.add(bottom);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(400, 0, 370, 275);
		panel_1.setBorder(BorderFactory.createTitledBorder("交易记录"));
		add(panel_1);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(400, 295, 370, 340);
		panel_2.setBorder(BorderFactory.createTitledBorder("交易策略"));
		add(panel_2);

	}

	private void draw(JPanel jp){
		Api api=new Api();
		Map<String,Balance>balanceMap=testDraw();
		Iterator it = balanceMap.entrySet().iterator();
		DefaultPieDataset dataSet = new DefaultPieDataset();
		while (it.hasNext()) {
			Map.Entry<String,Balance> entry = (Map.Entry) it.next();
			String key = entry.getKey();
			Balance value = entry.getValue();
			dataSet.setValue(key,Double.valueOf(value.getFree())+Double.valueOf(value.getLocked()));
		}
		//创建饼图
		JFreeChart chart = ChartFactory.createPieChart(null,
				dataSet, true, true, false);
		//用来放置图表
		ChartPanel panel = new ChartPanel(chart, 360, 250, 300, 200, 1024, 768, true, true, true, true, true, true);

		jp.add(panel, BorderLayout.CENTER);
	}

	private  void draw2(JPanel jp){

		Account account=new Account();
		ModelDao modelDao= DaoUtils.getDao(DaoUtils.getTemplate());
		try {
			java.util.List<Account> list=testDraw2();
			if(list.isEmpty())
				return;;
			// 获取数据集对象
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
			for(int i=0;i<list.size();i++){
				account=list.get(i);
				Date date=new Date(account.getUpdateTime()*1000);
				dataset.addValue(account.getMoneyCount(),"",simpleDateFormat.format(date));
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
			jp.add(panel, BorderLayout.CENTER);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	private List<Account>testDraw2(){
		List<Account>list=new ArrayList<>();
		Account account=new Account();
		account.setUpdateTime(1521529200L);
		account.setMoneyCount(15000L);
		list.add(account);
		account=new Account();
		account.setUpdateTime(1521615600L);
		account.setMoneyCount(17000L);
		list.add(account);
		account=new Account();
		account.setUpdateTime(1521702000L);
		account.setMoneyCount(16300L);
		list.add(account);
		account=new Account();
		account.setUpdateTime(1521788400L);
		account.setMoneyCount(14000L);
		list.add(account);
		return  list;

	}

	private Map<String,Balance> testDraw(){
		Map<String,Balance> map=new HashMap<>();
		Balance balance=new Balance();
		balance.setFree("1234.05");
		balance.setLocked("0");
		map.put("BTC",balance);

		balance=new Balance();
		balance.setFree("2304.05");
		balance.setLocked("0");
		map.put("TFX",balance);
		return  map;

	}


}
