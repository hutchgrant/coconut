package com.hutchgrant.Elements;

public class PrefObj {

	
	public boolean synced = true;
	public int syncTime = 30;
	public void fill(boolean prefSyncOn, int prefSyncTime) {
		this.synced = prefSyncOn;
		this.syncTime = prefSyncTime;
	}
}
