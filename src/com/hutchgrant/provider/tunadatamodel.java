package com.hutchgrant.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

public class tunadatamodel {
	
	public static final String AUTHORITY = "com.coconut.tunaprovider";
	private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
	public static final UriMatcher URI_MATCHER = MatchContentURI();
	public static final String SQL_INS_REP = "sql_insert_replace";

	private static UriMatcher MatchContentURI(){
		final UriMatcher checkmatch = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = AUTHORITY;
		checkmatch.addURI(authority, TunaProfile.PATH, TunaProfile.PATH_TOKEN);
		checkmatch.addURI(authority, TunaProfile.PATH_FOR_ID, TunaProfile.PATH_FOR_ID_TOKEN);
		checkmatch.addURI(authority, TunaImage.PATH, TunaImage.PATH_TOKEN);
		checkmatch.addURI(authority, TunaImage.PATH_FOR_ID, TunaImage.PATH_FOR_ID_TOKEN);
		checkmatch.addURI(authority, TunaAlbum.PATH, TunaAlbum.PATH_TOKEN);
		checkmatch.addURI(authority, TunaAlbum.PATH_FOR_ID, TunaAlbum.PATH_FOR_ID_TOKEN);
		checkmatch.addURI(authority, TunaContact.PATH, TunaContact.PATH_TOKEN);
		checkmatch.addURI(authority, TunaContact.PATH_FOR_ID, TunaContact.PATH_FOR_ID_TOKEN);
		checkmatch.addURI(authority, TunaGroup.PATH, TunaGroup.PATH_TOKEN);
		checkmatch.addURI(authority, TunaGroup.PATH_FOR_ID, TunaGroup.PATH_FOR_ID_TOKEN);
		checkmatch.addURI(authority, TunaSync.PATH, TunaSync.PATH_TOKEN);
		checkmatch.addURI(authority, TunaSync.PATH_FOR_ID, TunaSync.PATH_FOR_ID_TOKEN);
		checkmatch.addURI(authority, TunaPref.PATH, TunaPref.PATH_TOKEN);
		checkmatch.addURI(authority, TunaPref.PATH_FOR_ID, TunaPref.PATH_FOR_ID_TOKEN);
		checkmatch.addURI(authority, TunaInvite.PATH, TunaInvite.PATH_TOKEN);
		checkmatch.addURI(authority, TunaInvite.PATH_FOR_ID, TunaInvite.PATH_FOR_ID_TOKEN);
		checkmatch.addURI(authority, TunaMessage.PATH, TunaMessage.PATH_TOKEN);
		checkmatch.addURI(authority, TunaMessage.PATH_FOR_ID, TunaMessage.PATH_FOR_ID_TOKEN);
		return checkmatch;
	}
	
	public static class TunaProfile {
		
		// an identifying name for entity
				public static final String NAME = "profile";
				
				// define a URI paths to access entity
				// BASE_URI/restaurants - for list of restaurants
				// BASE_URI/restaurants/* - retreive specific restaurant by id
				// the toke value are used to register path in matcher (see above)
				public static final String PATH = "profiles";
				public static final int PATH_TOKEN = 100;
				public static final String PATH_FOR_ID = "profiles/*";
				public static final int PATH_FOR_ID_TOKEN = 200;
				
				// URI for all content stored as Restaurant entity
				// BASE_URI + PATH ==> "content://com.favrestaurant.contentprovider/restaurants";
				public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
				
				// define content mime type for entity
				public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.coconut.app";
				public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.coconut.app";
				
				// a static class to store columns in entity
				public static class Cols {
					public static final String ID = BaseColumns._ID; // convention
					public static final String USRID  = "profile_id";
					public static final String USRNAME = "profile_name";
					public static final String USRLOCATE = "profile_location";
					public static final String USRPROF = "profile_url";
					public static final String USRIMGURL = "profile_imgurl";
					public static final String USRIMG = "profile_image";
					public static final String USRTOKEN = "profile_token";
					public static final String USRREGDATE = "profile_reg_date";
					public static final String USRREGTIME = "profile_reg_time";
					public static final String USRLOGINDATE = "profile_login_date";
					public static final String USRLOGINTIME = "profile_login_time";
					public static final String USRPHONE = "profile_phone";
					public static final String USREMAIL = "profile_email";
					public static final String MAXALBUM = "profile_max_album";
					public static final String MAXIMG = "profile_max_img";
					public static final String MAXGRP = "profile_max_grp";
					public static final String MAXCONT = "profile_max_cont";
				}
				
	}
	public static class TunaImage {
		
