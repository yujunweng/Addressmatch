import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

import java.util.Scanner;
public class Addressmatch_m{
	public static void main(String[] args) {
	// 設定變數
	String address ;
	String ADDR_TOWN ="" ;
	String ADDR_VILL ="";
	String OTH  ="";
	String ADDR_LIN = "";
	String ADDR_ROAD = "";
	
	String ADDR_SEC = "";
	String ADDR_LANE = "";	
	String ADDR_ALLEY = "";
	String ADDR_NO = "";
	String ADDR_FLOOR = "";
	int TOWN_index = 0 ;
	int VILL_index = 0 ;
	int LIN_index = 0 ;
	int ROAD_index = 0;
	int SEC_index = 0;
	int LANE_index = 0;
	int ALLEY_index = 0;
	int NO_index = 0;
	int FLOOR_index = 0;

	//設定字串：連結 access 

	
	try {
	   Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
	   Connection con = DriverManager.getConnection("jdbc:ucanaccess://d:/Database1.accdb"); 
	   if (null == con) {
		   System.out.println("Unable to connect to data source ");
		   return;
	   }

	System.out.println( ": Successfully connected to database. Data source name:\n  " + con.getMetaData().getURL());

   	// 設定 資料庫連結字串變數
   	Statement stmt = con.createStatement();
	Statement stmt2 = con.createStatement();	

	//提供輸入畫面，以判斷挑檔類別
	Scanner scan = new Scanner(System.in);
	System.out.println( "請輸入挑檔的類別 1.戶政廢止門牌 2.醫療機構名冊 3.工廠廢止名冊: ");	
	int type = scan.nextInt();
	System.out.println(type);
			

	//先清空欲匯出的資料表。
	String query = "";

	if ( type == 1 ){
	query = " delete from export_YHQ_data";

	}
	else if (type == 2){
	query = " delete from export_Hospital_data";
	}
	else{
	query = " delete from export_Factory_data";	
	}
	stmt.execute(query);
	
	//取得連正式機的id/pw

	System.out.println( "請輸入資料庫帳號:");	
	String id = scan.next();
	System.out.println( "請輸入密碼:");	
	String pw = scan.next();

	System.out.println( "帳號密碼為：" + id + "/" +pw );	
	

	//執行查詢SQL，並連結 oracle 連線。

	if ( type == 1 ){
	query = "SELECT  * FROM YHQ ;";
	}
	else if ( type == 2){
	query = "SELECT  * FROM Hospital ;";
	}
	else {
	query = "SELECT  * FROM Factory ;";	
	}
	
	System.out.println( ": SQL query:\n " + query);
	//連結 oracle 
	DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
	Connection con2 =  DriverManager.getConnection("jdbc:oracle:thin:@10.38.110.55:1521/xe", id , pw); 
	
	
	Statement stmt3 = con2.createStatement();
	Statement stmt4 = con2.createStatement();

	//讀取戶役政註銷門牌資料
   	stmt.execute(query); 
   	ResultSet rs = stmt.getResultSet(); 

   	if (rs != null) {
   		System.out.println(": 資料表的schema : ");
   		ResultSetMetaData rsmd = rs.getMetaData();
   		for (int i=1; i <= rsmd.getColumnCount(); i++) {
		System.out.print(" | " + rsmd.getColumnName(i));
		}

		System.out.println("\n" + ": 資料列如下: ");
   		int rowCount = 0;
		String sql_tmp = "";
	//逐一讀取 access 的資料，進行地址正規化，然後與房屋稅資料表進行比對。
   		while (rs.next()) {
			sql_tmp = "";
			if ( type == 1 ){
				address = rs.getString(1);

				sql_tmp = "'" + address ; 
				sql_tmp = sql_tmp + "','" + rs.getString(2) + "','" + rs.getString(3) ;
				String aa = rs.getString(4);				
				address = address + aa;
				sql_tmp = sql_tmp + "','" + aa + "','" + rs.getString(5) + "','" + rs.getString(6) + "'";
				
			}
			else if ( type == 2){
				address = rs.getString(7);
				sql_tmp = "'" + rs.getString(1) + "','" + rs.getString(2) + "','" + rs.getString(3) + "','" + rs.getString(4) + "','" + rs.getString(5) + "','" + rs.getString(6) + "','" + address + "'";
				System.out.println(sql_tmp );
			}
			else{ 
				address = rs.getString(1);
				sql_tmp = "'" + address ; 
				sql_tmp = sql_tmp + "','" + rs.getString(2) + "','" + rs.getString(3) ;
				String aa = rs.getString(4);				
				address = address + aa;
				sql_tmp = sql_tmp + "','" + aa + "','" + rs.getString(5) + "','" + rs.getString(6) + "'";
			}
			System.out.println(address );
			
			//分解地址
			OTH = address;
			//找出區
			TOWN_index = 0 ;
			ADDR_TOWN = "";
			if (OTH.indexOf("區") >=0){
				TOWN_index = OTH.indexOf("區") +1 ;			
			}
			if (TOWN_index >0){
				ADDR_TOWN = OTH.substring(0,TOWN_index);		
				OTH=OTH.substring(TOWN_index);
			}

			//找出里
			VILL_index = 0 ;
			ADDR_VILL = "";
			if (OTH.indexOf("里") >=0){
				VILL_index = OTH.indexOf("里") +1 ;			
			}
			if (VILL_index >0){
				ADDR_VILL = OTH.substring(0,VILL_index);		
				OTH=OTH.substring(VILL_index);
			}


			//找出鄰
			LIN_index = 0 ;
			ADDR_LIN = "";
			if (OTH.indexOf("鄰") >=0){
				LIN_index = OTH.indexOf("鄰") +1 ;			
			}
			if (LIN_index >0){
				ADDR_LIN = OTH.substring(0,LIN_index);		
				OTH=OTH.substring(LIN_index);
				ADDR_LIN = half_replace(ADDR_LIN);
			}

			//找出街路
			ROAD_index = 0 ;
			ADDR_ROAD = "";
			if (OTH.indexOf("街") >= 0 ){
				ROAD_index = OTH.indexOf("街") +1 ;
			}
			if (OTH.indexOf("路") >=0){
				ROAD_index = OTH.indexOf("路") +1 ;			
			}
			if (ROAD_index >0){
			ADDR_ROAD = OTH.substring(0,ROAD_index);		
			OTH=OTH.substring(ROAD_index);
			}

			//找出段
			SEC_index = 0;
			ADDR_SEC = "";
			if (OTH.indexOf("段") >=0){
				SEC_index = OTH.indexOf("段") +1 ;			
			}
			if (SEC_index >0){
				ADDR_SEC = OTH.substring(0,SEC_index);		
				OTH=OTH.substring(SEC_index);
			}


			//找出巷
			LANE_index = 0 ;
			ADDR_LANE = "";
			if (OTH.indexOf("巷") >=0){
				LANE_index = OTH.indexOf("巷") +1 ;			
			}
			if (LANE_index >0){
				ADDR_LANE = OTH.substring(0,LANE_index);		
				OTH=OTH.substring(LANE_index);
				ADDR_LANE = half_replace(ADDR_LANE);
			}


			//找出弄
			ALLEY_index = 0 ;
			ADDR_ALLEY = "";
			if (OTH.indexOf("弄") >=0){
				ALLEY_index = OTH.indexOf("弄") +1 ;			
			}
			if (ALLEY_index >0){
				ADDR_ALLEY = OTH.substring(0,ALLEY_index);		
				OTH=OTH.substring(ALLEY_index);
				ADDR_ALLEY = half_replace(ADDR_ALLEY);
			}


			//找出號
			NO_index = 0 ;
			ADDR_NO = "";
			if (OTH.indexOf("號") >=0){
				NO_index = OTH.indexOf("號") +1 ;			
			}
			if (NO_index >0){
				ADDR_NO = OTH.substring(0,NO_index);		
				OTH=OTH.substring(NO_index);
				ADDR_NO = half_replace(ADDR_NO);
				ADDR_NO = ADDR_NO.replace('-' ,'－');
			}
		
			//找出樓
			FLOOR_index = 0 ;
			ADDR_FLOOR = "";
			if (OTH.indexOf("樓") >=0){
				FLOOR_index = OTH.indexOf("樓") +1 ;			
			}
			if (FLOOR_index >0){
				ADDR_FLOOR = OTH.substring(0,FLOOR_index);		
				OTH=OTH.substring(FLOOR_index);
				ADDR_FLOOR = half_replace(ADDR_FLOOR);	
				System.out.println("判斷樓之 addr_floor 前：" + ADDR_FLOOR);
				if (ADDR_FLOOR.equals("１樓") ) ADDR_FLOOR = "";
				System.out.println("判斷樓之 addr_floor 後：" + ADDR_FLOOR);
			}
			OTH = half_replace(OTH);			
			System.out.println(ADDR_TOWN + "||" + ADDR_VILL + "||" + ADDR_LIN + "||" + ADDR_ROAD + "||" + ADDR_SEC + "||" + ADDR_LANE + "||" + ADDR_ALLEY + "||" + ADDR_NO + "||" + ADDR_FLOOR + "||"+ OTH);
		
		
			//讀取YRXT103 取得稅號，及YRXT102確認是否註銷。
			if ( type == 1 ){
				System.out.println(sql_tmp + ADDR_TOWN + ADDR_ROAD + ADDR_SEC +  ADDR_LANE + ADDR_ALLEY + ADDR_NO + ADDR_FLOOR + OTH);		
				checkhou	 (sql_tmp , ADDR_TOWN , ADDR_VILL , ADDR_LIN , ADDR_ROAD , ADDR_SEC ,  ADDR_LANE , ADDR_ALLEY , ADDR_NO , ADDR_FLOOR , OTH ,  stmt2 , stmt3 , stmt4);
			}
			else if (type == 2){
				System.out.println(sql_tmp + ADDR_TOWN + ADDR_ROAD + ADDR_SEC +  ADDR_LANE + ADDR_ALLEY + ADDR_NO + ADDR_FLOOR + OTH);		
				checkHosiptal(sql_tmp , ADDR_TOWN , ADDR_ROAD , ADDR_SEC , ADDR_LANE , ADDR_ALLEY , ADDR_NO , ADDR_FLOOR , OTH ,  stmt2 , stmt3 , stmt4);
			}
			else{
				System.out.println(sql_tmp + ADDR_TOWN + ADDR_ROAD + ADDR_SEC +  ADDR_LANE + ADDR_ALLEY + ADDR_NO + ADDR_FLOOR + OTH);		
				checkFactory(sql_tmp , ADDR_TOWN , ADDR_ROAD , ADDR_SEC , ADDR_LANE , ADDR_ALLEY , ADDR_NO , ADDR_FLOOR , OTH ,  stmt2 , stmt3 , stmt4);
			}
   			rowCount++;
       		}
	   	System.out.println(": Total Row Count: " + rowCount);

   	}
	
	stmt4.close();   	
	stmt3.close();
	stmt2.close(); 
	stmt.close(); 
	con.close(); 
	con2.close(); 
	
	} catch (Exception err) {
	   System.out.println(": Exception: " + err.getMessage());
	} finally {
	   System.out.println(": Cleanup. Done.");
	}
	}

//鄰別數字半型轉換全型
	public static String half_replace(String ADDR){
		String return_string = ADDR;
		return_string = return_string.replace('0' ,'０');
		return_string = return_string.replace('1' ,'１');
		return_string = return_string.replace('2' ,'２');
		return_string = return_string.replace('3' ,'３');
		return_string = return_string.replace('4' ,'４');
		return_string = return_string.replace('5' ,'５');
		return_string = return_string.replace('6' ,'６');
		return_string = return_string.replace('7' ,'７');
		return_string = return_string.replace('8' ,'８');
		return_string = return_string.replace('9' ,'９');
		
		return_string = return_string.replace("五十" , "５０");
		return_string = return_string.replace("四十九" , "４９");
		return_string = return_string.replace("四十八" , "４８");
		return_string = return_string.replace("四十七" , "４７");
		return_string = return_string.replace("四十六" , "４６");
		return_string = return_string.replace("四十五" , "４５");
		return_string = return_string.replace("四十四" , "４４");
		return_string = return_string.replace("四十三" , "４３");
		return_string = return_string.replace("四十二" , "４２");
		return_string = return_string.replace("四十一" , "４１");
		return_string = return_string.replace("四十" , "４０");
		return_string = return_string.replace("三十九" , "３９");
		return_string = return_string.replace("三十八" , "３８");
		return_string = return_string.replace("三十七" , "３７");
		return_string = return_string.replace("三十六" , "３６");
		return_string = return_string.replace("三十五" , "３５");
		return_string = return_string.replace("三十四" , "３４");
		return_string = return_string.replace("三十三" , "３３");
		return_string = return_string.replace("三十二" , "３２");
		return_string = return_string.replace("三十一" , "３１");
		return_string = return_string.replace("三十" , "３０");
		return_string = return_string.replace("二十九" , "２９");
		return_string = return_string.replace("二十八" , "２８");
		return_string = return_string.replace("二十七" , "２７");
		return_string = return_string.replace("二十六" , "２６");
		return_string = return_string.replace("二十五" , "２５");
		return_string = return_string.replace("二十四" , "２４");
		return_string = return_string.replace("二十三" , "２３");
		return_string = return_string.replace("二十二" , "２２");
		return_string = return_string.replace("二十一" , "２１");
		return_string = return_string.replace("二十" , "２０");
		return_string = return_string.replace("十九" , "１９");
		return_string = return_string.replace("十八" , "１８");
		return_string = return_string.replace("十七" , "１７");
		return_string = return_string.replace("十六" , "１６");
		return_string = return_string.replace("十五" , "１５");
		return_string = return_string.replace("十四" , "１４");
		return_string = return_string.replace("十三" , "１３");
		return_string = return_string.replace("十二" , "１２");
		return_string = return_string.replace("十一" , "１１");
		return_string = return_string.replace("十" , "１０");
		return_string = return_string.replace("九" , "９");
		return_string = return_string.replace("八" , "８");
		return_string = return_string.replace("七" , "７");
		return_string = return_string.replace("六" , "６");
		return_string = return_string.replace("五" , "５");
		return_string = return_string.replace("四" , "４");
		return_string = return_string.replace("三" , "３");
		return_string = return_string.replace("二" , "２");
		return_string = return_string.replace("一" , "１");
		return_string = return_string.replace("壹" , "１");
		return_string = return_string.replace("之" , "－");
		return return_string;
	}

