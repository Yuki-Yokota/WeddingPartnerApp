/**
* ゲスト機能JS
*/
/*GET送信 */
function sendGetForm(button){
	let form = document.getElementById("guestForm");
	const page=button.dataset.page || document.getElementById("page").value;
	const sortKey = button.dataset.sort || '';
	const searchInput = document.querySelector('input[name="searchValue"]');
	let searchValue = '';
	if(searchInput.value){
		searchValue=(searchInput?searchInput.value : '')+(searchInput? ',' : '')+
						(searchInput?searchInput.dataset.role:'');
	}
	const operation=button.dataset.next || '';
	document.getElementById("checkedIdTemp").value='';
	
	let checkedId=document.querySelector('input[name="selected"]:checked');
	checkedId = checkedId ? checkedId.value : '';
	if(!checkedId){
		checkedId = document.getElementById("checkedIdTemp").value;
	}
	document.getElementById("checkedId").value=checkedId;
	let btnGuestId = button.dataset.guestid || '';
	const params = new URLSearchParams();
	if(page)params.append("page",page);
	if(sortKey)params.append("sort",sortKey);
	if(searchValue)params.append("search",searchValue);
	if(operation)params.append("operation",operation);
	if(checkedId)params.append("check",checkedId);
	fetch(`${contextPath}api/guest?${params.toString()}`, {method: "GET"})
	.then(response => {
		if(!response.ok){
			return response.json().then(err=>{throw err;});
		}else{
			return response.json();
		}
	})
	.then(result => {
		document.querySelectorAll('input[name="searchValue"]').forEach(input=>{input.value=""});
		
		let pageNum=result.pageInfo.pageNum;
		document.getElementById("page").value=pageNum;
		let totalPages=result.pageInfo.totalPages;
		document.getElementById("totalPages").value=totalPages;
		searchValue = '';
		document.getElementById("error").textContent = '';
		const nextForm = result.nextForm
		let guestList = result.pageInfo.list;
		renderTable(guestList);
		renderPagenation(pageNum,totalPages,form);
		var modal = "";
		if(nextForm=== "guestRegister" || nextForm=== "guestUpdate"){
			form.querySelector('input[name="guestFirstName"]').value='';
			form.querySelector('input[name="guestLastName"]').value='';
			form.querySelector('input[name="mailAddress"]').value='';
			modal = document.getElementById("guestRegisterUpdateModal");
			renderModal(modal);
			//ボタンは分岐
			const modalBtn = document.getElementById("modalBtn");
			modalBtn.textContent = ""
			//アレルギー入力欄の初期状態は表示ON
			const entryAllergy = document.getElementById("entryAllergy");
			entryAllergy.style.display="block";
			//アレルギーチェックボックス、テキストエリア初期化
			form.querySelectorAll('input[name="option"]').forEach(input=>{input.checked=false});
			form.querySelector('textarea[name="other"]').value='';
			form.querySelector('textarea[name="precaution"]').value='';
			var addCnt = 0;
			//リストから選択したcheckedIdに対応したゲストを取得
			guestList.forEach(gst => {
				if(checkedId==gst.guestId && nextForm=== "guestUpdate"){
					//ゲスト名をスペースで切り分けてそれぞれ格納
					let name = gst.guestName.split(/(?:\u3000| )/g);
					form.querySelector('input[name="guestFirstName"]').value=name[0];
					form.querySelector('input[name="guestLastName"]').value=name[1];
					form.querySelector('input[name="mailAddress"]').value=gst.mailAddress;
					//セレクトボックス
					if(gst.attendance=='有'){
						form.querySelector('select[name="pulldown"]').options[0].selected = true;
					}
					else if(gst.attendance=='無'){
						form.querySelector('select[name="pulldown"]').options[1].selected = true;
					}
					if(nextForm=== "guestUpdate"){
						//アレルギー情報格納
						gst.allergyInfo.forEach(agy=>{
							setAllergyName(form,agy.allergyName);
						});
						//注意事項格納の値を取得、配列の末尾の下に値を加える
						let text=form.querySelector('textarea[name="precaution"]');
						text = text ? text.value : '';
						if(text){
							let textArray = text.split('\n');
							textArray.push(gst.precaution);
							text=textArray.join('\n');
							form.querySelector('textarea[name="precaution"]').value=text;
						}
						else{
							if(gst.precaution){
								form.querySelector('textarea[name="precaution"]').value=gst.precaution;
							}
						}
						var updBtn = document.createElement('button');
							updBtn.type="button";
							updBtn.onclick=()=>sendPutForm(updBtn);
							updBtn.textContent='編集';
						modalBtn.appendChild(updBtn);
					}
				}
				else if(nextForm=== "guestRegister" && addCnt==0){
					addCnt++;
					//登録だとアレルギー入力欄を初期化(表示しない)
					entryAllergy.style.display="none";
					var addBtn = document.createElement('button');
						addBtn.type="button";
						addBtn.dataset.next='REGISTER';
						addBtn.onclick=()=>sendPostForm(addBtn);
						addBtn.textContent="追加";
					modalBtn.appendChild(addBtn);
				}
			});
		}
		else if(nextForm=== "guestDelete"){
			modal = document.getElementById("guestDeleteModal");
			renderModal(modal);
		}
		else if(nextForm=== "csvImport"){
			document.getElementById("importCSV").style.display = "block";
			modal = document.getElementById("csvImportModal");
			renderModal(modal);
		}
		else if(nextForm=== "mailDistibution"){
			const target=form.querySelector('input[name="target"]');
			target.value='';
			if(checkedId){
				target.value="ゲストID : "+checkedId;
				document.getElementById("checkedIdTemp").value=checkedId;
			}
			else{
				target.value="全員";
			}
			
			modal = document.getElementById("mailDistributionModal");	
			renderModal(modal);	
		}else{
			if(btnGuestId){
				//アレルギー表示へ遷移
				showAllergy(form,btnGuestId,guestList);
				
			}
		}
	})
	.catch(error => {
		console.log(error);
		document.getElementById("error").innerText = error[0].errorMessage;
	});
}
/*POST送信 */
function sendPostForm(button){
	const form = button.form;
	//csvか登録かで分岐する
	let id='';
	var url = `${contextPath}api/guest`;
	let action = button.dataset.next||'';
	let requestData = '';
	if (action&&action=='CSV_IMPORT'){
		url = url + `/batch`;
	}else if(action&&action=='MAIL_DISTRIBUTION'){
		url = url + `/mail`;
		requestData = {
			guestId:document.getElementById('checkedIdTemp').value || ''
		};
	}else{
		requestData = getRequestData(button,id);
	}
	
	fetch(url, {
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
		document.getElementById("guestRegisterUpdateModal").style.display = "none";
		document.getElementById("error").innerText = "";
		
		let registerGst = result.model;
		
		//pagesかguestかで分岐
		if(registerGst){
			sendGetForm(button);
		}else if(result.nextForm=== "mailDistributionClose"){
			document.getElementById("mailDistributionModal").style.display = "none";
		}
		//csvインポートでnewリストを取得した場合モーダル初期化、リストを最新に更新
		else if(result.pageInfo.list){
			document.getElementById("csvDataRegister").textContent='';
			form.querySelector('input[name="csvfile"]').value='';
			document.querySelector("#importCSV").style.display = "none";
			sendGetForm(button);
		}
	})
	.catch(error => {
		console.log(error);
		if(error[0].code=='err'){
			document.getElementById("error").innerText = error[0].errorMessage;
		}else{
			error.forEach(err=>{
				if(err.code=='guestNameValid'){
					document.getElementById("guestNameValid").textContent=err.errorMessage
				}else if(err.code=='mailAddressValid'){
					document.getElementById("mailAddressValid").textContent=err.errorMessage
				}
			})
		}
				
	});
}
/*PUT送信 */
function sendPutForm(button){
	const id = document.getElementById("checkedId").value;
	let requestData = getRequestData(button,id);
	fetch(`${contextPath}api/guest/${id}`, {
		method: "PUT",
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
		document.getElementById("guestRegisterUpdateModal").style.display = "none";
		document.getElementById("error").innerText = "";
		
		let updateGst = result;
		if(updateGst){
			sendGetForm(button);
		}
	})
	.catch(error => {
		console.log(error);
		if(error[0].code=='err'){
			document.getElementById("error").innerText = error[0].errorMessage;
		}else{
			error.forEach(err=>{
				if(err.code=='guestNameValid'){
					document.getElementById("guestNameValid").textContent=err.errorMessage
				}else if(err.code=='mailAddressValid'){
					document.getElementById("mailAddressValid").textContent=err.errorMessage
				}
			})
		}
					
	});
}
/*DELETE送信 */
function sendDeleteForm(button){
	const id = document.getElementById("checkedId").value;
	fetch(`${contextPath}api/guest/${id}`, {method: "DELETE"})
	.then(response => {
		if(response.status == 204){
			document.getElementById("error").innerText = "";
			sendGetForm(button);
					
			document.getElementById("guestDeleteModal").style.display = "none";
		}else{
			const error = response.json();
			document.getElementById("error").innerText = error[0].errorMessage;
		}
	});
}

