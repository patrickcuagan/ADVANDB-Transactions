package Model;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants {
	
	public static final String REGION_ASIA_AFRICA = "ASIA & AFRICA";
	public static final String REGION_EUROPE_AMERICA = "EUROPE & AMERICA";
	public static final String REGION_BOTH = "ALL";
	
	public static final String BQUERY_TITLE1 = "Both Read Table";
	public static final String BQUERY_TITLE2 = "Both TOT.POP +10 ABW YR2000";
	public static final String BQUERY_TITLE3 = "Both TOT.POP +20 ABW YR2000";
	public static final String BQUERY_TITLE4 = "Both TOT.POP -100 ABW YR2000";
	
	public static final String ASIA_AFRICA_TITLE1 = "ASIA_AFRICA Read Table";
	public static final String AAQUERY_TITLE2 = "ASIA_AFRICA TOP.POP +10 AFG YR2000";
	public static final String AAQUERY_TITLE3 = "ASIA_AFRICA TOT.POP +20 AFG YR2000";
	public static final String AAQUERY_TITLE4 = "ASIA_AFRICA TOT.POP -100 AFG YR2000";
	public static final String AAQUERY_TITLE5 = "ASIA_AFRICA Delete";
	/*public static final String MQUERY_TITLE6 = "Marinduque Increment 10";
	public static final String MQUERY_TITLE7 = "Marinduque Increment 20";
	public static final String MQUERY_TITLE8 = "Marinduque Increment 30";
	public static final String MQUERY_TITLE9 = "Marinduque Increment 40";
	public static final String MQUERY_TITLE0 = "Marinduque Increment 50";*/
	
	
	public static final String EUROPE_AMERICA_TITLE1 = "EUROPE_AMERICA Read Table";    
	public static final String EAQUERY_TITLE2 = "EUROPE_AMERICA TOT.POP +10 AFG YR2000";
	public static final String EAQUERY_TITLE3 = "EUROPE_AMERICA TOT.POP +20 AFG YR2000";
	public static final String EAQUERY_TITLE4 = "EUROPE_AMERICA TOT.POP -100 AFG YR2000";
	public static final String EAQUERY_TITLE5 = "EUROPE_AMERICA Delete";  
	/*public static final String PQUERY_TITLE6 = "Palawan Increment 10";    
	public static final String PQUERY_TITLE7 = "Palawan Increment 20";
	public static final String PQUERY_TITLE8 = "Palawan Increment 30";
	public static final String PQUERY_TITLE9 = "Palawan Increment 40";
	public static final String PQUERY_TITLE0 = "Palawan Increment 50";*/
	
	public static final String BQUERY_1 = "SELECT countrycode, yearc, data FROM databyyear WHERE seriescode = 'SM.POP.TOTL' AND yearc='2000 [YR2000]' AND  countrycode='ABW' ";
	public static final String BQUERY_2 = "UPDATE `databyyear` SET data = data + 10 WHERE `seriescode`='SM.POP.TOTL' and`yearc`='2000 [YR2000]' and`countrycode`='ABW';";
	public static final String BQUERY_3 = "UPDATE `databyyear` SET data = data + 20 WHERE `seriescode`='SM.POP.TOTL' and`yearc`='2000 [YR2000]' and`countrycode`='ABW';";
	public static final String BQUERY_4 = "UPDATE `databyyear` SET data = data - 100 WHERE `seriescode`='SM.POP.TOTL' and`yearc`='2000 [YR2000]' and`countrycode`='ABW';";
	
	public static final String ASIA_AFRICA_QUERY1 = "SELECT * from databyyear NATURAL JOIN countryregion WHERE `yearc` LIKE '201%' AND `seriescode`='SM.POP.TOTL' and`countrycode`='AFG' ";
	public static final String AAQUERY_2 = "UPDATE `databyyear` SET data = data + 10 WHERE `seriescode`='SM.POP.TOTL' and`yearc`='2000 [YR2000]' and`countrycode`='AFG';";
	public static final String AAQUERY_3 = "UPDATE `databyyear` SET data = data + 20 WHERE `seriescode`='SM.POP.TOTL' and`yearc`='2000 [YR2000]' and`countrycode`='AFG';";
	public static final String AAQUERY_4 = "UPDATE `databyyear` SET data = data - 100 WHERE `seriescode`='SM.POP.TOTL' and`yearc`='2000 [YR2000]' and`countrycode`='AFG';";
	public static final String AAQUERY_5 = "DELETE FROM hpq_death";
	/*public static final String MQUERY_6 = "UPDATE hpq_death SET mdeadage = mdeadage + 10 ";
	public static final String MQUERY_7 = "UPDATE hpq_death SET mdeadage = mdeadage + 20 ";
	public static final String MQUERY_8 = "UPDATE hpq_death SET mdeadage = mdeadage + 30 ";
	public static final String MQUERY_9 = "UPDATE hpq_death SET mdeadage = mdeadage + 40 ";
	public static final String MQUERY_0 = "UPDATE hpq_death SET mdeadage = mdeadage + 50 ";*/
	
	public static final String EUROPE_AMERICA_QUERY1 = "SELECT * from databyyear NATURAL JOIN countryregion WHERE `yearc` LIKE '201%' AND `seriescode`='SM.POP.TOTL' and`countrycode`='ABW' ";
	public static final String EAQUERY_2 = "UPDATE `databyyear` SET data = data + 10 WHERE `seriescode`='SM.POP.TOTL' and`yearc`='2000 [YR2000]' and`countrycode`='ABW';";//"UPDATE europe_america.databyyear SET data = data + 10 WHERE seriescode = 'SP.POP.TOTL' AND ";
	public static final String EAQUERY_3 = "UPDATE `databyyear` SET data = data + 20 WHERE seriescode = 'SP.POP.TOTL' AND ";
	public static final String EAQUERY_4 = "UPDATE `databyyear` SET data = data + 30 WHERE seriescode = 'SP.POP.TOTL' AND ";
	public static final String EAQUERY_5 = "DELETE FROM hpq_death";	
	/*public static final String PQUERY_6 = "UPDATE hpq_death SET mdeadage = mdeadage + 10 ";
	public static final String PQUERY_7 = "UPDATE hpq_death SET mdeadage = mdeadage + 20 ";
	public static final String PQUERY_8 = "UPDATE hpq_death SET mdeadage = mdeadage + 30 ";
	public static final String PQUERY_9 = "UPDATE hpq_death SET mdeadage = mdeadage + 40 ";
	public static final String PQUERY_0 = "UPDATE hpq_death SET mdeadage = mdeadage + 50 ";*/
	
	public static final ArrayList<String> BOTH_Q_TITLES =  new ArrayList<String>(Arrays.asList(
			BQUERY_TITLE1,
			BQUERY_TITLE2,
			BQUERY_TITLE3,
			BQUERY_TITLE4));
	
	public static final ArrayList<String> ASIA_AFRICA_Q_TITLES =  new ArrayList<String>(Arrays.asList(
			ASIA_AFRICA_TITLE1, 
			AAQUERY_TITLE2, 
			AAQUERY_TITLE3, 
			AAQUERY_TITLE4, 
			AAQUERY_TITLE5));
	
	public static final ArrayList<String> EUROPE_AMERICA_Q_TITLES =  new ArrayList<String>(Arrays.asList(
			EUROPE_AMERICA_TITLE1, 
			EAQUERY_TITLE2, 
			EAQUERY_TITLE3, 
			EAQUERY_TITLE4, 
			EAQUERY_TITLE5));
	
	public static final String HOST_ASIA_AFRICA = "ASIA & AFRICA";
	public static final String HOST_ASIA_AFRICA_REPLICA = "EUROPE & AMERICA";
	
	
	public static final String HOST_EUROPE_AMERICA = "EUROPE & AMERICA";
	public static final String HOST_EUROPE_AMERICA_REPLICA = "ALL";
	
	public static final String HOST_ALL = "ALL";
	public static final String HOST_ALL_REPLICA = "ASIA & AFRICA";

}
