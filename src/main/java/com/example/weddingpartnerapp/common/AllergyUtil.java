package com.example.weddingpartnerapp.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.weddingpartnerapp.model.Allergy;
import com.example.weddingpartnerapp.repository.AllergyMapper;

@Component
public class AllergyUtil {
	
	@Autowired
	private AllergyMapper agyMapper;
	
	/**
	 * アレルギー処理
	 * アレルギーテーブルの一覧取得、入力値と比較し一致する場合、ゲストアレルギーを更新する。
	 * 更新処理内容は1回目にゲストIdに紐づくものを全て削除し追加、2回目以降も次々追加する。
	 * ゲストサービス、アレルギーコントローラからアクセスされるため共通化
	 * @param allergyList
	 * @param guestId
	 * @throws ApplicationException
	 */
	public void allergyOperation(List<Allergy> allergyList,Integer guestId) {
		List<Allergy> allergyTableList = agyMapper.findAllAllergy();
		boolean one = false;
		for(Allergy agyTbl : allergyTableList) {
			for(Allergy agy : allergyList) {
				if(agy.getAllergyName().equals(agyTbl.getAllergyName())) {
					if(!one) {
						agyMapper.deleteGuestAllergyByGuestId(guestId);
						one=true;
					}
					agy.setAllergyId(agyTbl.getAllergyId());
					agyMapper.insertGuestAllergy(guestId, agy.getAllergyId());
				}
			}
		}
	}
}
