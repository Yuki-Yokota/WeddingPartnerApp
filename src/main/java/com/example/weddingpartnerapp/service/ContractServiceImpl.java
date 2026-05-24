package com.example.weddingpartnerapp.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.weddingpartnerapp.common.PDFUtil;
import com.example.weddingpartnerapp.model.BrideDress;
import com.example.weddingpartnerapp.model.Contract;
import com.example.weddingpartnerapp.model.Dish;
import com.example.weddingpartnerapp.model.Drink;
import com.example.weddingpartnerapp.model.GroomDress;
import com.example.weddingpartnerapp.model.OptionAmount;
import com.example.weddingpartnerapp.model.Venue;
import com.example.weddingpartnerapp.model.Wedding;
import com.example.weddingpartnerapp.model.WeddingType;
import com.example.weddingpartnerapp.repository.ContractMapper;
import com.example.weddingpartnerapp.repository.VenueMapper;
import com.example.weddingpartnerapp.repository.WeddingMapper;

@Service
public class ContractServiceImpl implements ContractService {

	@Autowired
	private ContractMapper conMapper;

	@Autowired
	private VenueMapper venMapper;

	@Autowired
	private WeddingMapper wedMapper;
	
	@Autowired
	private PDFUtil pdf;

	/**
	 * 会場名リストを取得
	 */
	@Override
	public List<String> getVenueNameList() {
		List<String> venueNameList = new ArrayList<>();
		venueNameList.add("-");
		List<String> addVenueNameList = new ArrayList<>();
		addVenueNameList = venMapper.findAllVenue().stream().map(Venue::getVenueName).collect(Collectors.toList());
		for (String s : addVenueNameList) {
			venueNameList.add(s);
		}
		return venueNameList;
	}

	/**
	 * 式形態リストを取得
	 */
	@Override
	public List<String> getWeddingTypeList(String venueName) {
		return venMapper.findAllWeddingType(venueName).stream().map(WeddingType::getWeddingTypeName)
				.collect(Collectors.toList());
	}

	/**
	 * 料理金額リストを取得
	 */
	@Override
	public List<Integer> getDishAmountList() {
		return conMapper.findAllDish().stream().map(Dish::getDishAmount).collect(Collectors.toList());
	}

	/**
	 * ドリンク金額リストを取得
	 */
	@Override
	public List<Integer> getDrinkAmountList() {
		return conMapper.findAllDrink().stream().map(Drink::getDrinkAmount).collect(Collectors.toList());
	}

	/**
	 * 新郎ドレス金額リストを取得
	 */
	@Override
	public List<Integer> getGroomDressAmountList() {
		return conMapper.findAllGroomDress().stream().map(GroomDress::getGroomDressAmount).collect(Collectors.toList());
	}

	/**
	 * 新婦ドレス金額リストを取得
	 */
	@Override
	public List<Integer> getBrideDressAmountList() {
		return conMapper.findAllBrideDress().stream().map(BrideDress::getBrideDressAmount).collect(Collectors.toList());
	}