/*テーブル描画機能 */
function renderTable(guestList) {
	var tbody= document.getElementById("guestTableBody");
	tbody.textContent = "";
	// templateのtr要素を取得
	var template = document.getElementById('guestTableTemplate').content.firstElementChild;
	const fragment = new DocumentFragment();
	guestList.forEach(guest => {
		// template要素の内容を複製
		var tr = template.cloneNode(true);
		var td = tr.children;
		//チェックボックス追加
		const checkbox = document.createElement('input');
		checkbox.type = "checkbox";
		checkbox.name = "selected";
		checkbox.value = guest.guestId;       
		checkbox.onchange=function(){onChecked(checkbox);};
		td[0].appendChild(checkbox);
		td[1].textContent = guest.guestId;
		td[2].textContent = guest.guestName;
		td[3].textContent = guest.mailAddress;
		td[4].textContent = guest.attendance;
		td[5].textContent = guest.ansFlg;
		const showBtn = document.createElement('button');
			showBtn.type = "button";
			showBtn.dataset.guestid = guest.guestId;
			showBtn.textContent='詳細';   
			showBtn.onclick=function(){sendGetForm(showBtn);};
		td[6].appendChild(showBtn)
		// guestTableBodyの中に流し込む
		fragment.appendChild(tr);
	});
	tbody.appendChild(fragment);
}