	public static void checkhou(String data , String ADDR_TOWN , String ADDR_VILL , String LIN ,  String ROAD , String SEC , String LANE , String ALLEY , String NO , String FLOOR , String OTH , Statement stmt2 , Statement stmt3 , Statement stmt4){
		try{
		//讀取HOUT103 坐落檔，取得稅號。
		String query1 = "SELECT \n  /*APP_NO:1081138 SYS_CD:HOU ACC_WITH:依工廠門牌廢止電子檔勾稽本處房屋稅坐落資料檔*/ distinct LOCAT_ADDR_HSN , LOCAT_ADDR_TOWN , LOCAT_ADDR_VILL , LOCAT_ADDR_LIN , LOCAT_ADDR_ROAD , LOCAT_ADDR_SEC , LOCAT_ADDR_OTH , a.HOU_LOSN  , c.SERV_AREA_CD FROM E77A.HOUT103 a , E77A.HOUT110 b , E77A.HOUC040 c WHERE b.HSN_CD = 'E' and a.hou_LOSN = b.HOU_LOSN  and c.town_cd || c.vill_cd = substr(a.HOU_LOSN,0,4) and c.serv_area_mk = 'Y' and  LOCAT_ADDR_HSN || LOCAT_ADDR_TOWN = '" + ADDR_TOWN ;

		if (!ADDR_VILL.equals(""))  query1 = query1  + "' and LOCAT_ADDR_VILL= '" + ADDR_VILL;
		if (!ROAD.equals("")) query1 = query1 + "' and LOCAT_ADDR_ROAD = '" + ROAD;
		if (!LANE.equals("") || !ALLEY.equals("") || !NO.equals("") || !FLOOR.equals("") || !OTH.equals("") ) query1 = query1  + "' and  LOCAT_ADDR_OTH = '" + LANE  + ALLEY +  NO +  FLOOR + OTH;

		query1 = query1 + "'";
		System.out.println( ": SQL query1:\n " + query1);
		stmt3.execute(query1);
		ResultSet rs2 = stmt3.getResultSet(); 		
		String query2 = "";
		int rowCount2 = 0;
		if (rs2 != null) {
			System.out.println(": match schema info for the given result set: ");
   			ResultSetMetaData rsmd2 = rs2.getMetaData();
  	 		for (int i=1; i <= rsmd2.getColumnCount(); i++) {
				System.out.print(" | " + rsmd2.getColumnName(i));
			}
			System.out.println("\n" + ": Fetch the actual data: ");
	   		
	   		while (rs2.next()) {
   			
				query2 = "INSERT into export_YHQ_data(行政區域名稱, 編釘日期 , 異動日期 , 變更前地址 , 變更後地址 , 編釘類別 , 房屋稅稅籍編號 , 是否註銷 , 管區員編 ) VALUES(" + data ;
				String tmp = rs2.getString(8);
				
				query2 = query2 + ", '" + tmp + "', '" + check_YN(tmp ,stmt4) + "' , '" + rs2.getString(9) +"')";
				System.out.println("query2 = " + query2 );
				stmt2.execute(query2);
				
			System.out.println("");
			
   			rowCount2++;
       			}
		if (rowCount2 == 0 ) {
			query2 = "INSERT into  export_YHQ_data(行政區域名稱 , 編釘日期 , 異動日期 , 變更前地址 , 變更後地址 , 編釘類別 , 房屋稅稅籍編號 , 是否註銷 , 管區員編) VALUES(" + data + ", '' ,'' ,'') ";
			System.out.println( ": SQL query2:\n " + query2);

			stmt2.execute(query2);
		}
   		System.out.println(": Check Row Count: " + rowCount2);

		}
	
	}catch (Exception err) {
	   System.out.println(": Exception checkhou : " + err.getMessage());
	} finally {
	   System.out.println(": Cleanup. Done.");
	}
  }

