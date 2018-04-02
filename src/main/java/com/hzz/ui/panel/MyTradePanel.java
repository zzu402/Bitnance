package com.hzz.ui.panel;

import com.hzz.common.dao.*;
import com.hzz.constant.QueryConstant;
import com.hzz.exception.CommonException;
import com.hzz.model.Config;
import com.hzz.model.MyTrade;
import com.hzz.model.Order;
import com.hzz.service.CommonService;
import com.hzz.utils.DaoUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyTradePanel extends JPanel {
    private CommonService commonService = new CommonService();
    private String symbol = null;
    JTextArea textArea = null;

    public MyTradePanel() {
        setLayout(null);

        JLabel label = new JLabel("选择币种：");
        label.setBounds(20, 10, 76, 15);
        add(label);

        JComboBox comboBox = new JComboBox();
        comboBox.setBounds(110, 10, 140, 20);
        add(comboBox);
        addSymbolData(comboBox);

        JButton button = new JButton("查看");
        button.setBounds(280, 10, 60, 20);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAreaText(getText());
            }
        });
        add(button);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 40, 340, 270);
        textArea = new JTextArea("");
        textArea.setEnabled(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setDisabledTextColor(Color.BLUE);
        scrollPane.setViewportView(textArea);
        add(scrollPane);
    }

    public void setAreaText(String s) {
        textArea.setText(s);
        textArea.repaint();
    }

    public String getText() {
		StringBuilder sb=new StringBuilder();
        ModelDao modelDao = DaoUtils.getDao(DaoUtils.getTemplate());
        Map<JoinModel, JoinType> joinMap = new LinkedHashMap<>();
        JoinModel joinModel = new JoinModel();
        joinModel.setAliasName("t");
        joinModel.setJoinModel(MyTrade.class);
        joinMap.put(joinModel, JoinType.INNER);

        joinModel = new JoinModel();
        joinModel.setAliasName("o");
        joinModel.setJoinModel(Order.class);
        joinModel.on().add(new ConditionCustom("o.orderId=t.orderId and o.symbol='"+symbol+"'"));
        joinMap.put(joinModel, JoinType.INNER);

        ConditionModel condition = new ConditionModel();
        condition.orderBy("t.time desc");
        condition.limitCount(13);
        condition.columns().addAll(Arrays.asList(new String[]{"t.time", "t.price", "t.qty", "t.isMaker", "t.isBuyer", "o.symbol"}));
        try {
            java.util.List<Map<String, Object>> tradeList = modelDao.select(joinMap, condition);
            if(tradeList==null||tradeList.isEmpty())
                return "";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
            Map<String, Object> trade = null;
            for(int i=0;i<tradeList.size();i++){
                trade=tradeList.get(i);
                sb.append(simpleDateFormat.format(trade.get("time")));
                sb.append("\t");
                sb.append("价格:"+trade.get("price"));
                sb.append("\t");
                sb.append("数量:"+trade.get("qty"));
                sb.append("\t");
                String isMakey = (String) trade.get("isMaker");
                boolean isBuyer = !isMakey.equals("true");
                sb.append(isBuyer?"买入":"卖出");
                sb.append("\r\n");
            }
        } catch (CommonException e) {
            return "";
        }
        return sb.toString();
    }


    private void addSymbolData(JComboBox comboBox) {
        Map<String, Config> map = commonService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_BUY, 1);
        Map<String, Config> map1 = commonService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_SELL, 1);
        map.putAll(map1);
        Iterator it = map.entrySet().iterator();
        comboBox.addItem("请选择");
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            comboBox.addItem(key);
        }
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    symbol = (String) e.getItem();
                }
            }
        });
    }

}
