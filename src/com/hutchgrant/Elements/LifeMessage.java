package com.hutchgrant.Elements;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;

import com.hutchgrant.app.ProfileAccess;
import com.hutchgrant.app.TunaApp;

public class LifeMessage {

	
	public ArrayList<Message> messages;
	
	
	public LifeMessage(){
		this.messages = new ArrayList<Message>();
	}
	
	
	public void addMsg(Message msg){
		this.messages.add(msg);
	}
	
	public void removeMsg(int position){
		this.messages.remove(position);
	}
	
	public Message getMsgByPos(int position){
		return this.messages.get(position);
	}
	
	public Message getMsgByID(String mID){
		
		Message msg = new Message();
		for(int i=0; i< this.messages.size(); i++){
			
			if(getMsgByPos(i).MID.equals(mID)){
				msg =getMsgByPos(i); 
			}
		}
		
		return msg;
	}
	
	public ArrayList<Message> getAll(){
		return this.messages;
	}
	
	public void setAll(LifeMessage msg){
		this.messages = new ArrayList<Message>();
		this.messages = msg.getAll();
	}
	
	public void writeNew(Context ctx, String msgType, String content, String contactID, String groupID){
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		
		String token = "", userID = "", userName = "";
		User profile = new User();
		profile = app.getProfile();
		userID = profile.userGID;
		userName = profile.userGName;
		token = UUID.randomUUID().toString();
		
		Message Mes = new Message();
		Mes.fill(token, userID, userName, msgType, content, contactID, groupID, "na", "na");
		
		app.writeNewMsg( Mes, false);
		app.setPendingData(true, true);
	}


	public LifeMessage getImgMsgs() {
		LifeMessage lifeMsg = new LifeMessage();
		for(int i =0; i< this.messages.size(); i++){
			if(this.messages.get(i).Type.equals("IMG")){
				lifeMsg.addMsg(this.messages.get(i));
			}
		}
		return lifeMsg;
	}
	
	public LifeMessage getAlbMsgs() {
		LifeMessage lifeMsg = new LifeMessage();
		for(int i =0; i< this.messages.size(); i++){
			if(this.messages.get(i).Type.equals("ALB")){
				lifeMsg.addMsg(this.messages.get(i));
			}
		}
		return lifeMsg;
	}
}