	//挑檔醫療機構
	public static void checkHosiptal(String data , String ADDR_TOWN ,  String ROAD , String SEC , String LANE , String ALLEY , String NO , String FLOOR , String OTH , Statement stmt2 , Statement stmt3 , Statement stmt4){
		try{
		//讀取HOUT103 坐落檔，取得稅號。
		String query1 = "SELECT \n  /*APP_NO:1081138 SYS_CD:HOU ACC_WITH:依工廠門牌廢止電子檔勾稽本處房屋稅坐落資料檔*/ distinct LOCAT_ADDR_HSN , LOCAT_ADDR_TOWN , LOCAT_ADDR_VILL , LOCAT_ADDR_LIN , LOCAT_ADDR_ROAD , LOCAT_ADDR_SEC , LOCAT_ADDR_OTH ,  a.HOU_LOSN , c.serv_area_cd  FROM E77A.HOUT103 a , E77A.HOUT110 b , E77A.HOUC040 c WHERE b.HSN_CD = 'E' and a.hou_LOSN = b.HOU_LOSN  and c.town_cd || c.vill_cd = substr(a.HOU_LOSN,0,4) and c.serv_area_mk = 'Y' and  LOCAT_ADDR_HSN || LOCAT_ADDR_TOWN = '" + ADDR_TOWN + "'  and LOCAT_ADDR_ROAD = '" + ROAD + "' and  LOCAT_ADDR_OTH = '" + SEC + LANE  + ALLEY +  NO +  FLOOR + OTH + "'";
		System.out.println( ": SQL query1:\n " + query1);
		stmt3.execute(query1);
		ResultSet rs2 = stmt3.getResultSet(); 		
		String query2 = "";
		int rowCount2 = 0;
		if (rs2 != null) {
			System.out.println(": match schema info for the given result set: ");
   			ResultSetMetaData rsmd2 = rs2.getMetaData();
  	 		for (int i=1; i <= rsmd2.getColumnCount(); i++) {
				System.out.print(" | " + rsmd2.getColumnName(i));
			}
			System.out.println("\n" + ": Fetch the actual data: ");
	   		
	   		while (rs2.next()) {
   			
				query2 = "INSERT into export_Hospital_data(申請類別 , 申請日期 , 機構類別 , 機構名稱 , 負責醫師 , 機構聯絡電話 , 地址 , 房屋稅稅籍編號 , 房屋稅座落地址 , 管區員編) VALUES(" + data + " , '" + rs2.getString(8) + "' , '" + rs2.getString(1) + rs2.getString(2) +  rs2.getString(3) + rs2.getString(4) + rs2.getString(5) + rs2.getString(6) + rs2.getString(7) +"' , '" + rs2.getString(9)  + "')";
				System.out.println("query2 = " + query2 );
				stmt2.execute(query2);
				
			System.out.println("");
			
   			rowCount2++;
       			}
		if (rowCount2 == 0 ) {
			query2 = "INSERT into  export_Hospital_data(申請類別 , 申請日期 , 機構類別 , 機構名稱 , 負責醫師 , 機構聯絡電話 , 地址 , 房屋稅稅籍編號 , 房屋稅座落地址 , 管區員編 ) VALUES(" + data + " , '' , '' , '' )";
			System.out.println( ": SQL query2:\n " + query2);

			stmt2.execute(query2);
		}
   		System.out.println(": Check Row Count: " + rowCount2);

		}
	
	}catch (Exception err) {
	   System.out.println(": Exception checkHospital : " + err.getMessage());
	} finally {
	   System.out.println(": Cleanup. Done.");
	}
  }
	
