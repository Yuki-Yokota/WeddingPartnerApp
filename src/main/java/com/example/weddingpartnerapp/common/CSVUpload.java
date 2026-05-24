package com.example.weddingpartnerapp.common;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.weddingpartnerapp.model.CSVCustomer;
import com.example.weddingpartnerapp.model.CSVGuest;
import com.example.weddingpartnerapp.model.CSVUser;

@Component
public class CSVUpload {
	
	private static Validator validator;
	
	@Autowired 
    public void setValidator(Validator validator) {
        CSVUpload.validator = validator;
    }
	/**
	 * csvアップロードメイン処理
	 * @param <T>
	 * @param part
	 * @param clazz
	 * @return
	 * @throws ApplicationException
	 */
	public static <T> List<T> csvUpload(MultipartFile file,Class<T> clazz)  {
		List<T> list = new ArrayList<>();
		try (InputStream inputStream = file.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"Windows-31J"))) {
            String line;
            
            //ヘッダーレコードを飛ばすためにあらかじめ１行だけ読み取っておく（ない場合は不要）
            line = br.readLine();
            
            while ((line = br.readLine()) != null) {
            	if(clazz==CSVUser.class) {
            		line = line.replace("\"", "");
					String[] csvSplit = line.split(",");
					CSVUser user = new CSVUser();
					try {
						user.setUserId(Integer.parseInt(csvSplit[0]));
						user.setUserName(csvSplit[1]);
						user.setMailAddress(csvSplit[2]);
						user.setRole(csvSplit[3]);
					}catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
						throw new ApplicationException(ErrorCode.INVALID_DATA);
					}
					//CSVUserのアノテーションチェック。エラーだと業務例外扱いにする
		            Set<ConstraintViolation<CSVUser>> violations = validator.validate(user);
		            if (!violations.isEmpty()) {
		                throw new ApplicationException(ErrorCode.INVALID_DATA);
		            }
					list.add(clazz.cast(user));
            	}else if(clazz==CSVCustomer.class) {
            		line = line.replace("\"", "");
					String[] csvSplit = line.split(",");
					CSVCustomer customer = new CSVCustomer();
					try {
						customer.setCustomerId(Integer.parseInt(csvSplit[0]));
						customer.setUserId(Integer.parseInt(csvSplit[1]));
						customer.setGroomName(csvSplit[2]);
						customer.setBrideName(csvSplit[3]);
						customer.setMailAddress(csvSplit[4]);
						customer.setPhoneNumber(csvSplit[5]);
					}catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
						throw new ApplicationException(ErrorCode.INVALID_DATA);
					}
					Set<ConstraintViolation<CSVCustomer>> violations = validator.validate(customer);
		            if (!violations.isEmpty()) {
		                throw new ApplicationException(ErrorCode.INVALID_DATA);
		            }
					list.add(clazz.cast(customer));
            	}else if(clazz==CSVGuest.class) {
            		line = line.replace("\"", "");
					String[] csvSplit = line.split(",");
					CSVGuest guest = new CSVGuest();
					try {
						guest.setGuestId(Integer.parseInt(csvSplit[0]));
						guest.setGuestName(csvSplit[1]);
						guest.setMailAddress(csvSplit[2]);
						guest.setAttendance(csvSplit[3]);
						guest.setAnsFlg(csvSplit[4]);
						if(csvSplit.length==6) {
							guest.setPrecaution(csvSplit[5]);
						}
					}catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
						throw new ApplicationException(ErrorCode.INVALID_DATA);
					}
					Set<ConstraintViolation<CSVGuest>> violations = validator.validate(guest);
		            if (!violations.isEmpty()) {
		                throw new ApplicationException(ErrorCode.INVALID_DATA);
		            }
					list.add(clazz.cast(guest));
            	}
            	
			}
			br.close();
		} catch (IOException e) {
			throw new ApplicationException(ErrorCode.FILE_NOT_READ);
		}
		return list;
	}
}
