package com.hutchgrant.app;

import java.util.ArrayList;

import com.hutchgrant.Elements.Album;
import com.hutchgrant.Elements.LifeAlbum;
import com.hutchgrant.Elements.Photo;
import com.hutchgrant.provider.tunadatamodel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class AlbumAccess {
	private static final String TAG = null;
	private static AlbumAccess instance;
	static Context ctx;
	ContentResolver cr;
	Cursor cur;
	ImageAccess imgAccess;
	
	public AlbumAccess(Context context){
		ctx = context;
		cr = ctx.getContentResolver();
		ImageAccess.initAccess(context);
		imgAccess = ImageAccess.getInstance();
	}
	
	public static AlbumAccess getInstance(){
		return instance;
	}
	
	public static void initAccess(Context context){
		if(instance == null){
			instance = new AlbumAccess(context);
		}
	}
	
	/* 
	 *  write to database, one Album's detail object. if update is true, it will update rather than insert.
	 */
	public void saveLocalAlbum(Album album, boolean update) {
    	ContentValues val = new ContentValues();
    	if(update){
    		val.put(tunadatamodel.SQL_INS_REP, true);  
    	}
    	val.put(tunadatamodel.TunaAlbum.Cols.ALBID, (album.ID != null) ? album.ID : null);
    	val.put(tunadatamodel.TunaAlbum.Cols.ALBNAME, (album.albName != null) ? album.albName : null);
    	val.put(tunadatamodel.TunaAlbum.Cols.ALBOWNER,(album.authorID != null) ? album.authorID : null);
    	val.put(tunadatamodel.TunaAlbum.Cols.ALBSIZE,(album.albSize != 0) ? album.albSize : 0);
    	val.put(tunadatamodel.TunaAlbum.Cols.ALBSYNCD, (album.syncDate != null) ? album.syncDate : null);
    	val.put(tunadatamodel.TunaAlbum.Cols.ALBSYNCT, (album.syncTime != null) ? album.syncTime : null);
    	cr.insert(tunadatamodel.TunaAlbum.CONTENT_URI, val);
	}

	/*
	 * Read All Albums from database into an ArrayList of Albums (CLEAN not removed).
	 */
	public ArrayList<Album> readAllAlbums() {
		
		ArrayList<Album> ListAlbums = new ArrayList<Album>();
		
		ContentResolver cr = ctx.getContentResolver();
		Cursor cur = cr.query(tunadatamodel.TunaAlbum.CONTENT_URI, null,null, null, null);
		int idIndex = 0, nameIndex = 0, ownerIndex = 0, dateSyncIndex = 0, timeSyncIndex = 0, dateCreatedIndex = 0;
		idIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBID);
		nameIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBNAME);
		ownerIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBOWNER);
		dateSyncIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBSYNCD);
		timeSyncIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBSYNCT);
		dateCreatedIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBINITD);
		int columnCount = cur.getColumnCount();
		
		String albID = null, albName = null, albAuthor = null, datesynced = null, timesynced = null, datecreated = null;
		if (cur.moveToFirst()) {
		
			do {
				for(int i = 0; i < columnCount; i++){
					if(i == nameIndex){
						albName = cur.getString(i);
					}else if(i == ownerIndex){
						albAuthor = cur.getString(i);
					}else if(i == idIndex){
						albID = cur.getString(i);
					} else if(i == dateSyncIndex){
						datesynced = cur.getString(i);
					}else if(i == timeSyncIndex){
						timesynced = cur.getString(i);
					} else if(i == dateCreatedIndex){
						datecreated = cur.getString(i);
					}
				}
			if(datesynced.equals("removed") == false){
				//			System.out.println(" album id: "+albID + " name: "+albName + " datesynced: "+datesynced + " datecreated: "+ datecreated);
					Album album = new Album(ctx);
					album.fill(albID, albAuthor, albName);
					album.setSync(datesynced, timesynced);
					ListAlbums.add(album);
			}
			}while(cur.moveToNext());
		}
		
		cur.close();
		return ListAlbums;
	}

	/*
	 *  Read All images within a single Album by that Album's ID returns ArrayList of Photo objects
	 *  IF CLEAN = true // get clean photos only
	 *  IF CLEAN = false // get dirty photos only
	 */
	public ArrayList<Photo> readImagesByAlbum(String id ,boolean clean) {
		ArrayList<Photo> ListPhotos = new ArrayList<Photo>();
		System.out.println("reading album "+id +".");
		cr = ctx.getContentResolver();
		cur = cr.query(tunadatamodel.TunaImage.CONTENT_URI, null, tunadatamodel.TunaImage.Cols.IMGALBID+" = ?", new String[]{id},
				null);
		int imgidIndex = 0, imgAlbIdIndex = 0, nameIndex = 0, usernameIndex = 0, useridIndex = 0, 
				dateIndex = 0, titleIndex = 0, descIndex = 0, imgSyncIndex = 0, imgTypeIndex = 0,
				tokenIndex = 0, timeIndex = 0;
		imgidIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGID);
		imgAlbIdIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGALBID);
		usernameIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUSR);
		useridIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUSRID);
		nameIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGNAME);
		dateIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUPDATE);
		timeIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUPTIME);
		tokenIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGUPTOKEN);
		titleIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGTITLE);
		descIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGDESC);
		imgSyncIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGSYNC);
		imgTypeIndex = cur.getColumnIndex(tunadatamodel.TunaImage.Cols.IMGTYPE);
		int columnCount = cur.getColumnCount();
		
		String username = null, albid = null, userid = null, imgid = null, imgName = null, imgDate = null, imgTitle = null, imgDesc = null;
		String synced = null, type = null, token = null, imgTime = null;
		if(cur.moveToFirst()){
			do {
				for(int i = 0; i < columnCount; i++){
					if(i == nameIndex){
						imgName = cur.getString(i);
					}else if(i == dateIndex){
						imgDate = cur.getString(i);
					}else if(i == timeIndex){
						imgTime = cur.getString(i);
					}else if(i == titleIndex){
						imgTitle = cur.getString(i);
					}else if(i == descIndex){
						imgDesc = cur.getString(i);
					}else if(i == usernameIndex){
						username = cur.getString(i);
					}else if(i == imgidIndex){
						imgid = cur.getString(i);
					} else if(i == useridIndex){
						userid = cur.getString(i);
					} else if(i == imgAlbIdIndex){
						albid = cur.getString(i);
					}else if(i == imgSyncIndex){
						synced = cur.getString(i);
					} else if( i == imgTypeIndex){
						type = cur.getString(i);
					}else if( i == tokenIndex){
						token = cur.getString(i);
					}
				}
				System.out.println("TEST READ PHOTO id: "+imgid + " title: "+imgTitle+ " albumid: "+albid + " token: "+token + "uploadDate: " +imgDate);

					if(clean){
						if(!imgDate.equals("removed")){
							System.out.println("CLEAN photo id: "+imgid + " title: "+imgTitle+ " albumid: "+albid + " token: "+token + "uploadDate: " +imgDate);
							Photo undatedPhoto = new Photo(type, imgName, username, userid, albid, imgTitle,imgDesc,Boolean.parseBoolean(synced));
							undatedPhoto.ID = imgid;
							undatedPhoto.setToken(token);
							undatedPhoto.setSync(imgDate, imgTime);
							ListPhotos.add(undatedPhoto);
						}
					}else{
						if(imgDate.equals("updated") == true || imgDate.equals("removed") == true || synced.equals("false") == true){
							System.out.println("DIRTY photo id: "+imgid + " title: "+imgTitle+ " albumid: "+albid + " token: "+token + "uploadDate: " +imgDate);
							Photo undatedPhoto = new Photo(type, imgName, username, userid, albid, imgTitle,imgDesc,Boolean.parseBoolean(synced));
							undatedPhoto.ID = imgid;
							undatedPhoto.setToken(token);
							undatedPhoto.setSync(imgDate, imgTime);
							ListPhotos.add(undatedPhoto);
						}
					}
			}while(cur.moveToNext());
		}
		cur.close();
		return ListPhotos;
	}
	
	/*
	 * update all albums(from within parameter array list of albums) within the database setting sync date for
	 * all included albums and photos to a syncdate removed".  If album or photo was never synced, remove the
	 * physical image, delete from database.
	 */
	public void setAlbumsForRemoval(ArrayList<Album> albums){
		for(int i =0; i< albums.size(); i++){
			if(albums.get(i).isSelected()){
				if(albums.get(i).syncDate != ""){
					albums.get(i).setSync("removed", "");
					saveLocalAlbum(albums.get(i), true);
					for(int x=0; x< albums.get(i).photos.size(); x++){
						if(albums.get(i).photos.get(x).Synced){
							albums.get(i).photos.get(x).UploadDate = "removed";
							imgAccess.saveLocalImage(albums.get(i).photos.get(x), true);
						}else{
							imgAccess.deleteImageFromUser(albums.get(i).photos.get(x));
						}
					}
				}else{
					imgAccess.removeSingleAlbum(albums.get(i));
					for(int x=0; x< albums.get(i).photos.size(); x++){
						imgAccess.deleteImageFromUser(albums.get(i).photos.get(x));
					}
				}
			}
		}
	}
	public int getLastAlbumID(){
		int LastID = 0;
		ContentResolver cr = ctx.getContentResolver();

		Cursor cur = cr.query(tunadatamodel.TunaAlbum.CONTENT_URI, null,null, null,null);
		if(cur.moveToLast()){
			LastID = cur.getInt(0);
		}
		cur.close();
		return LastID;
	}

	
	/*
	 * Read All DIRTY Albums and their images from database into an ArrayList of Albums 
	 */
	public ArrayList<Album> readAllDirtyAlbumsPhotos() {
		
		ArrayList<Album> ListAlbums = new ArrayList<Album>();
		
		ContentResolver cr = ctx.getContentResolver();
		Cursor cur = cr.query(tunadatamodel.TunaAlbum.CONTENT_URI, null,null, null, null);
		int idIndex = 0, nameIndex = 0, ownerIndex = 0, dateSyncIndex = 0, timeSyncIndex = 0, dateCreatedIndex = 0;
		idIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBID);
		nameIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBNAME);
		ownerIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBOWNER);
		dateSyncIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBSYNCD);
		timeSyncIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBSYNCT);
		dateCreatedIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBINITD);
		int columnCount = cur.getColumnCount();
		
		String albID = null, albName = null, albAuthor = null, datesynced = null, timesynced = null, datecreated = null;
		if (cur.moveToFirst()) {
		
			do {
				for(int i = 0; i < columnCount; i++){
					if(i == nameIndex){
						albName = cur.getString(i);
					}else if(i == ownerIndex){
						albAuthor = cur.getString(i);
					}else if(i == idIndex){
						albID = cur.getString(i);
					} else if(i == dateSyncIndex){
						datesynced = cur.getString(i);
					}else if(i == timeSyncIndex){
						timesynced = cur.getString(i);
					} else if(i == dateCreatedIndex){
						datecreated = cur.getString(i);
					}
				}
	//				System.out.println(" album id: "+albID + " name: "+albName + " datesynced: "+datesynced + " datecreated: "+ datecreated);
			if(datesynced.equals("removed") == true || datesynced.equals("updated") == true || datesynced.equals("") == true){
					Album album = new Album(ctx);
					album.fill(albID, albAuthor, albName);
					album.photos = readImagesByAlbum(albID, false);
					album.setSync(datesynced, timesynced);
					ListAlbums.add(album);
			}
			}while(cur.moveToNext());
		}
		
		cur.close();
		return ListAlbums;
	}
	
	public ArrayList<Album> saveToLife(ArrayList<Album> albumList) {
		LifeAlbum life = new LifeAlbum(ctx);
		life.Albums = readAllAlbums();
		
		Album currentAlbum = new Album(ctx);
		
		ArrayList<Album>newLifeImgs = new ArrayList<Album>();
		
		for(int i=0; i<albumList.size(); i++){
			///check if album already exists
			currentAlbum = life.getSpecifcAlbum(albumList.get(i).getID());
			if(currentAlbum != null){   /// album already exists
				/// check if sync date's match
				saveLocalAlbum(albumList.get(i), true);    // add new album
				for(int x=0; x<albumList.get(i).photos.size(); x++){
					System.out.println(albumList.get(i).photos.get(x).toString());
					imgAccess.saveLocalImage(albumList.get(i).photos.get(x), true);	// save all new album images				
				}
			///	saveLocalAlbum(albumList.get(i), true);
				/// check whether the album's images already exist 
			}else{  /// album doesn't exist on client's device
				saveLocalAlbum(albumList.get(i), false);    // add new album
				for(int x=0; x<albumList.get(i).photos.size(); x++){
					imgAccess.saveLocalImage(albumList.get(i).photos.get(x), false);	// save all new album images				
				}
				newLifeImgs.add(albumList.get(i));
			}
		}
		return newLifeImgs;  		
	}

	public void saveImagesByAlb(Album album, boolean update) {
		for(int i =0; i< album.photos.size(); i++){
			imgAccess.saveLocalImage(album.photos.get(i), update);
		}
	}

	public Album getSpecificAlbum(String id) {
		Album album = new Album(ctx);
		ContentResolver cr = ctx.getContentResolver();
		Cursor cur = cr.query(tunadatamodel.TunaAlbum.CONTENT_URI, null,tunadatamodel.TunaAlbum.Cols.ALBID+" = ?", new String[]{id},
				null);
		int idIndex = 0, nameIndex = 0, ownerIndex = 0, dateSyncIndex = 0, timeSyncIndex = 0, dateCreatedIndex = 0;
		idIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBID);
		nameIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBNAME);
		ownerIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBOWNER);
		dateSyncIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBSYNCD);
		timeSyncIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBSYNCT);
		dateCreatedIndex = cur.getColumnIndex(tunadatamodel.TunaAlbum.Cols.ALBINITD);
		int columnCount = cur.getColumnCount();
		
		String albID = null, albName = null, albAuthor = null, datesynced = null, timesynced = null, datecreated = null;
		if (cur.moveToFirst()) {
		
			do {
				for(int i = 0; i < columnCount; i++){
					if(i == nameIndex){
						albName = cur.getString(i);
					}else if(i == ownerIndex){
						albAuthor = cur.getString(i);
					}else if(i == idIndex){
						albID = cur.getString(i);
					} else if(i == dateSyncIndex){
						datesynced = cur.getString(i);
					}else if(i == timeSyncIndex){
						timesynced = cur.getString(i);
					} else if(i == dateCreatedIndex){
						datecreated = cur.getString(i);
					}
				}
				System.out.println("READING ALBUMS FROM DB");
				System.out.println(" album id: "+albID + " name: "+albName + " datesynced: "+datesynced + " datecreated: "+ datecreated);
					album = new Album(ctx);
					album.fill(albID, albAuthor, albName);
					album.setSync(datesynced, timesynced);
			}while(cur.moveToNext());
		}
		
		cur.close();
		
		
		return album;
	}
}
