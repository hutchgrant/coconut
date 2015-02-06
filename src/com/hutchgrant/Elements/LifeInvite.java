package com.hutchgrant.Elements;

import java.util.ArrayList;
import java.util.UUID;

import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.app.TunaApp;
import com.hutchgrant.tasks.InviteTask;

import android.app.Activity;
import android.content.Context;

public class LifeInvite {

	public ArrayList<Invite> invites;

	public LifeInvite() {
		this.invites = new ArrayList<Invite>();
	}

	public LifeInvite(LifeInvite life) {
		this.invites = life.invites;
	}

	public void add(Invite inv) {
		this.invites.add(inv);
	}

	public Invite getInvite(int position) {
		return this.invites.get(position);
	}

	public Invite getInviteByID(String id) {
		Invite inv = new Invite();

		for (int i = 0; i < this.invites.size(); i++) {
			if (this.invites.get(i).InviteID.equals(id)) {
				inv = this.invites.get(i);
			}
		}
		return inv;
	}

	public void removeInvite(int position) {
		this.invites.get(position).InviteStatus = "removed";
		// save removed invite (pre-sync)
		this.invites.remove(position);
	}

	public void confirm(int position) {
		this.invites.get(position).InviteStatus = "confirm";
	}

	public void fill(String invToken, String invSenderID, String invName,
			String invRecName, String invRecID, String invRecEmail,
			String invRecPhone, String invDate, String invTime, String invStatus) {

		Invite inv = new Invite();
		inv.fill(invToken, invSenderID, invName, invRecName, invRecID,
				invRecEmail, invRecPhone, invDate, invTime, invStatus);
		add(inv);
	}

	public void init(Context ctx) {

	}

	public LifeInvite getCleanInviteData(Context ctx, String type) {
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		this.invites = new ArrayList<Invite>();
		return app.getInvites(true, type);
	}

	public LifeInvite getDirtyInviteData(Context ctx, String type) {
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		this.invites = new ArrayList<Invite>();
		return app.getInvites(false, type);
	}

	public LifeInvite getSelected(ArrayList<Invite> lifeInvite) {
		LifeInvite life = new LifeInvite();

		for (int i = 0; i < lifeInvite.size(); i++) {
			if (lifeInvite.get(i).isSelected()) {
				life.add(lifeInvite.get(i));
			}
		}

		return life;
	}

	public void setRemoved(Context ctx, ArrayList<Invite> selInvites) {
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		boolean pending = false;
		for (int i = 0; i < invites.size(); i++) {
			if (!invites.get(i).InviteDate.equals("")) { // / set synced invite
															// to removed
				invites.get(i).InviteStatus = "removed";
				pending = true;
			} else { // / delete unsynced invite
				invites.remove(i);
			}
		}
		app.writeAllInvite(selInvites, true); // reminder to remove after synced
												// invite status = removed
		if (pending) {
			app.setPendingData(true, true);
		}

	}

	public void setConfirmed(Context ctx, ArrayList<Invite> selInvites) {
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();

		for (int i = 0; i < invites.size(); i++) {
			if (invites.get(i).InviteStatus == "na") {
				invites.get(i).InviteStatus = "confirmed";
			}

		}
		app.writeAllInvite(selInvites, true); // reminder to removed after
												// synced invite status =
												// confirmed
		app.setPendingData(true, true);
		startInviteTask(ctx);

	}

	public void convertContactsToInvite(Context ctx,
			ArrayList<Contact> lifeContact) {
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		User prof = new User();
		prof = app.getProfile();
		String token = "";
		Invite inv = new Invite();
		for (int i = 0; i < lifeContact.size(); i++) {
			if (lifeContact.get(i).isSelected()) {
				inv = new Invite();
				token = UUID.randomUUID().toString();
				inv.fill(token, prof.userGID, prof.userGName, lifeContact
						.get(i).getName(), lifeContact.get(i).getGoogleID(),
						lifeContact.get(i).getEmail(), lifeContact.get(i)
								.getPhone(), "0", "0", "na");
				add(inv);
				System.out.println("INVITE " + i + " name = "
						+ inv.RecipientName);
			}
		}
		app.writeAllInvite(this.invites, false);
		app.setPendingData(true, true);
		startInviteTask(ctx);
	}
	
	public void startInviteTask(Context ctx){
		InviteTask it = new InviteTask(ctx);
		it.setSyncType(1);
		it.execute(new SyncObj());
	}

}
