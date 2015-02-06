package com.hutchgrant.Elements.Sync;

public class SyncObj {

	public String syncUserID = "";
	public String syncToken = "";
	public String syncImgDate = "";
	public String syncImgTime = "";
	public String syncGrpDate = "";
	public String syncGrpTime = "";
	public String syncProfDate = "";
	public String syncProfTime = "";
	public int syncAlbAmount = 0;
	public int syncImgAmount = 0;
	public int syncGrpAmount = 0;
	public int syncConAmount  = 0;
	public String syncInviteToken = "";
	public String syncInviteTime = "";
	public String syncInviteDate = "";
	public int syncInvRecAmount = 0;
	public int syncInvSntAmount = 0;
	public String syncMsgToken = "";
	public String syncMsgDate = "";
	public String syncMsgTime = "";
	public int syncMsgSntAmount = 0;
	public int syncMsgRecAmount = 0;
	
	public SyncObj(){
		
		this.syncUserID = "";
		this.syncToken = "";
		this.syncImgDate = "";
		this.syncImgTime = "";
		this.syncGrpDate = "";
		this.syncGrpTime = "";
		this.syncProfTime = "";
		this.syncProfDate = "";
		this.syncInviteToken = "";
		this.syncInviteTime = "";
		this.syncInviteDate = "";
		this.syncAlbAmount = 0;
		this.syncImgAmount = 0;
		this.syncGrpAmount = 0;
		this.syncConAmount = 0;
		this.syncInvRecAmount = 0;
		this.syncInvSntAmount = 0;
		this.syncMsgToken = "";
		this.syncMsgDate = "";
		this.syncMsgTime = "";
		this.syncMsgSntAmount = 0;
		this.syncMsgRecAmount = 0;
	}
	
	public void fill(String profileID, String token, String imgDate, String imgTime, String grpDate, String grpTime, String profDate, String profTime, int albAmount, int imgAmount, int grpAmount, int cntAmount){
		this.syncUserID = profileID;
		this.syncToken = token;
		this.syncImgDate = imgDate;
		this.syncImgTime = imgTime;
		this.syncGrpDate = grpDate;
		this.syncGrpTime = grpTime;
		this.syncProfDate = profDate;
		this.syncProfTime = profTime;
		this.syncAlbAmount = albAmount;
		this.syncImgAmount = imgAmount;
		this.syncGrpAmount = grpAmount;
		this.syncConAmount = cntAmount;
		
	}
	
	public void fillInvite(String invToken, String invDate, String invTime, int recAmount, int sntAmount){
		this.syncInviteToken = invToken;
		this.syncInviteTime = invTime;
		this.syncInviteDate = invDate;
		this.syncInvRecAmount = recAmount;
		this.syncInvSntAmount = sntAmount;		
	}
	
	
	public void fillMsg(String token, String date, String time, int sntAmount, int recAmount){
		this.syncMsgToken = token;
		this.syncMsgDate = date;
		this.syncMsgTime = time;
		this.syncMsgSntAmount = sntAmount;
		this.syncMsgRecAmount = recAmount;
	}
	
}
