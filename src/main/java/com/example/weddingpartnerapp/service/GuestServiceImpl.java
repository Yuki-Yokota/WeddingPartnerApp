package com.example.weddingpartnerapp.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.weddingpartnerapp.common.Action;
import com.example.weddingpartnerapp.common.AllergyUtil;
import com.example.weddingpartnerapp.common.ApplicationException;
import com.example.weddingpartnerapp.common.CSVDownload;
import com.example.weddingpartnerapp.common.CSVUpload;
import com.example.weddingpartnerapp.common.ErrorCode;
import com.example.weddingpartnerapp.common.MailUtil;
import com.example.weddingpartnerapp.common.ThrowExcepFunction;
import com.example.weddingpartnerapp.common.Util;
import com.example.weddingpartnerapp.common.WeddingFunction;
import com.example.weddingpartnerapp.model.Allergy;
import com.example.weddingpartnerapp.model.CSVGuest;
import com.example.weddingpartnerapp.model.Combi;
import com.example.weddingpartnerapp.model.Guest;
import com.example.weddingpartnerapp.model.GuestDTO;
import com.example.weddingpartnerapp.model.Paginated;
import com.example.weddingpartnerapp.model.SessionUser;
import com.example.weddingpartnerapp.model.Wedding;
import com.example.weddingpartnerapp.model.WeddingDTO;
import com.example.weddingpartnerapp.repository.GuestMapper;
import com.example.weddingpartnerapp.repository.UserMapper;
import com.example.weddingpartnerapp.repository.VenueMapper;
import com.example.weddingpartnerapp.repository.WeddingMapper;

import jakarta.validation.Valid;

@Service
public class GuestServiceImpl implements GuestService {
	
	@Autowired
	private GuestMapper guestMapper;
	
	@Autowired
	private WeddingMapper wedMapper;
	
	@Autowired
	private VenueMapper venMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private AllergyUtil agyUtil;
	
	@Autowired
	private MailUtil mail;
	
	@Autowired
	private WeddingFunction wedFunc;
	
	@Value("${server.servlet.context-path}")
	private String contextPath;
	
	@Value("${local.mail.url}")
	private String localUrl;

	ThrowExcepFunction<Guest,GuestDTO> gstTogstDtoFunc = gst->{
		GuestDTO gstDto = new GuestDTO();
		List<Allergy>allergyInfo = new ArrayList<>();
		gstDto.setGuestId(gst.getGuestId());
		gstDto.setGuestName(gst.getGuestName());
		gstDto.setMailAddress(gst.getMailAddress());
		gstDto.setPrecaution(gst.getPrecaution());
		if(gst.isAttendance()) {
			gstDto.setAttendance("出");
		}
		else {
			gstDto.setAttendance("欠");
		}
		if(gst.isAnsFlg()) {
			gstDto.setAnsFlg("有");
		}
		else {
			gstDto.setAnsFlg("無");
		}
		allergyInfo = gst.getAllergyInfo();
		gstDto.setAllergyInfo(allergyInfo);
		return gstDto;
	};
	ThrowExcepFunction<GuestDTO,Guest> gstDtoTogstFunc = gstDto->{
		Guest gst = new Guest();
		Integer guestId = Optional.ofNullable(guestMapper.count()).map(s->s+1).orElse(1);
		gst.setGuestId(guestId);
		gst.setGuestName(gstDto.getGuestName());
		gst.setMailAddress(gstDto.getMailAddress());
		gst.setPrecaution(gstDto.getPrecaution());
		gst.setAnsFlg(false);
		gst.setDeleteFlg(false);
		if(gstDto.getAttendance().equals("出")) {
			gst.setAttendance(true);
		}
		else {
			gst.setAttendance(false);
		}
		return gst;
	};
	
	@Override
	public List<GuestDTO> findAll(String selectWeddingId) {
		List<GuestDTO> guestDTOList = new ArrayList<>();
		List<Guest> guestList = new ArrayList<>();
		Integer weddingId = Integer.parseInt(selectWeddingId);
		guestList = guestMapper.findByWeddingId(weddingId);
		if(guestList.isEmpty()) {
			return null;
		}
		
		for(Guest gst : guestList) {
			GuestDTO gstDto = new GuestDTO();
			gstDto = gstTogstDtoFunc.apply(gst);
			guestDTOList.add(gstDto);
		}
		return guestDTOList;
	}

