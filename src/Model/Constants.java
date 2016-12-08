package Model;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants {
		
	public static final String BQUERY_TITLE1 = "Both Read Table";
	public static final String BQUERY_TITLE2 = "Both TOT.POP +10 ABW YR2000";
	public static final String BQUERY_TITLE3 = "Both TOT.POP +20 ABW YR2000";
	public static final String BQUERY_TITLE4 = "Both INSERT POP ABW Year 2001";
	public static final String BQUERY_TITLE5 = "Both INSERT POP ABW Year 2002";
	public static final String BQUERY_TITLE6 = "Both DELETE POP ABW Year 2001";
	public static final String BQUERY_TITLE7 = "Both DELETE POP ABW Year 2002";
	
	public static final String ASIA_AFRICA_TITLE1 = "ASIA_AFRICA Read Table";
	public static final String AAQUERY_TITLE2 = "ASIA_AFRICA TOP.POP +10 AFG YR2000";
	public static final String AAQUERY_TITLE3 = "ASIA_AFRICA TOT.POP +20 AFG YR2000";
	public static final String AAQUERY_TITLE4 = "ASIA_AFRICA POP INSERT AFG Year 2001";
	public static final String AAQUERY_TITLE5 = "ASIA_AFRICA POP INSERT AFG Year 2002";
	public static final String AAQUERY_TITLE6 = "ASIA_AFRICA DELETE POP AFG Year 2001";
	public static final String AAQUERY_TITLE7 = "ASIA_AFRICA DELETE POP AFG Year 2002";
	
	public static final String EUROPE_AMERICA_TITLE1 = "EUROPE_AMERICA Read Table";    
	public static final String EAQUERY_TITLE2 = "EUROPE_AMERICA TOT.POP +10 ABW YR2000";
	public static final String EAQUERY_TITLE3 = "EUROPE_AMERICA TOT.POP +20 ABW YR2000";
	public static final String EAQUERY_TITLE4 = "EUROPE_AMERICA POP INSERT ABW Year 2001";
	public static final String EAQUERY_TITLE5 = "EUROPE_AMERICA POP INSERT ABW Year 2002";
	public static final String EAQUERY_TITLE6 = "EUROPE_AMERICA DELETE POP ABW Year 2001";
	public static final String EAQUERY_TITLE7 = "EUROPE_AMERICA DELETE POP ABW Year 2002";
	
	public static final String BQUERY_1 = "SELECT countrycode, yearc, data FROM databyyear WHERE seriescode = 'SM.POP.TOTL' AND yearc='2000 [YR2000]' AND  countrycode='ABW' ";
	public static final String BQUERY_2 = "UPDATE `databyyear` SET data = data + 10 WHERE `seriescode`='SM.POP.TOTL' AND `yearc`='2000 [YR2000]' AND `countrycode`='ABW';";
	public static final String BQUERY_3 = "UPDATE `databyyear` SET data = data + 20 WHERE `seriescode`='SM.POP.TOTL' AND `yearc`='2000 [YR2000]' AND `countrycode`='ABW';";
	public static final String BQUERY_4 = "INSERT INTO `databyyear`(`countrycode`,`seriescode`,`yearc`,`Data`)VALUES('ABW','SM.POP.TOTL','2001 [YR2000]',5000);";
	public static final String BQUERY_5 = "INSERT INTO `databyyear`(`countrycode`,`seriescode`,`yearc`,`Data`)VALUES('ABW','SM.POP.TOTL','2002 [YR2000]',5000);";
	public static final String BQUERY_6 = "DELETE FROM `databyyear`WHERE seriescode = 'SM.POP.TOTL' AND countrycode = 'ABW' AND yearc = '2001 [YR2000]';";
	public static final String BQUERY_7 = "DELETE FROM `databyyear`WHERE seriescode = 'SM.POP.TOTL' AND countrycode = 'ABW' AND yearc = '2002 [YR2000]';";
	
	public static final String ASIA_AFRICA_QUERY1 = "SELECT * from databyyear NATURAL JOIN countryregion WHERE `yearc` LIKE '2000%' AND `seriescode`='SM.POP.TOTL' and`countrycode`='AFG' ";
	public static final String AAQUERY_2 = "UPDATE `databyyear` SET data = data + 10 WHERE `seriescode`='SM.POP.TOTL' AND `yearc`='2000 [YR2000]' AND `countrycode`='AFG';";
	public static final String AAQUERY_3 = "UPDATE `databyyear` SET data = data + 20 WHERE `seriescode`='SM.POP.TOTL' AND `yearc`='2000 [YR2000]' AND `countrycode`='AFG';";
	public static final String AAQUERY_4 = "INSERT INTO `databyyear`(`countrycode`,`seriescode`,`yearc`,`Data`)VALUES('AFG','SM.POP.TOTL','2001 [YR2000]',5000);";
	public static final String AAQUERY_5 = "INSERT INTO `databyyear`(`countrycode`,`seriescode`,`yearc`,`Data`)VALUES('AFG','SM.POP.TOTL','2002 [YR2000]',5000);";
	public static final String AAQUERY_6 = "DELETE FROM `databyyear`WHERE seriescode = 'SM.POP.TOTL' AND countrycode = 'AFG' AND yearc = '2001 [YR2000]';";
	public static final String AAQUERY_7 = "DELETE FROM `databyyear`WHERE seriescode = 'SM.POP.TOTL' AND countrycode = 'AFG' AND yearc = '2002 [YR2000]';";
	
	public static final String EUROPE_AMERICA_QUERY1 = "SELECT * from databyyear NATURAL JOIN countryregion WHERE `yearc` LIKE '2000%' AND `seriescode`='SM.POP.TOTL' and`countrycode`='ABW' ";
	public static final String EAQUERY_2 = "UPDATE `databyyear` SET data = data + 10 WHERE `seriescode`='SM.POP.TOTL' AND `yearc`='2000 [YR2000]' AND `countrycode`='ABW';";//"UPDATE europe_america.databyyear SET data = data + 10 WHERE seriescode = 'SP.POP.TOTL' AND ";
	public static final String EAQUERY_3 = "UPDATE `databyyear` SET data = data + 20 WHERE `seriescode`='SM.POP.TOTL' AND `yearc`='2000 [YR2000]' AND `countrycode`='ABW';";//"UPDATE
	public static final String EAQUERY_4 = "INSERT INTO `databyyear`(`countrycode`,`seriescode`,`yearc`,`Data`)VALUES('ABW','SM.POP.TOTL','2001 [YR2000]',5000);";
	public static final String EAQUERY_5 = "INSERT INTO `databyyear`(`countrycode`,`seriescode`,`yearc`,`Data`)VALUES('ABW','SM.POP.TOTL','2002 [YR2000]',5000);";
	public static final String EAQUERY_6 = "DELETE FROM `databyyear`WHERE seriescode = 'SM.POP.TOTL' AND countrycode = 'ABW' AND yearc = '2001 [YR2000]';";
	public static final String EAQUERY_7 = "DELETE FROM `databyyear`WHERE seriescode = 'SM.POP.TOTL' AND countrycode = 'ABW' AND yearc = '2002 [YR2000]';";
	
	public static final ArrayList<String> BOTH_Q_TITLES =  new ArrayList<String>(Arrays.asList(
			BQUERY_TITLE1,
			BQUERY_TITLE2,
			BQUERY_TITLE3,
			BQUERY_TITLE4,
			BQUERY_TITLE5,
			BQUERY_TITLE6,
			BQUERY_TITLE7));
	
	public static final ArrayList<String> ASIA_AFRICA_Q_TITLES =  new ArrayList<String>(Arrays.asList(
			ASIA_AFRICA_TITLE1, 
			AAQUERY_TITLE2, 
			AAQUERY_TITLE3, 
			AAQUERY_TITLE4, 
			AAQUERY_TITLE5,
			AAQUERY_TITLE6,
			AAQUERY_TITLE7));
	
	public static final ArrayList<String> EUROPE_AMERICA_Q_TITLES =  new ArrayList<String>(Arrays.asList(
			EUROPE_AMERICA_TITLE1, 
			EAQUERY_TITLE2, 
			EAQUERY_TITLE3, 
			EAQUERY_TITLE4, 
			EAQUERY_TITLE5,
			EAQUERY_TITLE6,
			EAQUERY_TITLE7));
	
	public static final String REGION_ASIA_AFRICA = "ASIA & AFRICA";
	public static final String REGION_EUROPE_AMERICA = "EUROPE & AMERICA";
	public static final String REGION_BOTH = "ALL";
	
	public static final String HOST_ASIA_AFRICA = "ASIA & AFRICA";
	public static final String HOST_ASIA_AFRICA_REPLICA = "ALL";
	public static final String HOST_ASIA_AFRICAS_REPLICA = "EUROPE & AMERICA";
	
	public static final String HOST_EUROPE_AMERICA = "EUROPE & AMERICA";
	public static final String HOST_EUROPE_AMERICA_REPLICA = "ASIA & AFRICA";
	public static final String HOST_EUROPE_AMERICAS_REPLICA = "ALL";
	
	public static final String HOST_ALL = "ALL";
	public static final String HOST_ALL_REPLICA = "EUROPE & AMERICA";
	public static final String HOST_ALLS_REPLICA = "ASIA & AFRICA";
}
