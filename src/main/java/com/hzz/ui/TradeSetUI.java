package com.hzz.ui;

import com.hzz.constant.AppConstant;
import com.hzz.utils.AlertUtils;
import com.hzz.utils.PropertiesUtils;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TradeSetUI extends AbstractUI implements ChangeListener,ActionListener{
	private JSlider slider;
	private JSlider slider_1;
	private JSlider slider_2;
	private JSlider slider_3;
	private JSlider slider_4;
	private JSlider slider_5;
	private JSlider slider_6;
	private JLabel label;
	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;
	private JLabel label_4;
	private JLabel label_5;
	private JLabel label_6;
	private  JLabel label_7;
	private JCheckBox checkBox_1;
	private JCheckBox checkBox_2;
	private JCheckBox checkBox_3;
	private Integer v1= Math.toIntExact(AppConstant.SAVE_PRICE_MARGIN_TIME / 1000);
	private Integer v2=Math.toIntExact(AppConstant.SAVE_ACCOUNT_MARGIN_TIME/(60*1000));
	private Integer v3=(int) (AppConstant.DO_BUY_TRADE_MARGIN_TIME/1000);
	private Integer v4=(int) (AppConstant.DO_SELL_TRADE_MARGIN_TIME/1000);
	private Integer v5=AppConstant.TIME_MARGIN_DATA_COUNT/6;
	private Integer v6=AppConstant.DISTANCE_THRESHOLD_MAX/6;
	private Integer v7=AppConstant.DISTANCE_THRESHOLD_MIN/6;
	private  Integer v8=AppConstant.PRICE_STORY_METHOD;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TradeSetUI window = new TradeSetUI(WindowConstants.EXIT_ON_CLOSE);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TradeSetUI(int closeOperation) {
		initialize(closeOperation);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	protected void initialize(int closeOperation) {
		frame = new JFrame();
		frame.setTitle("交易策略");
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(closeOperation);
		frame.getContentPane().setLayout(null);
		
		label= new JLabel("价格存储时间间距(秒)："+ AppConstant.SAVE_PRICE_MARGIN_TIME/1000);
		label.setBounds(10, 10, 180, 15);
		frame.getContentPane().add(label);
		slider = new JSlider();
		slider.setValue(Math.toIntExact(AppConstant.SAVE_PRICE_MARGIN_TIME/1000));
		slider.setBounds(197, 10, 358, 40);
		setJSlider(slider,10,1);
		frame.getContentPane().add(slider);
		
		label_1 = new JLabel("账户金额更新时间(分)："+AppConstant.SAVE_ACCOUNT_MARGIN_TIME/(60*1000));
		label_1.setBounds(10, 50, 180, 15);
		frame.getContentPane().add(label_1);
		
		slider_1 = new JSlider();
		slider_1.setValue(Math.toIntExact(AppConstant.SAVE_ACCOUNT_MARGIN_TIME/(60*1000)));
		slider_1.setBounds(197, 50, 358, 40);
		setJSlider(slider_1,10,1);
		frame.getContentPane().add(slider_1);
		
		label_2 = new JLabel("买入交易执行间距(秒)："+AppConstant.DO_BUY_TRADE_MARGIN_TIME/1000);
		label_2.setBounds(10, 90, 180, 15);
		frame.getContentPane().add(label_2);
		
		slider_2 = new JSlider();
		slider_2.setValue((int) (AppConstant.DO_BUY_TRADE_MARGIN_TIME/1000));
		slider_2.setBounds(197, 90, 358, 40);
		setJSlider(slider_2,10,1);
		frame.getContentPane().add(slider_2);

		
		label_3 = new JLabel("卖出交易执行间距(秒)："+AppConstant.DO_SELL_TRADE_MARGIN_TIME/1000);
		label_3.setBounds(10, 130, 180, 15);
		frame.getContentPane().add(label_3);
		
		slider_3 = new JSlider();
		slider_3.setValue((int) (AppConstant.DO_SELL_TRADE_MARGIN_TIME/1000));
		slider_3.setBounds(197, 130, 358, 40);
		setJSlider(slider_3,10,1);
		frame.getContentPane().add(slider_3);
		
		label_4 = new JLabel("查询极点时间范围(分)："+AppConstant.TIME_MARGIN_DATA_COUNT/6);
		label_4.setBounds(10, 170, 180, 15);
		frame.getContentPane().add(label_4);
		
		slider_4 = new JSlider();
		slider_4.setValue(AppConstant.TIME_MARGIN_DATA_COUNT/6);
		slider_4.setMaximum(240);
		slider_4.setBounds(197, 170, 358, 40);
		setJSlider(slider_4,240,1);
		frame.getContentPane().add(slider_4);
		
		label_5 = new JLabel("极点附近最大距离(分)："+AppConstant.DISTANCE_THRESHOLD_MAX/6);
		label_5.setBounds(10, 210, 180, 15);
		frame.getContentPane().add(label_5);
		
		slider_5 = new JSlider();
		slider_5.setValue(AppConstant.DISTANCE_THRESHOLD_MAX/6);
		slider_5.setMaximum(60);
		slider_5.setBounds(197, 210, 358, 40);
		setJSlider(slider_5,60,1);
		frame.getContentPane().add(slider_5);
		
		label_6 = new JLabel("极点附近最小距离(分)："+AppConstant.DISTANCE_THRESHOLD_MIN/6);
		label_6.setBounds(10, 250, 180, 15);
		frame.getContentPane().add(label_6);
		
		slider_6 = new JSlider();
		slider_6.setValue(AppConstant.DISTANCE_THRESHOLD_MIN/6);
		slider_6.setMaximum(60);
		slider_6.setBounds(197, 250, 358, 40);
		setJSlider(slider_6,60,1);
		frame.getContentPane().add(slider_6);


		label_7=new JLabel("价格存储策略：");
		label_7.setBounds(10,290,180,15);
		frame.getContentPane().add(label_7);

		checkBox_1 = new JCheckBox("暂不存储");
		checkBox_1.setBounds(197, 290, 80, 20);
		frame.getContentPane().add(checkBox_1);

		checkBox_2 = new JCheckBox("存储全部币种");
		checkBox_2.setBounds(285, 290, 115, 20);
		frame.getContentPane().add(checkBox_2);

		checkBox_3 = new JCheckBox("存储设置币种（推荐）");
		checkBox_3.setBounds(400, 290, 165, 20);
		frame.getContentPane().add(checkBox_3);

		if(v8==0){
			checkBox_1.setSelected(true);
			checkBox_2.setSelected(false);
			checkBox_3.setSelected(false);
		}else if(v8==1){
			checkBox_2.setSelected(true);
			checkBox_1.setSelected(false);
			checkBox_3.setSelected(false);
		}else if(v8==2){
			checkBox_3.setSelected(true);
			checkBox_1.setSelected(false);
			checkBox_2.setSelected(false);
		}
		checkBox_1.setActionCommand("0");
		checkBox_2.setActionCommand("1");
		checkBox_3.setActionCommand("2");
		checkBox_1.addActionListener(this);
		checkBox_2.addActionListener(this);
		checkBox_3.addActionListener(this);
		
		JButton button = new JButton("保存设置");
		button.setBounds(253, 329, 93, 23);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File userFile= PropertiesUtils.getUserDir();

				if(v6<v7){
					AlertUtils.showMessage("极值最大值不能小于最小值");
					return;
				}
				if(v5<v6||v5<v7){
					AlertUtils.showMessage("极值不能小于最大值或最小值");
					return;
				}

				if(v1>-1){
					AppConstant.SAVE_PRICE_MARGIN_TIME=v1*1000L;
					PropertiesUtils.updateProperty(userFile,"savePriceMarginTime", String.valueOf(AppConstant.SAVE_PRICE_MARGIN_TIME));
				}
				if(v2>-1){
					AppConstant.SAVE_ACCOUNT_MARGIN_TIME=v2*60*1000L;
					PropertiesUtils.updateProperty(userFile,"saveAccountMarginTime", String.valueOf(AppConstant.SAVE_ACCOUNT_MARGIN_TIME));
				}
				if(v3>-1){
					AppConstant.DO_BUY_TRADE_MARGIN_TIME=v3*1000L;
					PropertiesUtils.updateProperty(userFile,"doBuyMarginTime", String.valueOf(AppConstant.DO_BUY_TRADE_MARGIN_TIME));
				}
				if(v4>-1){
					AppConstant.DO_SELL_TRADE_MARGIN_TIME=v4*1000L;
					PropertiesUtils.updateProperty(userFile,"doSellMarginTime", String.valueOf(AppConstant.DO_SELL_TRADE_MARGIN_TIME));
				}
				if(v5>-1){
					AppConstant.TIME_MARGIN_DATA_COUNT=v5*6;
					PropertiesUtils.updateProperty(userFile,"tradeDataCount", String.valueOf(AppConstant.TIME_MARGIN_DATA_COUNT));
				}
				if(v6>-1){
					AppConstant.DISTANCE_THRESHOLD_MAX=v6*6;
					PropertiesUtils.updateProperty(userFile,"tradeMarginMax", String.valueOf(AppConstant.DISTANCE_THRESHOLD_MAX));
				}
				if(v7>-1){
					AppConstant.DISTANCE_THRESHOLD_MIN=v7*6;
					PropertiesUtils.updateProperty(userFile,"tradeMarginMin", String.valueOf(AppConstant.DISTANCE_THRESHOLD_MIN));
				}

				if(checkBox_1.isSelected()){
					AppConstant.PRICE_STORY_METHOD=0;
				}else  if(checkBox_2.isSelected()){
					AppConstant.PRICE_STORY_METHOD=1;
				}else  if(checkBox_3.isSelected()){
					AppConstant.PRICE_STORY_METHOD=2;
				}
				PropertiesUtils.updateProperty(userFile,"priceStoryMethod",String.valueOf(AppConstant.PRICE_STORY_METHOD));
				AlertUtils.showMessage("保存成功！");
			}
		});
		frame.getContentPane().add(button);
		frame.setLocationRelativeTo(null);
	}

	private void setJSlider(JSlider slider,Integer major,Integer minor){
		slider.setMajorTickSpacing(major);
		slider.setMinorTickSpacing(minor);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
			JSlider eslider= (JSlider) e.getSource();
			String text=null;
			if(eslider==slider){
				text=label.getText().split("：")[0];
				label.setText(text+"："+eslider.getValue());
				v1=eslider.getValue();
			}else if(eslider==slider_1){
				text=label_1.getText().split("：")[0];
				label_1.setText(text+"："+eslider.getValue());
				v2=eslider.getValue();
			}else if(eslider==slider_2){
				text=label_2.getText().split("：")[0];
				label_2.setText(text+"："+eslider.getValue());
				v3=eslider.getValue();
			}else if(eslider==slider_3){
				text=label_3.getText().split("：")[0];
				label_3.setText(text+"："+eslider.getValue());
				v4=eslider.getValue();
			}else if(eslider==slider_4){
				text=label_4.getText().split("：")[0];
				label_4.setText(text+"："+eslider.getValue());
				v5=eslider.getValue();
			}else if(eslider==slider_5){
				text=label_5.getText().split("：")[0];
				label_5.setText(text+"："+eslider.getValue());
				v6=eslider.getValue();
			}else if(eslider==slider_6){
				text=label_6.getText().split("：")[0];
				label_6.setText(text+"："+eslider.getValue());
				v7=eslider.getValue();
			}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
			String c=e.getActionCommand();
			if (c.equals("0")){
				checkBox_1.setSelected(true);
				checkBox_2.setSelected(false);
				checkBox_3.setSelected(false);
			}else if(c.equals("1")){
				checkBox_2.setSelected(true);
				checkBox_1.setSelected(false);
				checkBox_3.setSelected(false);
			}else if(c.equals("2")){
				checkBox_3.setSelected(true);
				checkBox_2.setSelected(false);
				checkBox_1.setSelected(false);
			}
	}
}
