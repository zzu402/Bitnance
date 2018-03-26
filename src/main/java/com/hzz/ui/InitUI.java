package com.hzz.ui;

import com.hzz.utils.AlertUtils;
import com.hzz.utils.DBUtils;
import com.hzz.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.EventQueue;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class InitUI extends AbstractUI{
	private Logger logger = LoggerFactory.getLogger(InitUI.class);
	private JTextField textField;
	private JTextField txtBitcon;
	private JTextField txtBitcon_1;
	private JLabel lblmysql;
	private JTextField txtBitcon_2;
	public InitUI(int closeOperation) {
		initialize(closeOperation);
	}
	protected void initialize(int closeOperation) {
		frame = new JFrame();
		frame.setTitle("\u672C\u5730\u6570\u636E\u5E93\u914D\u7F6E");
		frame.setBounds(100, 100, 404, 394);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		JLabel label = new JLabel("数据库端口:");
		label.setBounds(55, 119, 87, 15);
		textField = new JTextField();
		textField.setBounds(152, 116, 196, 21);
		textField.setColumns(10);
		JLabel label_1 = new JLabel("数据库名称:");
		label_1.setBounds(55, 159, 72, 15);
		txtBitcon = new JTextField();
		txtBitcon.setBounds(152, 156, 196, 21);
		txtBitcon.setColumns(10);
		JLabel label_2 = new JLabel("用户名:");
		label_2.setBounds(55, 201, 54, 15);
		txtBitcon_1 = new JTextField();
		txtBitcon_1.setBounds(152, 198, 196, 21);
		txtBitcon_1.setColumns(10);
		JLabel label_3 = new JLabel("密码:");
		label_3.setBounds(55, 238, 54, 15);
		lblmysql = new JLabel("提示：目前软件只支持MYSQL数据库！");
		lblmysql.setBounds(55, 10, 312, 99);
		txtBitcon_2 = new JTextField();
		txtBitcon_2.setBounds(152, 235, 196, 21);
		txtBitcon_2.setColumns(10);
		initData();
		frame.getContentPane().add(textField);
		frame.getContentPane().add(label_1);
		frame.getContentPane().add(txtBitcon);
		frame.getContentPane().add(txtBitcon_1);
		frame.getContentPane().add(label_2);
		frame.getContentPane().add(label_3);
		frame.getContentPane().add(txtBitcon_2);
		frame.getContentPane().add(lblmysql);
		frame.getContentPane().add(label);
		JButton button_1 = new JButton("取消");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		JButton button = new JButton("设置");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.error("数据库信息初始化开始写入配置");
				String port=textField.getText().trim();
				String name=txtBitcon.getText().trim();
				String userName=txtBitcon_1.getText().trim();
				String password=txtBitcon_2.getText().trim();
				File userFile=PropertiesUtils.getUserDir();
				PropertiesUtils.updateProperty(userFile,"port",port);
				PropertiesUtils.updateProperty(userFile,"dbName",name);
				PropertiesUtils.updateProperty(userFile,"userName",userName);
				PropertiesUtils.updateProperty(userFile,"password",password);
				logger.error("数据库信息初始化写入配置结束");
				AlertUtils.showMessage("数据库配置信息写入成功！");
			}
		});
		button.setBounds(55, 295, 63, 23);
		frame.getContentPane().add(button);

		JButton button_2 = new JButton("设置并初始化数据库");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.error("数据库信息初始化开始");
				String port=textField.getText().trim();
				String name=txtBitcon.getText().trim();
				String userName=txtBitcon_1.getText().trim();
				String password=txtBitcon_2.getText().trim();
				File userFile=PropertiesUtils.getUserDir();
				PropertiesUtils.updateProperty(userFile,"port",port);
				PropertiesUtils.updateProperty(userFile,"dbName",name);
				PropertiesUtils.updateProperty(userFile,"userName",userName);
				PropertiesUtils.updateProperty(userFile,"password",password);
				DBUtils.initDatabase();
				logger.error("数据库信息初始化结束");
				AlertUtils.showMessage("数据库信息初始化成功！");
			}
		});
		button_2.setBounds(125, 295, 153, 23);
		frame.getContentPane().add(button_2);

		button_1.setBounds(285, 295, 63, 23);
		frame.getContentPane().add(button_1);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(closeOperation);// 设置主窗体关闭按钮样式
	}
	private void initData(){
		PropertiesUtils.loadProps(PropertiesUtils.getUserDir());
		String port=PropertiesUtils.getString("port","3306");
		String name=PropertiesUtils.getString("dbName","bitcon");;
		String userName=PropertiesUtils.getString("userName","root");;
		String password=PropertiesUtils.getString("password","root");;
		textField.setText(port);
		txtBitcon.setText(name);
		txtBitcon_1.setText(userName);
		txtBitcon_2.setText(password);
	}
}
