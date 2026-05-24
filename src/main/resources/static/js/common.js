/**
* 共通機能JS
*/
/*ページネーション描画機能 */
function renderPagenation(pageNum,totalPages,form){
	let formId = form.id;
	const prevPage = form.querySelector('.prev-page');
	if(prevPage != null)prevPage.textContent='';
	const currentPage = form.querySelector('.current-page');
	if(currentPage != null)currentPage.textContent='';
	const nextPage = form.querySelector('.next-page');
	if(nextPage != null)nextPage.textContent='';
	
	if(pageNum > 1){
		const prevBtn = document.createElement('button');
		prevBtn.type="button";
		prevBtn.dataset.page=pageNum - 1;
		if(formId=='todoForm'){
			prevBtn.className='transit';
		}
		prevBtn.onclick=function(){sendGetForm(prevBtn);};
		prevBtn.textContent="前のページ";
		prevPage.appendChild(prevBtn);
	}
	for (let i = 1; i <= totalPages; i++) {
		const curbtn = document.createElement('button');
		curbtn.type="button";
		curbtn.dataset.page=i;
		if(formId=='todoForm'){
			curbtn.className='transit';
		}
		curbtn.onclick=function(){sendGetForm(curbtn);};
		curbtn.textContent=i;
		//iのみ赤にしたい
		if(i==pageNum){
			curbtn.style.color="red";
			curbtn.style.fontWeight="bold";
		}
		currentPage.appendChild(curbtn);
	}
	
	if(pageNum < totalPages){
		const nextBtn = document.createElement('button');
		nextBtn.type="button";
		nextBtn.dataset.page=pageNum + 1;
		if(formId=='todoForm'){
			nextBtn.className='transit';
		}
		nextBtn.onclick=function(){sendGetForm(nextBtn);};
		nextBtn.textContent="次のページ";
		nextPage.appendChild(nextBtn);
	}
}
/*モーダル描画機能 */
function renderModal(modal){
	var span = document.getElementsByClassName("close")[0];
	modal.style.display = "block";
	// <span> (x) をクリックするとモーダルを閉じる
	span.onclick = function() {
		modal.style.display = "none";
	}
	// モーダルの外側をクリックすると閉じる
	window.onclick = function(event) {
		if (event.target == modal) {
			 modal.style.display = "none";
		}
	}
}
/*csvダウンロード機能 */
function sendGetCSVForm(button){
	var action=button.dataset.next || '';
	const url = `${contextPath}csv/`+action.toLowerCase();
	window.location.href = url;
	alert('CSVを出力しました');
}
/*csvインポート機能のうちアップロード処理 */
function sendPostCSVForm(button){
	const form=button.form;
	//送信データオブジェ作成
	var action=button.dataset.next || '';
	const url = `${contextPath}csv/`+action.toLowerCase();
	const formData=new FormData();
	formData.append("csvfile",form.querySelector('input[name="csvfile"]').files[0]);
	// JSONに変換して送信
	fetch(url, {
		method: "POST",
		body: formData
	})
	.then(response => {
		if(!response.ok){
			return response.json().then(err=>{throw err;});
		}else{
			return response.json();
		}
	})
	.then(result => {
		let csvUploadLim=result.csvUploadLim;
		let csvRegisterArea=document.getElementById("csvDataRegister");
		csvRegisterArea.textContent="";
		if(csvUploadLim > 0){
			var br = document.createElement('br');
			csvRegisterArea.appendChild(br);
			const textNode = document.createTextNode('更新数：'+csvUploadLim);
			csvRegisterArea.appendChild(textNode);
			csvRegisterArea.appendChild(br);
			var regBtn = document.createElement('button');
				regBtn.type="button";
				regBtn.dataset.next="CSV_IMPORT";
				regBtn.onclick=()=>sendPostForm(regBtn);
				regBtn.textContent="登録";
			csvRegisterArea.appendChild(regBtn);
		}
	})
	.catch(error => {
		console.log(error);
		document.getElementById("error").innerText = error[0].errorMessage;
	});
}

/*チェックボックス押下時の処理 */
function onChecked(checkbox){
	const cb = checkbox.form.querySelectorAll('input[name="selected"]');
	if(checkbox.checked) {
		cb.forEach(value=>{
			if (value !== checkbox) {
			    value.disabled = true;
		}});
	} else {
		cb.forEach(value=>value.disabled=false);
	}
}
