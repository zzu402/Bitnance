package com.hzz.ui;

import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.Account;
import com.hzz.utils.DaoUtils;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.jdbc.core.JdbcTemplate;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class MainUI {

	private JFrame frmBitcon;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					MainUI window = new MainUI();
//					window.frmBitcon.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
		ModelDao dao=new ModelDao();
		dao.setJdbcTemplate(DaoUtils.getTemplate());
		Account account=new Account();
		account.setSellerCommission("hello");
		try {
			dao.insert(account);
		} catch (CommonException e) {
			e.printStackTrace();
		}
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
		frmBitcon.setBounds(100, 100, 802, 705);
		frmBitcon.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmBitcon.setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("基本设置");
		menuBar.add(menu);
		
		JMenuItem menuItem = new JMenuItem("数据库设置");
		menu.add(menuItem);
		
		JMenuItem mntmKey = new JMenuItem("Key设置");
		menu.add(mntmKey);
		
		JMenuItem menuItem_1 = new JMenuItem("日志设置");
		menu.add(menuItem_1);
		
		JMenu menu_1 = new JMenu("信息查询");
		menuBar.add(menu_1);
		
		JMenuItem menuItem_6 = new JMenuItem("历史交易");
		menu_1.add(menuItem_6);
		
		JMenuItem menuItem_7 = new JMenuItem("历史订单");
		menu_1.add(menuItem_7);
		
		JMenuItem menuItem_5 = new JMenuItem("实时价格");
		menu_1.add(menuItem_5);
		
		JMenuItem menuItem_8 = new JMenuItem("我的账户");
		menu_1.add(menuItem_8);
		
		JMenuItem menuItem_9 = new JMenuItem("汇兑信息");
		menu_1.add(menuItem_9);
		
		JMenu menu_2 = new JMenu("交易设置");
		menuBar.add(menu_2);
		
		JMenuItem menuItem_2 = new JMenuItem("买入设置");
		menu_2.add(menuItem_2);
		
		JMenuItem menuItem_3 = new JMenuItem("卖出设置");
		menu_2.add(menuItem_3);
		
		JMenuItem menuItem_4 = new JMenuItem("交易策略");
		menu_2.add(menuItem_4);
		
		JMenu menu_3 = new JMenu("关于软件");
		menuBar.add(menu_3);
		
		JMenuItem menuItem_10 = new JMenuItem("关于作者");
		menu_3.add(menuItem_10);
	}
}
