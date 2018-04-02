package com.hzz.ui;

import com.hzz.constant.UIConstant;
import com.hzz.ui.AbstractUI;
import com.hzz.ui.panel.GuidePanel;

import javax.swing.*;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/26
 */
public class HelpUI extends AbstractUI {

    public HelpUI(){
        initialize(0);
    }

    @Override
    protected void initialize(int closeOperation) {
        frame = new JFrame();
        frame.setTitle("使用步骤");
        frame.setBounds(0, 0, 800, 500);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.add(new GuidePanel());
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }
}
