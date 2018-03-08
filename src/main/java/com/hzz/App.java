package com.hzz;
import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.Account;
import com.hzz.utils.SpringUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/8
 */
@Configuration
@ComponentScan("com.hzz")
@EnableAutoConfiguration
@SpringBootApplication
public class App {

    public static void main(String[]args){
        SpringUtils.init(App.class,args);
        ModelDao modelDao= (ModelDao) SpringUtils.getBean(ModelDao.class);
        Account account=new Account();
        account.setBuyerCommission("hello");
        try {
            modelDao.insert(account);
        } catch (CommonException e) {
            e.printStackTrace();
        }
    }


}
