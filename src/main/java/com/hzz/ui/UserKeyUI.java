package com.hzz.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class UserKeyUI {

	private JFrame frmKey;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserKeyUI window = new UserKeyUI();
					window.frmKey.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UserKeyUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmKey = new JFrame();
		frmKey.setTitle("Key设置");
		frmKey.getContentPane().setLayout(null);
		
		JLabel lblSecretkey = new JLabel("SecretKey:");
		lblSecretkey.setBounds(32, 101, 60, 15);
		frmKey.getContentPane().add(lblSecretkey);
		
		JLabel lblApikey = new JLabel("ApiKey:");
		lblApikey.setBounds(32, 150, 54, 15);
		frmKey.getContentPane().add(lblApikey);
		
		textField = new JTextField();
		textField.setBounds(96, 98, 328, 21);
		frmKey.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(96, 147, 328, 21);
		frmKey.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblsecretkeyapikey = new JLabel("提示：此处设置SecretKey和ApiKey，当前软件仅支持币安网。");
		lblsecretkeyapikey.setBounds(32, 10, 380, 15);
		frmKey.getContentPane().add(lblsecretkeyapikey);
		
		JLabel lblsecretkeyapikey_1 = new JLabel("请到币安网注册并获取SecretKey和ApiKey。");
		lblsecretkeyapikey_1.setBounds(32, 35, 267, 15);
		frmKey.getContentPane().add(lblsecretkeyapikey_1);
		
		JButton button = new JButton("设置");
		button.setBounds(30, 197, 69, 23);
		frmKey.getContentPane().add(button);
		
		JButton button_1 = new JButton("取消");
		button_1.setBounds(355, 197, 69, 23);
		frmKey.getContentPane().add(button_1);
	}
}
