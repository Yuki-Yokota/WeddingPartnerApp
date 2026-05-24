package com.example.weddingpartnerapp.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.weddingpartnerapp.common.Action;
import com.example.weddingpartnerapp.common.ApplicationException;
import com.example.weddingpartnerapp.common.CSVDownload;
import com.example.weddingpartnerapp.common.CSVUpload;
import com.example.weddingpartnerapp.common.ErrorCode;
import com.example.weddingpartnerapp.common.PBKDF2Util;
import com.example.weddingpartnerapp.common.Util;
import com.example.weddingpartnerapp.model.CSVUser;
import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.User;
import com.example.weddingpartnerapp.repository.UserMapper;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;
	
	@Override
	public User authenticate(User entryUser) {
		Optional<User>userOpt = userMapper.findUserByMailAddress(entryUser.getMailAddress());
		User loginUser = new User();
		String hashPassword=null;
		boolean loginFlg=false;
		if(userOpt.isPresent()) {
			loginUser = userOpt.get();
			if(loginUser.getSalt()==null) {
				byte[] salt = PBKDF2Util.generateSalt();
				try {
					hashPassword = PBKDF2Util.hashPassword(loginUser.getPassword(), salt);
				} catch (Exception e) {
					throw new ApplicationException(ErrorCode.SYSTEM_ERROR);
				}
				loginUser.setPassword(hashPassword);
				loginUser.setSalt(salt);
				userMapper.update(loginUser);
			}
			try {
				loginFlg = PBKDF2Util.verifyPassword(entryUser.getPassword(),loginUser.getPassword(), loginUser.getSalt());
			} catch (Exception e) {
				throw new ApplicationException(ErrorCode.SYSTEM_ERROR);
			}
		}
		if(loginFlg) {
			return loginUser;
		}
		else {
			return null;
		}
	}
	
	@Override
	public List<User> findAll() {
		return userMapper.findAllUser();
	}

	@Override
	public User register(User user) {
		String hashPassword=null;
		Integer userId = userMapper.count();
		if(userId!=null) {
			user.setUserId(userId+1);
		}
		else {
			user.setUserId(1);
		}
		byte[] salt = PBKDF2Util.generateSalt();
		try {
			hashPassword = PBKDF2Util.hashPassword(user.getPassword(), salt);
		} catch (Exception e) {
			throw new ApplicationException(ErrorCode.SYSTEM_ERROR);
		}
		user.setRole("user");
		user.setPassword(hashPassword);
		user.setSalt(salt);
		userMapper.insert(user);
		return user;
	}
	
	/**
	 * 複数ユーザ登録処理(セキュリティの観点からパスワード登録はCSVから読込しない,権限は全て"user"にする)
	 */
	public Paginated<User> registerMultiple(List<User>userList) {
		for(User user : userList) {
			if(user.getUserId()==null) {
				user.setUserId(1);
			}
			user.setRole("user");
			userMapper.insert(user);
		}
		return Util.pagenation(userList, null);
	}

	@Override
	public User update(String checkedId, User user) {
		Integer userId = Integer.parseInt(checkedId);
		String hashPassword=null;
		byte[] salt = PBKDF2Util.generateSalt();
		//ソルト取得処理
		try {
			hashPassword = PBKDF2Util.hashPassword(user.getPassword(), salt);
		} catch (Exception e) {
			throw new ApplicationException(ErrorCode.SYSTEM_ERROR);
		}
		user.setUserId(userId);
		user.setPassword(hashPassword);
		user.setSalt(salt);
		userMapper.update(user);
		return userMapper.findById(userId);
	}

	@Override
	public void delete(String checkedId) {
		Integer userId = Integer.parseInt(checkedId);
		userMapper.delete(userId);
	}

	@Override
	public Paginated<User> sort(String page, String sortKey) {
		List<User> userList = userMapper.findAllUser();
		String[] getSortAction=new String[2];
		Comparator<User> comparator = null;
		if(sortKey!=null){
			getSortAction = sortKey.split(",");
		}else {
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		String sortAction=getSortAction[0];
		String attribute=getSortAction[1];
		switch(attribute) {
		case "userId":
			comparator = Comparator.comparing(User::getUserId, Comparator.naturalOrder()); 
			break;
		case "userName":
			comparator = Comparator.comparing(User::getUserName, Comparator.naturalOrder()); 
			break;
		case "mailAddress":
			comparator = Comparator.comparing(User::getMailAddress, Comparator.naturalOrder()); 
			break;
		case "role":
			comparator = Comparator.comparing(User::getRole, Comparator.naturalOrder()); 
			break;
		default:
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		if("DESC".equals(sortAction)) {
			comparator=comparator.reversed();
		}
		userList=Util.sort(userList, comparator);
		return Util.pagenation(userList, page);
	}

	@Override
	public Paginated<User> search(String page,String searchValue) {
		List<User> userList = userMapper.findAllUser();
		String[] getFilterAction=new String[2];
		if(searchValue!=null){
			getFilterAction = searchValue.split(",");
		}else {
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		String entryValue=getFilterAction[0].replaceAll("\\u3000| ","").toUpperCase();
		String attribute=getFilterAction[1];
		Function<User,String> func = null;
		switch(attribute) {
		case "userName":
			func=User::getUserName;
			break;
		default:
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		userList=Util.search(userList, entryValue,func);
		if(userList.isEmpty()) {
			userList = userMapper.findAllUser();
		}
		return Util.pagenation(userList, page);
	}

	@Override
	public Paginated<User> filter(String page, String filterValue) {
		List<User> userList = userMapper.findAllUser();
		String[] getFilterAction=new String[2];
		if(filterValue!=null){
			getFilterAction = filterValue.split(",");
		}else {
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		String entryValue=getFilterAction[0];
		String attribute=getFilterAction[1];
		Function<User,String> func = null;
		switch(attribute) {
		case "role":
			func=User::getRole;
			break;
		default:
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		userList=Util.filter(userList, entryValue,func);
		if(userList.isEmpty()) {
			userList = userMapper.findAllUser();
		}
		return Util.pagenation(userList, page);
	}

	@Override
	public Combi<Paginated<User>> selectUser(String page, String operation, String checkedId) {
		List<User> userList = userMapper.findAllUser();
		Action action=null;
    	String nextForm=null;
    	
		action = Action.form(operation);
		switch(action) {
		case REGISTER_FORM:
			nextForm = "userRegister";
			break;
		case UPDATE_FORM:
			Optional.ofNullable(checkedId)
					.orElseThrow(()->new ApplicationException(ErrorCode.CHOOSE_ONLY_ONE));
			nextForm = "userUpdate";
			break;
		case DELETE_FORM:
			Optional.ofNullable(checkedId)
					.orElseThrow(()->new ApplicationException(ErrorCode.CHOOSE_ONLY_ONE));
			nextForm = "userDelete";
			break;
		case CSV_IMPORT_FORM:
			nextForm = "csvImport";
			break;
		default:
			break;
		}
		
		Paginated<User> pagenated = Util.pagenation(userList, page);
		return new Combi<Paginated<User>>(nextForm,pagenated);
	}
	/**
	 * CSVエクスポート処理
	 * 全件取得、DTOに詰めなおし後CSVファイルを取得
	 * @throws ApplicationException 
	 */
	public byte[] exportCsv() {
		List<User> userList = userMapper.findAllUser();
		if(userList.isEmpty()) {
			throw new ApplicationException(ErrorCode.LIST_IS_EMPTY);
		}
	    List<CSVUser> csvList = userList.stream().map(
	        e -> new CSVUser(e.getUserId(), e.getUserName(),e.getMailAddress(),e.getRole())
	    ).collect(Collectors.toList());
		return CSVDownload.getCsvFile(csvList,CSVUser.class);
	}
	
	/**
	 * CSVインポート処理
	 * ユーザIDが重複していた場合、(DB、CSVのID)の最大値+1したものを新しいIDとして振り直す
	 * パスワードは仮としてUUIDを設定、すぐ変えるよう案内する
	 */
	public Combi<List<User>> importCsv(MultipartFile file) {
		List<CSVUser> csvBeforeList = CSVUpload.csvUpload(file,CSVUser.class);
		List<User> csvList = new ArrayList<>();
		
		for(CSVUser e : csvBeforeList) {
			//ここで詰め替え作業、1件ごとに異なるランダム値生成
			UUID uuid = UUID.randomUUID();
			Random rand = new Random();
			String password = String.valueOf(uuid).substring(0,rand.nextInt(16 - 8 + 1) + 8);
			csvList.add(new User(e.getUserId(), e.getUserName(),e.getMailAddress(),password,e.getRole(),null));
		}
		
		List<User> userList = userMapper.findAllUser();
		
		int updateCnt = 0;
		int csvMaxId = csvList.stream()
		        .map(User::getUserId)
		        .max(Integer::compare)
		        .orElse(0);
		int newUserId=Math.max(userMapper.count(),csvMaxId)+1;
		Set<Integer> usedUserIds = userList.stream()
										.map(User::getUserId)
										.collect(Collectors.toSet());
		for (User user : csvList) {
		    Integer userId = user.getUserId();
		    if (usedUserIds.contains(userId)) {
		    	user.setUserId(newUserId);
			    usedUserIds.add(newUserId);
		        updateCnt++;
		        newUserId++;
		    }
		    else {
			    usedUserIds.add(userId);
		    }
		}
		Combi<List<User>>result = new Combi<>();
		result.setKey(String.valueOf(updateCnt));
		result.setData(csvList);
		return result;
	}

}
