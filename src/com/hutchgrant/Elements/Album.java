package com.hutchgrant.Elements;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;

import com.hutchgrant.app.TunaApp;
import com.hutchgrant.coconut.Endpoints;

public class Album {

	public String ID = "";
	public String authorID = "";
	public String albName = "";
	public ArrayList<Photo> photos;
	public int albSize = 0;
	public String[] uriList;
	public boolean selected= false;
	public String syncDate = "";
	public String syncTime = "";
	Context ctx;
	
	public Album(Context context){
		this.photos = new ArrayList<Photo>();
		this.ctx = context;
		fill("", "", "");
	}
	
	public Album(String id, String userid, String albumname){
		fill(id, userid, albumname);
	}
	
	public void fill(String id, String userid, String name){
		this.ID = id;
		this.authorID = userid;
		this.albName = name;
	}
	
	public void getDefaultPhotos(){		
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		this.photos = app.readAllImages();
	}
	
	public void addPhoto(Photo photo){
		photos.add(photo);
		albSize++;
	}
	
	public void addPhotos(ArrayList<Photo> images){
		this.photos = images;
	}
	
	public void removePhoto(Photo photo){
		for(int i=0; i<photos.size(); i++){
			if(photos.get(i).ID == photo.ID){
				photos.remove(i);
				albSize--;
			}
		}
	}
	
	public void removeSelected(){
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		User profile = new User();
		profile = app.getProfile();
		ArrayList<Photo> selectImgs = new ArrayList<Photo>();
		for(int i =0; i< photos.size(); i++){
			if(photos.get(i).Selected == true){
				if(!photos.get(i).userID.equals(profile.userGID)){
					app.removeLocalImage(photos.get(i));
				}else{selectImgs.add(photos.get(i));
					selectImgs.add(photos.get(i));
					photos.remove(i);
				}
			}
		}
		app.setImagesForRemoval(selectImgs);

	}
	
	public Album getSelectedPhotos(){
		Album sImg = new Album(ctx);
		for(int i=0; i< photos.size(); i++){
			if(photos.get(i).Selected){
				sImg.addPhoto(photos.get(i));
			}
		}
		return sImg;
	}
	
	public void modifyPhoto(Photo photo){
		for(int i=0; i<photos.size(); i++){
			if(photos.get(i).ID == photo.ID){
				photos.add(i, photo);
			}
		}
	}
	
	public Photo getPhoto(int index){
		return this.photos.get(index);
	}
	
	public void selectPhoto(int position, boolean select){
		photos.get(position).Selected = select;
	}

	public String[] fillURIList() {
		this.uriList = new String[photos.size()];
		
		for(int i=0; i< photos.size(); i++){
			this.uriList[i] = "file:///mnt/sdcard/Pictures/Tuna/"+photos.get(i).Name;
			System.out.println("THIS PHOTO's UPLOAD TOKEN IS === "+photos.get(i).UploadToken);
			System.out.println("THIS PHOTO's UPLOAD DATE IS === "+photos.get(i).UploadDate);
		}
		return uriList;
	}
	public String[] getUriList(){
		return this.uriList;
	}

	public void setSelected(boolean checked) {
		this.selected = checked;
	}

	public String getID() {
		return this.ID;
	}
	
	public String getName() {
		return this.albName;
	}
	public boolean isSelected(){
		return this.selected;
	}

	public void getPhotos(String albumID, boolean clean) {
		this.ID = albumID;
		System.out.println("GETING PHOTOS FOR ALBUM ID: "+albumID);
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		this.photos = app.readImages(this.ID, true);
	}
	
	public void setSync(String syncd, String synct){
		this.syncDate = syncd;
		this.syncTime = synct;
	}
	
	public String CreateSendAlb(String profileGID){
		Album alb = new Album(ctx);
		String albumID = UUID.randomUUID().toString();
		alb.fill(albumID, profileGID, albName);
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		app.writeAlbum(alb);	
		app.setPendingData(true, true);
	//	sendMessage(Endpoints.EVENT_ALBUM_WRITE, true);
		return albumID;
	}
	
	public void giveRandomID(){
		this.ID = UUID.randomUUID().toString();
	}
	
	public void setAllImagesToAlbum(){
		for(int i=0; i<this.photos.size(); i++){
			photos.get(i).AlbumID = this.ID;
			photos.get(i).ID = UUID.randomUUID().toString();
		}
	}

}
