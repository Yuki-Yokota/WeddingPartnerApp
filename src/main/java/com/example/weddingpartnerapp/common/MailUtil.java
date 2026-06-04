package com.example.weddingpartnerapp.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.weddingpartnerapp.model.ResendPayload;

@Component
@PropertySource(value = "classpath:mailcustom_ja.properties", encoding = "UTF-8")
public class MailUtil {

	@Value("${mailcustom.sendfrom}")
	private String sendFrom;
	
	@Value("${mailcustom.title}")
	private String title;

	@Value("${RESEND_API_KEY}")
	private String apiKey;

//	@Autowired
//	private JavaMailSender javaMailSender;

	/**
	 * メールを送信する
	 * 1.ヘッダーの作成時Resendで生成したAPIキーを仕込む
	 * 2.送信データの設定。無料プランの初期状態では、送信元(from)は "onboarding@resend.dev" 固定
	 *   送信先(to)は、Resendに登録した自分のmailアドレス宛てのみテスト送信可能(ドメイン登録すると制限解除？)
	 * 3.APIにリクエストを送信！（443番ポートなのでRenderでも100%通る）
	 * 
	 * @param checkedId
	 * @param sendToMailAddress
	 * @param contextPath
	 * @throws ApplicationException
	 */
	public void sendMail(String sendToMailAddress, String html) {
		
		RestTemplate restTemplate = new RestTemplate();
		String url = "https://api.resend.com/emails";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + apiKey);

//		ResendPayload payload = new ResendPayload("onboarding@resend.dev", sendToMailAddress, title, html);
		ResendPayload payload = new ResendPayload(sendFrom, sendToMailAddress, title, html);

		HttpEntity<ResendPayload> request = new HttpEntity<>(payload, headers);

		try {
			restTemplate.postForEntity(url, request, String.class);
		} catch (Exception e) {
			throw new ApplicationException(ErrorCode.NOT_SENDMAIL);
		}

	}

	/**
	 * メールテンプレートを取得する
	 * 
	 * @param url
	 * @return
	 * @throws ApplicationException
	 */
	public String getMailTemplate(String url) {
		String content = null;
		StringBuilder sb = new StringBuilder();
		try (InputStream is = new ClassPathResource(url).getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

			List<String> lines = reader.lines().collect(Collectors.toList());

			for (String s : lines) {
				sb.append(s);
			}
		} catch (IOException e) {
			throw new ApplicationException(ErrorCode.FILE_NOT_FOUND);
		}

		content = sb.toString();
		return content;
	}

	public void sendGMail(String sendToMailAddress, String html) {
		// MimeMessage message = javaMailSender.createMimeMessage();
//		try {
//      MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
//      messageHelper.setFrom(sendFrom);
//      messageHelper.setTo(sendToMailAddress);
//      messageHelper.setText(html,true);
//      messageHelper.setSubject(title);
//
//      javaMailSender.send(message);

//  } catch(Exception e) {
//  	throw new ApplicationException(ErrorCode.NOT_SENDMAIL);
//  }
	}
}