	//挑檔工廠
	public static Void checkFactory(String data , String ADDR_TOWN ,  String ROAD , String SEC , String LANE , String ALLEY , String NO , String FLOOR , String OTH , Statement stmt2 , Statement stmt3 , Statement stmt4){
		try{		
		//讀取HOUT103 坐落檔，取得稅號。
		String query1 = "SELECT \n  /*APP_NO:1081138 SYS_CD:HOU ACC_WITH:依工廠門牌廢止電子檔勾稽本處房屋稅坐落資料檔*/ distinct LOCAT_ADDR_HSN , LOCAT_ADDR_TOWN , LOCAT_ADDR_VILL , LOCAT_ADDR_LIN , LOCAT_ADDR_ROAD , LOCAT_ADDR_SEC , LOCAT_ADDR_OTH , a.HOU_LOSN  , c.SERV_AREA_CD FROM E77A.HOUT103 a , E77A.HOUT110 b , E77A.HOUC040 c WHERE b.HSN_CD = 'E' and a.hou_LOSN = b.HOU_LOSN  and c.town_cd || c.vill_cd = substr(a.HOU_LOSN,0,4) and c.serv_area_mk = 'Y' and  LOCAT_ADDR_HSN || LOCAT_ADDR_TOWN = '" + ADDR_TOWN ;

		if (!ADDR_VILL.equals(""))  query1 = query1  + "' and LOCAT_ADDR_VILL= '" + ADDR_VILL;
		if (!ROAD.equals("")) query1 = query1 + "' and LOCAT_ADDR_ROAD = '" + ROAD;
		if (!LANE.equals("") || !ALLEY.equals("") || !NO.equals("") || !FLOOR.equals("") || !OTH.equals("") ) query1 = query1  + "' and  LOCAT_ADDR_OTH = '" + LANE  + ALLEY +  NO +  FLOOR + OTH;

		query1 = query1 + "'";
		System.out.println( ": SQL query1:\n " + query1);
		stmt3.execute(query1);
		ResultSet rs2 = stmt3.getResultSet(); 		
		String query2 = "";
		int rowCount2 = 0;
		if (rs2 != null) {
			System.out.println(": match schema info for the given result set: ");
   			ResultSetMetaData rsmd2 = rs2.getMetaData();
  	 		for (int i=1; i <= rsmd2.getColumnCount(); i++) {
				System.out.print(" | " + rsmd2.getColumnName(i));
			}
			System.out.println("\n" + ": Fetch the actual data: ");
	   		
	   		while (rs2.next()) {
   			
				query2 = "INSERT into export_fac_data(行政區域名稱, 編釘日期 , 異動日期 , 變更前地址 , 變更後地址 , 編釘類別 , 房屋稅稅籍編號 , 是否註銷 , 管區員編 ) VALUES(" + data ;
				String tmp = rs2.getString(8);
				
				query2 = query2 + ", '" + tmp + "', '" + check_YN(tmp ,stmt4) + "' , '" + rs2.getString(9) +"')";
				System.out.println("query2 = " + query2 );
				stmt2.execute(query2);
				
			System.out.println("");
			
   			rowCount2++;
       			}
		if (rowCount2 == 0 ) {
			query2 = "INSERT into  export_fac_data(行政區域名稱 , 編釘日期 , 異動日期 , 變更前地址 , 變更後地址 , 編釘類別 , 房屋稅稅籍編號 , 是否註銷 , 管區員編) VALUES(" + data + ", '' ,'' ,'') ";
			System.out.println( ": SQL query2:\n " + query2);

			stmt2.execute(query2);
		}
   		System.out.println(": Check Row Count: " + rowCount2);

		}
	
	}catch (Exception err) {
	   System.out.println(": Exception checkfactory : " + err.getMessage());
	} finally {
	   System.out.println(": Cleanup. Done.");
	}
  }
			
  
  
  
  
	public static String check_YN(String LOSN , Statement stmt4){
		String yn = "";
		try{
		//讀取HOUT102 註銷檔，是否已存在。
		
		String query4 = "SELECT \n /*APP_NO:1081138 SYS_CD:HOU ACC_WITH:依工廠門牌廢止電子檔勾稽本處房屋稅坐落資料檔*/ * from E77A.HOUT102 where HOU_LOSN = '" + LOSN + "'";
		System.out.println( ": SQL query4:\n " + query4);
		stmt4.execute(query4);

		ResultSet rs3 = stmt4.getResultSet(); 		

		if (rs3.next()) yn = "已廢止";
		else yn = "未廢止";

		}catch (Exception err) {
		   System.out.println(": Exception check_YN: " + err.getMessage());
		} finally {
	   	   System.out.println(": Cleanup. Done.");
		}
		return yn ;
	}
}
