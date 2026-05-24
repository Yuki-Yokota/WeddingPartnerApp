/**
* 契約機能JS
*/
/*GET送信 */
function sendGetForm(button){
	const form = button.form;
	let venueName =form.querySelector('select[name="selectVenueName"]').value;
	const params = new URLSearchParams();
	if(venueName)params.append("venueName",venueName);
	fetch(`${contextPath}api/contract?${params.toString()}`, {method: "GET"})
	.then(response => {
		if(!response.ok){
			return response.json().then(err=>{throw err;});
		}else{
			return response.json();
		}
	})
	.then(result => {
		document.getElementById("error").textContent = "";
		
		let weddingTypeList = '';
		weddingTypeList.length = 0;
		weddingTypeList = result;
		let selectBoxName = form.querySelector('select[name="selectWeddingType"]');
		selectBoxName.length = 0;
		selectBoxName.disabled = false;
		// ループでoptionタグを追加
		weddingTypeList.forEach(type => {
			const option = document.createElement("option");
			option.value = type;
			option.textContent = type;
			selectBoxName.appendChild(option);
		});
		
	})
	.catch(error => {
		console.log(error);
		document.getElementById("error").innerText = error[0].errorMessage;
	});
}
/*POST送信 */
function sendPostForm(button){
	const form = button.form;
	
	let requestData = getRequestData(button);
	fetch(`${contextPath}api/contract`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(requestData)
	})
	.then(response => {
		if(!response.ok){
			return response.json().then(err=>{throw err;});
		}else{
			return response.json();
		}
	})
	.then(result => {
		document.getElementById("error").textContent = "";
		
		document.getElementById("estBtn").style.display="none";
		document.getElementById("totalAmountArea").style.display="block";
		disabledTable(form);
		let model = result;
		const nf = new Intl.NumberFormat('ja-JP');
		
		document.getElementById("totalAmount").textContent = nf.format(model.totalAmount)+'円';
		document.getElementById("ownExpense").textContent = nf.format(model.totalAmount - 30000 * model.numOfPerson)+'円';
		document.getElementById("contractId").value = model.contractId;
		document.getElementById("subject").textContent = "";
		const h1 = document.createElement('h1');
		h1.textContent = '見積画面';
		document.getElementById("subject").appendChild(h1);
	})
	.catch(error => {
		console.log(error);
		if(error[0].code=='err'){
			document.getElementById("error").innerText = error[0].errorMessage;
		}else{
			
			error.forEach(err=>{
				if(err.code=='numOfPersonValid'){
					document.getElementById("numOfPersonValid").textContent=err.errorMessage
				}else if(err.code=='weddingDateValid'){
					document.getElementById("weddingDateValid").textContent=err.errorMessage
				}else if(err.code=='venueNameValid'){
					document.getElementById("venueNameValid").textContent=err.errorMessage
				}
			})
		}
	});
}
/*PUT送信 */
function sendPutForm(button){
	//更新処理前に契約明細出力(契約が取得できなくなるため)
	sendGetPDFForm(button);
	
	const form = button.form;
	const id = document.getElementById("contractId").value || 0;
	fetch(`${contextPath}api/contract/${id}`, {
		method: "PUT"
	})
	.then(response => {
		if(response.status == 204){
			document.getElementById("error").textContent = "";		
			document.getElementById("inputCondition").style.display = "none";
			document.getElementById("complete").style.display = "block";
			
		}else{
			const error = response.json();
			document.getElementById("error").innerText = error[0].errorMessage;
		}
	});
}

/*pdfダウンロード機能 */
function sendGetPDFForm(button){
	let id=document.getElementById("contractId").value || '';
	let funcname='';
	if(button.id=='estimate'){
		funcname='estimate';
	}else if(button.id=='compContract'){
		funcname='contract';
	}
	const params = new URLSearchParams();
	if(id)params.append("id",id);
	if(funcname)params.append("funcname",funcname);
	const url = `${contextPath}api/contract/pdf?${params.toString()}`
	window.location.href = url;
	alert('PDFを出力しました');
}

/*テーブル内の入力欄を非活性にする */
function disabledTable(form){
	form.querySelector('input[name="numofperson"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectVenueName"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectWeddingType"]').setAttribute("disabled", true);
	form.querySelector('input[name="weddingdate"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectDish"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectDrink"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectGroomDress"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectBrideDress"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectPhotoShoot"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectVideoShoot"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectFlowerArrangement"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectGift"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectAcoustic"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectHairMakuUp"]').setAttribute("disabled", true);
	form.querySelector('select[name="selectModerator"]').setAttribute("disabled", true);
	form.querySelector('textarea[name="other"]').setAttribute("disabled", true);
}
function getRequestData(button){
	const form = button.form;
	let numOfPerson = form.querySelector('input[name="numofperson"]');
	numOfPerson = numOfPerson ? numOfPerson.value : 0;
	let venueName = form.querySelector('select[name="selectVenueName"]');
	venueName = venueName ? venueName.value : '';
	let weddingType = form.querySelector('select[name="selectWeddingType"]');
	weddingType = weddingType ? weddingType.value : '';
	let weddingDate = form.querySelector('input[name="weddingdate"]');
	weddingDate = weddingDate ? weddingDate.value : '';
	let dish = form.querySelector('select[name="selectDish"]');
	dish = dish ? dish.value : 0;
	let drink = form.querySelector('select[name="selectDrink"]');
	drink = drink ? drink.value : 0;
	let groomDress = form.querySelector('select[name="selectGroomDress"]');
	groomDress = groomDress ? groomDress.value : 0;
	let brideDress = form.querySelector('select[name="selectBrideDress"]');
	brideDress = brideDress ? brideDress.value : 0;
	let photoShootFlg = form.querySelector('select[name="selectPhotoShoot"]');
	photoShootFlg = photoShootFlg ? photoShootFlg.value : '';
	let videoShootFlg = form.querySelector('select[name="selectVideoShoot"]');
	videoShootFlg = videoShootFlg ? videoShootFlg.value : '';
	let flowerArrangementFlg = form.querySelector('select[name="selectFlowerArrangement"]');
	flowerArrangementFlg = flowerArrangementFlg ? flowerArrangementFlg.value : '';
	let giftFlg = form.querySelector('select[name="selectGift"]');
	giftFlg = giftFlg ? giftFlg.value : '';
	let acousticFlg = form.querySelector('select[name="selectAcoustic"]');
	acousticFlg = acousticFlg ? acousticFlg.value : '';
	let hairMakeUpFlg = form.querySelector('select[name="selectHairMakuUp"]');
	hairMakeUpFlg = hairMakeUpFlg ? hairMakeUpFlg.value : '';
	let moderatorFlg = form.querySelector('select[name="selectModerator"]');
	moderatorFlg = moderatorFlg ? moderatorFlg.value : '';
	let otherText = form.querySelector('textarea[name="other"]');
	otherText = (otherText && otherText.value) ? otherText.value : '特になし';
	return requstData = {
		numOfPerson:numOfPerson,
		venueName:venueName,
		weddingType:weddingType,
		weddingDate:weddingDate,
		dish:dish,
		drink:drink,
		groomDress:groomDress,
		brideDress:brideDress,
		photoShootFlg:photoShootFlg,
		videoShootFlg:videoShootFlg,
		flowerArrangementFlg:flowerArrangementFlg,
		giftFlg:giftFlg,
		acousticFlg:acousticFlg,
		hairMakeUpFlg:hairMakeUpFlg,
		moderatorFlg:moderatorFlg,
		otherText:otherText
	};
}

