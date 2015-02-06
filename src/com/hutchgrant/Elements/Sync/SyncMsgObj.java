package com.hutchgrant.Elements.Sync;

public class SyncMsgObj {

	public String syncMsgToken = "";
	public String syncMsgTime = "";
	public String syncMsgDate = "";
	public int syncMsgSntAmount = 0;
	public int syncMsgRecAmount = 0;
	public int syncMsgCached = 0;
	
	public SyncMsgObj(){
		this.syncMsgToken = "";
		this.syncMsgTime = "";
		this.syncMsgDate = "";
		this.syncMsgSntAmount = 0;
		this.syncMsgRecAmount = 0;
	}
	
	public void fill(String syncToken, String syncDate,String syncTime, int syncRecAmt, int syncSntAmt) {
		this.syncMsgToken = syncToken;
		this.syncMsgDate = syncDate;
		this.syncMsgTime = syncTime;
		this.syncMsgSntAmount = syncSntAmt;
		this.syncMsgRecAmount = syncRecAmt;
	}
}
