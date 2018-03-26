package com.hzz.ui;

import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.User;
import com.hzz.utils.AlertUtils;
import com.hzz.utils.DaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

public class NotifyUI extends AbstractUI {
	private Logger logger = LoggerFactory.getLogger(NotifyUI.class);
	private JTextField textField;
	private JTextField textField_1;
	private  JTextArea txtrsymbolprice;
	private JTextArea textArea_1;
	public NotifyUI(int closeOperation) {
		initialize(closeOperation);
	}
	protected void initialize(int closeOperation) {
		frame = new JFrame();
		frame.setBounds(100, 100, 487, 454);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("通知设置");
		frame.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("用户昵称:");
		label.setBounds(78, 27, 60, 15);
		frame.getContentPane().add(label);
		
		textField = new JTextField();
		textField.setBounds(142, 24, 249, 21);

		textField.setColumns(10);
		
		JLabel label_1 = new JLabel("用户邮箱:");
		label_1.setBounds(78, 71, 60, 15);
		frame.getContentPane().add(label_1);
		
		textField_1 = new JTextField();
		textField_1.setBounds(142, 68, 249, 21);

		textField_1.setColumns(10);
		
		JLabel label_2 = new JLabel("买入用语:");
		label_2.setBounds(78, 109, 60, 15);
		frame.getContentPane().add(label_2);

		txtrsymbolprice= new JTextArea();
		txtrsymbolprice.setRows(5);
		txtrsymbolprice.setBounds(142, 109, 249, 94);
		txtrsymbolprice.setLineWrap(true);
		txtrsymbolprice.setWrapStyleWord(true);

		
		JLabel label_3 = new JLabel("卖出用语:");
		label_3.setBounds(78, 214, 60, 15);
		frame.getContentPane().add(label_3);
		
		textArea_1 = new JTextArea();
		textArea_1.setRows(5);
		textArea_1.setBounds(142, 213, 249, 94);
		textArea_1.setLineWrap(true);
		textArea_1.setWrapStyleWord(true);

		initData();
		frame.getContentPane().add(textField);
		frame.getContentPane().add(textField_1);
		frame.getContentPane().add(txtrsymbolprice);
		frame.getContentPane().add(textArea_1);
		JLabel lblsymbolprice = new JLabel("提示：使用{symbol}代表货币名称，使用{price}代表价格。");
		lblsymbolprice.setBounds(78, 327, 357, 15);
		frame.getContentPane().add(lblsymbolprice);

		JLabel lblsymbolprice1 = new JLabel("使用{time}代表当前时间。");
		lblsymbolprice1.setBounds(78, 327+20, 357, 15);
		frame.getContentPane().add(lblsymbolprice1);
		JButton button = new JButton("设置");
		button.setBounds(202, 368+10, 79, 23);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name=textField.getText().trim();
				String email=textField_1.getText().trim();
				String sell=txtrsymbolprice.getText().trim();
				String buy=textArea_1.getText().trim();
				ModelDao modelDao= DaoUtils.getDao(DaoUtils.getTemplate());
				try {
					logger.info("插入用户表开始");
					List list=modelDao.select(new User());
					User user=null;
					if(list.isEmpty()){
						user=new User();
					}else {
						user= (User) list.get(0);
					}
					user.setEmail(email);
					user.setName(name);
					user.setSellTemplet(sell);
					user.setBuyTemplet(buy);
					modelDao.insertOrUpdate(user,new String[]{"name","email","sellTemplet","buyTemplet"});
					logger.info("插入用户表结束");
					AlertUtils.showMessage("数据设置成功！");
				} catch (CommonException e1) {
					logger.error("插入用户表失败",e1);
					AlertUtils.showMessage("数据设置失败！");
				}

			}
		});

		frame.getContentPane().add(button);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(closeOperation);// 设置主窗体关闭按钮样式
	}

	private void initData(){
		ModelDao modelDao=DaoUtils.getDao(DaoUtils.getTemplate());
		try {
			List list=modelDao.select(new User());
			if(list.isEmpty())
				return ;
			User user= (User) list.get(0);
			textField.setText(user.getName());
			textField_1.setText(user.getEmail());
			txtrsymbolprice.setText(user.getSellTemplet());
			textArea_1.setText(user.getBuyTemplet());
		} catch (CommonException e) {
			logger.error("初始化用户数据失败",e);
		}
	}
}
