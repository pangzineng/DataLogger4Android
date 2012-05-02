package com.hsr.datalogger.pachube;

public class PachubeProperty {
	public static String masterKeyName = "Android Pachube API Masterkey";
	public static String sharingKeyName = "Sharing Key";
	public static String defaultViewKey = "EppoRYwcGi-QRG0ieqk-XOlgAv2SAKxNRmM4cGRWMkNEST0g";
	public static String defaultCreateKey = "N3qO2UOfFh0xTsOaGU2Ttdu-h2ySAKxhRUd3WHFmNkNOTT0g";
	public static String newKeyRequest = "<key><label>"+ masterKeyName  + "</label><private-access>true</private-access><permissions><permission><access-methods><access-method>get</access-method><access-method>put</access-method><access-method>post</access-method><access-method>delete</access-method></access-methods></permission></permissions></key>";
	public static int RANGE_6HRS	= 0;
	public static int RANGE_12HRS	= 30;
	public static int RANGE_24HRS	= 60;
	public static int RANGE_5DAYS	= 300;
	public static int RANGE_14DAYS	= 900;
	public static int RANGE_31DAYS	= 3600;
	public static int RANGE_90DAYS	= 10800;
	public static int RANGE_180DAYS	= 21600;
	public static int RANGE_1YEARS	= 43200;
	
}
