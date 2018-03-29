package com.hzz.service;

import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.MailBean;
import com.hzz.model.User;
import com.hzz.utils.DaoUtils;
import com.hzz.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;


public class MailService {
    private Logger logger = LoggerFactory.getLogger(MailService.class);

    public String toChinese(String text) {
        try {
            text = MimeUtility.encodeText(new String(text.getBytes(), "utf-8"));
        } catch (Exception e) {
            logger.error("编码转换异常", e);
        }
        return text;
    }

    public MailBean getBasicMailBean() {
        MailBean mailBean = new MailBean();
        mailBean.setHost("smtp.163.com");
        mailBean.setFrom("Mr.?<18903811375@163.com>");
        mailBean.setPassword("qaz123456");
        mailBean.setSubject("有您的邮件");
        mailBean.setTo("415354918@qq.com");
        mailBean.setContent("这是一封来自22世纪的邮件");
        mailBean.setUsername("18903811375@163.com");
        return mailBean;
    }

    private String getString(String[] str, String s) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            if (StringUtil.isBlank(str[i]))
                continue;
            if (i > 0)
                stringBuilder.append(s);
            stringBuilder.append(str[i]);
        }
        return stringBuilder.toString();
    }



    public  void sendNotify(String symbol, String price,int type) {
        MailBean mailBean = getBasicMailBean();
        ModelDao modelDao = DaoUtils.getDao(DaoUtils.getTemplate());
        List list = null;
        try {
            logger.info(String.format("发送%s通知提醒开始...",type==1?"买入":"卖出"));
            list = modelDao.select(new User());
            if (list.isEmpty()) {
                logger.info(String.format("没有设置通知信息，发送%s信息失败...",type==1?"买入":"卖出"));
                return;
            }
            User user = (User) list.get(0);
            mailBean.setTo(user.getEmail());
            mailBean.setSubject(String.format("bitcon:%s%s提醒",symbol,type==1?"买入":"卖出"));
            if (StringUtil.isBlank(user.getEmail()) || StringUtil.isBlank(user.getBuyTemplet())) {
                logger.info(String.format("没有设置通知信息，发送%s信息失败...",type==1?"买入":"卖出"));
                return;
            }
            String temple=null;
            if(type==1) {
                 temple= user.getBuyTemplet();
            }else {
                temple=user.getSellTemplet();
            }
            String[] str = temple.split("\\{symbol\\}");
            temple = getString(str, symbol);
            str = temple.split("\\{price\\}");
            temple = getString(str, price);
            str = temple.split("\\{time\\}");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
            temple = getString(str, simpleDateFormat.format(new Date()));
            temple = "尊敬的用户:" + user.getName() + "\r\n\t\t" + temple;
            mailBean.setContent(temple);
            sendMail(mailBean);
            logger.info(String.format("发送%s通知成功...",type==1?"买入":"卖出"));
        } catch (CommonException e) {
            logger.error(String.format("查找用户失败，发送%s通知提醒失败",type==1?"买入":"卖出"), e);
        }
    }


    public boolean sendMail(MailBean mb) {
        String host = mb.getHost();
        final String username = mb.getUsername();
        final String password = mb.getPassword();
        String from = mb.getFrom();
        String to = mb.getTo();
        String subject = mb.getSubject();
        String content = mb.getContent();
        String fileName = mb.getFilename();
        Vector<String> file = mb.getFile();
        Properties props = new Properties();
        props.put("mail.smtp.host", host); // 设置SMTP的主机
        props.put("mail.smtp.auth", "true"); // 需要经过验证
        props.put("mail.smtp.timeout", "8000000");
        Session session = Session.getInstance(props, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(toChinese(subject));

            Multipart mp = new MimeMultipart();
            MimeBodyPart mbpContent = new MimeBodyPart();
            mbpContent.setText(content);
            mp.addBodyPart(mbpContent);

                    /* 往邮件中添加附件 */
            if (file != null) {
                Enumeration<String> efile = file.elements();
                while (efile.hasMoreElements()) {
                    MimeBodyPart mbpFile = new MimeBodyPart();
                    fileName = efile.nextElement().toString();
                    FileDataSource fds = new FileDataSource(fileName);
                    mbpFile.setDataHandler(new DataHandler(fds));
                    mbpFile.setFileName(toChinese(fds.getName()));
                    mp.addBodyPart(mbpFile);
                }
                logger.info("邮件添加成功");
            }

            msg.setContent(mp);
            msg.setSentDate(new Date());
            Transport.send(msg);

        } catch (Exception e) {
            logger.error("邮件添加失败", e);
            return false;
        }
        return true;
    }

//    public static void main(String[] args) {
//        MailService mailService = new MailService();
//    }
}
