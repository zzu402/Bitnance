package com.hzz.ui;

import com.hzz.service.CommonService;
import com.hzz.model.Price;
import com.hzz.utils.AlertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

public class PriceUI extends AbstractUI{
	private static Logger logger = LoggerFactory.getLogger(PriceUI.class);
	private JTextField textField;
	private List<Price> prices=null;
	private Integer pageIndex=0;
	private CommonService commonService=new CommonService();
	public PriceUI(int closeOperation) {
		initialize(closeOperation);
	}
	protected void initialize(int closeOperation) {
		frame = new JFrame();
		frame.setTitle("实时价格查询");
		frame.setBounds(100, 100, 320, 445);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel label = new JLabel("币名:");
		label.setBounds(31, 10, 35, 15);
		frame.getContentPane().add(label);

		textField = new JTextField();
		textField.setBounds(65, 7, 150, 21);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		JButton button = new JButton("查找");
		button.setBounds(225, 6, 69, 23);
		frame.getContentPane().add(button);

		JPanel panel = new JPanel();
		panel.setBounds(10, 35, 284, 327);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JButton button_1 = new JButton("上一页");
		button_1.setBounds(10, 372, 93, 23);

		frame.getContentPane().add(button_1);

		JButton button_2 = new JButton("下一页");
		button_2.setBounds(201, 372, 93, 23);
		button_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pageIndex++;
				setData(button_1,button_2,panel);
			}
		});
		button_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pageIndex--;
				setData(button_1,button_2,panel);
			}
		});
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text=textField.getText().trim();
				prices=commonService.getPrices(text);
				if(prices==null||prices.isEmpty()){
					AlertUtils.showMessage("您查找的货币不存在，请检查输入是否正确！");
				}
				pageIndex=0;
				setData(button_1,button_2,panel);
			}
		});

		frame.getContentPane().add(button_2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				prices=commonService.getPrices("");
				setData(button_1,button_2,panel);
			}
		}).start();

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(closeOperation);// 设置主窗体关闭按钮样式



	}

	private void setData(JButton button_1, JButton button_2, JPanel panel) {
		panel.removeAll();
		if(prices==null||prices.isEmpty()) {
			AlertUtils.showMessage("查找不到实时货币信息，请检查网络是否异常");
			return;
		}
		int pageCount=prices.size()/15;
		if(pageIndex==0){
			button_1.setVisible(false);
		}else{
			button_1.setVisible(true);
		}
		if(pageCount>pageIndex&&prices.size()%15!=0)
			button_2.setVisible(true);
		else if(pageCount>pageIndex+1&&prices.size()%15==0)
			button_2.setVisible(true);
		else
			button_2.setVisible(false);
		int size=prices.size()>(pageIndex+1)*15?(pageIndex+1)*15:prices.size();
		Price price=null;
		JLabel label1=null;
		JLabel label2=null;
		int margin=-20;
		for(int i=pageIndex*15;i<size;i++){
			price=prices.get(i);
			margin+=20;
			label1=new JLabel("币名:"+price.getSymbol());
			label1.setBounds(20,10+margin,100,20);
			label2=new JLabel("价格:"+price.getPrice());
			label2.setBounds(160,10+margin,100,20);
			panel.add(label1);
			panel.add(label2);
		}
		panel.repaint();
	}
}
