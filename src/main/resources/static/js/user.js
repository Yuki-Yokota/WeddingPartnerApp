/**
* ユーザ機能JS
*/
/*GET送信 */
function sendGetForm(button){
	let form = document.getElementById("userForm");
	const page=button.dataset.page || document.getElementById("page").value;
	const sortKey = button.dataset.sort || '';
	const pulldownSelect = document.querySelector('select[name="pulldown"]');
	let pulldown = '';
	//プルダウンが押下された場合のみプルダウン値をセットする
	
	if(pulldownSelect.value&&button.name=='pulldown'){
		pulldown=(pulldownSelect ? pulldownSelect.value : '')+(pulldownSelect ? ',' : '')+
					(pulldownSelect ? pulldownSelect.dataset.role : '');
	}
	const searchInput = document.querySelector('input[name="searchValue"]');
	let searchValue = '';
	if(searchInput.value){
		searchValue=(searchInput ? searchInput.value : '')+(searchInput ? ',' : '')+
					(searchInput ? searchInput.dataset.role :'');
	}
	
	const operation=button.dataset.next || '';
	
	let checkedId= document.querySelector('input[name="selected"]:checked');
	checkedId = checkedId ? checkedId.value : '';
	document.getElementById("checkedId").value = checkedId;
	const params = new URLSearchParams();
	if(page)params.append("page",page);
	if(sortKey)params.append("sort",sortKey);
	if(pulldown)params.append("filter",pulldown);
	if(searchValue)params.append("search",searchValue);
	if(operation)params.append("operation",operation);
	if(checkedId)params.append("check",checkedId);
	fetch(`${contextPath}api/user?${params.toString()}`, {method: "GET"})
	.then(response => {
		if(!response.ok){
			return response.json().then(err=>{throw err;});
		}else{
			return response.json();
		}
	})
	.then(result => {
		let pageNum=result.pageInfo.pageNum;
		document.getElementById("page").value=pageNum;
		let totalPages=result.pageInfo.totalPages;
		document.getElementById("totalPages").value=totalPages;
		document.querySelector('input[name="searchValue"]').value="";
		searchValue = '';
		const nextForm = result.nextForm
		let userList = result.pageInfo.list;
		renderTable(userList);
		renderPagenation(pageNum,totalPages,form);
		var modal = "";
		if(nextForm=== "userRegister" || nextForm=== "userUpdate"){
			modal = document.getElementById("userRegisterUpdateModal");
			renderModal(modal);
			//ボタンは分岐
			const modalBtn = form.querySelector(".modalBtn");
			modalBtn.textContent='';
			form.querySelector('input[name="userFirstName"]').value='';
			form.querySelector('input[name="userLastName"]').value='';
			form.querySelector('input[name="mailAddress"]').value=''
			form.querySelector('input[name="password"]').value='';
			//リストから選択したcheckedIdに対応したユーザを取得
			var addcnt = 0;
			userList.forEach(user => {
				if(checkedId==user.userId && nextForm=== "userUpdate"){
					//ユーザ名をスペースで切り分けてそれぞれ格納
					let name = user.userName.split(/(?:\u3000| )/g);
					if(name[0]){form.querySelector('input[name="userFirstName"]').value=name[0];}
					if(name[1]){form.querySelector('input[name="userLastName"]').value=name[1];}
					form.querySelector('input[name="mailAddress"]').value=user.mailAddress;
					var updBtn = document.createElement('button');
						updBtn.type="button";
						updBtn.onclick=()=>sendPutForm(updBtn);
						updBtn.textContent="編集";
					form.querySelector(".modalBtn").appendChild(updBtn);
				}
				else if(nextForm=== "userRegister" && addcnt == 0){
					addcnt++;
					//チェックが何もされてない時新規登録と見なす
					var addBtn = document.createElement('button');
						addBtn.type="button";
						addBtn.dataset.next="REGISTER";
						addBtn.onclick=()=>sendPostForm(addBtn);
						addBtn.textContent="追加";
					form.querySelector(".modalBtn").appendChild(addBtn);
				}
			});
			
		}
		else if(nextForm=== "userDelete"){
			modal = document.getElementById("userDeleteModal");
			renderModal(modal);
		}
		else if(nextForm=== "csvImport"){
			document.getElementById("importCSV").style.display = "block";
			modal = document.getElementById("csvImportModal");
			renderModal(modal);
		}
		
	})
	.catch(error => {
		console.log(error);
		document.getElementById("error").innerText = error[0].errorMessage;
	});
	
}
/**
* POST機能
*/
function sendPostForm(button){
	const form = button.form;
	var url = `${contextPath}api/user`;
	let action = button.dataset.next||'';
	var requestData = '';
	//csvか登録かで分岐する
	if(action&&action=='CSV_IMPORT'){
		url = url + `/batch`;
	}else{
		requestData = getRequestData(button);
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
		document.getElementById("userRegisterUpdateModal").style.display = "none";
		document.getElementById("error").innerText = "";
		
		let registerUser = result.model;
			
		//pagesかuserかで分岐
		if(registerUser){
			sendGetForm(button);
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
				if(err.code=='userNameValid'){
					document.getElementById("userNameValid").textContent=err.errorMessage
				}else if(err.code=='mailAddressValid'){
					document.getElementById("mailAddressValid").textContent=err.errorMessage
				}else if(err.code=='passwordValid'){
					document.getElementById("passwordValid").textContent=err.errorMessage
				}
			})
		}
			
	});
}
/*PUT送信 */
function sendPutForm(button){
	const form = button.form;
	const id = document.getElementById("checkedId").value;
	let requestData = getRequestData(button);
	fetch(`${contextPath}api/user/${id}`, {
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
		document.getElementById("userRegisterUpdateModal").style.display = "none";
		document.getElementById("error").innerText ='';
		if(result){
			sendGetForm(button);
		}
		
	})
	.catch(error => {
		console.log(error);
		if(error[0].code=='err'){
			document.getElementById("error").innerText = error[0].errorMessage;
		}else{
			error.forEach(err=>{
				if(err.code=='userNameValid'){
					document.getElementById("userNameValid").textContent=err.errorMessage
				}else if(err.code=='mailAddressValid'){
					document.getElementById("mailAddressValid").textContent=err.errorMessage
				}else if(err.code=='passwordValid'){
					document.getElementById("passwordValid").textContent=err.errorMessage
				}
			})
		}
		
	});
}
/*DELETE送信 */
function sendDeleteForm(button){
	const id = document.getElementById("checkedId").value;
	fetch(`${contextPath}api/user/${id}`, {method: "DELETE"})
	.then(response => {
		if(response.status == 204){
			document.getElementById("error").innerText = "";
			sendGetForm(button);
			
			document.getElementById("userDeleteModal").style.display = "none";
		}else{
			const error = response.json();
			document.getElementById("error").innerText = error[0].errorMessage;
		}
	});
}
/*テーブル描画機能 */
function renderTable(userList) {
	var tbody= document.getElementById("userTableBody");
	tbody.textContent = "";
	// templateのtr要素を取得
	var template = document.getElementById('userTableTemplate').content.firstElementChild;
	const fragment = new DocumentFragment();
    userList.forEach(user => {
		// template要素の内容を複製
		var tr = template.cloneNode(true);
		var td = tr.children;
		//チェックボックス追加
		const checkbox = document.createElement('input');
		checkbox.type = "checkbox";
		checkbox.name = "selected";
		checkbox.value = user.userId;       
		checkbox.onchange=function(){onChecked(checkbox);};
		td[0].appendChild(checkbox);
		td[1].textContent = user.userId;
		td[2].textContent = user.userName;
		td[3].textContent = user.mailAddress;
		td[4].textContent = user.role;
		// userTableBodyの中に流し込む
		fragment.appendChild(tr);
   });
	tbody.appendChild(fragment);
}
/*送信データ取得 */
function getRequestData(button){
	const form = button.form
	let userFirstName = form.querySelector('input[name="userFirstName"]');
	userFirstName = userFirstName ? userFirstName.value : '';
	let userLastName = form.querySelector('input[name="userLastName"]');
	userLastName = userLastName ? userLastName.value : '';
	let space = '　';
	//first,lastNameが両方とも半角英字の場合真ん中は半角スペースで文字列結合、両方ない場合空白、それ以外全角スペースで結合
	if(userFirstName.match(/^[A-Za-z0-9]*$/) && userLastName.match(/^[A-Za-z0-9]*$/)){
		space = ' ';
	}
	else if (userFirstName == "" && userLastName == ""){
		space = '';
	}
	let mailAddress = form.querySelector('input[name="mailAddress"]');
	mailAddress = mailAddress ? mailAddress.value : '';
	let passWord = form.querySelector('input[name="password"]');
	passWord = passWord ? passWord.value : '';
	let requestData = 	{
		userName:userFirstName + space + userLastName,
		mailAddress:mailAddress,
		password:passWord
	};
	return requestData;
}
