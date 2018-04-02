package com.hzz.ui.panel;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Color;

public class GuidePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public GuidePanel() {
		setLayout(null);
		int offsetX=-60;
		int widthOffset=30;
		JLabel label = new JLabel("第一步：");
		label.setBounds(80+offsetX, 65, 54, 15);
		add(label);
		
		JLabel lblNewLabel = new JLabel("设置数据库.");
		lblNewLabel.setBounds(144+offsetX, 65, 392+widthOffset, 15);
		add(lblNewLabel);
		
		JLabel lblmysql = new JLabel("使用MySQL创建一个数据库，点击左上角“基本设置”，选择“数据库设置”。");
		lblmysql.setBounds(144+offsetX, 90, 559+widthOffset, 15);
		add(lblmysql);
		
		JLabel label_1 = new JLabel("填写数据库配置信息，点击“设置并初始化数据库”。");
		label_1.setBounds(144+offsetX, 115, 337+widthOffset, 15);
		add(label_1);
		
		JLabel label_2 = new JLabel("提示：“设置”按钮仅将数据库配置写入配置文件，“设置并初始化数据库”按钮生成软件所需数据表。");
		label_2.setForeground(Color.RED);
		label_2.setBounds(144+offsetX, 140, 559+widthOffset, 15);
		add(label_2);
		
		JLabel label_3 = new JLabel("第二步：");
		label_3.setBounds(80+offsetX, 168, 54, 15);
		add(label_3);
		
		JLabel lblkey = new JLabel("设置Key");
		lblkey.setBounds(144+offsetX, 168, 54, 15);
		add(lblkey);
		
		JLabel lblapikey = new JLabel("该软件进行对接币安网API接口，访问需要到币安网进行相应注册并获取所需的两项Key.");
		lblapikey.setBounds(144+offsetX, 193, 528+widthOffset, 15);
		add(lblapikey);
		
		JLabel lblkey_1 = new JLabel("点击左上角\"基本设置\",选择\"Key设置\"，填写密钥，点击设置。");
		lblkey_1.setBounds(144+offsetX, 218, 464+widthOffset, 15);
		add(lblkey_1);
		
		JLabel label_4 = new JLabel("第三步：");
		label_4.setBounds(80+offsetX, 252, 54, 15);
		add(label_4);
		
		JLabel lblNewLabel_1 = new JLabel("选择交易策略");
		lblNewLabel_1.setBounds(144+offsetX, 252, 111, 15);
		add(lblNewLabel_1);
		
		JLabel label_5 = new JLabel("软件交易策略分手动和自动，手动策略靠人为设置触发动作，程序根据触发动作执行相应触发行为。");
		label_5.setBounds(144+offsetX, 277, 593+widthOffset, 15);
		add(label_5);
		
		JLabel label_6 = new JLabel("自动交易则依靠程序自身的算法进行交易选择。用户可根据自己需求进行相应选择。");
		label_6.setBounds(144+offsetX, 302, 528+widthOffset, 15);
		add(label_6);
		
		JLabel label_7 = new JLabel("点击左上角“交易设置”，点击“买入设置”，或“卖出设置”，选择对应的策略方式。");
		label_7.setBounds(144+offsetX, 327, 571+widthOffset, 15);
		add(label_7);
		
		JLabel label_8 = new JLabel("第四步：");
		label_8.setBounds(80+offsetX, 376, 54, 15);
		add(label_8);
		
		JLabel label_9 = new JLabel("通知提醒（可选）");
		label_9.setBounds(144+offsetX, 376, 159, 15);
		add(label_9);
		
		JLabel label_10 = new JLabel("用户要在软件完成每一笔交易时及时获得通知，可在左上角“基本设置”中的“通知设置”填写对应信息获取通知信息。");
		label_10.setBounds(144+offsetX, 401, 646+widthOffset, 15);
		add(label_10);

	}
}