	/**
	 * PDF変換処理 1.PDFに入れるデータを配列にセットする 2.PDF出力処理
	 */
	@Override
	public byte[] convertToPDF(String id,String funcName) {
		String subject = null;
		Integer ownExpenseValue=null;
		String contentName = null;
		
		Integer contractId = Integer.parseInt(id);
		Contract con = conMapper.findContractById(contractId);
 
		if(funcName.equals("estimate")) {
			subject = "見積明細書";
			contentName="予想見積額";
			ownExpenseValue = con.getTotalAmount() - 30000 * con.getNumOfPerson();
		}else if(funcName.equals("contract")) {
			subject = "契約明細書";
			contentName="総額";
		}
		
		List<String[]> list = new ArrayList<>();
		NumberFormat nf = NumberFormat.getNumberInstance();
		String weddingDate = String.valueOf(con.getWeddingDate());
		String[] weddateArray = weddingDate.split("-");
		String weddate = weddateArray[0] + "年" + weddateArray[1] + "月" + weddateArray[2] + "日";
		list.add(new String[] { "人数", String.valueOf(con.getNumOfPerson()) });
		list.add(new String[] { "会場", con.getVenueName() });
		list.add(new String[] { "式形態", con.getWeddingType() });
		list.add(new String[] { "日時", weddate });
		list.add(new String[] { "料理", String.valueOf(nf.format(con.getDish())) + "円" });
		list.add(new String[] { "ドリンク", String.valueOf(nf.format(con.getDrink())) + "円" });
		list.add(new String[] { "新郎ドレス", String.valueOf(nf.format(con.getGroomDress())) + "円" });
		list.add(new String[] { "新婦ドレス", String.valueOf(nf.format(con.getBrideDress())) + "円" });
		list.add(new String[] { "写真撮影", con.isPhotoShootFlg() ? "あり" : "なし" });
		list.add(new String[] { "映像撮影", con.isVideoShootFlg() ? "あり" : "なし" });
		list.add(new String[] { "装花", con.isFlowerArrangementFlg() ? "あり" : "なし" });
		list.add(new String[] { "引き出物", con.isGiftFlg() ? "あり" : "なし" });
		list.add(new String[] { "音響", con.isAcousticFlg() ? "あり" : "なし" });
		list.add(new String[] { "ヘアメイク", con.isHairMakeUpFlg() ? "あり" : "なし" });
		list.add(new String[] { "司会者", con.isModeratorFlg() ? "あり" : "なし" });
		list.add(new String[] { "その他入力欄", con.getOtherText() });
		list.add(new String[] { contentName, String.valueOf(nf.format(con.getTotalAmount())) + "円" });
		if (ownExpenseValue != null) {
			Optional.ofNullable(list.add(new String[] { "予想自己負担額", String.valueOf(nf.format(ownExpenseValue)) + "円" }));
		}
		String[][] data = list.toArray(new String[0][0]);

		byte[] file = pdf.outputPDF(subject, data);
		return file;
	}

	/**
	 * 契約登録(見積もり段階) 1.計算し合計金額を算出、dataに合計金額をセットする 2.契約Idを探しあるとmax値+1、ないと1を入れる
	 * 3.completedFlg=falseにしてテーブルに値を入れる。契約成立でtrueにする ※OptionAmountは一つしかデータがない想定
	 */
	@Override
	public Contract registerContract(Contract data,String customerId) {
		Integer totalAmount = null;
		OptionAmount optAmount = conMapper.findOptionAmountById(1);
		Integer numOfPerson = data.getNumOfPerson();
		String venueName = data.getVenueName();
		Integer venueAmount = venMapper.findByName(venueName).getVenueAmount();
		String weddingType = data.getWeddingType();
		Integer weddingTypeAmount = conMapper.findWeddingTypeByName(weddingType).getWeddingTypeAmount();
		Integer dishAmount = data.getDish();
		Integer drinkAmount = data.getDrink();
		Integer groomDressAmount = data.getGroomDress();
		Integer brideDressAmount = data.getBrideDress();
		Integer photoShootAmount = 0;
		if (data.isPhotoShootFlg()) {
			photoShootAmount = optAmount.getPhotoShootAmount();
		}
		Integer videoShootAmount = 0;
		if (data.isVideoShootFlg()) {
			videoShootAmount = optAmount.getVideoShootAmount();
		}
		Integer flowerArrangementAmount = 0;
		if (data.isFlowerArrangementFlg()) {
			flowerArrangementAmount = optAmount.getFlowerArrangementAmount();
		}
		Integer giftAmount = 0;
		if (data.isGiftFlg()) {
			giftAmount = optAmount.getGiftAmount();
		}
		Integer acousticAmount = 0;
		if (data.isAcousticFlg()) {
			acousticAmount = optAmount.getAcousticAmount();
		}
		Integer hairMakeUpAmount = 0;
		if (data.isHairMakeUpFlg()) {
			hairMakeUpAmount = optAmount.getHairMakeUpAmount();
		}
		Integer moderatorAmount = 0;
		if (data.isModeratorFlg()) {
			moderatorAmount = optAmount.getModeratorAmount();
		}
		Integer cakeAmount = 0;
		cakeAmount = optAmount.getCakeAmount();

		totalAmount = calculateEstimatedValue(numOfPerson, venueAmount, weddingTypeAmount, dishAmount, drinkAmount,
				groomDressAmount, brideDressAmount, photoShootAmount, videoShootAmount, flowerArrangementAmount,
				giftAmount, acousticAmount, hairMakeUpAmount, moderatorAmount, cakeAmount, totalAmount);
		data.setTotalAmount(totalAmount);

		data.setCompletedFlg(false);
		Integer contractId = Optional.ofNullable(conMapper.count()).map(s -> s + 1).orElse(1);
		data.setContractId(contractId);
		data.setCustomerId(Integer.parseInt(customerId));

		conMapper.insert(data);
		data = conMapper.findContractById(contractId);
		return data;
	}

