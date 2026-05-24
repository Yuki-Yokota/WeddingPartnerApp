/**
* 顧客機能JS
*/
/*GET送信 */
function sendGetForm(button){
	let form = document.getElementById("customerForm");
	const page=button.dataset.page || document.getElementById("page").value;
	const sortKey = button.dataset.sort || '';
	//テキストボックスが3つの場合要改修
	let searchInput = [];
	searchInput = document.querySelectorAll('input[name="searchValue"]');
	let search='';
	if(button.id=="groom"){
		search=searchInput[0];
	}
	else if(button.id=="bride"){
		search=searchInput[1];
	}
	let searchValue = '';
	searchValue=(search ? search.value : '') + (search ? ',' : '') + (search ? search.dataset.role : '');
	const operation=button.dataset.next || '';
	let checkedId = document.querySelector('input[name="selected"]:checked');
	checkedId = checkedId ? checkedId.value : '';
	if(checkedId == ''){
		if(button.dataset.customerid){
			checkedId = button.dataset.customerid;
		}else{
			let customerId = document.getElementById('customerId');
				customerId = customerId ? customerId.value : 0;
			if(customerId > 0){
				checkedId = customerId;
			}
		}
	}
	document.getElementById("checkedId").value=checkedId;
	document.getElementById("customerId").value=checkedId;
	
	let formId = '';
	//遷移ボタンだとformIdを遷移先Idにする
	if(button.className=="transit"){
		form = document.getElementById('todoForm');
		formId = form.id;
		formId = (form.id=="customerForm") ?  "todoForm" : "customerForm";
	}
	
	const params = new URLSearchParams();
	if(page)params.append("page",page);
	if(sortKey)params.append("sort",sortKey);
	if(searchValue)params.append("search",searchValue);
	if(operation)params.append("operation",operation);
	if(checkedId)params.append("check",checkedId);
	if(formId)params.append("form",formId);
	fetch(`${contextPath}api/customer?${params.toString()}`, {method: "GET"})
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
		document.querySelectorAll('input[name="searchValue"]').forEach(input=>{input.value=""});
		searchValue = '';
		var sessionUser = document.getElementById("sessionUser").value;
		const nextForm = result.nextForm;
		if(nextForm==="todoForm"){
			let todoList = result.pageInfo.list;
			renderTable(todoList,nextForm,sessionUser);
			renderPagenation(pageNum,totalPages,form);
			document.querySelector("#customerForm").style.display = "none";
			document.querySelector("#exportCSV").style.display = "none";
			document.querySelector("#transition").style.display = "none";
			document.querySelector("#todoForm")	.style.display = "block";	
		}
		else{
			let customerList = result.pageInfo.list;
			renderTable(customerList,nextForm,sessionUser);
			renderPagenation(pageNum,totalPages,form);
			var modal = "";
			if(nextForm=== "customerRegister" || nextForm=== "customerUpdate"){
				modal = document.getElementById("customerRegisterUpdateModal");
				renderModal(modal);
				//ボタンは分岐
				const modalBtn = document.querySelector(".modalBtn");
				modalBtn.textContent = "";
				form.querySelector('input[name="groomFirstName"]').value='';
				form.querySelector('input[name="groomLastName"]').value='';
				form.querySelector('input[name="brideFirstName"]').value='';
				form.querySelector('input[name="brideLastName"]').value='';
				form.querySelector('input[name="mailAddress"]').value=''
				form.querySelectorAll('input[name="phoneNumber"]').forEach(input=>{input.value=""});
				//リストから選択したcheckedIdに対応した顧客を取得
				var addcnt = 0;
				customerList.forEach(customer => {
					if(checkedId==customer.customerId && nextForm=== "customerUpdate"){
						//顧客名をスペースで切り分けてそれぞれ格納
						let groomName = customer.groomName.split(/(?:\u3000| )/g);
						let brideName = customer.brideName.split(/(?:\u3000| )/g);
						if(groomName[0]){form.querySelector('input[name="groomFirstName"]').value=groomName[0];}
						if(groomName[1]){form.querySelector('input[name="groomLastName"]').value=groomName[1];}
						if(brideName[0]){form.querySelector('input[name="brideFirstName"]').value=brideName[0];}
						if(brideName[1]){form.querySelector('input[name="brideLastName"]').value=brideName[1];}
						form.querySelector('input[name="mailAddress"]').value=customer.mailAddress;
						//電話番号は3つに切り分け
						const values = customer.phoneNumber.split('-');
						Array.from(form.querySelectorAll('input[name="phoneNumber"]')).forEach((input, i) => input.value = values[i] || "");
						var updBtn = document.createElement('button');
							updBtn.type="button";
							updBtn.onclick=()=>sendPutForm(updBtn);
							updBtn.textContent="編集";
						form.querySelector(".modalBtn").appendChild(updBtn);
					}
					else if(nextForm=== "customerRegister" && addcnt == 0){
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
			else if(nextForm=== "customerDelete"){
				modal = document.getElementById("customerDeleteModal");
				renderModal(modal);
			}
			else if(nextForm=== "csvImport"){
				document.getElementById("importCSV").style.display = "block";
				modal = document.getElementById("csvImportModal");
				renderModal(modal);
			}
			else{
				document.querySelector("#customerForm").style.display = "block";
				document.querySelector("#exportCSV").style.display = "block";
				document.querySelector("#transition").style.display = "block";
				document.querySelector("#todoForm")	.style.display = "none";
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
	const form = button.closest('form');
	let requestData = '';
	var url = `${contextPath}api/customer`;
	let action = button.dataset.next||'';
	if(action&&action== "TODO_ADD"){
		requestData = getRequestDataOfTodo(button);
		url = url + `/todo`;
	}
	else if (action&&action=='CSV_IMPORT'){
		url = url + `/batch`;
	}else{
		requestData = getRequestDataOfCustomer(button);
	}
	//csvか顧客登録かToDo追加で分岐する
	
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
		let registerModel = result.model;
		const nextForm = result.nextForm;
		if(nextForm == "todoForm"){
			if(registerModel){
				sendGetForm(button);
			}
			document.getElementById("todoRegisterUpdateModal").style.display = "none";
		}
		else{
			//pagesかuserかで分岐
			if(registerModel){
				sendGetForm(button);
			}
			//csvインポートでnewリストを取得した場合モーダル初期化、リストを最新に更新
			else if(result.pageInfo.list){
				document.getElementById("csvDataRegister").textContent='';
				form.querySelector('input[name="csvfile"]').value='';
				document.querySelector("#importCSV").style.display = "none";
				sendGetForm(button);
			}
			document.getElementById("customerRegisterUpdateModal").style.display = "none";
		}
	})
	.catch(error => {
		console.log(error);
		if(error[0].code=='err'){
			document.getElementById("error").innerText = error[0].errorMessage;
		}else{
			error.forEach(err=>{
				if(err.code=='groomNameValid'){
					document.getElementById("groomNameValid").textContent=err.errorMessage
				}else if(err.code=='brideNameValid'){
					document.getElementById("brideNameValid").textContent=err.errorMessage
				}else if(err.code=='mailAddressValid'){
					document.getElementById("mailAddressValid").textContent=err.errorMessage
				}else if(err.code=='phoneNumberValid'){
					document.getElementById("phoneNumberValid").textContent=err.errorMessage
				}else if(err.code=='taskContentValid'){
					document.getElementById("taskContentValid").textContent=err.errorMessage
				}else if(err.code=='deadlineValid'){
					document.getElementById("deadlineValid").textContent=err.errorMessage
				}
			})
		}
	});
}
/*PUT送信 */
function sendPutForm(button){
	const form = button.closest('form');
	let id = '';
	let requestData = '';
	if(form.id==='customerForm'){
		id = document.getElementById("checkedId").value;
		requestData = getRequestDataOfCustomer(button);
	}
	else{
		id = form.querySelector('input[name="todoId"]');
		id = 'todo/'+(id ? id.value : 0);
		requestData = getRequestDataOfTodo(button);
	}
	fetch(`${contextPath}api/customer/${id}`, {
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
		const nextForm = result.nextForm;
		const updModel = result.model;
		var sessionUser = document.getElementById("sessionUser").value;
		if(nextForm === "todoForm"){
			if(updModel){
				sendGetForm(button);
			}
			document.getElementById("todoRegisterUpdateModal").style.display = "none";
		}
		else{
			if(updModel){
				sendGetForm(button);
			}
			document.getElementById("customerRegisterUpdateModal").style.display = "none";
		}
		
	})
	.catch(error => {
		console.log(error);
		if(error[0].code=='err'){
			document.getElementById("error").innerText = error[0].errorMessage;
		}else{
			error.forEach(err=>{
				if(err.code=='groomNameValid'){
					document.getElementById("groomNameValid").textContent=err.errorMessage
				}else if(err.code=='brideNameValid'){
					document.getElementById("brideNameValid").textContent=err.errorMessage
				}else if(err.code=='mailAddressValid'){
					document.getElementById("mailAddressValid").textContent=err.errorMessage
				}else if(err.code=='phoneNumberValid'){
					document.getElementById("phoneNumberValid").textContent=err.errorMessage
				}else if(err.code=='taskContentValid'){
					document.getElementById("taskContentValid").textContent=err.errorMessage
				}else if(err.code=='deadlineValid'){
					document.getElementById("deadlineValid").textContent=err.errorMessage
				}
			})
		}
	});
}
// セッション中だけ完了IDを保持
const completedTodoIds = new Set();
/*DELETE送信 */
function sendDeleteForm(obj){
	const form = obj.closest('form');
	let id = '';
	if(form.id==='todoForm'){
		const todoId = obj.value;
		completedTodoIds.add(todoId);  // 複数保持可能
		id="todo/"+todoId||0;
	}
	else{
		id=document.getElementById("checkedId").value;
	}
	fetch(`${contextPath}api/customer/${id}`, {method: "DELETE"})
	.then(response => {
		if(response.status == 204){
			document.getElementById("error").innerText = "";
			sendGetForm(obj);
				
			document.getElementById("customerDeleteModal").style.display = "none";
		}else{
			const error = response.json();
			document.getElementById("error").innerText = error[0].errorMessage;
		}
	});
}
/*テーブル描画機能 */
function renderTable(list,nextForm,sessionUser) {
	if(nextForm === "todoForm"){
		var tbody= document.getElementById("todoTableBody");
		tbody.textContent = "";
		// templateのtr要素を取得
		var template = document.getElementById('todoTableTemplate').content.firstElementChild;
		const fragment = new DocumentFragment();
		list.forEach(todo => {
			// template要素の内容を複製
			var tr = template.cloneNode(true);
			var td = tr.children;
			//チェックボックス追加
			const checkbox = document.createElement('input');
				checkbox.type = "checkbox";
				checkbox.dataset.next = 'COMPLETE';
				checkbox.className = 'transit';
				checkbox.value = todo.todoId;       
				checkbox.onchange=function(){sendDeleteForm(checkbox);};
			td[0].appendChild(checkbox);
			td[1].textContent = todo.todoId;
			td[2].textContent = todo.taskContent;
			td[2].className='taskContent';
			td[3].textContent = todo.deadline;
			td[3].className='deadline';
			const updBtn = document.createElement('button');
					updBtn.type = "button";
					updBtn.dataset.addeditflg = 'edit';
					updBtn.textContent='更新';   
					updBtn.onclick=function(){todoModalOperation(updBtn);};
			td[4].appendChild(updBtn)
			// customerTableBodyの中に流し込む
			fragment.appendChild(tr);
		});
		tbody.appendChild(fragment);
		
		// 完了済みタスク（セッション中に完了したもの）を追加で描画
		completedTodoIds.forEach(todoId => {
			// DBレスポンスには含まれないので、id しか持っていない場合は簡易的な行を生成
			const tr = document.createElement("tr");
			const a = tr.insertCell(-1);
			const todoIdArea = tr.insertCell(-1);
			todoIdArea.textContent = todoId;
			const compText = tr.insertCell(-1);
			compText.colSpan = 6;
			compText.textContent = '(完了済みタスク)';
			tr.classList.add("completed");
			tbody.appendChild(tr);
		});
	}
	else{
		var tbody= document.getElementById("customerTableBody");
		tbody.textContent = "";
		// templateのtr要素を取得
		var template = document.getElementById('customerTableTemplate').content.firstElementChild;
		const fragment = new DocumentFragment();
		list.forEach(customer => {
			// template要素の内容を複製
			var tr = template.cloneNode(true);
			var td = tr.children;
			//チェックボックス追加
			const checkbox = document.createElement('input');
				checkbox.type = "checkbox";
				checkbox.name = "selected";
				checkbox.value = customer.customerId;       
				checkbox.onchange=function(){onCheckedCus(checkbox);};
			td[0].appendChild(checkbox);
			td[1].textContent = customer.customerId;
			td[2].textContent = customer.groomName;
			td[3].textContent = customer.brideName;
			td[4].textContent = customer.mailAddress;
			td[5].textContent = customer.phoneNumber;
			const todoButton = document.createElement('button');
				todoButton.type = "button";
				todoButton.name = "selected";
				todoButton.className = "transit";
				todoButton.textContent="詳細";
				todoButton.dataset.customerid=customer.customerId;
				todoButton.value = customer.customerId;       
				todoButton.onclick=function(){sendGetForm(todoButton);};
			td[6].appendChild(todoButton)
			// customerTableBodyの中に流し込む
			fragment.appendChild(tr);
		});
		tbody.appendChild(fragment);
	}
}
/*送信顧客データ取得 */
function getRequestDataOfCustomer(button){
	const form = button.form
	let groomFirstName = form.querySelector('input[name="groomFirstName"]');
	groomFirstName = groomFirstName ? groomFirstName.value : '';
	let groomLastName = form.querySelector('input[name="groomLastName"]');
	groomLastName = groomLastName ? groomLastName.value : '';
	let brideFirstName = form.querySelector('input[name="brideFirstName"]');
	brideFirstName = brideFirstName ? brideFirstName.value : '';
	let brideLastName = form.querySelector('input[name="brideLastName"]');
	brideLastName = brideLastName ? brideLastName.value : '';
	let mailAddress = form.querySelector('input[name="mailAddress"]');
	mailAddress = mailAddress ? mailAddress.value : '';
	let space1 = '　';
	let space2 = '　';
	//first,lastNameが両方とも半角英字の場合真ん中は半角スペースで文字列結合、両方ない場合空白、それ以外全角スペースで結合
	if(groomFirstName.match(/^[A-Za-z0-9]*$/) && groomLastName.match(/^[A-Za-z0-9]*$/)){
		space1 = ' ';
	}
	else if (groomFirstName == "" && groomLastName == ""){
		space1 = '';
	}
	//first,lastNameが両方とも半角英字の場合真ん中は半角スペースで文字列結合、両方ない場合空白、それ以外全角スペースで結合
	if(brideFirstName.match(/^[A-Za-z0-9]*$/) && brideLastName.match(/^[A-Za-z0-9]*$/)){
		space2 = ' ';
	}
	else if (brideFirstName == "" && brideLastName == ""){
		space2 = '';
	}
	let requestData = 	{
		groomName:groomFirstName + space1 + groomLastName,
		brideName:brideFirstName + space2 + brideLastName,
		mailAddress:mailAddress,
		phoneNumber:Array.from(form.querySelectorAll('input[name="phoneNumber"]')).map(v=>v.value||"").join('-')
	};
	return requestData;
}
/*送信Todoデータ取得 */
function getRequestDataOfTodo(button){
	const form = button.form;
	let todoId = form.querySelector('input[name="todoId"]');
	todoId = todoId ? todoId.value : 0;
	let customerId = document.getElementById('customerId');
	customerId = customerId ? customerId.value : 0;
	let status = form.querySelector('input[name="status"]');
	status = status ? status.value : '';
	let taskContent = form.querySelector('input[name="taskContent"]');
	taskContent = taskContent ? taskContent.value : '';
	let deadline = form.querySelector('input[name="deadline"]');
	deadline = deadline ? deadline.value : '';
	let deleteFlag = form.querySelector('input[name="deleteFlag"]');
	deleteFlag = deleteFlag ? deleteFlag.value : '';
	let requestData = 	{
		todoId:todoId,
		customerId:customerId,
		status:status,
		taskContent:taskContent,
		deadline:deadline,
		deleteFlag:deleteFlag
		
	};
	return requestData;
}
/*Todoモーダルボタン処理 */
function todoModalOperation(button){
	// 要素を取得
	const form = button.form;
	var modal = document.getElementById("todoRegisterUpdateModal");
	renderModal(modal);
	
	const row = button.closest("tr");
	document.getElementById("todoIdEntry").style.display="none";
	const todoModalBtn = document.getElementById("todoModalBtn");
	todoModalBtn.textContent = "";
	var sessionUser = document.getElementById("sessionUser").value;
	
	if(button.dataset.addeditflg === "add"){
		form.querySelector('input[name="taskContent"]').value="";
		form.querySelector('input[name="deadline"]').value="";
		var addBtn = document.createElement('button');
			addBtn.type="button";
			addBtn.dataset.next="TODO_ADD";
			addBtn.onclick=()=>sendPostForm(addBtn);
			addBtn.textContent='追加';
			addBtn.className='transit'; 
		todoModalBtn.appendChild(addBtn);
	}
	else if(button.dataset.addeditflg === "edit"){
		document.getElementById("todoIdEntry").style.display="block";
		form.querySelector('input[name="todoId"]').value=row.querySelector('input[type="checkbox"]').value;
		form.querySelector('input[name="taskContent"]').value=row.querySelector(".taskContent").textContent;
		form.querySelector('input[name="deadline"]').value=row.querySelector(".deadline").textContent;
		var updBtn = document.createElement('button');
			updBtn.type="button";
			updBtn.onclick=()=>sendPutForm(updBtn);
			updBtn.textContent='更新';
			updBtn.className='transit'; 
		todoModalBtn.appendChild(updBtn);
	}
	
}
/*チェックボックス押下時の処理 */
function onCheckedCus(checkbox){
	const cbAll = checkbox.form.querySelectorAll('input[name="selected"]')
	if(checkbox.checked) {
		cbAll.forEach(cb=>{
			if (cb !== checkbox) {
				cb.disabled = true;
			}
			else if (cb === checkbox) {
				document.querySelector('input[name="selectCustomerId"]').value=cb.value;
			}
		});
	}else {
		cbAll.forEach(value=>value.disabled=false);
	}
}