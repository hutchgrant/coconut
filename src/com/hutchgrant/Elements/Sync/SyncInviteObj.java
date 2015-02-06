package com.hutchgrant.Elements.Sync;

public class SyncInviteObj {

	public String syncInviteToken = "";
	public String syncInviteDate = "";
	public String syncInviteTime = "";
	public int syncInvRecAmount = 0;
	public int syncInvSntAmount = 0;
	
	public SyncInviteObj(){
		this.syncInviteToken = "";
		this.syncInviteTime = "";
		this.syncInviteDate = "";
		this.syncInvRecAmount = 0;
		this.syncInvSntAmount = 0;
	}
	
	public void fill(String invToken, String invDate, String invTime, int recAmount, int sntAmount){
		this.syncInviteToken = invToken;
		this.syncInviteTime = invTime;
		this.syncInviteDate = invDate;
		this.syncInvRecAmount = recAmount;
		this.syncInvSntAmount = sntAmount;
	}
}
