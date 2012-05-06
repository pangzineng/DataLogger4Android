package com.hsr.datalogger.pachube;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.util.Base64;

public class Pachube {

	/**
	 * Gets a Feed by Feed ID
	 * 
	 * @param feed
	 *            Id of the Pachube feed to retrieve
	 * @return Feed which corresponds to the id provided as the parameter
	 * @throws PachubeException
	 *             If something goes wrong.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static Feed getFeed(int feed, String key) throws PachubeException, MalformedURLException, IOException {
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/feeds/"
				+ feed + ".xml").openConnection());
		hr.setRequestProperty("X-PachubeApiKey", key);

		if (hr.getResponseMessage().equalsIgnoreCase("OK")) {
			return PachubeFactory.toFeed(key, hr.getInputStream());
		} else {
			throw new PachubeException(hr.getResponseMessage());
		}
	}

	/**
	 * Creates a new feed from the feed provide. The feed provide should have no
	 * ID, and after this method is called is useless, to make changes to the new
	 * feed methods should be invoked on the return object.
	 * 
	 * @param f
	 *            Feed to create, This Feed Should have no ID field and at least
	 *            should have its title field filled in.
	 * @return Representation of the feed from pachube, this is a 'live' Feed
	 *         and method can invoked which will change the state of the online
	 *         feed.
	 * @throws PachubeException
	 *             If something goes wrong.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws NumberFormatException 
	 */
	public static Feed createFeed(Feed f, String key) throws PachubeException, NumberFormatException, MalformedURLException, IOException {
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/feeds.xml").openConnection());
		hr.setRequestMethod("POST");
		hr.setDoOutput(true);
		hr.setRequestProperty("X-PachubeApiKey", key);
		
		OutputStreamWriter out = new OutputStreamWriter(hr.getOutputStream());
		out.write(PachubeFactory.toFeedXML(f));
		out.flush();
		out.close();

		if (hr.getResponseMessage().equalsIgnoreCase("Created")) {
			String[] a = hr.getHeaderField("Location").split("/");
			Feed n = getFeed(Integer.parseInt(a[a.length - 1]), key);
			return n;
		} else {
			throw new PachubeException(hr.getResponseMessage());
		}
	}

