package com.hzz.ui;

import com.hzz.utils.AlertUtils;

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BuyConfigUI implements ActionListener{

	public JFrame frame;
	private JCheckBox checkBox;
	private JCheckBox checkBox_1;
	private JCheckBox checkBox_2;
	private JCheckBox checkBox_3;
	private JCheckBox checkBox_4;
	private JCheckBox checkBox_5;
	private JCheckBox checkBox_6;
	private JCheckBox checkBox_7;
	private JButton btnB;
	private JButton button_2;
	private JButton button_3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BuyConfigUI window = new BuyConfigUI();
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
	public BuyConfigUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("买入设置");
		frame.setBounds(100, 100, 375, 250);
		frame.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("手动买入设置:");
		label.setBounds(37, 26, 91, 15);
		frame.getContentPane().add(label);
		
		checkBox = new JCheckBox("启用");
		checkBox.setBounds(119, 22, 62, 23);

		frame.getContentPane().add(checkBox);
		
		checkBox_1 = new JCheckBox("关闭");
		checkBox_1.setBounds(189, 22, 62, 23);
		frame.getContentPane().add(checkBox_1);

		JLabel label_1 = new JLabel("自动买入设置:");
		label_1.setBounds(37, 82, 84, 15);
		frame.getContentPane().add(label_1);
		
		checkBox_2 = new JCheckBox("启用");
		checkBox_2.setBounds(119, 78, 62, 23);
		frame.getContentPane().add(checkBox_2);
		
		checkBox_3 = new JCheckBox("关闭");
		checkBox_3.setBounds(184, 78, 67, 23);
		frame.getContentPane().add(checkBox_3);
		
		checkBox_4 = new JCheckBox("启用");
		checkBox_4.setBounds(119, 125, 62, 23);
		frame.getContentPane().add(checkBox_4);
		
		checkBox_5 = new JCheckBox("关闭");
		checkBox_5.setBounds(189, 125, 67, 23);
		frame.getContentPane().add(checkBox_5);
		
		JLabel label_2 = new JLabel("策略1:");
		label_2.setBounds(37, 129, 54, 15);
		frame.getContentPane().add(label_2);
		
		JLabel label_3 = new JLabel("策略2:");
		label_3.setBounds(37, 178, 54, 15);
		frame.getContentPane().add(label_3);
		
		checkBox_6 = new JCheckBox("启用");
		checkBox_6.setBounds(119, 174, 62, 23);
		frame.getContentPane().add(checkBox_6);
		
		checkBox_7 = new JCheckBox("关闭");
		checkBox_7.setBounds(189, 174, 67, 23);
		frame.getContentPane().add(checkBox_7);
		
		button_2= new JButton("帮助");
		button_2.setBounds(262, 125, 90, 23);
		frame.getContentPane().add(button_2);
		
		button_3 = new JButton("帮助");
		button_3.setBounds(262, 174, 90, 23);
		frame.getContentPane().add(button_3);
		
		btnB = new JButton("币种设置");

		btnB.setActionCommand("SET");
		button_2.setActionCommand("HELP1");
		button_3.setActionCommand("HELP2");
		checkBox.setActionCommand("0");
		checkBox_1.setActionCommand("1");
		checkBox_2.setActionCommand("2");
		checkBox_3.setActionCommand("3");
		checkBox_4.setActionCommand("4");
		checkBox_5.setActionCommand("5");
		checkBox_6.setActionCommand("6");
		checkBox_7.setActionCommand("7");
		checkBox.addActionListener(this);
		checkBox_1.addActionListener(this);
		checkBox_2.addActionListener(this);
		checkBox_3.addActionListener(this);
		checkBox_4.addActionListener(this);
		checkBox_5.addActionListener(this);
		checkBox_6.addActionListener(this);
		checkBox_7.addActionListener(this);
		btnB.addActionListener(this);
		button_2.addActionListener(this);
		button_3.addActionListener(this);
		btnB.setBounds(262, 22, 90, 23);
		check(checkBox,checkBox_1,checkBox_2,checkBox_3,checkBox_4,checkBox_5,checkBox_6,checkBox_7);
		frame.getContentPane().add(btnB);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);// 设置主窗体关闭按钮样式
	}

	private void check(JCheckBox checkBox, JCheckBox checkBox_1, JCheckBox checkBox_2, JCheckBox checkBox_3, JCheckBox checkBox_4, JCheckBox checkBox_5, JCheckBox checkBox_6, JCheckBox checkBox_7) {
		boolean isAuto=false;
		if(checkBox.isSelected()){
			isAuto=false;
			checkBox_1.setSelected(false);
			checkBox_2.setSelected(false);
			checkBox_3.setSelected(false);
			checkBox_4.setSelected(false);
			checkBox_5.setSelected(false);
			checkBox_6.setSelected(false);
			checkBox_7.setSelected(false);
		}
		if(checkBox_1.isSelected()){
			isAuto=true;
			checkBox.setSelected(false);
			checkBox_1.setSelected(true);
		}
		if(checkBox_2.isSelected()){
			isAuto=true;
			checkBox.setSelected(false);
			checkBox_1.setSelected(false);
			checkBox_3.setSelected(false);
		}
		if(checkBox_3.isSelected()){
			isAuto=false;
			checkBox_2.setSelected(false);
			checkBox_4.setSelected(false);
			checkBox_5.setSelected(false);
			checkBox_6.setSelected(false);
			checkBox_7.setSelected(false);
		}
		if(checkBox_4.isSelected()){
			checkBox_5.setSelected(false);
			checkBox_6.setSelected(false);
			checkBox_7.setSelected(false);
		}
		if(checkBox_5.isSelected()){
			checkBox_4.setSelected(false);
		}
		if(checkBox_6.isSelected()){
			checkBox_5.setSelected(false);
			checkBox_4.setSelected(false);
			checkBox_7.setSelected(false);
		}
		if(checkBox_7.isSelected()){
			checkBox_6.setSelected(false);
		}
		btnB.setEnabled(!isAuto);
		checkBox.setEnabled(!isAuto);
		checkBox_1.setEnabled(!isAuto);
		checkBox_2.setEnabled(isAuto);
		checkBox_3.setEnabled(isAuto);
		checkBox_4.setEnabled(isAuto);
		checkBox_5.setEnabled(isAuto);
		checkBox_6.setEnabled(isAuto);
		checkBox_7.setEnabled(isAuto);
		button_2.setEnabled(isAuto);
		button_3.setEnabled(isAuto);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String command=e.getActionCommand();

		if (command.equals("0")) {
			unSelected();
			checkBox.setSelected(true);
			check(checkBox,checkBox_1,checkBox_2,checkBox_3,checkBox_4,checkBox_5,checkBox_6,checkBox_7);
		}
		else  if(command.equals("1")) {
			unSelected();
			checkBox_1.setSelected(true);
			check(checkBox,checkBox_1,checkBox_2,checkBox_3,checkBox_4,checkBox_5,checkBox_6,checkBox_7);
		}
		else  if(command.equals("2")) {
			unSelected();
			checkBox_2.setSelected(true);
			check(checkBox,checkBox_1,checkBox_2,checkBox_3,checkBox_4,checkBox_5,checkBox_6,checkBox_7);
		}
		else  if(command.equals("3")) {
			unSelected();
			checkBox_3.setSelected(true);
			check(checkBox,checkBox_1,checkBox_2,checkBox_3,checkBox_4,checkBox_5,checkBox_6,checkBox_7);
		}
		else  if(command.equals("4")) {
			unSelected();
			checkBox_2.setSelected(true);
			checkBox_4.setSelected(true);
			check(checkBox,checkBox_1,checkBox_2,checkBox_3,checkBox_4,checkBox_5,checkBox_6,checkBox_7);
		}
		else  if(command.equals("5")) {
			unSelected();
			checkBox_2.setSelected(true);
			checkBox_5.setSelected(true);
			check(checkBox,checkBox_1,checkBox_2,checkBox_3,checkBox_4,checkBox_5,checkBox_6,checkBox_7);
		}
		else  if(command.equals("6")) {
			unSelected();
			checkBox_2.setSelected(true);
			checkBox_6.setSelected(true);
			check(checkBox,checkBox_1,checkBox_2,checkBox_3,checkBox_4,checkBox_5,checkBox_6,checkBox_7);

		}
		else  if(command.equals("7")) {
			checkBox_2.setSelected(true);
			checkBox_7.setSelected(true);
			check(checkBox,checkBox_1,checkBox_2,checkBox_3,checkBox_4,checkBox_5,checkBox_6,checkBox_7);
		}else if(command.equals("SET")){
			AlertUtils.showMessage("币种设置");
		}else if (command.equals("HELP1")){
			AlertUtils.showMessage("策略1帮助");
		}else if (command.equals("HELP2")){
			AlertUtils.showMessage("策略2帮助");
		}

	}
	private  void unSelected(){
		checkBox.setSelected(false);
		checkBox_1.setSelected(false);
		checkBox_2.setSelected(false);
		checkBox_3.setSelected(false);
		checkBox_4.setSelected(false);
		checkBox_5.setSelected(false);
		checkBox_6.setSelected(false);
		checkBox_7.setSelected(false);
	}
}
