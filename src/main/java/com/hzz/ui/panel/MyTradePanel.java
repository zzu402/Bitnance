package com.hzz.ui.panel;
import com.hzz.constant.QueryConstant;
import com.hzz.model.Config;
import com.hzz.service.ConfigService;
import com.hzz.service.TradeService;
import com.hzz.utils.AlertUtils;
import com.hzz.utils.StringUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.*;

public class MyTradePanel extends JPanel {
    private ConfigService configService = new ConfigService();
    private TradeService tradeService=new TradeService();
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
                if(StringUtil.isBlank(symbol)){
                    AlertUtils.showMessage("请选择币种");
                    return;
                }
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
        tradeService.saveMyTrade(symbol);
        tradeService.saveOrder(symbol);
		StringBuilder sb=new StringBuilder();
        java.util.List<Map<String, Object>>tradeList=tradeService.getTradeBySymbol(symbol);
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
        return sb.toString();
    }


    private void addSymbolData(JComboBox comboBox) {
        Map<String, Config> map = configService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_BUY, 1);
        Map<String, Config> map1 = configService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_SELL, 1);
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
