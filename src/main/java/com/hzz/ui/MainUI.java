package com.hzz.ui;
import com.hzz.constant.CommandConstant;
import com.hzz.constant.UIConstant;
import com.hzz.service.TradeService;
import com.hzz.ui.panel.GuidePanel;
import com.hzz.ui.panel.UserInfoPanel;
import com.hzz.utils.AlertUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;

public class MainUI extends AbstractUI implements ActionListener {
    private static AbstractUI subWindow=null;
    private  TradeService tradeService=new TradeService();
    public MainUI(int closeOperation) {
        initialize(closeOperation);
    }
    protected void initialize(int closeOperation) {
        frame = new JFrame();
        frame.setTitle(UIConstant.MAIN_UI_TITLE);
        frame.setBounds(0, 0, 800, 705);
        frame.setDefaultCloseOperation(closeOperation);
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        frame.setLocationRelativeTo(null);
        JMenu menu = new JMenu(UIConstant.MAIN_UI_MENU_1);
        menuBar.add(menu);
        JMenuItem menuItem = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_1_1);
        menuItem.setActionCommand(CommandConstant.MAIN_UI_DB);
        menuItem.addActionListener(this);
        menu.add(menuItem);


        JMenuItem mntmKey = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_1_2);
        mntmKey.setActionCommand(CommandConstant.MAIN_UI_KEY);
        mntmKey.addActionListener(this);
        menu.add(mntmKey);

        JMenuItem tzItem = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_1_3);
        tzItem.setActionCommand(CommandConstant.MAIN_UI_NOTIFY);
        tzItem.addActionListener(this);
        menu.add(tzItem);


        JMenu menu_1 = new JMenu(UIConstant.MAIN_UI_MENU_2);
        menuBar.add(menu_1);


        JMenuItem menuItem_5 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_2_1);
        menuItem_5.setActionCommand(CommandConstant.MAIN_UI_PRICE);
        menuItem_5.addActionListener(this);
        menu_1.add(menuItem_5);

        JMenuItem menuItem_8 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_2_2);
        menu_1.add(menuItem_8);


        JMenuItem menuItem_17 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_2_4);
        menuItem_17.setActionCommand(CommandConstant.MAIN_IMITATE);
        menuItem_17.addActionListener(this);
        menu_1.add(menuItem_17);


        JMenu menu_2 = new JMenu(UIConstant.MAIN_UI_MENU_3);
        menuBar.add(menu_2);

        JMenuItem menuItem_2 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_3_1);
        menuItem_2.setActionCommand(CommandConstant.MAIN_UI_BUY);
        menuItem_2.addActionListener(this);
        menu_2.add(menuItem_2);

        JMenuItem menuItem_3 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_3_2);
        menuItem_3.setActionCommand(CommandConstant.MAIN_UI_SELL);
        menuItem_3.addActionListener(this);
        menu_2.add(menuItem_3);

        JMenuItem menuItem_4 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_3_3);
        menuItem_4.setActionCommand(CommandConstant.MAIN_UI_TRADE);
        menuItem_4.addActionListener(this);
        menu_2.add(menuItem_4);

        JMenu menu_3 = new JMenu(UIConstant.MAIN_UI_MENU_4);
        menuBar.add(menu_3);

        JMenuItem menuItem_10 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_4_1);
        menuItem_10.setActionCommand(CommandConstant.MAIN_UI_ABOUT);
        menuItem_10.addActionListener(this);
        menu_3.add(menuItem_10);

        JMenuItem menuItem_11 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_4_2);
        menuItem_11.setActionCommand(CommandConstant.MAIN_UI_HELP);
        menuItem_11.addActionListener(this);
        menu_3.add(menuItem_11);

        selectPanel(frame);
        frame.setResizable(false);
    }

    private void selectPanel(JFrame frame) {
        File file=new File(String.format("%s//config.properties",System.getProperty("user.dir")));
        if(!file.exists()||!tradeService.initKey()) {
            GuidePanel guidePanel = new GuidePanel();
            guidePanel.setBounds(0, 40, 800, 600);
            frame.add(guidePanel);
        } else {
            UserInfoPanel userInfoPanel = new UserInfoPanel();
            userInfoPanel.setBounds(0, 40, 800, 600);
            frame.add(userInfoPanel);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(CommandConstant.MAIN_UI_DB)) {
            subWindow = new InitUI(WindowConstants.HIDE_ON_CLOSE);
        } else if (command.equals(CommandConstant.MAIN_UI_KEY)) {
            subWindow = new UserKeyUI(WindowConstants.HIDE_ON_CLOSE);
        } else if (command.equals(CommandConstant.MAIN_UI_NOTIFY)) {
            subWindow = new NotifyUI(WindowConstants.HIDE_ON_CLOSE);
        } else if (command.equals(CommandConstant.MAIN_UI_PRICE)) {
            subWindow = new PriceUI(WindowConstants.HIDE_ON_CLOSE);
        }else if(command.equals(CommandConstant.MAIN_UI_BUY)){
            subWindow = new ConfigUI("买入设置",UIConstant.CONFIG_BUY_UI,WindowConstants.HIDE_ON_CLOSE);
        }else if(command.equals(CommandConstant.MAIN_UI_SELL)){
            subWindow = new ConfigUI("卖出设置",UIConstant.CONFIG_SELL_UI,WindowConstants.HIDE_ON_CLOSE);
        }else if(command.equals(CommandConstant.MAIN_UI_HELP)){
            subWindow=new HelpUI();
        }else if(command.equals(CommandConstant.MAIN_IMITATE)){
            subWindow=new ImitateUI(WindowConstants.HIDE_ON_CLOSE);
        }else if(command.equals(CommandConstant.MAIN_UI_TRADE)){
            subWindow=new TradeSetUI(WindowConstants.HIDE_ON_CLOSE);
        }else if (command.equals(CommandConstant.MAIN_UI_ABOUT)){
            AlertUtils.showMessage("联系QQ:415354918");
            return;
        }
      new Thread(new Runnable() {
          @Override
          public void run() {
              if(subWindow!=null)
                    subWindow.frame.setVisible(true);
          }
      }).start();
    }
}
