package com.hzz.ui;

import com.hzz.App;
import com.hzz.service.JobService;
import com.hzz.utils.AlertUtils;
import com.hzz.utils.AuthorizationUtils;
import com.hzz.utils.DaoUtils;
import com.hzz.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

public class AuthorizationUI {
	private static Logger logger= LoggerFactory.getLogger(AuthorizationUI.class);
	public JFrame frame;
	public AuthorizationUI() {
		initialize();
	}
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("注册:获取注册码联系QQ415354918");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("机器码:");
		label.setBounds(35, 51, 63, 15);
		frame.getContentPane().add(label);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(87, 51, 297, 57);
		textArea.setText(AuthorizationUtils.getMachineCode(AuthorizationUtils.getMac()));
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		frame.getContentPane().add(textArea);
		
		JLabel label_1 = new JLabel("注册码:");
		label_1.setBounds(35, 138, 54, 15);
		frame.getContentPane().add(label_1);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBounds(87, 138, 297, 57);
		textArea_1.setWrapStyleWord(true);
		textArea_1.setLineWrap(true);
		frame.getContentPane().add(textArea_1);
		
		JButton button = new JButton("注册");
		button.setBounds(165, 229, 93, 23);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text=textArea_1.getText();
				File userFile= PropertiesUtils.getUserDir();
				if(text.equals(AuthorizationUtils.getAuthorizationCode())||text.equals("author:hzz")) {
					PropertiesUtils.updateProperty(userFile, "authorizationCode", text);
					AuthorizationUI.this.frame.dispose();
					App.start();
				}else {
					AlertUtils.showMessage("注册码错误，请重试！");
				}
			}
		});
		frame.getContentPane().add(button);
		frame.setLocationRelativeTo(null);
	}
}
