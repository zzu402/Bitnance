package com.hzz.ui;

import com.hzz.constant.QueryConstant;
import com.hzz.constant.UIConstant;
import com.hzz.model.Config;
import com.hzz.service.ConfigService;
import com.hzz.ui.panel.UserInfoPanel;
import com.hzz.utils.AlertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;

public class ConfigUI extends AbstractUI implements ActionListener{
	private static Logger logger = LoggerFactory.getLogger(ConfigUI.class);
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
	private JButton save;
	private String title;
	private Integer type;
	private ConfigService configService=new ConfigService();

	public ConfigUI(String title ,Integer type,int closeOperation) {
		this.title=title;
		this.type=type;
		initialize(closeOperation);
	}
	protected void initialize(int closeOperation) {
		frame = new JFrame();
		frame.setTitle(title);
		frame.setBounds(100, 100, 375, 300);
		frame.getContentPane().setLayout(null);

		JLabel label = new JLabel("手动"+title+":");
		label.setBounds(37, 26, 91, 15);
		frame.getContentPane().add(label);
		
		checkBox = new JCheckBox("启用");
		checkBox.setBounds(119, 22, 62, 23);

		frame.getContentPane().add(checkBox);
		
		checkBox_1 = new JCheckBox("关闭");
		checkBox_1.setBounds(189, 22, 62, 23);
		frame.getContentPane().add(checkBox_1);

		JLabel label_1 = new JLabel("自动"+title+":");
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


		save=new JButton("保存设置");
		save.setBounds(140,230,100,20);
		save.setActionCommand("SAVE");
		save.addActionListener(this);
		frame.getContentPane().add(save);

		
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
		setData();
		check(checkBox,checkBox_1,checkBox_2,checkBox_3,checkBox_4,checkBox_5,checkBox_6,checkBox_7);
		frame.getContentPane().add(btnB);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(closeOperation);// 设置主窗体关闭按钮样式
	}

	private void setData() {
		String configType=null;
		if(type== UIConstant.CONFIG_BUY_UI){
			configType=QueryConstant.CONFIG_BUY_TYPE;
		}else if(type==UIConstant.CONFIG_SELL_UI){
			configType=QueryConstant.CONFIG_SELL_TYPE;
		}
		java.util.List <Config>list=configService.getConfigs(1,"",configType);
		if(list==null||list.isEmpty())
			return;
		Config config=list.get(0);
		if(config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_HAND_TYPE)){
			checkBox.setSelected(true);
		}else if(config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_AUTO_TYPE_1)) {
			checkBox_2.setSelected(true);
			checkBox_4.setSelected(true);
		}else if(config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_AUTO_TYPE_2)){
			checkBox_2.setSelected(true);
			checkBox_6.setSelected(true);
		}
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
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						ConfigSetUI window = new ConfigSetUI(type,WindowConstants.HIDE_ON_CLOSE);
						window.frame.setVisible(true);
					} catch (Exception e) {
						logger.error("启动异常",e);
					}
				}
			});
		}else if (command.equals("HELP1")){
			String message=null;
			if(type== UIConstant.CONFIG_BUY_UI){
				message="->币种设置->选择关注币种\r\n如果实时价格大于设定价格，则中止买入。\r\n如果设定价格为0，则执行下降拐点买入！";
			}else{
				message="->币种设置->选择关注币种\r\n如果实时价格小于设定价格，则中止卖出。\r\n如果设定价格为0，则执行上升拐点卖出！";
			}
			AlertUtils.showMessage(message);
		}else if (command.equals("HELP2")){
			AlertUtils.showMessage("策略2帮助");
		}else if(command.equals("SAVE")){
			save();
			//更新主页面的交易策略
			new Thread(new Runnable() {
				@Override
				public void run() {
					synchronized (UserInfoPanel.object) {
						UserInfoPanel.isNeedUpdateTradeMethod = true;
						UserInfoPanel.object.notify();
					}

				}
			}).start();

		}
	}

	private void save() {
		Map<String,Config> map=null;
		String configType=null;
		String configSelectedType="";
		if(type== UIConstant.CONFIG_BUY_UI){
			map=configService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_BUY,1);
			configType=QueryConstant.CONFIG_BUY_TYPE;
		}else if(type==UIConstant.CONFIG_SELL_UI){
			map=configService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_SELL,1);
			configType=QueryConstant.CONFIG_SELL_TYPE;
		}
		boolean isClose=false;
		if (map==null&&checkBox.isSelected()){
			AlertUtils.showMessage("币种未配置，请先配置币种");
			return;
		}
		if(checkBox.isSelected()){//手动开关开启
			configSelectedType=QueryConstant.CONFIG_SELECTED_HAND_TYPE;
		}else  if(checkBox_2.isSelected()&&checkBox_4.isSelected()){//自动开关+策略1
			configSelectedType=QueryConstant.CONFIG_SELECTED_AUTO_TYPE_1;
		}else  if(checkBox_2.isSelected()&&checkBox_6.isSelected()){//自动开关+策略2
			configSelectedType=QueryConstant.CONFIG_SELECTED_AUTO_TYPE_2;
		}else if(checkBox_1.isSelected()&&!checkBox_2.isSelected()&&!checkBox_4.isSelected()&&!checkBox_6.isSelected()){
			isClose=true;
		}else if(checkBox_3.isSelected()&&!checkBox.isSelected()){
			isClose=true;
		}
		java.util.List<Config> list=configService.getConfigs(0,null,configType);
		Config config=null;
		int find=-1;
		if(list!=null){
			for(int i=0;i<list.size();i++){
				 config=list.get(i);
				if(!config.getSymbol().equals(configSelectedType)){
					config.setStatus(0);
				}else {
					config.setStatus(1);
					find=i;
				}
				config.setId(null);
			}
		}
		if(find<0&&!isClose){
			config=new Config();
			config.setStatus(1);
			config.setSymbol(configSelectedType);
			config.setType(configType);
			config.setDescription("配置开关信息");
			config.setCreateTime(System.currentTimeMillis()/1000);
			config.setConfigInfo("{}");
			if(list==null)
				list=new ArrayList<>();
			list.add(config);
		}
		configService.insertOrUpdateConfigs(list);
		AlertUtils.showMessage("保存成功，程序将按照配置进行！");
		this.frame.dispose();
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