	@Override
	public Paginated<GuestDTO> sort(String page, String sort, String selectWeddingId) {
		List<GuestDTO>guestList = findAll(selectWeddingId);
		String[] getSortAction=new String[2];
		Comparator<GuestDTO> comparator = null;
		if(sort!=null){
			getSortAction = sort.split(",");
		}else {
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		String sortAction=getSortAction[0];
		String attribute=getSortAction[1];
		switch(attribute) {
		case "guestId":
			comparator = Comparator.comparing(gst->gst.getGuestId(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "guestName":
			comparator = Comparator.comparing(gst->gst.getGuestName(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "mailAddress":
			comparator = Comparator.comparing(gst->gst.getMailAddress(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "attendance":
			comparator = Comparator.comparing(gst->gst.getAttendance(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		case "ansFlg":
			comparator = Comparator.comparing(gst->gst.getAnsFlg(), Comparator.nullsLast((a,b)->a.compareTo(b))); 
			break;
		default:
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		if("DESC".equals(sortAction)) {
			comparator=comparator.reversed();
		}
		guestList=Util.sort(guestList, comparator);
		return Util.pagenation(guestList, page);
	}

	@Override
	public Paginated<GuestDTO> search(String page, String search, String selectWeddingId) {
		List<GuestDTO>guestList = findAll(selectWeddingId);
		String[] getFilterAction=new String[2];
		if(search!=null){
			getFilterAction = search.split(",");
		}else {
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		String entryValue=getFilterAction[0].replaceAll("\\u3000| ","").toUpperCase();
		String attribute=getFilterAction[1];
		Function<GuestDTO,String> func = null;
		switch(attribute) {
		case "guestName":
			func=wed->wed.getGuestName();
			break;
		default:
			throw new ApplicationException(ErrorCode.INVALID_INPUT);
		}
		guestList=Util.search(guestList, entryValue,func);
		if(guestList.isEmpty()) {
			guestList = findAll(selectWeddingId);
		}
		return Util.pagenation(guestList, page);
	}

	@Override
	public Combi<Paginated<GuestDTO>> selectGuest(String page, String actionParam, String checkedId,
			String selectWeddingId) {
		List<GuestDTO> guestList = findAll(selectWeddingId);
		Action action=null;
    	String nextForm=null;
    	
		action = Action.form(actionParam);
		switch(action) {
		case REGISTER_FORM:
			nextForm = "guestRegister";
			break;
		case UPDATE_FORM:
			Optional.ofNullable(checkedId)
					.orElseThrow(()->new ApplicationException(ErrorCode.CHOOSE_ONLY_ONE));
			nextForm = "guestUpdate";
			break;
		case DELETE_FORM:
			Optional.ofNullable(checkedId)
					.orElseThrow(()->new ApplicationException(ErrorCode.CHOOSE_ONLY_ONE));
			nextForm = "guestDelete";
			break;
		case CSV_IMPORT_FORM:
			nextForm = "csvImport";
			break;
		case MAIL_DISTRIBUTION_FORM:
			nextForm = "mailDistibution";
			break;
		default:
			break;
		}
		
		Paginated<GuestDTO> pagenated = Util.pagenation(guestList, page);
		return new Combi<Paginated<GuestDTO>>(nextForm,pagenated);
	}

	@Override
	public @Valid GuestDTO register(@Valid GuestDTO gstDto,String selectWeddingId) {
		Integer weddingId = Integer.parseInt(selectWeddingId);
		Guest gst = gstDtoTogstFunc.apply(gstDto);
		gst.setWeddingId(weddingId);
		guestMapper.insert(gst);
		Integer guestId = Optional.ofNullable(guestMapper.count()).orElse(1);
		gst = guestMapper.findByGuestId(guestId);
		gstDto = gstTogstDtoFunc.apply(gst);
		return gstDto;
	}
	
	@Override
	public Paginated<GuestDTO> registerMultiple(@Valid List<GuestDTO> guestList,String selectWeddingId) {
		Integer weddingId = Integer.parseInt(selectWeddingId);
		for(GuestDTO gstDto : guestList) {
			Guest gst = gstDtoTogstFunc.apply(gstDto);
			gst.setWeddingId(weddingId);
			guestMapper.insert(gst);
		}
		return Util.pagenation(guestList, null);
	}
	
	@Override
	public void registerAllergy(GuestDTO gstDto){
		agyUtil.allergyOperation(gstDto.getAllergyInfo(), gstDto.getGuestId());
		Guest guest=guestMapper.findByGuestId(gstDto.getGuestId());
		guest.setAnsFlg(true);
		guest.setPrecaution(gstDto.getPrecaution());
		guestMapper.update(guest);
	}

	@Override
	public Object update(String checkedId, String selectWeddingId,@Valid GuestDTO gstDto) {
		Guest gst = new Guest();
		Integer guestId = Integer.parseInt(checkedId);
		gst.setGuestId(guestId);
		Integer weddingId = Integer.parseInt(selectWeddingId);
		gst.setWeddingId(weddingId);
		gst.setGuestName(gstDto.getGuestName());
		gst.setMailAddress(gstDto.getMailAddress());
		gst.setPrecaution(gstDto.getPrecaution());
		if(gstDto.getAttendance().equals("attendance")) {
			gst.setAttendance(true);
		}
		else {
			gst.setAttendance(false);
		}
		guestMapper.update(gst);
		
		//アレルギー情報更新
		agyUtil.allergyOperation(gstDto.getAllergyInfo(),guestId);
		//ゲストアレルギー付与
		gst = guestMapper.findByGuestId(guestId);
		gstDto = gstTogstDtoFunc.apply(gst);
		return gstDto;
	}

	@Override
	public void delete(String checkedId) {
		Integer guestId = Integer.parseInt(checkedId);
		guestMapper.delete(guestId);
	}

	@Override
	public byte[] exportCsv(String selectWeddingId) {
		List<GuestDTO> guestList = findAll(selectWeddingId);
		if(guestList.isEmpty()) {
			throw new ApplicationException(ErrorCode.LIST_IS_EMPTY);
		}
	    List<CSVGuest> csvList = guestList.stream().map(
	        e -> new CSVGuest(e)
	    ).collect(Collectors.toList());
		return CSVDownload.getCsvFile(csvList,CSVGuest.class);
	}

	@Override
	public Combi<List<GuestDTO>> importCsv(MultipartFile file, String selectWeddingId) {
		List<CSVGuest> csvBeforeList = CSVUpload.csvUpload(file,CSVGuest.class);
		List<GuestDTO> csvList = new ArrayList<>();
		//加工処理
		for(CSVGuest e : csvBeforeList) {
			csvList.add(new GuestDTO(e));
		}
		
		List<GuestDTO> guestList = findAll(selectWeddingId);
		
		int updateCnt = 0;
		int csvMaxId = csvList.stream()
		        .map(GuestDTO::getGuestId)
		        .max(Integer::compare)
		        .orElse(0);
		int newguestId=Math.max(guestMapper.count(),csvMaxId)+1;
		Set<Integer> usedGuestIds = guestList.stream()
										.map(GuestDTO::getGuestId)
										.collect(Collectors.toSet());
		for (GuestDTO guest : csvList) {
		    Integer guestId = guest.getGuestId();
		    if (usedGuestIds.contains(guestId)) {
		    	guest.setGuestId(newguestId);
		    	usedGuestIds.add(newguestId);
		        updateCnt++;
		        newguestId++;
		    }
		    else {
		    	usedGuestIds.add(guestId);
		    }
		}
		Combi<List<GuestDTO>>result = new Combi<>();
		result.setKey(String.valueOf(updateCnt));
		result.setData(csvList);
		return result;
	}
	
	/**
	 * メール配信処理
	 * 特定の個人に向ける場合、全員に向ける場合の2種類がある。以下の流れで実行
	 * 1.メールテンプレート取得処理
	 * 2.メール送信処理
	 */
	@Override
	public void mailDistribution(String checkedId, 
			SessionUser user,String selectWeddingId)  {
		List<GuestDTO>guestList = new ArrayList<>();
		String mailAddress=null;
		String content = null;
		Integer guestId=null;
		if(checkedId != null) {
			Guest gst = new Guest();
			guestId = Integer.parseInt(checkedId);
			gst = guestMapper.findByGuestId(guestId);
			if(gst.isAnsFlg()) {
				throw new ApplicationException(ErrorCode.SURVEY_COMPLETED);
			}
			mailAddress=gst.getMailAddress();
			content = getAllergyQuestionnaireMailTemplate(user,guestId,mailAddress,selectWeddingId);
			mail.sendMail(mailAddress,content);
		}
		else {
			for(GuestDTO gstDto:guestList) {
				checkedId=String.valueOf(gstDto.getGuestId());
				guestId=Integer.parseInt(checkedId);
				mailAddress=gstDto.getMailAddress();
				if(!(gstDto.getAnsFlg().equals("有"))) {
					content = getAllergyQuestionnaireMailTemplate(user,guestId,mailAddress,selectWeddingId);
					mail.sendMail(mailAddress,content);
				}
			}
		}
	}
	
	/**
	 * アレルギーアンケートメールテンプレートを入手しパラメータをセットする
	 * 特定の個人が対象だと遷移先URLのクエリパラメタにゲストIdを付与
	 * @param user
	 * @param guestId
	 * @param mailAddress
	 * @param selectWeddingId
	 * @param contextPath
	 * @return
	 * @throws ApplicationException
	 */
	public String getAllergyQuestionnaireMailTemplate(SessionUser user,Integer guestId,String mailAddress,
			String selectWeddingId) throws ApplicationException{
		Guest guest = guestMapper.findByGuestId(guestId);
		Wedding wed = wedMapper.findByWeddingId(Integer.parseInt(selectWeddingId));
		WeddingDTO wedDto = wedFunc.apply(wed);
		String venuePhoneNumber=venMapper.findById(wed.getVenueId()).getPhoneNumber();
		String userName = userMapper.findById(user.getUserId()).getUserName();
		String destinationUrl=localUrl+contextPath+"/questionnaire";
		if(guestId != null) {
			destinationUrl=destinationUrl+"?id="+guestId;
		}
		String url="/templates/mail/mailtemplate.html";
		
		//url取得できているか検証
		String content=mail.getMailTemplate(url);
		
		String replaceContent = content
				.replace("${guestId}", String.valueOf(guestId))
				.replace("${guestName}", guest.getGuestName())
				.replace("${groomName}", wedDto.getGroomName())
				.replace("${brideName}", wedDto.getBrideName())
				.replace("${venueName}", wedDto.getVenueName())
				.replace("${userName}",userName)
				.replace("${destinationUrl}",destinationUrl)
				.replace("${phoneNumber}",venuePhoneNumber);
		return replaceContent;
	}

}
