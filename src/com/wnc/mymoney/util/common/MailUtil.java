package com.wnc.mymoney.util.common;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import android.util.Log;

/**
 * @author yh
 * 
 */
public class MailUtil
{

    /**
     * ReceiveEmail类测试
     */
    public static void main(String args[]) throws Exception
    {
        // getMail();
        // sendMail("","",);
    }

    private static void getMail()
    {
        // Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        Properties props = System.getProperties();
        props.setProperty("mail.pop3.host", "smtp.qq.com");
        props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", "995");
        props.setProperty("mail.pop3.socketFactory.port", "995");
        props.setProperty("mail.pop3.auth", "true");
        // props.put("mail.pop3.host", "smtp.qq.com");
        // props.put("mail.pop3.socketFactory.class", SSL_FACTORY);
        // props.put("mail.pop3.socketFactory.fallback", "false");
        // props.put("mail.pop3.port", "995");
        // props.put("mail.pop3.socketFactory.port", "995");
        // props.put("mail.pop3.auth", "true");
        // 建立邮件会话
        Session session = Session.getDefaultInstance(props, null);
        // 设置连接邮件仓库的环境
        String user = "529801034@qq.com";
        String pw = "eqxkjmlacvuabjgc";
        URLName url = new URLName("pop3", "pop.qq.com", 995, null, user, pw);
        Store store = null;
        Folder folder = null;
        try
        {
            // 得到邮件仓库并连接
            store = session.getStore(url);
            store.connect();

            // 得到收件箱并抓取邮件
            Folder defaultFolder = store.getDefaultFolder();

            Folder[] allFolder = defaultFolder.list();
            for (Folder folder2 : allFolder)
            {
                System.out.println(folder2.getFullName());
            }
            folder = store.getFolder("INBOX");
            // inbox = store.getDefaultFolder();
            folder.open(Folder.READ_ONLY);
            FetchProfile profile = new FetchProfile();
            profile.add(FetchProfile.Item.ENVELOPE);
            Message[] messages = folder.getMessages();
            // inbox.fetch(messages, profile);
            // 打印收件箱邮件部分信息
            int length = messages.length;
            System.out.println("收件箱的邮件数：" + length);
            System.out.println("-------------------------------------------\n");
            for (int i = 0; i < length; i++)
            {
                System.out.println("发件人：" + getRendMan(messages[i]));
                System.out.println("主题：" + messages[i].getSubject());
                System.out.println("邮件大小：" + messages[i].getSize());
                System.out.println("邮件发送时间:"
                        + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(messages[i].getSentDate()));
                System.out
                        .println("-------------------------------------------\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                folder.close(false);
            }
            catch (MessagingException e)
            {
                e.printStackTrace();
            }
            try
            {
                store.close();
            }
            catch (MessagingException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void sendMail(String title, String content, String attachPath)
            throws Exception
    {
        System.out.println("......sendmail");
        final String user = "529801034@qq.com";
        final String pw = "eqxkjmlacvuabjgc";
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", "smtp.qq.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtp.auth", "true");
        // 建立邮件会话
        Session session = Session.getDefaultInstance(props, null);
        // new Authenticator()
        // {
        // // 身份认证
        // @Override
        // protected PasswordAuthentication getPasswordAuthentication()
        // {
        // return new PasswordAuthentication(user, pw);
        // }
        // });
        // 建立邮件对象
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(user));
        message.setRecipients(Message.RecipientType.TO, "wnc_435100@126.com");
        message.setSubject(title);
        // // 文本部分
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(content, "text/html;charset=UTF-8");
        // textPart.setContent("图<img src='cid:myimg'/>文加附件邮件测试",
        // "text/html;charset=UTF-8");

        // 内嵌图片部分
        // MimeBodyPart imagePart = new MimeBodyPart();
        // imagePart.setDataHandler(new DataHandler(new FileDataSource(
        // Environment.getExternalStorageDirectory().getPath()
        // + "/wnc/12306.jpg")));// 图片路径
        // imagePart.setContentID("myimg");

        // 图文整合，关联关系
        MimeMultipart mmp1 = new MimeMultipart();
        mmp1.addBodyPart(textPart);
        // mmp1.addBodyPart(imagePart);
        mmp1.setSubType("related");
        MimeBodyPart textImagePart = new MimeBodyPart();
        textImagePart.setContent(mmp1);
        // 附件部分
        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataHandler dh = new DataHandler(new FileDataSource(attachPath));// 附件路径
        String fileName = dh.getName();
        attachmentPart.setDataHandler(dh);
        attachmentPart.setFileName(fileName);
        // 图文和附件整合，复杂关系
        MimeMultipart mmp2 = new MimeMultipart();
        mmp2.addBodyPart(textImagePart);
        mmp2.addBodyPart(attachmentPart);
        mmp2.setSubType("mixed");
        // 将以上内容添加到邮件的内容中并确认
        message.setContent(mmp2);
        message.saveChanges();

        /**
         * 对邮件内容格式化
         */
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap
                .getDefaultCommandMap();
        mc.addMailcap("text/html;; x-Java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
        // 发送邮件
        Transport transport = session.getTransport("smtp");
        Log.i("check", "connecting");
        transport.connect("smtp.qq.com", user, pw);
        Log.i("check", "wana send");
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
        Log.i("check", "sent");

    }

    public static void sendMail2() throws Exception
    {
        System.out.println("......sendmail");
        final String user = "529801034@qq.com";
        final String pw = "eqxkjmlacvuabjgc";
        try
        {
            // 设置SSL连接、邮件环境
            // Security.addProvider(new
            // com.sun.net.ssl.internal.ssl.Provider());
            final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
            Properties props = System.getProperties();
            props.setProperty("mail.smtp.host", "smtp.qq.com");
            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.port", "465");
            props.setProperty("mail.smtp.socketFactory.port", "465");
            props.setProperty("mail.smtp.auth", "true");
            Session session = Session.getDefaultInstance(props, null);

            DataHandler handler = new DataHandler(new ByteArrayDataSource(
                    "DDDDDDD".getBytes(), "text/plain"));
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setDataHandler(handler);
            Log.i("Check", "done sessions");

            InternetAddress toAddress;
            toAddress = new InternetAddress("wnc_435100@126.com");
            message.addRecipient(Message.RecipientType.TO, toAddress);
            Log.i("Check", "added recipient");
            message.setSubject("安卓发邮件");
            message.setContent("Hello JMail", "text/html;charset=UTF-8");
            message.setText("JMail");

            Log.i("check", "transport");
            Transport transport = session.getTransport("smtp");
            Log.i("check", "connecting");
            transport.connect("smtp.qq.com", user, pw);
            Log.i("check", "wana send");
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            Log.i("check", "sent");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static String getRendMan(Message message)
            throws UnsupportedEncodingException, MessagingException,
            AddressException
    {
        String x;
        String from = MimeUtility.decodeText(message.getFrom()[0].toString());
        InternetAddress ia = new InternetAddress(from);
        x = ia.getPersonal() + '(' + ia.getAddress() + ')';
        return x;
    }

}
