package com.hutchgrant.provider;

import com.hutchgrant.provider.tunadatabase;
import com.hutchgrant.provider.tunadatamodel;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;



	/**
	 * This class defines the TunaProfileContentProvider.
	 * When registered with in the Android manifest file, the Android runtime
	 * will manage the instantiation and shutdown of the provider.
	 * @author vladimir
	 *
	 */
	public class tunaprovider extends ContentProvider {
		private tunadatabase tunaDb;

		@Override
		public boolean onCreate() {
			Context ctx = getContext();
			tunaDb = new tunadatabase(ctx);
			return true;
		}
		
		/**
		 * Utility function to return the mime type based on a given URI
		 */
		@Override
		public String getType(Uri uri) {
			final int match = tunadatamodel.URI_MATCHER.match(uri);
			switch(match){
			case tunadatamodel.TunaProfile.PATH_TOKEN:
				return tunadatamodel.TunaProfile.CONTENT_TYPE_DIR;
			case tunadatamodel.TunaProfile.PATH_FOR_ID_TOKEN:
				return tunadatamodel.TunaProfile.CONTENT_ITEM_TYPE;
			case tunadatamodel.TunaContact.PATH_FOR_ID_TOKEN:
				return tunadatamodel.TunaContact.CONTENT_ITEM_TYPE;
			case tunadatamodel.TunaImage.PATH_FOR_ID_TOKEN:
				return tunadatamodel.TunaImage.CONTENT_ITEM_TYPE;
			case tunadatamodel.TunaAlbum.PATH_FOR_ID_TOKEN:
				return tunadatamodel.TunaAlbum.CONTENT_ITEM_TYPE;
			case tunadatamodel.TunaGroup.PATH_FOR_ID_TOKEN:
				return tunadatamodel.TunaGroup.CONTENT_ITEM_TYPE;
			case tunadatamodel.TunaSync.PATH_FOR_ID_TOKEN:
				return tunadatamodel.TunaSync.CONTENT_ITEM_TYPE;
			case tunadatamodel.TunaPref.PATH_FOR_ID_TOKEN:
				return tunadatamodel.TunaPref.CONTENT_ITEM_TYPE;
			case tunadatamodel.TunaInvite.PATH_FOR_ID_TOKEN:
				return tunadatamodel.TunaInvite.CONTENT_ITEM_TYPE;
			case tunadatamodel.TunaMessage.PATH_FOR_ID_TOKEN:
				return tunadatamodel.TunaMessage.CONTENT_ITEM_TYPE;
	        default:
	            throw new UnsupportedOperationException ("URI " + uri + " is not supported.");
			}	
		}

		@Override
		public Uri insert(Uri uri, ContentValues values) {
			SQLiteDatabase db = tunaDb.getWritableDatabase();
			int token = tunadatamodel.URI_MATCHER.match(uri);
			switch(token){
				case tunadatamodel.TunaProfile.PATH_TOKEN:{
					long id = db.insert(tunadatamodel.TunaProfile.NAME, null, values);
					getContext().getContentResolver().notifyChange(uri, null);
					return tunadatamodel.TunaProfile.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
				}
				case tunadatamodel.TunaImage.PATH_TOKEN:{
					boolean replace = false;
					  String grpID = "";
					    if ( values.containsKey( tunadatamodel.SQL_INS_REP ) ){
					        replace = values.getAsBoolean( tunadatamodel.SQL_INS_REP );
					        values = new ContentValues( values );
					        values.remove( tunadatamodel.SQL_INS_REP );
					    }

					    if( values.containsKey(tunadatamodel.TunaImage.Cols.IMGID)){
					    	grpID = values.getAsString(tunadatamodel.TunaImage.Cols.IMGID);
					    }
					    long rowId;
					    if ( replace ) {
					        rowId = db.update(tunadatamodel.TunaImage.NAME, values, tunadatamodel.TunaImage.Cols.IMGID +"=?", new String[] {grpID} );
					    } else {
					        rowId = db.insert(tunadatamodel.TunaImage.NAME, null, values);
					    }
					    getContext().getContentResolver().notifyChange(uri, null);
						return tunadatamodel.TunaImage.CONTENT_URI.buildUpon().appendPath(String.valueOf(rowId)).build();
				}
				case tunadatamodel.TunaAlbum.PATH_TOKEN:{
					boolean replace = false;
					  String grpID = "";
					    if ( values.containsKey( tunadatamodel.SQL_INS_REP ) ){
					        replace = values.getAsBoolean( tunadatamodel.SQL_INS_REP );
					        values = new ContentValues( values );
					        values.remove( tunadatamodel.SQL_INS_REP );
					    }

					    if( values.containsKey(tunadatamodel.TunaAlbum.Cols.ALBID)){
					    	grpID = values.getAsString(tunadatamodel.TunaAlbum.Cols.ALBID);
					    }
					    long rowId;
					    if ( replace ) {
					        rowId = db.update(tunadatamodel.TunaAlbum.NAME, values, tunadatamodel.TunaAlbum.Cols.ALBID +"=?", new String[] {grpID} );
					    } else {
					        rowId = db.insert(tunadatamodel.TunaAlbum.NAME, null, values);
					    }
					    getContext().getContentResolver().notifyChange(uri, null);
						return tunadatamodel.TunaAlbum.CONTENT_URI.buildUpon().appendPath(String.valueOf(rowId)).build();
				}
				case tunadatamodel.TunaContact.PATH_TOKEN:{
					boolean replace = false;
					  String grpID = "", cntID = "";
					    if ( values.containsKey( tunadatamodel.SQL_INS_REP ) ){
					        replace = values.getAsBoolean( tunadatamodel.SQL_INS_REP );
					        values = new ContentValues( values );
					        values.remove( tunadatamodel.SQL_INS_REP );
					    }

					    if( values.containsKey(tunadatamodel.TunaContact.Cols.CNTGRPID)){
					    	grpID = values.getAsString(tunadatamodel.TunaContact.Cols.CNTGRPID);
					    }
					    if( values.containsKey(tunadatamodel.TunaContact.Cols.CNTID)){
					    	cntID = values.getAsString(tunadatamodel.TunaContact.Cols.CNTID);
					    }
					    long rowId;
					    if ( replace ) {
					        rowId = db.update(tunadatamodel.TunaContact.NAME, values, tunadatamodel.TunaContact.Cols.CNTGRPID +"=? and "+tunadatamodel.TunaContact.Cols.CNTID+ "=?", new String[] {grpID, cntID} );
					    } else {
					        rowId = db.insert(tunadatamodel.TunaContact.NAME, null, values);
					    }
					    getContext().getContentResolver().notifyChange(uri, null);
						return tunadatamodel.TunaContact.CONTENT_URI.buildUpon().appendPath(String.valueOf(rowId)).build();
				}
				case tunadatamodel.TunaGroup.PATH_TOKEN:{
					  boolean replace = false;
					  String grpID = "";
					    if ( values.containsKey( tunadatamodel.SQL_INS_REP ) ){
					        replace = values.getAsBoolean( tunadatamodel.SQL_INS_REP );
					        values = new ContentValues( values );
					        values.remove( tunadatamodel.SQL_INS_REP );
					    }

					    if( values.containsKey(tunadatamodel.TunaGroup.Cols.GRPID)){
					    	grpID = values.getAsString(tunadatamodel.TunaGroup.Cols.GRPID);
					    }
					    long rowId;
					    if ( replace ) {
					        rowId = db.update(tunadatamodel.TunaGroup.NAME, values, tunadatamodel.TunaGroup.Cols.GRPID +"=?", new String[] {grpID} );
					    } else {
					        rowId = db.insert(tunadatamodel.TunaGroup.NAME, null, values);
					    }
					    getContext().getContentResolver().notifyChange(uri, null);
					    return tunadatamodel.TunaGroup.CONTENT_URI.buildUpon().appendPath(String.valueOf(rowId)).build();
				}
				case tunadatamodel.TunaSync.PATH_TOKEN:{
					  boolean replace = false;
					  String grpID = "";
					    if ( values.containsKey( tunadatamodel.SQL_INS_REP ) ){
					        replace = values.getAsBoolean( tunadatamodel.SQL_INS_REP );
					        values = new ContentValues( values );
					        values.remove( tunadatamodel.SQL_INS_REP );
					    }

					    if( values.containsKey(tunadatamodel.TunaSync.Cols.SYNCUSERID)){
					    	grpID = values.getAsString(tunadatamodel.TunaSync.Cols.SYNCUSERID);
					    }
					    long rowId;
					    if ( replace ) {
					        rowId = db.update(tunadatamodel.TunaSync.NAME, values, tunadatamodel.TunaSync.Cols.SYNCUSERID +"=?", new String[] {grpID} );
					    } else {
					        rowId = db.insert(tunadatamodel.TunaSync.NAME, null, values);
					    }
					    getContext().getContentResolver().notifyChange(uri, null);
					    return tunadatamodel.TunaSync.CONTENT_URI.buildUpon().appendPath(String.valueOf(rowId)).build();
				}
				case tunadatamodel.TunaPref.PATH_TOKEN:{
					  boolean replace = false;
					  String grpID = "";
					    if ( values.containsKey( tunadatamodel.SQL_INS_REP ) ){
					        replace = values.getAsBoolean( tunadatamodel.SQL_INS_REP );
					        values = new ContentValues( values );
					        values.remove( tunadatamodel.SQL_INS_REP );
					    }

					    if( values.containsKey(tunadatamodel.TunaPref.Cols.ID)){
					    	grpID = values.getAsString(tunadatamodel.TunaPref.Cols.ID);
					    }
					    long rowId;
					    if ( replace ) {
					        rowId = db.update(tunadatamodel.TunaPref.NAME, values, tunadatamodel.TunaPref.Cols.ID +"=?", new String[] {grpID} );
					    } else {
					        rowId = db.insert(tunadatamodel.TunaPref.NAME, null, values);
					    }
					    getContext().getContentResolver().notifyChange(uri, null);
					    return tunadatamodel.TunaPref.CONTENT_URI.buildUpon().appendPath(String.valueOf(rowId)).build();
				}

				case tunadatamodel.TunaInvite.PATH_TOKEN:{
					  boolean replace = false;
					  String grpID = "";
					    if ( values.containsKey( tunadatamodel.SQL_INS_REP ) ){
					        replace = values.getAsBoolean( tunadatamodel.SQL_INS_REP );
					        values = new ContentValues( values );
					        values.remove( tunadatamodel.SQL_INS_REP );
					    }

					    if( values.containsKey(tunadatamodel.TunaInvite.Cols.INVTOKEN)){
					    	grpID = values.getAsString(tunadatamodel.TunaInvite.Cols.INVTOKEN);
					    }
					    long rowId;
					    if ( replace ) {
					        rowId = db.update(tunadatamodel.TunaInvite.NAME, values, tunadatamodel.TunaInvite.Cols.INVTOKEN +"=?", new String[] {grpID} );
					    } else {
					        rowId = db.insert(tunadatamodel.TunaInvite.NAME, null, values);
					    }
					    getContext().getContentResolver().notifyChange(uri, null);
					    return tunadatamodel.TunaInvite.CONTENT_URI.buildUpon().appendPath(String.valueOf(rowId)).build();
				}
				case tunadatamodel.TunaMessage.PATH_TOKEN:{
					  boolean replace = false;
					  String grpID = "";
					    if ( values.containsKey( tunadatamodel.SQL_INS_REP ) ){
					        replace = values.getAsBoolean( tunadatamodel.SQL_INS_REP );
					        values = new ContentValues( values );
					        values.remove( tunadatamodel.SQL_INS_REP );
					    }

					    if( values.containsKey(tunadatamodel.TunaMessage.Cols.MID)){
					    	grpID = values.getAsString(tunadatamodel.TunaMessage.Cols.MID);
					    }
					    long rowId;
					    if ( replace ) {
					        rowId = db.update(tunadatamodel.TunaMessage.NAME, values, tunadatamodel.TunaMessage.Cols.MID +"=?", new String[] {grpID} );
					    } else {
					        rowId = db.insert(tunadatamodel.TunaMessage.NAME, null, values);
					    }
					    getContext().getContentResolver().notifyChange(uri, null);
					    return tunadatamodel.TunaMessage.CONTENT_URI.buildUpon().appendPath(String.valueOf(rowId)).build();
				}
	            default: {
	                throw new UnsupportedOperationException("URI: " + uri + " not supported.");
	            }
			}
		} 

		/**
		 * Function to query the content provider.  This example queries the backing database.
		 * It uses the SQLite API to retrieve TunaProfile data based on the URI specified.
		 */
		public Cursor MyQuery(Uri uri, String[] projection, String selection,
				String[] selectionArgs, String sortOrder, String limit) {
			

			SQLiteDatabase db = tunaDb.getReadableDatabase();
			final int match = tunadatamodel.URI_MATCHER.match(uri);
			switch(match){
				case tunadatamodel.TunaMessage.PATH_TOKEN:{
					SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
					builder.setTables(tunadatamodel.TunaMessage.NAME);
					return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit);
				}
				default: return null;
			}
		}
		@Override
		public int delete(Uri uri, String selection, String[] selectionArgs) {
			final int match = tunadatamodel.URI_MATCHER.match(uri);
    SQLiteDatabase sqlDB = tunaDb.getWritableDatabase();
    int rowsAffected = 0;
    switch (match) {
    case tunadatamodel.TunaImage.PATH_TOKEN:
        rowsAffected = sqlDB.delete(tunadatamodel.TunaImage.NAME,
                selection, selectionArgs);
        break;
    case tunadatamodel.TunaAlbum.PATH_TOKEN:
        rowsAffected = sqlDB.delete(tunadatamodel.TunaAlbum.NAME,
                selection, selectionArgs);
        break;
    case tunadatamodel.TunaGroup.PATH_TOKEN:
        rowsAffected = sqlDB.delete(tunadatamodel.TunaGroup.NAME,
                selection, selectionArgs);
        break;
    case tunadatamodel.TunaContact.PATH_TOKEN:
        rowsAffected = sqlDB.delete(tunadatamodel.TunaContact.NAME,
                selection, selectionArgs);
        break; 
    case tunadatamodel.TunaSync.PATH_TOKEN:
        rowsAffected = sqlDB.delete(tunadatamodel.TunaSync.NAME,
                selection, selectionArgs);
        break; 
    case tunadatamodel.TunaInvite.PATH_TOKEN:
        rowsAffected = sqlDB.delete(tunadatamodel.TunaInvite.NAME,
                selection, selectionArgs);
        break;
    case tunadatamodel.TunaMessage.PATH_TOKEN:
        rowsAffected = sqlDB.delete(tunadatamodel.TunaMessage.NAME,
                selection, selectionArgs);
        break;
    default:
        throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return rowsAffected;
		}

		@Override
		public int update(Uri uri, ContentValues values, String selection,
				String[] selectionArgs) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Cursor query(Uri uri, String[] projection, String selection,
				String[] selectionArgs, String sortOrder) {
			
			SQLiteDatabase db = tunaDb.getReadableDatabase();
			final int match = tunadatamodel.URI_MATCHER.match(uri);
			switch(match){
				// retrieve TunaProfile list
				case tunadatamodel.TunaProfile.PATH_TOKEN:{
					SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
					builder.setTables(tunadatamodel.TunaProfile.NAME);
					return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				}
				case tunadatamodel.TunaContact.PATH_TOKEN:{
					SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
					builder.setTables(tunadatamodel.TunaContact.NAME);
					return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				}
				case tunadatamodel.TunaImage.PATH_TOKEN:{
					SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
					builder.setTables(tunadatamodel.TunaImage.NAME);
					return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				}
				case tunadatamodel.TunaAlbum.PATH_TOKEN:{
					SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
					builder.setTables(tunadatamodel.TunaAlbum.NAME);
					return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				}
				case tunadatamodel.TunaGroup.PATH_TOKEN:{
					SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
					builder.setTables(tunadatamodel.TunaGroup.NAME);
					return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				}
				case tunadatamodel.TunaSync.PATH_TOKEN:{
					SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
					builder.setTables(tunadatamodel.TunaSync.NAME);
					return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				}
				case tunadatamodel.TunaPref.PATH_TOKEN:{
					SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
					builder.setTables(tunadatamodel.TunaPref.NAME);
					return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				}
				case tunadatamodel.TunaInvite.PATH_TOKEN:{
					SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
					builder.setTables(tunadatamodel.TunaInvite.NAME);
					return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				}
				case tunadatamodel.TunaMessage.PATH_TOKEN:{
					SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
					builder.setTables(tunadatamodel.TunaMessage.NAME);
					return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				}
				default: return null;
			}

	}
}