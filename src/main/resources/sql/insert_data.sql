--初回挿入のため自動採番Id属性は挿入対象外属性
--ユーザ
INSERT INTO users (user_name,mail_address,password,role) VALUES ('Yuki Yokota','a@g.com','hanarose0','admin');
INSERT INTO users (user_name,mail_address,password,role) VALUES ('Yasuda','yasuda@outlook.jp','bluerose0','user');
INSERT INTO users (user_name,mail_address,password,role) VALUES ('Ariyoshi','ariyoshi@outline.co.jp','blackrose0','user');
--顧客
INSERT INTO customers (user_id,groom_name,bride_name,mail_address,phone_number) VALUES (2,'山田 明','王子 京香','hulu@g.jp','090-1234-5678');
INSERT INTO customers (user_id,groom_name,bride_name,mail_address,phone_number) VALUES (2,'Alex Maxia','源 斐子','huaway@g.jp','090-1233-3511');
INSERT INTO customers (user_id,groom_name,bride_name,mail_address,phone_number) VALUES (3,'軍人 ロックスター','安田 和博','sandori@jfn.jp','080-9999-1239');
--ToDo
INSERT INTO todos (customer_id,status,task_content,deadline,delete_flag) VALUES(1,TRUE,'契約締結','2026-01-01',FALSE);
INSERT INTO todos (customer_id,status,task_content,deadline,delete_flag) VALUES(1,TRUE,'1回目打合せ','2025-09-01',FALSE);
INSERT INTO todos (customer_id,status,task_content,deadline,delete_flag) VALUES(2,TRUE,'FOD解約','2026-01-05',FALSE);
--式
INSERT INTO weddings (customer_id,venue_id,wedding_date,completed_flag) VALUES(1,1,'2026-06-01',FALSE);
INSERT INTO weddings (customer_id,venue_id,wedding_date,completed_flag) VALUES(2,1,'2026-08-01',FALSE);
INSERT INTO weddings (customer_id,venue_id,wedding_date,completed_flag) VALUES(3,2,'2026-09-01',FALSE);
--ゲスト
INSERT INTO guests (wedding_id,guest_name,mail_address,precaution,attendance,ans_flag,delete_flag) VALUES(1,'高宮　慈英','taka@gmail.com','',TRUE,TRUE,FALSE);
INSERT INTO guests (wedding_id,guest_name,mail_address,precaution,attendance,ans_flag,delete_flag) VALUES(1,'観音寺 瑛敬','kan@gmail.com','生ものNG。全て加熱希望',TRUE,TRUE,FALSE);
INSERT INTO guests (wedding_id,guest_name,mail_address,precaution,attendance,ans_flag,delete_flag) VALUES(1,'九条　実','kujo@gmail.com','',TRUE,FALSE,FALSE);
--アレルギー(食物アレルギー表示対象品目)
INSERT INTO allergies (allergy_name) VALUES('卵');
INSERT INTO allergies (allergy_name) VALUES('小麦');
INSERT INTO allergies (allergy_name) VALUES('くるみ');
INSERT INTO allergies (allergy_name) VALUES('乳');
INSERT INTO allergies (allergy_name) VALUES('蕎麦');
INSERT INTO allergies (allergy_name) VALUES('海老');
INSERT INTO allergies (allergy_name) VALUES('蟹');
INSERT INTO allergies (allergy_name) VALUES('落花生');
INSERT INTO allergies (allergy_name) VALUES('アーモンド');
INSERT INTO allergies (allergy_name) VALUES('アワビ');
INSERT INTO allergies (allergy_name) VALUES('いか');
INSERT INTO allergies (allergy_name) VALUES('オレンジ');
INSERT INTO allergies (allergy_name) VALUES('カシューナッツ');
INSERT INTO allergies (allergy_name) VALUES('キウイフルーツ');
INSERT INTO allergies (allergy_name) VALUES('牛肉');
INSERT INTO allergies (allergy_name) VALUES('ごま');
INSERT INTO allergies (allergy_name) VALUES('さけ');
INSERT INTO allergies (allergy_name) VALUES('さば');
INSERT INTO allergies (allergy_name) VALUES('大豆');
INSERT INTO allergies (allergy_name) VALUES('鶏肉');
INSERT INTO allergies (allergy_name) VALUES('バナナ');
INSERT INTO allergies (allergy_name) VALUES('豚肉');
INSERT INTO allergies (allergy_name) VALUES('マカダミアナッツ');
INSERT INTO allergies (allergy_name) VALUES('もも');
INSERT INTO allergies (allergy_name) VALUES('やまいも');
INSERT INTO allergies (allergy_name) VALUES('りんご');
INSERT INTO allergies (allergy_name) VALUES('ゼラチン');
--ゲスト_アレルギー
INSERT INTO guests_allergies (guest_id,allergy_id) VALUES(1,1);
INSERT INTO guests_allergies (guest_id,allergy_id) VALUES(2,3);
INSERT INTO guests_allergies (guest_id,allergy_id) VALUES(2,5);
INSERT INTO guests_allergies (guest_id,allergy_id) VALUES(2,6);
INSERT INTO guests_allergies (guest_id,allergy_id) VALUES(2,7);
INSERT INTO guests_allergies (guest_id,allergy_id) VALUES(2,9);
INSERT INTO guests_allergies (guest_id,allergy_id) VALUES(2,10);
INSERT INTO guests_allergies (guest_id,allergy_id) VALUES(2,11);
--会場
INSERT INTO venues (venue_name,phone_number,venue_amount) VALUES ('広島会場','090-1234-5678',400000);
INSERT INTO venues (venue_name,phone_number,venue_amount) VALUES ('松山会場','090-5563-9999',300000);
INSERT INTO venues (venue_name,phone_number,venue_amount) VALUES ('高松会場','080-2241-1299',350000);
INSERT INTO venues (venue_name,phone_number,venue_amount) VALUES ('小倉会場','070-512-3491',400000);
--式タイプ
INSERT INTO wedding_types (wedding_type_name,wedding_type_amount) VALUES ('神前式',50000);
INSERT INTO wedding_types (wedding_type_name,wedding_type_amount) VALUES ('人前式',100000);
INSERT INTO wedding_types (wedding_type_name,wedding_type_amount) VALUES ('教会式',120000);
--会場-式タイプ
INSERT INTO venues_wedding_types (venue_id,wedding_type_id) VALUES (1,2);
INSERT INTO venues_wedding_types (venue_id,wedding_type_id) VALUES (1,3);
INSERT INTO venues_wedding_types (venue_id,wedding_type_id) VALUES (2,1);
INSERT INTO venues_wedding_types (venue_id,wedding_type_id) VALUES (3,2);
INSERT INTO venues_wedding_types (venue_id,wedding_type_id) VALUES (4,1);
INSERT INTO venues_wedding_types (venue_id,wedding_type_id) VALUES (4,2);
--料理
INSERT INTO dishes (dish_amount) VALUES(6000);
INSERT INTO dishes (dish_amount) VALUES(8000);
INSERT INTO dishes (dish_amount) VALUES(10000);
--ドリンク
INSERT INTO drinks (drink_amount) VALUES(3500);
INSERT INTO drinks (drink_amount) VALUES(4500);
INSERT INTO drinks (drink_amount) VALUES(5000);
--新郎ドレス
INSERT INTO groom_dresses (groom_dress_amount) VALUES(100000);
INSERT INTO groom_dresses (groom_dress_amount) VALUES(150000);
INSERT INTO groom_dresses (groom_dress_amount) VALUES(200000);
--新婦ドレス
INSERT INTO bride_dresses (bride_dress_amount) VALUES(200000);
INSERT INTO bride_dresses (bride_dress_amount) VALUES(250000);
INSERT INTO bride_dresses (bride_dress_amount) VALUES(300000);
INSERT INTO bride_dresses (bride_dress_amount) VALUES(350000);
INSERT INTO bride_dresses (bride_dress_amount) VALUES(400000);
--オプション金額
INSERT INTO option_amount (photoshoot_amount,videoshoot_amount,flower_arrangement_amount,gift_amount,
			acoustic_amount,hair_makeup_amount,moderator_amount,cake_amount) VALUES(
				180000,200000,100000,6000,120000,60000,70000,35000
			)
--契約
INSERT INTO contracts (customer_id,numofperson,venue_name,wedding_type,wedding_date,dish,drink,groom_dress,bride_dress,photoshoot_flg,
			videoshoot_flg,flower_arrangement_flg,
			gift_flg,acoustic_flg,hair_makeup_flg,moderator_flg,other_text,total_amount,completed_flg) VALUES(
				1,60,'広島会場','人前式','2026-08-01',6000,3500,100000,300000,false,false,false,true,true,true,true,'',2600000,false
			);