	/**
	 * This Method is not intended to be used by Users, instead get the Feed
	 * object using getFeed() and update the Feed from there, All changes will
	 * be made to the online Feed.
	 * 
	 * @param feed
	 * @param f
	 * @return
	 * @throws PachubeException
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static boolean updateFeed(int feed, String f, String key) throws PachubeException, MalformedURLException, IOException {
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/feeds/"
				+ feed + ".xml").openConnection());
		hr.setRequestMethod("PUT");
		hr.setDoOutput(true);
		hr.setRequestProperty("X-PachubeApiKey", key);
		
		OutputStreamWriter out = new OutputStreamWriter(hr.getOutputStream());
		out.write(f);
		out.flush();
		out.close();

		if (hr.getResponseMessage().equalsIgnoreCase("OK")) {
			return true;
		} else {
			throw new PachubeException(hr.getResponseMessage());
		}
	}

	/**
	 * Delete a Feed specified by the feed id. If any Feed object exists that is
	 * a representation of the item to be deleted, they will no longer work and
	 * will throw errors if method are invoked on them.
	 * 
	 * @param feed
	 *            If of the feed to delete
	 * @return boolean
	 * @throws IOException 
	 * @throws PachubeException 
	 */
	public static boolean deleteFeed(int feed, String key) throws PachubeException, IOException {
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/feeds/"
				+ feed + ".xml").openConnection());
		hr.setRequestMethod("DELETE");
		hr.setDoOutput(true);
		hr.setRequestProperty("X-PachubeApiKey", key);

		if (hr.getResponseMessage().equalsIgnoreCase("OK")) {
			return true;
		} else {
			throw new PachubeException(hr.getResponseMessage());
		}
	}

	/**
	 * This Method is not intended to be used by Users, instead get the Feed
	 * object using getFeed() and create Datastreams from there, All changes
	 * will be made to the online Feed.
	 * 
	 * @param feed
	 * @param s
	 * @return
	 * @throws PachubeException
	 * @throws IOException 
	 */
	public static boolean createDatastream(int feed, String s, String key) throws PachubeException, IOException {
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/feeds/"
				+ feed + "/datastreams.xml").openConnection());
		hr.setRequestMethod("POST");
		hr.setDoOutput(true);
		hr.setRequestProperty("X-PachubeApiKey", key);

		OutputStreamWriter out = new OutputStreamWriter(hr.getOutputStream());
		out.write(s);
		out.flush();
		out.close();
		
		if (hr.getResponseMessage().equalsIgnoreCase("Created")) {
			return true;
		} else {
			throw new PachubeException(hr.getResponseMessage());
		}
	}

	/**
	 * This Method is not intended to be used by Users, instead get the Feed
	 * object using getFeed() and delete Datastreams from there, All changes
	 * will be made to the online Feed.
	 * 
	 * @param feed
	 * @param datastream
	 * @return
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws PachubeException 
	 */
	public static boolean deleteDatastream(int feed, String datastream, String key) throws MalformedURLException, IOException, PachubeException {
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/feeds/"
				+ feed + "/datastreams/" + datastream + ".xml").openConnection());
		hr.setRequestMethod("DELETE");
		hr.setDoOutput(true);
		hr.setRequestProperty("X-PachubeApiKey", key);

		if (hr.getResponseMessage().equalsIgnoreCase("OK")) {
			return true;
		} else {
			throw new PachubeException(hr.getResponseMessage());
		}
	}
	
	/**
	 * update new value to a Datastream (for online mode) 
	 * 
	 * @param feed
	 * @param dataID
	 * @param s
	 * @param key
	 * @return
	 * @throws IOException
	 * @throws PachubeException
	 */
	public static boolean updateDatastream(int feed, String dataID, String s, String key) throws IOException, PachubeException {
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/feeds/"
				+ feed + "/datastreams/" + dataID + ".xml").openConnection());
		hr.setRequestMethod("PUT");
		hr.setDoOutput(true);
		hr.setRequestProperty("X-PachubeApiKey", key);
		
		OutputStreamWriter out = new OutputStreamWriter(hr.getOutputStream());
		out.write(s);
		out.flush();
		out.close();

		if (hr.getResponseMessage().equalsIgnoreCase("OK")) {
			return true;
		} else {
			throw new PachubeException(hr.getResponseMessage());
		}
	}

	/**
	 * update Datapoints to a feed (for offline mode).
	 * 
	 * @param feed
	 * @param dataID
	 * @param s
	 * @return
	 * @throws IOException 
	 * @throws PachubeException 
	 */
	public static boolean updateDataPoints(int feed, String dataID, String s, String key) throws IOException, PachubeException {
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/feeds/"
				+ feed + "/datastreams/" + dataID + "/datapoints.xml").openConnection());
		hr.setRequestMethod("POST");
		hr.setDoOutput(true);
		hr.setRequestProperty("X-PachubeApiKey", key);
		
		OutputStreamWriter out = new OutputStreamWriter(hr.getOutputStream());
		out.write(s);
		out.flush();
		out.close();

		if (hr.getResponseMessage().equalsIgnoreCase("OK")) {
			return true;
		} else {
			throw new PachubeException(hr.getResponseMessage());
		}
	}

	/**
	 * This Method is not intended to be used by Users, instead get the Feed
	 * object using getFeed() and get Datastreams from there.
	 * 
	 * @param feed
	 * @param dataID
	 * @return
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws PachubeException 
	 */
	public static Data getDatastream(int feed, String dataID, String key) throws MalformedURLException, IOException, PachubeException {
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/feeds/"
				+ feed + "/datastreams/" + dataID + ".xml").openConnection());
		hr.setRequestProperty("X-PachubeApiKey", key);

		if (hr.getResponseMessage().equalsIgnoreCase("OK")) {
			return PachubeFactory.toData(hr.getInputStream());
		} else {
			throw new PachubeException(hr.getResponseMessage());
		}
	}

	/**
	 * Gets a Pachube graph of the datastream
	 * 
	 * @param feedID
	 *            ID of feed the datastream belongs to.
	 * @param streamID
	 *            ID of the stream to graph
	 * @param width
	 *            Width of the image
	 * @param height
	 *            Height of the image
	 *            
	 * @return Drawable object for display.
	 * @throws IOException 
	 */
	public static InputStream showGraph(int feedID, String streamID, int width, int height, String duration, int timezone) throws IOException{
		URL graph = new URL("http://api.pachube.com/v2/feeds/" + feedID + "/datastreams/"
				+ streamID + ".png?width=" + width + "&height=" + height + "&colour=%23000000" 
				+ "&duration=" + duration + "&show_axis_labels=true&detailed_grid=true&timezone=" + timezone);
		
		InputStream in = (InputStream) graph.getContent();
		return in;
	}
	
	/**
	 * Create a new user from the object User provided. The returned object is
	 * the live user storing the new created user's profile.
	 * 
	 * @param u
	 *          User object to be created
	 * @return 
	 * 			Boolean value of whether the user is created.
	 * @throws PachubeException
	 *             If something goes wrong.
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static boolean createUser(User u, String key) throws PachubeException, MalformedURLException, IOException, ParserConfigurationException, SAXException {
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/users.xml").openConnection());
		hr.setRequestMethod("POST");
		hr.setDoOutput(true);
		hr.setRequestProperty("X-PachubeApiKey", key);
		String s = PachubeFactory.toUserXML(u);
		
		OutputStreamWriter out = new OutputStreamWriter(hr.getOutputStream());
		out.write(s);
		out.flush();
		out.close();
		if (hr.getResponseCode() == 422)
			throw new PachubeException("Username has already been taken.");
		if (hr.getResponseMessage().equalsIgnoreCase("Created")) {			
			return true;
		} else {
			throw new PachubeException(hr.getResponseMessage());
		}
	}

	/**
	 * This Method is not intended to be used by Users, instead get the User
	 * object using getFeed() and update the Feed from there, All changes will
	 * be made to the online Feed.
	 * 
	 * @param login
	 * @param s
	 * @return
	 * @throws PachubeException
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static boolean updateUser(String login, String s, String key) throws PachubeException, MalformedURLException, IOException {
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/users/"
				+ login + ".xml").openConnection());
		hr.setRequestMethod("PUT");
		hr.setDoOutput(true);
		hr.setRequestProperty("X-PachubeApiKey", key);
		
		OutputStreamWriter out = new OutputStreamWriter(hr.getOutputStream());
		out.write(s);
		out.flush();
		out.close();
		
		if (hr.getResponseMessage().equalsIgnoreCase("OK")) {
			return true;
		} else {
			throw new PachubeException(hr.getResponseMessage());
		}
	}
	
	/**
	 * Creates new masterkey for the user
	 * 
	 * @param username
	 * @param password
	 * @param keyRequest
	 * @return
	 * @throws IOException
	 */
	public static boolean createMasterKey(String username, String password, String keyRequest) throws IOException{
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/keys.xml")).openConnection();	
		String encoding = Base64.encodeToString((username+":"+password).getBytes(),Base64.DEFAULT);
		hr.setRequestMethod("POST");
		hr.setDoOutput(true);
		hr.setRequestProperty ("Authorization", "Basic " + encoding.trim());
		
		OutputStreamWriter out = new OutputStreamWriter(hr.getOutputStream());		
		out.write(keyRequest);
		out.flush();
		out.close();

        if ((hr.getResponseCode()) == 201){
        	return true;
        }
		return false;
	}
	
	public static boolean createSharingKey(String key, String keyRequest) throws IOException{
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/keys.xml")).openConnection();	
		hr.setRequestMethod("POST");
		hr.setDoOutput(true);
		hr.setRequestProperty("X-PachubeApiKey", key);
		
		OutputStreamWriter out = new OutputStreamWriter(hr.getOutputStream());		
		out.write(keyRequest);
		out.flush();
		out.close();

        if ((hr.getResponseCode()) == 201){
        	return true;
        }
		return false;
	}
	/**
	 * Creates new sharing key for a certain feed
	 * 
	 * @param 
	 */
	
	/**
	 * Gets the new key for the user
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static String getKey(String username, String password, String keyName) throws ParserConfigurationException, SAXException, IOException{
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/keys.xml")).openConnection();	
		String encoding = Base64.encodeToString((username+":"+password).getBytes(),Base64.DEFAULT);
		hr.setRequestProperty ("Authorization", "Basic " + encoding.trim());
		
		if (hr.getResponseCode() == 200)
			return PachubeFactory.toKey(hr.getInputStream(), keyName);
		else 
			return null;
	}
	
	public static String getKey(String key, String keyName) throws ParserConfigurationException, SAXException, IOException{
		HttpURLConnection hr = (HttpURLConnection) (new URL("http://api.pachube.com/v2/keys.xml")).openConnection();	
		hr.setRequestProperty("X-PachubeApiKey", key);
		
		if (hr.getResponseCode() == 200)
			return PachubeFactory.toKey(hr.getInputStream(), keyName);
		else 
			return null;
	}
	
	/**
	 * Test key's functions with a certain feed
	 * 
	 * @param key 
	 * 			The key to be tested
	 * @param id 
	 * 			Feed's id
	 * 
	 * @return permission 
	 * 			Full permission or not.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static boolean checkKey(String key, int id) throws MalformedURLException, IOException{
		try{
			updateFeed(id, "", key);
		} catch (PachubeException e){
			if (e.errorMessage.equalsIgnoreCase("Bad Request"))
				return true;
			else return false;
		}
		return false;
	}
	
	
	/**
	 * Login function for the user
	 * 	
	 * @param username
	 * @param password
	 * @return the master API key for the user, if null is returned, it means a fail login.
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static String login(String username, String password) throws MalformedURLException, IOException, ParserConfigurationException, SAXException {
		String key = getKey(username, password, PachubeProperty.masterKeyName);
		if ( key == null){
			if (createMasterKey(username, password, PachubeProperty.newKeyRequest))
				return getKey(username, password, PachubeProperty.masterKeyName);
			else
				return null;
		} else return key;
	}
}

