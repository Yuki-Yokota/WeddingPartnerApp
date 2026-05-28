package com.example.weddingpartnerapp.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
@PropertySource(value="classpath:mailcustom_ja.properties", encoding = "UTF-8")
public class MailUtil {
	
	@Value("${mailcustom.sendfrom}")
	private String sendFrom;
	
	@Value("${mailcustom.title}")
	private String title;
	
	@Autowired
	private JavaMailSender javaMailSender;
	/**
	 * メールを送信する
	 * @param checkedId
	 * @param sendToMailAddress
	 * @param contextPath
	 * @throws ApplicationException
	 */
	public void sendMail(String sendToMailAddress,String html) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setFrom(sendFrom);
            messageHelper.setTo(sendToMailAddress);
            messageHelper.setText(html,true);
            messageHelper.setSubject(title);

            javaMailSender.send(message);
            
        } catch(Exception e) {
        	throw new ApplicationException(ErrorCode.NOT_SENDMAIL);
        }
		
	}
	
	/**
	 * メールテンプレートを取得する
	 * @param url
	 * @return
	 * @throws ApplicationException
	 */
	public String getMailTemplate(String url){
		String content=null;
		StringBuilder sb = new StringBuilder();
		try (InputStream is = new ClassPathResource(url).getInputStream();
			     BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			
			List<String> lines = reader.lines().collect(Collectors.toList());
			
			for (String s : lines) {
				sb.append(s);
			}
		}catch (IOException e) {
			throw new ApplicationException(ErrorCode.FILE_NOT_FOUND);
		}

		content=sb.toString();
		return content;
	}
}
