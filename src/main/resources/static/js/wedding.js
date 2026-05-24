/**
* 式機能JS
*/
/*GET送信 */
function sendGetForm(button){
	const form = button.form
	const page=button.dataset.page ||document.getElementById("page").value;
	const sortKey = button.dataset.sort || '';
	//テキストボックスが3つの場合要改修
	let searchInput = [];
	searchInput = form.querySelectorAll('input[name="searchValue"]');
	let search='';
	if(button.id=="groom"){
		search=searchInput[0];
	}
	else if(button.id=="bride"){
		search=searchInput[1];
	}
	else if(button.id=="planner"){
		search=searchInput[2];
	}
	let searchValue = '';
	searchValue=(search ? search.value : '') + (search ? ',' : '') + (search ? search.dataset.role : '');
	const operation=button.dataset.next || '';
	let checkedId= form.querySelector('input[name="selected"]:checked');
	checkedId = checkedId ? checkedId.value : '';
	document.getElementById("checkedId").value = checkedId;
	const params = new URLSearchParams();
	if(page)params.append("page",page);
	if(sortKey)params.append("sort",sortKey);
	if(searchValue)params.append("search",searchValue);
	if(operation)params.append("operation",operation);
	if(checkedId)params.append("check",checkedId);
	fetch(`${contextPath}api/wedding?${params.toString()}`, {method: "GET"})
	.then(response => {
		if(!response.ok){
			return response.json().then(err=>{throw err;});
		}else{
			return response.json();
		}
	})
	.then(result => {
		document.querySelectorAll('input[name="searchValue"]').forEach(input=>{input.value=""});
		let selectBox = document.getElementById("selectbox");
		selectBox.textContent='';
		let pageNum=result.pageInfo.pageNum;
		document.getElementById("page").value=pageNum;
		let totalPages=result.pageInfo.totalPages;
		document.getElementById("totalPages").value=totalPages;
		
		const nextForm = result.nextForm;
		let weddingList = result.pageInfo.list;
		renderTable(weddingList);
		renderPagenation(pageNum,totalPages,form);
		var modal = "";
		if(nextForm=== 'weddingUpdate'){	
			// 要素を取得
			modal = document.getElementById("wedUpdateModal");
			renderModal(modal);
			
			//ここでフェッチして会場名リストを取得
			fetch(`${contextPath}api/wedding/venue`, {method: "GET"})
			.then(response => {
				if(!response.ok){
					return response.json().then(err=>{throw err;});
				}else{
					return response.json();
				}
			})
			.then(result => {
				let venueNameList = result.pageInfo.list;
				venueNameList.forEach(venname=>{
					let opt = document.createElement('option');
						opt.value=venname;
						opt.textContent=venname;
					selectBox.appendChild(opt);
				});
			})
			.catch(error => {
				console.log(error);
				document.getElementById("error").innerText = error[0].errorMessage;
				return;
			});	
		}
		if(nextForm=== 'weddingDelete'){
			modal = document.getElementById("wedDeleteModal");
			renderModal(modal);
		}
	})
	.catch(error => {
		console.log(error);
		document.getElementById("error").innerText = error[0].errorMessage;
	});
}

/*PUT送信 */
function sendPutForm(button){
	const form = button.form;
	const id = document.getElementById("checkedId").value;
	let weddingDate = form.querySelector('input[name="weddingDate"]');
	weddingDate = weddingDate ? weddingDate.value : '';
	let venueName = document.getElementById("selectbox");
	venueName = venueName ? venueName.value : '';
	let requestData = 	{
		weddingDate:weddingDate,
		venueName:venueName
	};
	fetch(`${contextPath}api/wedding/${id}`, {
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
		document.getElementById("wedUpdateModal").style.display = "none";
		document.getElementById("error").textContent = "";
		
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
				if(err.code=='weddingDateValid'){
					document.getElementById("weddingDateValid").textContent=err.errorMessage
				}
			})
		}	
	});
}

/*DELETE送信 */
function sendDeleteForm(button){
	const id = document.getElementById("checkedId").value;
	fetch(`${contextPath}api/wedding/${id}`, {method: "DELETE"})
	.then(response => {
		if(response.status == 204){
			document.getElementById("error").innerText = "";
			sendGetForm(button);
					
			document.getElementById("wedDeleteModal").style.display = "none";
		}else{
			const error = response.json();
			document.getElementById("error").innerText = error[0].errorMessage;
		}
	})
	.catch(error => console.error("エラー:", error));
}

/*テーブル描画機能 */
function renderTable(weddingList) {
	var tbody= document.getElementById("weddingTableBody");
	tbody.textContent = "";
	// templateのtr要素を取得
	var template = document.getElementById('weddingTableTemplate').content.firstElementChild;
	const fragment = new DocumentFragment();
	weddingList.forEach(wedding => {
		// template要素の内容を複製
		var tr = template.cloneNode(true);
		var td = tr.children;
		//チェックボックス追加
		const checkbox = document.createElement('input');
		checkbox.type = "checkbox";
		checkbox.name = "selected";
		checkbox.value = wedding.weddingId;       
		checkbox.onchange=function(){onCheckedWed(checkbox);};
		td[0].appendChild(checkbox);
		td[1].textContent = wedding.weddingId;
		td[2].textContent = wedding.groomName;
		td[3].textContent = wedding.brideName;
		td[4].textContent = wedding.plannerName;
		td[5].textContent = wedding.weddingDate;
		td[6].textContent = wedding.venueName;
		// weddingTableBodyの中に流し込む
		fragment.appendChild(tr);
	});
	tbody.appendChild(fragment);
}

/*チェックボックス押下時の処理 */
function onCheckedWed(checkbox){
	const cbAll = checkbox.form.querySelectorAll('input[name="selected"]')
	if(checkbox.checked) {
		cbAll.forEach(cb=>{
			if (cb !== checkbox) {
			    cb.disabled = true;
			}
			else{
				document.querySelector('input[name="selectWeddingId"]').value=cb.value;
			}
		});
	}
	else {
		cbAll.forEach(cb=>cb.disabled=false);
	}
}