	/**
	 * 式総額見積もり計算式 最後に10%のサービス料を加算する、その後消費税10%加算
	 * 
	 * @param numofPerson
	 * @param weddingTypeAmount
	 * @param dishAmount
	 * @param drinkAmount
	 * @param groomDressAmount
	 * @param brideDressAmount
	 * @param photoShootAmount
	 * @param videoShootAmount
	 * @param flowerArrangementAmount
	 * @param giftAmount
	 * @param acousticAmount
	 * @param hairMakeUpAmount
	 * @param moderatorAmount
	 * @return
	 */
	public Integer calculateEstimatedValue(Integer numOfPerson, Integer venueAmount, Integer weddingTypeAmount,
			Integer dishAmount, Integer drinkAmount, Integer groomDressAmount, Integer brideDressAmount,
			Integer photoShootAmount, Integer videoShootAmount, Integer flowerArrangementAmount, Integer giftAmount,
			Integer acousticAmount, Integer hairMakeUpAmount, Integer moderatorAmount, Integer cakeAmount,
			Integer totalAmount) {

		totalAmount = venueAmount + weddingTypeAmount + numOfPerson * (dishAmount + drinkAmount) + groomDressAmount
				+ brideDressAmount + photoShootAmount + videoShootAmount + flowerArrangementAmount
				+ numOfPerson * giftAmount + acousticAmount + hairMakeUpAmount + moderatorAmount + cakeAmount;

		totalAmount = (int) (totalAmount + (totalAmount * 0.1));

		totalAmount = (int) (totalAmount + (totalAmount * 0.1));
		return totalAmount;
	}

	/**
	 * 契約処理 1.契約機能独自の値をセット、契約明細書PDF出力 2.契約テーブル内一致idのcompleted_flg = trueにする
	 * 3.特定の値を抽出したWeddingクラスを作成、weddingsテーブルに格納 4.complete画面へ遷移
	 */
	@Override
	public void contract(String id) {
		Integer contractId = Integer.parseInt(id);
		Contract contract = conMapper.findContractById(contractId);
		//ここではupdateのみに集中する
		//byte[] file = convertToPDF(contract, subject, totalAmountContentName, funcName, null);

		conMapper.update(contractId);

		Integer weddingId = Optional.ofNullable(wedMapper.count()).map(s -> s + 1).orElse(1);
		Integer venueId = venMapper.findByName(contract.getVenueName()).getVenueId();
		LocalDate weddingDate = contract.getWeddingDate();
		Wedding wed = new Wedding(weddingId, contract.getCustomerId(), venueId, weddingDate, false);
		wedMapper.insert(wed);
	}

}