		// an identifying name for entity
				public static final String NAME = "image";
				
				// define a URI paths to access entity
				// BASE_URI/restaurants - for list of restaurants
				// BASE_URI/restaurants/* - retreive specific restaurant by id
				// the toke value are used to register path in matcher (see above)
				public static final String PATH = "images";
				public static final int PATH_TOKEN = 300;
				public static final String PATH_FOR_ID = "images/*";
				public static final int PATH_FOR_ID_TOKEN = 400;
				
				// URI for all content stored as Restaurant entity
				// BASE_URI + PATH ==> "content://com.favrestaurant.contentprovider/restaurants";
				public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
				
				// define content mime type for entity
				public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.coconut.app";
				public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.coconut.app";
				
				// a static class to store columns in entity
				public static class Cols {
					public static final String ID = BaseColumns._ID; // convention
					public static final String IMGID  = "image_id";
					public static final String IMGALBID = "image_album_id";
					public static final String IMGNAME = "image_name";
					public static final String IMGTYPE = "image_type";
					public static final String IMGUSR = "image_author";
					public static final String IMGUSRID = "image_authorID";
					public static final String IMGTITLE = "image_title";
					public static final String IMGDESC = "image_description";
					public static final String IMGUPTIME = "image_upload_time";
					public static final String IMGUPDATE = "image_upload_date";
					public static final String IMGUPTOKEN = "image_upload_token";
					public static final String IMGTPLUS = "image_total_plus";
					public static final String IMGTMINUS = "image_total_minus";
					public static final String IMGTCOMMENTS = "image_total_comments";
					public static final String IMGPERMISS = "image_permission";
					public static final String IMGSYNC = "image_synced";
				}
				
	}
	public static class TunaAlbum {
		
		// an identifying name for entity
				public static final String NAME = "album";
				
				// define a URI paths to access entity
				// BASE_URI/restaurants - for list of restaurants
				// BASE_URI/restaurants/* - retreive specific restaurant by id
				// the toke value are used to register path in matcher (see above)
				public static final String PATH = "albums";
				public static final int PATH_TOKEN = 900;
				public static final String PATH_FOR_ID = "albums/*";
				public static final int PATH_FOR_ID_TOKEN = 1000;
				
				// URI for all content stored as Restaurant entity
				// BASE_URI + PATH ==> "content://com.favrestaurant.contentprovider/restaurants";
				public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
				
				// define content mime type for entity
				public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.coconut.app";
				public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.coconut.app";
				
				// a static class to store columns in entity
				public static class Cols {
					public static final String ID = BaseColumns._ID; // convention
					public static final String ALBID  = "album_id";
					public static final String ALBOWNER = "album_owner";
					public static final String ALBNAME = "album_name";
					public static final String ALBSIZE = "album_size";
					public static final String ALBSYNCD = "abum_sync_date";
					public static final String ALBSYNCT = "album_sync_time";
					public static final String ALBINITD = "album_init_date";
				}
				
	}
	
	public static class TunaContact {
		
		// an identifying name for entity
				public static final String NAME = "contact";
				
				// define a URI paths to access entity
				// BASE_URI/restaurants - for list of restaurants
				// BASE_URI/restaurants/* - retreive specific restaurant by id
				// the toke value are used to register path in matcher (see above)
				public static final String PATH = "contacts";
				public static final int PATH_TOKEN = 500;
				public static final String PATH_FOR_ID = "contacts/*";
				public static final int PATH_FOR_ID_TOKEN = 600;
				
				// URI for all content stored as Restaurant entity
				// BASE_URI + PATH ==> "content://com.favrestaurant.contentprovider/restaurants";
				public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
				
