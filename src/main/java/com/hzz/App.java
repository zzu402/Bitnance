package com.hzz;

import com.hzz.service.JobService;
import com.hzz.ui.AbstractUI;
import com.hzz.ui.AuthorizationUI;
import com.hzz.ui.InitUI;
import com.hzz.ui.MainUI;
import com.hzz.utils.AlertUtils;
import com.hzz.utils.AuthorizationUtils;
import com.hzz.utils.DaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/8
 */
//@Configuration
//@ComponentScan("com.hzz")
//@EnableAutoConfiguration
//@SpringBootApplication
public class App {
    private static Logger logger= LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        System.setProperty("user.timezone","GMT +08");
        if(AuthorizationUtils.isAuthorization()) {
          App.start();
        }else {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        AuthorizationUI window = new AuthorizationUI();
                        window.frame.setVisible(true);
                    } catch (Exception e) {
                        logger.error("注册时错误",e);
                    }
                }
            });

        }
    }
    public static void start(){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JobService.doJob();
                    logger.info("Main UI start ...");
                    AbstractUI window = new MainUI(WindowConstants.EXIT_ON_CLOSE);
                    window.frame.setVisible(true);
                    logger.info("Main UI start finish");
                } catch (Exception e) {
                    logger.error("Main UI error...", e);
                    if (e.getMessage().contains("JDBC Connection")) {
                        AlertUtils.showMessage("数据库信息异常!");
                        DaoUtils.DBError(new InitUI(WindowConstants.EXIT_ON_CLOSE));
                    }
                }
            }
        });
    }


}
