package com.hzz.ui;

import javax.swing.*;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/23
 */
public abstract class AbstractUI {
    public JFrame frame;
    protected  abstract void initialize(int closeOperation);

//    static {
//
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
//            }
//        });
//    }
}
