package com.hzz.ui;
import com.hzz.constant.QueryConstant;
import com.hzz.exception.CommonException;
import com.hzz.model.Config;
import com.hzz.model.Price;
import com.hzz.service.ConfigService;
import com.hzz.service.PriceService;
import com.hzz.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class ConfigSetUI extends AbstractUI{
	private static Logger logger = LoggerFactory.getLogger(ConfigSetUI.class);
	private JTextField textField;
	private List<Price> prices=null;
	private Integer pageIndex=0;
	private List<JTextField> textFieldList=null;
	private List<JCheckBox>checkboxList=null;
	private String titlePre;
	private String configTypePre;
	private String configInfoPre;
	private ConfigService configService=new ConfigService();
	private PriceService priceService=new PriceService();


	public ConfigSetUI(Integer type,int closeOperation) {
		switch (type){
			case 1:
				titlePre="买入";
				configTypePre= QueryConstant.CONFIG_TYPE_PRE_BUY;
				configInfoPre="buy";
				break;
			case 2:
				titlePre="卖出";
				configTypePre=QueryConstant.CONFIG_TYPE_PRE_SELL;
				configInfoPre="sell";
				break;
		}
		initialize(closeOperation);
	}
	protected void initialize(int closeOperation) {
		frame = new JFrame();
		frame.setTitle(titlePre+"币种设置");
		frame.setBounds(100, 100, 1050+150, 445);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel label = new JLabel("币名:");
		label.setBounds(31, 10, 35, 15);
		frame.getContentPane().add(label);

		JLabel label1 = new JLabel("提示:断点幅度为百分比，如2%填写2 优先级0-1000,数字大优先级高 截止时间格式yyyy-MM-dd hh:mm:ss,如2018-1-1 09:00:00");
		label1.setBounds(300, 10, 720, 15);
		frame.getContentPane().add(label1);

		textField = new JTextField();
		textField.setBounds(65, 7, 150, 21);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		JButton button = new JButton("查找");
		button.setBounds(225, 6, 69, 23);
		frame.getContentPane().add(button);

		JPanel panel = new JPanel();
		panel.setBounds(10, 35, 1050+150, 327);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JButton button_1 = new JButton("上一页");
		button_1.setBounds(30, 372, 90, 23);

		frame.getContentPane().add(button_1);

		JButton button_3 = new JButton("保存本页设置");
		button_3.setBounds(450, 372, 120, 23);
		button_3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveData();
			}
		});
		frame.getContentPane().add(button_3);
		JButton button_2 = new JButton("下一页");
		button_2.setBounds(920, 372, 90, 23);
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
				String symbol=textField.getText().trim();
				prices=priceService.getPrices(symbol);
				if(prices.isEmpty()){
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
				prices=priceService.getPrices("");
				if(prices==null)
					return;
				textFieldList=new ArrayList<>(prices.size()*2);
				checkboxList=new ArrayList<>(prices.size());
				setData(button_1,button_2,panel);
			}
		}).start();

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(closeOperation);// 设置主窗体关闭按钮样式
	}


	private void saveData() {
		int size=prices.size()>(pageIndex+1)*15?(pageIndex+1)*15:prices.size();
		JCheckBox checkBox=null;
		JTextField jTextField;
		JTextField jTextField1;
		JTextField jTextField2;
		JTextField jTextField3;
		JTextField jTextField4;
		Price price;
		List<Config>configList=new ArrayList<>();
		Map map=configService.getConfigSets(configTypePre,0);
		int index=0;
		for(int i=pageIndex*15;i<size;i++,index++){
			price=prices.get(i);
			checkBox=checkboxList.get(index);
			Integer status =1;
			if(map!=null&&map.containsKey(price.getSymbol())&&!checkBox.isSelected()){
				status=0;
			}
			if(map!=null&&!map.containsKey(price.getSymbol())&&!checkBox.isSelected())
				continue;
			if(map==null&&!checkBox.isSelected())
				continue;
			jTextField=textFieldList.get(5*index);
			jTextField1=textFieldList.get(5*index+1);
			jTextField2=textFieldList.get(5*index+2);
			jTextField3=textFieldList.get(5*index+3);
			jTextField4=textFieldList.get(5*index+4);

			String t1=jTextField.getText();
			String t2=jTextField1.getText();
			String t3=jTextField2.getText();
			String t4=jTextField3.getText();
			String t5=jTextField4.getText();
			if((StringUtil.isBlank(t1)||StringUtil.isBlank(t2))&&status==1) {
				AlertUtils.showMessage("请填写选用币种的断点幅度！");
				return;
			}
			if(StringUtil.isBlank(t3))
				t3="0";
			if(StringUtil.isBlank(t4)||t4.equals("无限"))
				t4="0";
			else{
				try {
					t4=String.valueOf(DateUtil.parse("yyyy-MM-dd hh:mm:ss", t4).getTime() / 1000);
				} catch (CommonException e) {
					AlertUtils.showMessage("日期格式错误，请检查！");
					return;
				}
			}
			if(StringUtil.isBlank(t5))
				t5="0";
			Map<String,Object> configMap=new HashMap<>();
			configMap.put("symbol",price.getSymbol());
			configMap.put("price",price.getPrice());
			configMap.put("scope",t1);
			configMap.put(configInfoPre+"Price",t2);
			configMap.put("priority",t3);
			configMap.put("time",t4);
			configMap.put("num",t5);
			Config config=new Config();
			config.setConfigInfo(JsonMapper.nonDefaultMapper().toJson(configMap));
			config.setCreateTime(System.currentTimeMillis()/1000);
			config.setStatus(status);
			config.setSymbol(price.getSymbol());
			config.setType(configTypePre+"SetConfig");
			String description=String.format("买入设置:symbol 买入币种名称,price 设置时币种价格,scope断点幅度，%sPrice断点价格，当币种价格达到断点价格时候自动买入！",configInfoPre);
			config.setDescription(description);
			configList.add(config);
		}
		if(configService.insertOrUpdateConfigs(configList))
			AlertUtils.showMessage("保存成功");
	}


	private void setData(JButton button_1, JButton button_2, JPanel panel) {
		panel.removeAll();
		textFieldList.clear();
		checkboxList.clear();
		Price price=null;
		JLabel label1=null;
		JLabel label2=null;
		JLabel label3=null;
		JLabel label4=null;
		JLabel label5=null;
		JLabel label6=null;
		JLabel label7=null;
		JCheckBox jCheckBox=null;
		JTextField jTextField=null;
		JTextField jTextField1=null;
		JTextField jTextField2=null;
		JTextField jTextField3=null;
		JTextField jTextField4=null;
		Map map=configService.getConfigSets(configTypePre,1);
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

		int margin=-20;
		boolean isExist=false;
		for(int i=pageIndex*15;i<size;i++){
			isExist=false;

			price=prices.get(i);
			if(map!=null&&map.containsKey(price.getSymbol()))
				isExist=true;
			margin+=20;
			label1=new JLabel("币名:"+price.getSymbol());
			label1.setBounds(20,10+margin,100,20);
			label2=new JLabel("价格:"+price.getPrice());
			label2.setBounds(140,10+margin,100,20);
			jCheckBox=new JCheckBox("选用");
			jCheckBox.setBounds(250,10+margin,70,20);
			label3=new JLabel("断点幅度:");
			label3.setBounds(320,10+margin,90,20);
			jTextField=new JTextField(10);
			jTextField.setBounds(390,10+margin,80,20);
			label4=new JLabel("断点价格:");
			label4.setBounds(490,10+margin,90,20);
			jTextField1=new JTextField(10);
			jTextField1.setBounds(560,10+margin,80,20);
			label5=new JLabel("优先级:");
			label5.setBounds(650,10+margin,90,20);
			jTextField2=new JTextField(10);
			jTextField2.setBounds(710,10+margin,80,20);
			label6=new JLabel("截止日期:");
			label6.setBounds(800,10+margin,90,20);
			jTextField3=new JTextField(10);
			jTextField3.setBounds(880,10+margin,130,20);
			label7=new JLabel("交易数量:");
			label7.setBounds(1020,10+margin,90,20);
			jTextField4=new JTextField(10);
			jTextField4.setBounds(1080,10+margin,80,20);

			if(isExist){
				Config config= (Config) map.get(price.getSymbol());
				jCheckBox.setSelected(true);
				String configInfoStr=config.getConfigInfo();
				Map<String,Object>configInfo=JsonMapper.nonEmptyMapper().fromJson(configInfoStr,Map.class);
				jTextField.setText((String) configInfo.get("scope"));
				jTextField1.setText((String) configInfo.get(configInfoPre+"Price"));
				jTextField2.setText((String) configInfo.get("priority"));
				Long time=Long.parseLong((String) configInfo.get("time"));
				if(time>0)
					jTextField3.setText(DateUtil.format("yyyy-MM-dd hh:mm:ss",new Date(time*1000)));
				else
					jTextField3.setText("无限");

				jTextField4.setText((String) configInfo.get("num"));
			}
			setListener(jTextField,jTextField1,i);

			textFieldList.add(jTextField);
			textFieldList.add(jTextField1);
			textFieldList.add(jTextField2);
			textFieldList.add(jTextField3);
			textFieldList.add(jTextField4);

			checkboxList.add(jCheckBox);

			panel.add(jTextField2);
			panel.add(jTextField3);
			panel.add(jTextField4);
			panel.add(label7);
			panel.add(label5);
			panel.add(label6);
			panel.add(jTextField);
			panel.add(jTextField1);
			panel.add(label1);
			panel.add(label2);
			panel.add(label3);
			panel.add(label4);
			panel.add(jCheckBox);
		}
		panel.repaint();
	}

	private void setListener(JTextField jTextField, JTextField jTextField1, int i) {
		jTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				try{
					if(jTextField.getText().indexOf("-")==0&&jTextField.getText().length()==1)
						return;
					Double d=Double.valueOf(jTextField.getText());
					Double price=Double.valueOf(prices.get(i).getPrice());
					jTextField1.setText(String.format("%.2f",price+price*d/100));
				}catch (Exception e1){
					AlertUtils.showMessage("非数字格式，请检查！");
				}

			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				jTextField1.setText("");
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
	}
}
