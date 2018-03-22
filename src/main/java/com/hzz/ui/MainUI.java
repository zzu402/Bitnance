package com.hzz.ui;

import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.Account;
import com.hzz.ui.panel.UserInfoPanel;
import com.hzz.utils.DaoUtils;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.jdbc.core.JdbcTemplate;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class MainUI implements ActionListener {

    private JFrame frmBitcon;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainUI window = new MainUI();
                    window.frmBitcon.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmBitcon = new JFrame();
        frmBitcon.setTitle("Bitcon");
        frmBitcon.setBounds(0, 0, 802, 705);
        frmBitcon.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JMenuBar menuBar = new JMenuBar();
        frmBitcon.setJMenuBar(menuBar);
        frmBitcon.setLocationRelativeTo(null);
        JMenu menu = new JMenu("基本设置");
        menuBar.add(menu);
        JMenuItem menuItem = new JMenuItem("数据库设置");
        menuItem.setActionCommand("DB");
        menuItem.addActionListener(this);
        menu.add(menuItem);


        JMenuItem mntmKey = new JMenuItem("Key设置");
        mntmKey.setActionCommand("KEY");
        mntmKey.addActionListener(this);
        menu.add(mntmKey);

        JMenuItem tzItem = new JMenuItem("通知设置");
        tzItem.setActionCommand("NOTIFY");
        tzItem.addActionListener(this);
        menu.add(tzItem);

        JMenuItem menuItem_1 = new JMenuItem("日志设置");
        menu.add(menuItem_1);

        JMenu menu_1 = new JMenu("信息查询");
        menuBar.add(menu_1);


        JMenuItem menuItem_5 = new JMenuItem("实时价格");
        menuItem_5.setActionCommand("PRICE");
        menuItem_5.addActionListener(this);
        menu_1.add(menuItem_5);

        JMenuItem menuItem_8 = new JMenuItem("我的账户");
        menu_1.add(menuItem_8);

        JMenuItem menuItem_9 = new JMenuItem("汇兑信息");
        menu_1.add(menuItem_9);

        JMenu menu_2 = new JMenu("交易设置");
        menuBar.add(menu_2);

        JMenuItem menuItem_2 = new JMenuItem("买入设置");
        menuItem_2.setActionCommand("BUY");
        menuItem_2.addActionListener(this);
        menu_2.add(menuItem_2);

        JMenuItem menuItem_3 = new JMenuItem("卖出设置");
        menuItem_3.setActionCommand("SELL");
        menuItem_3.addActionListener(this);
        menu_2.add(menuItem_3);

        JMenuItem menuItem_4 = new JMenuItem("交易策略");
        menu_2.add(menuItem_4);

        JMenu menu_3 = new JMenu("关于软件");
        menuBar.add(menu_3);

        JMenuItem menuItem_10 = new JMenuItem("关于作者");
        menu_3.add(menuItem_10);

        UserInfoPanel userInfoPanel = new UserInfoPanel();
        userInfoPanel.setBounds(0, 40, 800, 600);
        frmBitcon.add(userInfoPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("DB")) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        InitUI window = new InitUI();
                        window.frame.setVisible(true);
                    } catch (Exception e) {

                    }
                }
            });
        } else if (command.equals("KEY")) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        UserKeyUI window = new UserKeyUI();
                        window.frmKey.setVisible(true);
                    } catch (Exception e) {

                    }
                }
            });
        } else if (command.equals("NOTIFY")) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        NotifyUI window = new NotifyUI();
                        window.frame.setVisible(true);
                    } catch (Exception e) {

                    }
                }
            });
        } else if (command.equals("PRICE")) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        PriceUI window = new PriceUI();
                        window.frame.setVisible(true);
                    } catch (Exception e) {

                    }
                }
            });
        }else if(command.equals("BUY")){
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        BuyConfigUI window = new BuyConfigUI();
                        window.frame.setVisible(true);
                    } catch (Exception e) {

                    }
                }
            });
        }else if(command.equals("SELL")){

        }

    }
}