				// define content mime type for entity
				public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.coconut.app";
				public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.coconut.app";

				
				// a static class to store columns in entity
				public static class Cols {
					public static final String ID = BaseColumns._ID; // convention
					public static final String CNTID  = "contact_id";
					public static final String CNTGRPID = "contact_group_id";
					public static final String CNTNAME = "contact_name";
					public static final String CNTPHONE = "contact_phone";
					public static final String CNTEMAIL = "contact_email";
					public static final String CNTGOOGID = "contact_google_id";
					public static final String CNTIMG = "contact_image";

				}
				
	}
	public static class TunaGroup {
		
		// an identifying name for entity
				public static final String NAME = "circles";
				
				// define a URI paths to access entity
				// BASE_URI/restaurants - for list of restaurants
				// BASE_URI/restaurants/* - retreive specific restaurant by id
				// the toke value are used to register path in matcher (see above)
				public static final String PATH = "groups";
				public static final int PATH_TOKEN = 700;
				public static final String PATH_FOR_ID = "circles/*";
				public static final int PATH_FOR_ID_TOKEN = 800;
				
				// URI for all content stored as Restaurant entity
				// BASE_URI + PATH ==> "content://com.favrestaurant.contentprovider/restaurants";
				public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
				
				// define content mime type for entity
				public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.coconut.app";
				public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.coconut.app";
				
				// a static class to store columns in entity
				public static class Cols {
					public static final String ID = BaseColumns._ID; // convention
					public static final String GRPID = "group_id";
					public static final String GRPNAME = "group_name";
					public static final String GRPSIZE = "group_size";
					public static final String GRPSYNCDATE = "sync_date";
					public static final String GRPSYNCTIME = "sync_time";
				}
				
	}
	public static class TunaSync {
		
		// an identifying name for entity
				public static final String NAME = "sync";
				
				// define a URI paths to access entity
				// BASE_URI/restaurants - for list of restaurants
				// BASE_URI/restaurants/* - retreive specific restaurant by id
				// the toke value are used to register path in matcher (see above)
				public static final String PATH = "sync";
				public static final int PATH_TOKEN = 1100;
				public static final String PATH_FOR_ID = "sync/*";
				public static final int PATH_FOR_ID_TOKEN = 1200;
				
				// URI for all content stored as Restaurant entity
				// BASE_URI + PATH ==> "content://com.favrestaurant.contentprovider/restaurants";
				public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
				
				// define content mime type for entity
				public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.coconut.app";
				public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.coconut.app";
				
				// a static class to store columns in entity
				public static class Cols {
					public static final String ID = BaseColumns._ID; // convention
					public static final String SYNCUSERID = "sync_prof_userid";
					public static final String SYNCTOKEN = "sync_token";
					public static final String SYNCPROFDATE = "sync_prof_date";
					public static final String SYNCPROFTIME = "sync_prof_time";
					public static final String SYNCALBAMT = "sync_alb_amount";
					public static final String SYNCIMGAMT = "sync_img_amount";
					public static final String SYNCIMGDATE = "sync_img_date";
					public static final String SYNCIMGTIME = "sync_img_time";
					public static final String SYNCGRPAMT = "sync_grp_amount";
					public static final String SYNCCONAMT = "sync_con_amount";
					public static final String SYNCGRPDATE = "sync_grp_date";
					public static final String SYNCGRPTIME = "sync_grp_time";
					public static final String SYNCINVDATE = "sync_invite_date";
					public static final String SYNCINVTIME = "sync_invite_time";
					public static final String SYNCINVTOKEN = "sync_invite_token";
					public static final String SYNCINV_RECAMT = "sync_invite_recamt";
					public static final String SYNCINV_SNTAMT = "sync_invite_sntamt";
					public static final String SYNCMSGTOKEN = "sync_msg_token";
					public static final String SYNCMSGDATE = "sync_msg_date";
					public static final String SYNCMSGTIME = "sync_msg_time";
					public static final String SYNCMSG_RECAMT = "sync_msg_recamt";
					public static final String SYNCMSG_SNTAMT = "sync_msg_sntamt";
					public static final String SYNCMSG_CACHE = "sync_msg_cache";
				}
				
	}
	
