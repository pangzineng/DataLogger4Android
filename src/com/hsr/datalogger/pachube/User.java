package com.hsr.datalogger.pachube;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class User {
	/**
	 * Login name for the user
	 */
	private String username;
	
	/**
	 * Password for the user
	 */
	private String password;
	
	/**
	 * Info about the user
	 */
	private String about;
	
	/**
	 * Full name of the user
	 */
	private String fullName;
	
	/**
	 * Email address of the user
	 */
	private String email;
	
	/**
	 * Master APIKey of the User
	 */
	private String api_key;
	
	/**
	 * Time zone of the user
	 */
	private String timeZone;
	
	/**
	 * List of roles the user has
	 */
	private ArrayList<String> roles;
	
	/**
	 * List of feeds of the user
	 */
	private ArrayList<Feed> feedList;
	
	/**
	 * Default constructor
	 */
	public User(String username, String password){
		this.username = username;
		this.password = password;
	}

	/**
	 * Gets the login name
	 * @return 
	 */
	public String getUsername(){
		return this.username;
	}
	
	/**
	 * Gets the password
	 * @return 
	 */
	public String getPassword(){
		return this.password;
	}
	
	/**
	 * Sets the new password
	 * @return 
	 */
	public void setPassword(String s){
		this.password = s;
	}
	
	/**
	 * Sets the new info of the user
	 * @param s the new info to replace the previous info in profile
	 */
	public void setAbout(String s){
		this.about = s;
	}
	
	/**
	 * Gets the info of the user
	 * @return
	 */
	public String getAbout(){
		return this.about;
	}
	
	/**
	 * Sets the new full name of the user
	 * @param s the new name to replace the previous name in profile
	 */
	public void setName(String s){
		this.fullName = s;
	}
	
	/**
	 * Gets the full name of the user
	 * @return
	 */
	public String getName(){
		return this.fullName;
	}
	
	/**
	 * Sets a new email for the user
	 * @param s new email to replace the previous email in profile
	 */
	public void setEmail(String s){
		this.email = s;
	}
	
	/**
	 * Gets the user's email
	 * @return
	 */
	public String getEmail(){
		return this.email;		
	}
	
	/**
	 * Sets new API Key for the user
	 * @param u URL to the new website
	 */
	public void setKey(String key){
		this.api_key = key;
	}
	
	/**
	 * Gets the user's API Key
	 * @return
	 */
	public String getKey(){
		return this.api_key;
	}
	
	/**
	 * Sets the new time zone
	 * @param t new integer indicating the new time zone of the user
	 */
	public void setTimezone(String t){
		this.timeZone = t;
	}
	
	/**
	 * Gets the user's time zone
	 * @return
	 */
	public String getTimezone(){
		return this.timeZone;
	}
	
	/**
	 * Sets the roles for this user
	 * @param roles new list of roles to replace the old list
	 */
	public void setRoles(ArrayList<String> roles){
		this.roles = roles;
	}
	
	/**
	 * Gets the list of roles of this user
	 * @return
	 */
	public ArrayList<String> getRoles(){
		return this.roles;		
	}
	
	/**
	 * Sets the list of feeds
	 * @param fL new list of feeds to replace the old list
	 */
	public void setFeeds(ArrayList<Feed> fL){
		this.feedList = fL;
	}
	
	/**
	 * Gets the list of feeds of this user
	 * @return
	 */
	public ArrayList<Feed> getFeeds(){
		return this.feedList;
	}
	
	/**
	 * Adds a new feed to this user account
	 * @param f the new feed to be added
	 */
	public void addFeed(Feed f){
		this.feedList.add(f);
	}
	
	/**
	 * Creates a new API Masterkey for the user.
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void createMasterKey() throws MalformedURLException, IOException, ParserConfigurationException, SAXException{		
        if (Pachube.createMasterKey(username, password, PachubeProperty.newKeyRequest))
        	setKey(Pachube.getKey(username, password, PachubeProperty.masterKeyName));
	}
}
