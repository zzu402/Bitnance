package com.hzz.ui;

import com.hzz.constant.QueryConstant;
import com.hzz.model.Config;
import com.hzz.service.ConfigService;
import com.hzz.service.PriceService;
import com.hzz.service.TestService;
import com.hzz.utils.AlertUtils;
import com.hzz.utils.StringUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

import javax.swing.*;

public class ImitateUI extends AbstractUI {

	private TestService testService=new TestService();
	private Integer type=0;
	private ConfigService configService=new ConfigService();
	private String symbol=null;
	private Integer methodType=0;
	private PriceService priceService=new PriceService();
	public ImitateUI(Integer closeOperation) {
		initialize(closeOperation);
	}
	protected void initialize(int closeOperation) {
		frame = new JFrame();
		frame.setBounds(100, 100, 300, 300);
		frame.setDefaultCloseOperation(closeOperation);
		frame.getContentPane().setLayout(null);
		frame.setTitle("买卖模拟");
		JLabel label = new JLabel("模拟种类");
		label.setBounds(24, 37, 69, 15);
		frame.getContentPane().add(label);
		
		JLabel label_1 = new JLabel("模拟币种");
		label_1.setBounds(24, 82, 54, 15);
		frame.getContentPane().add(label_1);
		
		JRadioButton radioButton = new JRadioButton("买入模拟");
		radioButton.setBounds(89, 33, 83, 23);
		frame.getContentPane().add(radioButton);
		
		JRadioButton radioButton_1 = new JRadioButton("卖出模拟");
		radioButton_1.setBounds(174, 33, 121, 23);
		frame.getContentPane().add(radioButton_1);

		radioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				type=QueryConstant.TYPE_BUY;
			}
		});
		radioButton_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				type=QueryConstant.TYPE_SELL;
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(radioButton);
		group.add(radioButton_1);

		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(88, 79, 159, 21);
		addSymbolData(comboBox);

		frame.getContentPane().add(comboBox);
		
		JLabel lblprice = new JLabel("提示：仅对数据库数据进行模拟。");
		lblprice.setBounds(24, 149, 300, 15);
		frame.getContentPane().add(lblprice);
		
		JLabel label_2 = new JLabel("策略选择");
		label_2.setBounds(24, 121, 54, 15);
		frame.getContentPane().add(label_2);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setBounds(89, 118, 158, 21);
		frame.getContentPane().add(comboBox_1);
		addMethod(comboBox_1);
		
		JButton button = new JButton("生成模拟");
		button.setBounds(89, 203, 93, 23);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(type<1) {
					AlertUtils.showMessage("请选择模拟种类");
					return;
				}
				if(StringUtil.isBlank(symbol)){
					AlertUtils.showMessage("请选择币种");
					return;
				}
				if(methodType<1){
					AlertUtils.showMessage("请选择策略");
					return;
				}
				testService.doImitate();
				final String title = "模拟";
				final ImitateBuyOrSellUI demo = new ImitateBuyOrSellUI( title,priceService.getPrice(symbol,0,0),priceService.getPrice("BTCUSDT",type==1?1:0,type==1?0:1),type);
				demo.pack( );
				demo.setLocationRelativeTo(null);
				demo.setVisible( true );

			}
		});
		frame.getContentPane().add(button);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
	}

	private void addMethod(JComboBox comboBox_1) {
		comboBox_1.addItem("请选择");
		comboBox_1.addItem("自动策略1");
//		comboBox_1.addItem("自动策略2");
		comboBox_1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){

					String s= (String) e.getItem();
					if(s.equals("自动策略1")){
						methodType=1;
					}else if(s.equals("自动策略2")){
						methodType=2;
					}
				}
			}
		});
	}

	private void addSymbolData(JComboBox comboBox) {
		Map<String, Config> map=configService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_BUY,1);
		Map<String,Config>	map1=configService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_SELL,1);
		map.putAll(map1);
		Iterator it=map.entrySet().iterator();
		comboBox.addItem("请选择");
		while (it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			comboBox.addItem(key);
		}
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					symbol= (String) e.getItem();
				}
			}
		});
	}


}
