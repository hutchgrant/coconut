package com.hutchgrant.app;

import java.util.ArrayList;

import com.hutchgrant.Elements.*;
import com.hutchgrant.Elements.Sync.SyncMsgObj;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.coconut.provider;
import com.hutchgrant.coconut.provider.LocalBinder;
import com.hutchgrant.coconut.syncProvider;
import com.hutchgrant.coconut.syncProvider.SyncBinder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class TunaApp extends Application{
	provider mProvider;
	syncProvider mSyncProvider;
	boolean logged = false;
	boolean mBounded, mSyncBounded;

	@Override
	public void onCreate(){
		super.onCreate();
		init();
	}

	ServiceConnection mConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBounded = true;
			LocalBinder mLocalBinder = (LocalBinder)service;
			mProvider = mLocalBinder.getServerInstance();
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBounded = false;
			mProvider = null;
		}
	};
	ServiceConnection mSyncConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mSyncBounded = true;
			SyncBinder mLocalSyncBinder = (SyncBinder)service;
			mSyncProvider = mLocalSyncBinder.getServerInstance();
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSyncBounded = false;
			mSyncProvider = null;
		}
	};

	/*
	 * Network login and init
	 */
	protected void init(){
		Intent mIntent = new Intent(this, provider.class);
		bindService(mIntent, mConnection, BIND_AUTO_CREATE);
		Intent mSyncIntent = new Intent(this, syncProvider.class);
		bindService(mSyncIntent, mSyncConnection, BIND_AUTO_CREATE);
		// Create global configuration and initialize ImageLoader with this configuration
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.build();
		ImageLoader.getInstance().init(config);
	}
	///call service to check the profile, login, return profile, write to database
	public boolean login(){
		mProvider.profAccess.login();
		return true;
	}
	////get All GFriends from google using GToken
	public void getGFriends() {
		mProvider.profAccess.getFriends();
	}
	public provider getProvider(){
		return mProvider;
	}

	/*
	 * check network connection
	 */
	public boolean checkNetworkAvail(){
		NetworkAccess.getInstance().networkAvailable();
		return mProvider.networkAccess.isNetworkAvailable();
	}
	///------------------------------------------------
	/*
	 * Profile read/write/update/remove methods
	 */
	public ProfileAccess getProfileAccess(){
		return ProfileAccess.getInstance();
	}
	////call service to write profile
	public boolean newProfile(User profile){
		mProvider.profAccess.newProfile(profile);
		ProfileAccess.getInstance().setUser(profile);  
		return true;  /// when service msg is complete
	}
	public User getProfile(){
		return	ProfileAccess.getInstance().getProfile();
	}
	///----------------------------------------------
	/*
	 * Preference add/remove/update/checksync
	 */
	////Check sync is turned on
	public boolean isSyncOn(){
		PrefObj pref = new PrefObj();
		pref = getPref();
		return pref.synced;
	}
	///Get currect saved preferences
	public PrefObj getPref() {
		return mProvider.prefAccess.getPreference();
	}
	/// Set current saved preferences
	public void setPref(PrefObj pref, boolean update) {
		mProvider.prefAccess.setPreference(pref, update);
	}


	///------------------------------------------------
	/*
	 *   Group read/write/update/remove methods
	 */
	///call service to write new Group
	public void saveGroup(Group group, boolean update){
		mProvider.grpAccess.saveGroup(group, update);  /// write new group
		mProvider.grpAccess.saveAllContacts(group, update); // write all new contacts
	}
	///call service to write new Group
	public void saveAllContacts(Group group, boolean update){
		saveGroup(group, update);
	}
	/// call service to read all Groups
	public ArrayList<Group> readGroups( ){
		return mProvider.grpAccess.getGroupData();
	}
	/// call service to read details of one group by its id
	public Group readGroup( String selGroupID ){
		return mProvider.grpAccess.getGroupContacts(selGroupID);
	}
	public Group getGroup(String string, boolean searchID) {
		mProvider.syncGrpAccess.groupAccess.getGroup(string, searchID);
		return null;
	}	
	// call service to remove local and remote group by its id
	public void removeGroup( String string ){
		mProvider.grpAccess.removeLocalGroup(string);
		mProvider.syncGrpAccess.removeRemoteGroup(string);
	}
	/// call service to remove local contact only
	public void removeContacts(String contactID){
		mProvider.grpAccess.removeLocalContact(contactID);
	}
	/// call service to update group details with fresh data
	public void updateGroup( Group group){
		mProvider.grpAccess.saveGroup(group, true);
	}

	///------------------------------------------------
	/*
	 *   Contact read/write/update/remove methods
	 */
	/// call service to update contact info with fresh data
	public void updateContact( Contact contact){
		mProvider.grpAccess.saveContact(contact, true);
	}
	/// call service to read all Contacts from tuna's contact database
	public Group readContactsFromDB(String string ){
		return mProvider.grpAccess.getGroupContacts(string);
	}
	/// call service to read all Contacts From Phone's contact database
	public ArrayList<Contact> readContactsFromPhone( ){
		return mProvider.grpAccess.getContactData();
	}
	public void writeAddedContacts(ArrayList<Contact> addPPL) {
		mProvider.grpAccess.writeAddedContacts(addPPL);
	}
	///--------------------------------------------------
	/*
	 * Image read/write/update/remove methods
	 */
	public ArrayList<Photo> readAllImages(){
		return mProvider.albAccess.imgAccess.readAllImages();
	}
	public void setImagesForRemoval(ArrayList<Photo> images){
		mProvider.albAccess.imgAccess.setForRemovalImages(images);
	}
	public void setAlbumsForRemoval(ArrayList<Album> albums){
		mProvider.albAccess.setAlbumsForRemoval(albums);
	}
	public void removeLocalImage(Photo image){
		mProvider.albAccess.imgAccess.deleteImageFromUser(image);
	}
	public void removeLocalImages(ArrayList<Photo> images){
		mProvider.albAccess.imgAccess.removeLocalImages(images);
	}
	public void updateLocalImage(Photo image){
		mProvider.albAccess.imgAccess.saveLocalImage(image, true);
	}
	/// call service to write new image
	public boolean newImage(Photo photo){
		mProvider.imgAccess.saveLocalImage(photo, false);   
		return true; 
	}
	///---------------------------------------------
	/*
	 * Album read/write/update methods
	 */
	public void writeAlbum(Album album){
		mProvider.albAccess.saveLocalAlbum(album, false);
	}
	public void writeAlbumImages(Album album, boolean update){
		for(int i=0; i< album.photos.size(); i++){
			System.out.println("writing photo" + album.photos.get(i).toString());
			mProvider.imgAccess.saveLocalImage(album.photos.get(i), update);
		}
	}
	
	public void updateAlbum(Album album){
		mProvider.albAccess.saveLocalAlbum(album, true);
	}
	public ArrayList<Album> readLocalAlbums() {
		return mProvider.albAccess.readAllAlbums();
	}
	public ArrayList<Photo> readImages(String id, boolean clean) {
		return mProvider.albAccess.readImagesByAlbum(id, clean);
	}
	public Photo readImage(String id) {
		return mProvider.imgAccess.getSpecificPhoto(id);
	}
	public Album readAlbum(String id) {
		return mProvider.albAccess.getSpecificAlbum(id);
		
	}
	///------------------------------------------
	/*
	 *   Invites add/update/remove  tally, sync all
	 */
	public void writeInvite(Invite invite, boolean update){
		mProvider.invAccess.saveInvite(invite, update);
	}
	public LifeInvite getInvites(boolean clean, String status){
		String id = "";
		id = getProfile().userGID;
		return mProvider.invAccess.getInvites(clean, status, id);
	}
	public void writeAllInvite(ArrayList<Invite> life, boolean update){
		mProvider.invAccess.saveAllInvite(life, update);
	}
	///------------------------------------------
	/*
	 *   Sync add/update/remove  tally, sync all
	 */
	///save the remote sync tally and timestamps
	public void writeTally(SyncObj sync, boolean update){
		mProvider.syncAllAccess.saveLocalTally(sync, update);
	}
	/*
	 *  Get local sync Tally
	 */
	public SyncObj getSyncTally(){
		return mProvider.syncAllAccess.getLocalTally();
	}
	//// Sync EVERYTHING
	public void getAllSync(){
		boolean pending = false;
		if(isSyncOn()){
			if(mProvider.networkAccess.isNetworkAvailable()){
				mProvider.syncAllAccess.getAllSync();
			}
		}
	}
	///Tell the autosync their is pending data to send (write this to the database
	public void setPendingData(boolean pending, boolean update) {
		mProvider.prefAccess.setPendingData(pending, update);
	}
	// start sync thread
	public void startThread() {
		mSyncProvider.testThread();
	}
	public void saveLocalTally(SyncObj sync) {
		mProvider.syncAllAccess.saveLocalTally(sync, true);
	}
	public SyncMsgObj getLocalMsgTally() {
		return mProvider.syncMsgAccess.getLocalMsgTally();
	}
	
	///Messaging
	public void writeNewMsg(Message mes, boolean update) {
		mProvider.msgAccess.saveMessage(mes, update);
		setPendingData(true, true);
	}
	public void startMsgThread() {
		mSyncProvider.msgThread();
	}





}
