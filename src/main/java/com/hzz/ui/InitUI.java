package com.hzz.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class InitUI {

	private JFrame frame;
	private JTextField textField;
	private JTextField txtBitcon;
	private JTextField txtBitcon_1;
	private JLabel lblmysql;
	private JTextField txtBitcon_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InitUI window = new InitUI();
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
	public InitUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("\u672C\u5730\u6570\u636E\u5E93\u914D\u7F6E");
		frame.setBounds(100, 100, 404, 394);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel label = new JLabel("数据库端口:");
		label.setBounds(55, 119, 87, 15);
		frame.getContentPane().add(label);

		textField = new JTextField();
		textField.setText("3306");
		textField.setBounds(152, 116, 196, 21);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		JLabel label_1 = new JLabel("数据库名称:");
		label_1.setBounds(55, 159, 72, 15);
		frame.getContentPane().add(label_1);

		txtBitcon = new JTextField();
		txtBitcon.setText("bitcon");
		txtBitcon.setBounds(152, 156, 196, 21);
		frame.getContentPane().add(txtBitcon);
		txtBitcon.setColumns(10);

		JLabel label_2 = new JLabel("用户名:");
		label_2.setBounds(55, 201, 54, 15);
		frame.getContentPane().add(label_2);

		txtBitcon_1 = new JTextField();
		txtBitcon_1.setText("bitcon");
		txtBitcon_1.setBounds(152, 198, 196, 21);
		frame.getContentPane().add(txtBitcon_1);
		txtBitcon_1.setColumns(10);

		JLabel label_3 = new JLabel("密码:");
		label_3.setBounds(55, 238, 54, 15);
		frame.getContentPane().add(label_3);

		lblmysql = new JLabel("提示：目前软件只支持MYSQL数据库！");
		lblmysql.setBounds(55, 10, 312, 99);
		frame.getContentPane().add(lblmysql);

		JButton button = new JButton("设置");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		button.setBounds(55, 295, 63, 23);
		frame.getContentPane().add(button);

		JButton button_1 = new JButton("取消");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		button_1.setBounds(285, 295, 63, 23);
		frame.getContentPane().add(button_1);

		txtBitcon_2 = new JTextField();
		txtBitcon_2.setText("bitcon123");
		txtBitcon_2.setBounds(152, 235, 196, 21);
		frame.getContentPane().add(txtBitcon_2);
		txtBitcon_2.setColumns(10);
	}
}
