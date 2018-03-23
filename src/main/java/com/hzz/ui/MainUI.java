package com.hzz.ui;
import com.hzz.constant.CommandConstant;
import com.hzz.constant.UIConstant;
import com.hzz.ui.panel.UserInfoPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class MainUI extends AbstractUI implements ActionListener {
    private static Logger logger = LoggerFactory.getLogger(MainUI.class);
    private static AbstractUI subWindow=null;
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    logger.info("Main UI start ...");
                    AbstractUI window = new MainUI();
                    window.frame.setVisible(true);
                    logger.info("Main UI start finish");
                } catch (Exception e) {
                   logger.error("Main UI error...",e);
                }
            }
        });
    }

    public MainUI() {
        initialize();
    }
    protected void initialize() {
        frame = new JFrame();
        frame.setTitle(UIConstant.MAIN_UI_TITLE);
        frame.setBounds(0, 0, 800, 705);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        JMenuItem menuItem_1 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_1_4);
        menu.add(menuItem_1);

        JMenu menu_1 = new JMenu(UIConstant.MAIN_UI_MENU_2);
        menuBar.add(menu_1);


        JMenuItem menuItem_5 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_2_1);
        menuItem_5.setActionCommand(CommandConstant.MAIN_UI_PRICE);
        menuItem_5.addActionListener(this);
        menu_1.add(menuItem_5);

        JMenuItem menuItem_8 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_2_2);
        menu_1.add(menuItem_8);

        JMenuItem menuItem_9 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_2_3);
        menu_1.add(menuItem_9);

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
        menu_2.add(menuItem_4);

        JMenu menu_3 = new JMenu(UIConstant.MAIN_UI_MENU_4);
        menuBar.add(menu_3);

        JMenuItem menuItem_10 = new JMenuItem(UIConstant.MAIN_UI_MENU_ITEM_4_1);
        menu_3.add(menuItem_10);

        UserInfoPanel userInfoPanel = new UserInfoPanel();
        userInfoPanel.setBounds(0, 40, 800, 600);
        frame.add(userInfoPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(CommandConstant.MAIN_UI_DB)) {
            subWindow = new InitUI();
        } else if (command.equals(CommandConstant.MAIN_UI_KEY)) {
            subWindow = new UserKeyUI();
        } else if (command.equals(CommandConstant.MAIN_UI_NOTIFY)) {
            subWindow = new NotifyUI();
        } else if (command.equals(CommandConstant.MAIN_UI_PRICE)) {
            subWindow = new PriceUI();
        }else if(command.equals(CommandConstant.MAIN_UI_BUY)){
            subWindow = new ConfigUI("买入设置",1);
        }else if(command.equals(CommandConstant.MAIN_UI_SELL)){
            subWindow = new ConfigUI("卖出设置",2);
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
