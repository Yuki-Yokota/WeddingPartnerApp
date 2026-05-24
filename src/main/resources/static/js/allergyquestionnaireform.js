/**
* アレルギーアンケートフォーム
*/
/*POST送信 */
function sendPostForm(button){
	let guestId = document.getElementById("guestId").value || '';
	let requestData = getRequestData(button,guestId);
	fetch(`${contextPath}questionnaire`, {
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
		document.getElementById("error").innerText = "";
		document.getElementById("complete").style.display="block";
		document.getElementById("questionnaire").style.display="none";
	})
	.catch(error => {
		console.log(error);
		document.getElementById("error").innerText = error[0].errorMessage;
	});
}
/*送信データ取得 */
function getRequestData(button,guestId){
	const form = button.form
	//アレルギー情報
	let allergyName=[];
	//チェックボックスの値、その他の値をアレルギー名リストに格納
	allergyName=getAllergyName(form,allergyName);
	var precaution = form.querySelector('textarea[name="precaution"]');
	precaution = precaution ? precaution.value : '';
	let requestData = 	{
		guestId:guestId,
		precaution:precaution,
		allergyInfo:allergyName.map(name=>{
			return{
				allergyName:name
			};
		})
	};
	return requestData;
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