	public static class TunaPref {
		
		// an identifying name for entity
				public static final String NAME = "preferences";
				
				// define a URI paths to access entity
				// BASE_URI/restaurants - for list of restaurants
				// BASE_URI/restaurants/* - retreive specific restaurant by id
				// the toke value are used to register path in matcher (see above)
				public static final String PATH = "preferences";
				public static final int PATH_TOKEN = 1300;
				public static final String PATH_FOR_ID = "preferences/*";
				public static final int PATH_FOR_ID_TOKEN = 1400;
				
				// URI for all content stored as Restaurant entity
				// BASE_URI + PATH ==> "content://com.favrestaurant.contentprovider/restaurants";
				public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
				
				// define content mime type for entity
				public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.coconut.app";
				public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.coconut.app";
				
				// a static class to store columns in entity
				public static class Cols {
					public static final String ID = BaseColumns._ID; // convention
					public static final String PREFSYNCON = "pref_sync_on";
					public static final String PREFSYNCTIME = "pref_sync_time";
					public static final String PENDINGDATA = "pending_data";
				}
				
	}
	
	public static class TunaInvite {
		
		// an identifying name for entity
				public static final String NAME = "invites";
				
				// define a URI paths to access entity
				// BASE_URI/restaurants - for list of restaurants
				// BASE_URI/restaurants/* - retreive specific restaurant by id
				// the toke value are used to register path in matcher (see above)
				public static final String PATH = "invites";
				public static final int PATH_TOKEN = 1500;
				public static final String PATH_FOR_ID = "invites/*";
				public static final int PATH_FOR_ID_TOKEN = 1600;
				
				// URI for all content stored as Restaurant entity
				// BASE_URI + PATH ==> "content://com.favrestaurant.contentprovider/restaurants";
				public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
				
				// define content mime type for entity
				public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.coconut.app";
				public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.coconut.app";
				
				// a static class to store columns in entity
				public static class Cols {
					public static final String ID = BaseColumns._ID; // convention
					public static final String INVTOKEN = "invite_token";
					public static final String INVSENDID = "invite_sender_id";
					public static final String INVRECNAME = "invite_recep_name";
					public static final String INVRECID = "invite_recep_id";
					public static final String INVRECEMAIL = "invite_recep_email";
					public static final String INVRECPHONE = "invite_recep_phone";
					public static final String INVDATE = "invite_date";
					public static final String INVTIME = "invite_time";
					public static final String INVSTATUS = "invite_status";
					public static final String INVSENDNAME = "invite_sender_name";
				}
				
	}
	
	public static class TunaMessage {
		
		// an identifying name for entity
				public static final String NAME = "messages";
				
				// define a URI paths to access entity
				// BASE_URI/restaurants - for list of restaurants
				// BASE_URI/restaurants/* - retreive specific restaurant by id
				// the toke value are used to register path in matcher (see above)
				public static final String PATH = "messages";
				public static final int PATH_TOKEN = 1700;
				public static final String PATH_FOR_ID = "messages/*";
				public static final int PATH_FOR_ID_TOKEN = 1800;
				
				// URI for all content stored as Restaurant entity
				// BASE_URI + PATH ==> "content://com.favrestaurant.contentprovider/restaurants";
				public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH).build();
				
				// define content mime type for entity
				public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.coconut.app";
				public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.coconut.app";
				
				// a static class to store columns in entity
				public static class Cols {
					public static final String ID = BaseColumns._ID; // convention
					public static final String MID = "message_id";
					public static final String USERID = "message_user_id";
					public static final String USERNAME = "message_user_name";
					public static final String TYPE = "message_type";
					public static final String CONTENT = "message_content";
					public static final String RECEIVER = "message_recipient";
					public static final String RECEIVEGRP = "message_recipient_group";
					public static final String MDATE = "message_date";
					public static final String MTIME = "message_time";
				}
				
	}
}
