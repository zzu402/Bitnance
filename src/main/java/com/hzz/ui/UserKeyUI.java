package com.hzz.ui;
import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.User;
import com.hzz.utils.AlertUtils;
import com.hzz.utils.DaoUtils;
import com.hzz.utils.EnDecryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.UUID;
import javax.swing.*;

public class UserKeyUI extends AbstractUI{
	private Logger logger = LoggerFactory.getLogger(UserKeyUI.class);
	private JTextField textField;
	private JTextField textField_1;

	public UserKeyUI(int closeOperation) {
		initialize(closeOperation);
	}
	protected void initialize(int closeOperation) {
		frame = new JFrame();
		frame.setTitle("Key设置");
		frame.getContentPane().setLayout(null);
		frame.setBounds(0,0,460,300);
		JLabel lblSecretkey = new JLabel("SecretKey:");
		lblSecretkey.setBounds(32, 101, 65, 15);
		frame.getContentPane().add(lblSecretkey);

		JLabel lblApikey = new JLabel("ApiKey:");
		lblApikey.setBounds(32, 150, 54, 15);
		frame.getContentPane().add(lblApikey);

		textField = new JTextField();
		textField.setBounds(96, 98, 328, 21);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setBounds(96, 147, 328, 21);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);

		initData();

		JLabel lblsecretkeyapikey = new JLabel("提示：此处设置SecretKey和ApiKey，当前软件仅支持币安网。");
		lblsecretkeyapikey.setBounds(32, 10, 380, 15);
		frame.getContentPane().add(lblsecretkeyapikey);

		JLabel lblsecretkeyapikey_1 = new JLabel("请到币安网注册并获取SecretKey和ApiKey。");
		lblsecretkeyapikey_1.setBounds(32, 35, 267, 15);
		frame.getContentPane().add(lblsecretkeyapikey_1);

		JButton button = new JButton("设置");
		button.setBounds(30, 197, 69, 23);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String secretKey=textField.getText().trim();
				String apiKey=textField_1.getText().trim();
				ModelDao modelDao=DaoUtils.getDao(DaoUtils.getTemplate());
				try {
					logger.info("插入用户表开始");
					List list=modelDao.select(new User());
					User user=null;
					String salt= UUID.randomUUID().toString();
					if(list.isEmpty()){
						user=new User();
					}else {
						user= (User) list.get(0);
					}
					user.setApi_key(EnDecryptUtil.encryptAES(apiKey,salt));
					user.setSecret_key(EnDecryptUtil.encryptAES(secretKey,salt));
					user.setSalt(salt);
					modelDao.insertOrUpdate(user,new String[]{"secret_key","api_key","salt"});
					logger.info("插入用户表结束");
					AlertUtils.showMessage("数据设置成功！");
				} catch (CommonException e1) {
					logger.error("插入用户表失败",e1);
					AlertUtils.showMessage("数据设置失败！");
				}

			}
		});
		frame.getContentPane().add(button);
		JButton button_1 = new JButton("取消");
		button_1.setBounds(355, 197, 69, 23);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		frame.getContentPane().add(button_1);
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
			String apiKey=user.getApi_key();
			String secretKey=user.getSecret_key();
			String salt=user.getSalt();
			textField.setText(EnDecryptUtil.decryptAES(secretKey,salt));
			textField_1.setText(EnDecryptUtil.decryptAES(apiKey,salt));
		} catch (CommonException e) {
			logger.error("初始化用户数据失败",e);
		}

	}
}