/*送信データ取得 */
function getRequestData(button,id){
	const form = button.form
	let guestFirstName = form.querySelector('input[name="guestFirstName"]');
	guestFirstName = guestFirstName ? guestFirstName.value : '';
	let guestLastName = form.querySelector('input[name="guestLastName"]');
	guestLastName = guestLastName ? guestLastName.value : '';
	//アレルギー情報(名前だけリストに格納する、controllerで一つずつ検索、一致するもののみゲストと紐づけるテーブルへ追加)
	let allergyName=[];
	//更新の場合
	if(id){
		//チェックボックスの値、その他の値をアレルギー名リストに格納
		allergyName=getAllergyName(form,allergyName);
	}
	let space = '　';
	//first,lastNameが両方とも半角英字の場合真ん中は半角スペースで文字列結合、両方ない場合空白、それ以外全角スペースで結合
	if(guestFirstName.match(/^[A-Za-z0-9]*$/) && guestLastName.match(/^[A-Za-z0-9]*$/)){
		space = ' ';
	}
	else if (guestFirstName == "" && guestLastName == ""){
		space = '';
	}
	var mailAddress = form.querySelector('input[name="mailAddress"]');
	mailAddress = mailAddress ? mailAddress.value : '';
	var precaution = form.querySelector('textarea[name="precaution"]');
	precaution = precaution ? precaution.value : '';
	var attendance = form.querySelector('select[name="pulldown"]');
	attendance = attendance ? attendance.value : '';
	let requestData = 	{
		guestName:guestFirstName + space + guestLastName,
		mailAddress:mailAddress,
		precaution:precaution,
		attendance:attendance,
		allergyInfo:allergyName.map(name=>{
			return{
				allergyName:name
			};
		})
	};
	return requestData;
}

/*アレルギー名セット */
function setAllergyName(form,allergyName){
	var flag=false;
	const allergyNameList=['卵','小麦','くるみ','乳','蕎麦','海老','蟹','落花生'];
	for(i=0;i<allergyNameList.length;i++){
		if(allergyName==allergyNameList[i]){
			form.option[i].checked=true;
			flag=true;
		}
	}	
	if(flag==false){
		//その他テキストエリアの値を取得、配列の末尾の下に値を加える
		let text=form.querySelector('textarea[name="other"]');
		text = text ? text.value : '';
		if(text){
			let textArray = text.split('\n');
			textArray.push(allergyName);
			text=textArray.join('\n');
			form.querySelector('textarea[name="other"]').value=text;
		}
		else{
			if(allergyName){
				form.querySelector('textarea[name="other"]').value=allergyName;
			}
		}
	}
}
/*アレルギー名ゲット */
function getAllergyName(form,allergyName){
	const allergyNameList=['卵','小麦','くるみ','乳','蕎麦','海老','蟹','落花生'];
	for(i=0;i<allergyNameList.length;i++){
		if(form.option[i].checked){
			allergyName.push(allergyNameList[i]);
		}
	}
	let text=form.querySelector('textarea[name="other"]');
	text = text ? text.value : '';
	if(text){
		let textArray = text.split('\n');
		textArray.forEach(t=>{
			allergyName.push(t);
		});
	}
	return allergyName;
}

/*アレルギーモーダルを表示 */
function showAllergy(form,btnGuestId,guestList){
	var modal = "";
	let allergyName='';
	let allergyNameList=[];
	var flag1 = false;
	var flag2 = false;
	const precaution = form.querySelector('textarea[name="precaution"]');
	const other = form.querySelector('textarea[name="other"]');
	precaution.value='';
	other.value='';
	
	//fetchしてゲストリスト取得、
	guestList.forEach(gst => {
		if(gst.guestId==btnGuestId){
			gst.allergyInfo.forEach(agy=>{
				if(agy.allergyName){
					allergyName=agy.allergyName + ","+allergyName;
					//値が4の倍数ごとに配列に格納
					let cnt=(allergyName.match(/,/g) || []).length;
					if(cnt%4===0){
						allergyNameList.push(allergyName);
						allergyName='';
					}	
					flag1 = true;
				}
			});
			if(gst.precaution){
				precaution.value=gst.precaution;
				flag2 = true;
			}
		}
	});
	if(flag1){
		if(allergyName.endsWith(',')){
			allergyName=allergyName.substring(0,allergyName.length-1);
		}
		allergyNameList.push(allergyName);
		let text = allergyNameList.join('\n');
		other.value=text;
	}
	else{
		other.value='特になし';
	}
	if(!flag2){
		precaution.value='特になし';
	}
	modal = document.getElementById("AllergyModal");
	renderModal(modal);
	
}
