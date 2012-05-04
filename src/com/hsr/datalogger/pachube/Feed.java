package com.hsr.datalogger.pachube;

import java.net.URL;
import java.util.ArrayList;

public class Feed {
	/**
	 * id of the feed
	 */
	private Integer id;

	/**
	 * Title of the feed
	 */
	private String title;

	/**
	 * Last time the feed was updated
	 */
	private String updated;

	/**
	 * Url of the feed
	 */
	private URL feed;

	/**
	 * Wheather the feed is live or frozen
	 */
	private Status status;

	/**
	 * A description of this feed
	 */
	private String description;

	/**
	 * Url of a Website which is related to this feed
	 */
	private URL website;

	/**
	 * Publicly available email address
	 */
	private String email;

	/**
	 * Privacy of feed
	 */
	private boolean isPrivate = false;

	/**
	 * Details of the PachubeLocation of the feed source
	 */
	private PachubeLocation location;

	/**
	 * Collection of Data object which represent the datastreams of this feed
	 */
	private ArrayList<Data> data;

	private boolean isCreated;
	
	/**
	 * Default constructor
	 */
	public Feed() {
		this.data = new ArrayList<Data>();
		this.isCreated=false;
		this.id = -1;
		this.title = null;
		this.updated = null;
		this.feed = null;
		this.description = null;
		this.website = null;
		this.email = null;
		this.isPrivate = false;
		this.location = new PachubeLocation();
	}

	/**
	 * Creates a datastream on this feed
	 * @param d Datastream
	 * @throws PachubeException 
	 */
	public void createDatastream(Data d) throws PachubeException {
		data.add(d);
	}

	/**
	 * Deletes a datastream
	 * @param id id of the datastream to delete
	 */
	public void deleteDatastream(String id) {
		for (int i = 0; i < this.data.size(); i++) {
			if (this.data.get(i).getId().equalsIgnoreCase(id)) {
				data.remove(i);
			}
		}
	}

	/**
	 * Gets a datastream from the feed
	 * @param id id of the datastream to get
	 * @return
	 */
	public Data getDatastream(String id) {
		return lookup(id);
	}
	
	public void updateDataStream(String id, DataPoint dp){
		lookup(id).addDataPoint(dp);
	}

	/**
	 * Gets a Data object from the internal collection
	 * @param id id of the Data object to get
	 * @return
	 */
	private Data lookup(String id) {
		for (int i = 0; i < this.data.size(); i++) {
			if (this.data.get(i).getId().equalsIgnoreCase(id)) {
				return this.data.get(i);
			}
		}
		return null;
	}

	/**
	 * Add a datastream to the internal data collection
	 * This method does not automatically submit data to pachube, this method is intended 
	 * for the creation of feeds.
	 * @param d
	 */
	public void addData(Data d) {
		this.data.add(d);
	}

	/**
	 * Gets the id of the Feed
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * sets the id of the feed, this is not intended for users to use.
	 * When creating a new datastream to submit to pachube, Pachube will provide a Unique id.
	 * THIS METHOD SHOULD ONLY BE USED BY THE PachubeFactory.
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * sets the id of the feed, this is not intended for users to use.
	 * When creating a new datastream to submit to pachube, Pachube will provide a Unique id.
	 * THIS METHOD SHOULD ONLY BE USED BY THE PachubeFactory.
	 * @param id
	 */
	public void setId(String id) {
		try {
			this.id = Integer.parseInt(id);
		} catch (Exception e) {

		}

	}

	/**
	 * Gets the title of the feed
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the feed, this method will submit any changes to Pachube 
	 * @param title
	 * @throws PachubeException
	 */
	public void setTitle(String title) throws PachubeException {
		this.title = title;
	}

	/**
	 * Gets when the feed was last updated, this will be the last time the feed was update after
	 * getting the feed from pachube.
	 * @return
	 */
	public String getUpdated() {
		return updated;
	}

	/**
	 * Sets when the feed was last updated, this method is not intended for users to use.
	 * THIS METHOD IS INTENDED TO BE USED BY THE PachubeFactory.
	 * @param updated
	 */
	public void setUpdated(String updated) {
		this.updated = updated;
	}

	/**
	 * Gets the Url of the Feed.
	 * @return
	 */
	public URL getFeed() {
		return feed;
	}

	/**
	 * Sets the Url of the feed, this method is not intended for users to use.
	 * THIS METHOD IS INTENDED TO BE USED BY THE PachubeFactory.
	 * @param feed
	 */
	public void setFeed(URL feed) {
		this.feed = feed;
	}

	/**
	 * Gets the status of the feed when the feed was featched from pachube
	 * @return
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Sets the url of the feed
	 * @param status
	 * @throws PachubeException
	 */
	public void setStatus(Status status) throws PachubeException {
		this.status = status;
	}

	/**
	 * Gets the privacy of the feed from pachube
	 * @return
	 */
	public boolean isPrivate() {
		return isPrivate;
	}

	/**
	 * Sets the privacy of the feed
	 * @param status
	 * @throws PachubeException
	 */
	public void setPrivacy(boolean isPrivate) throws PachubeException {
		this.isPrivate = isPrivate;
	}

	/**
	 * Gets the Description of the feed
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the Description of the feed
	 * @param description
	 * @throws PachubeException
	 */
	public void setDescription(String description) throws PachubeException {
		this.description = description;
	}

	/**
	 * Gets the URL of the website associated with the feed
	 * @return
	 */
	public URL getWebsite() {
		return website;
	}

	/**
	 * Sets the URL of the website associated with the feed
	 * @param website
	 * @throws PachubeException
	 */
	public void setWebsite(URL website) throws PachubeException {
		this.website = website;
	}

	/**
	 * Gets the publicly available email address of this feed
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the publicly available email address of thie feed.
	 * THE EMAIL ADDRESS WILL BE PUBLICLY AVAILABLE, DO NOT USE AN EMAIL ADDRESS YOU WISH TO KEEP PRIVATE
	 * @param email
	 * @throws PachubeException
	 */
	public void setEmail(String email) throws PachubeException {
		this.email = email;
	}

	/**
	 * Gets the PachubeLocation details of the feed
	 * @return
	 */
	public PachubeLocation getLocation() {
		return location;
	}

	/**
	 * Sets the location detals of the feed
	 * @param location
	 * @throws PachubeException
	 */
	public void setLocation(PachubeLocation location) throws PachubeException {
		this.location = location;
	}

	/**
	 * Get a list of all datastreams in the feed
	 * @return
	 */
	public ArrayList<Data> getData() {
		return data;
	}

	/**
	 * Sets a list of all datastream in the feed
	 * @param data
	 */
	public void setData(ArrayList<Data> data) {
		this.data = data;
	}
	
	public boolean isCreated(){
		return this.isCreated;
	}

	@Override
	public String toString() {
		return "Feed [data=" + data + ", description=" + description
				+ ", email=" + email + ", feed=" + feed + ", id=" + id
				+ ", location=" + location + ", status=" + status + ", title="
				+ title + ", updated=" + updated + ", website=" + website + "]";
	}

	public void setCreated(boolean b) {
		this.isCreated=b;
	}
}