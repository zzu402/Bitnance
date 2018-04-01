package com.hzz;

import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.Account;
import com.hzz.service.DataService;
import com.hzz.service.JobService;
import com.hzz.service.TradeService;
import com.hzz.ui.AbstractUI;
import com.hzz.ui.InitUI;
import com.hzz.ui.MainUI;
import com.hzz.utils.AlertUtils;
import com.hzz.utils.DBUtils;
import com.hzz.utils.DaoUtils;
import com.hzz.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JobService.doJob();
                    logger.info("Main UI start ...");
                    AbstractUI window = new MainUI(WindowConstants.EXIT_ON_CLOSE);
                    window.frame.setVisible(true);
                    logger.info("Main UI start finish");

                } catch (Exception e) {
                    logger.error("Main UI error...",e);
                    if (e.getMessage().contains("JDBC Connection")){
                        AlertUtils.showMessage("数据库信息异常!");
                        DaoUtils.DBError(new InitUI(WindowConstants.EXIT_ON_CLOSE));
                    }
                }
            }
        });
    }


}
