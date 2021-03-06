package tasktracker.model;

/**
 * TaskTracker
 * 
 * Copyright 2012 Jeanine Bonot, Michael Dardis, Katherine Jasniewski,
 * Jason Morawski
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may 
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */

import tasktracker.controller.DatabaseAdapter;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.Context;
/** 
 * DatabaseModel<br>
 * Creates the tables in the database and opens and closes it to allow
 * it to be queried.
 * 
 * Much of the code of this class is from the Notepad Tutorial on
 * the Android Developer website.<br>
 * Found at:
 * http://developer.android.com/resources/tutorials/notepad/index.html
 * 
 * @author Andrea Budac: abudac
 * @author Christian Jukna: jukna
 * @author Kurtis Morin: kmorin1
 * @author Jeanine Bonot: jbonot<br><br>
 * 
 */
// October 2012 - J Bonot - Made changes to suit TaskTracker application
public class DatabaseModel {

	private DatabaseHelper mDbHelper;
	private Context mCtx;

	/**
	 * Database creation SQL statement.
	 */
	private static final String USERS = "CREATE TABLE users (_id TEXT NOT NULL PRIMARY KEY, "
			+ "user TEXT NOT NULL, "
			+ "email TEXT, "
			+ "password TEXT);";

	private static final String VOTES = "CREATE TABLE votes (_id INTEGER, "
			+ "task_id INTEGER, "
			+ "user TEXT NOT NULL, PRIMARY KEY(task_id, user));";

	private static final String TASKS = "CREATE TABLE tasks (_id TEXT NOT NULL primary key, "
			+ "user TEXT NOT NULL, "
			+ "task TEXT NOT NULL, "
			+ "date TEXT NOT NULL, "
			+ "text TEXT NOT NULL, "
			+ "requiresPhoto INTEGER, "
			+ "requiresText INTEGER, "
			+ "private INTEGER, " + "downloaded TEXT NOT NULL);";

	private static final String MEMBERS = "CREATE TABLE members(_id INTEGER, "
			+ "task_id INTEGER, " + "user TEXT NOT NULL,"
			+ " PRIMARY KEY(task_id, user));";

	private static final String FULFILLMENTS = "CREATE TABLE fulfillments(_id INTEGER primary key autoincrement,  "
			+ "task_id INTEGER, "
			+ "user TEXT NOT NULL, "
			+ "date TEXT NOT NULL, " + "text TEXT);";

	private static final String PHOTOS = "CREATE TABLE photos(_id INTEGER primary key autoincrement,  "
			+ "task_id INTEGER, "
			+ "user TEXT NOT NULL, "
			+ "date TEXT NOT NULL, " + "photo BLOB NOT NULL);";

	private static final String NOTIFICATIONS = "CREATE TABLE notifications(_id TEXT NOT NULL, "
			+ "task_id TEXT NOT NULL,"
			+ "user TEXT NOT NULL,"
			+ "text TEXT NOT NULL, " + "viewed INTEGER, PRIMARY KEY(task_id, user));";

	public static final String[] CREATE_TABLES = new String[] {//
	USERS, TASKS, MEMBERS, FULFILLMENTS, PHOTOS, NOTIFICATIONS, VOTES };

	public static final String[] TABLE_NAMES = new String[] { //
	"users", "tasks", "members", "fulfillments", "photos", "notifications",
			"votes" };

	private static final String DATABASE_NAME = "data";
	
	private static final int DATABASE_VERSION = 5;

	private static final String NAME = "dbAdapter";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			for (String createTable : CREATE_TABLES) {
				db.execSQL(createTable);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			Log.w(NAME, "Upgrading database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data");
			for (String table : TABLE_NAMES) {
				db.execSQL("DROP TABLE IF EXISTS " + table);
			}
			onCreate(db);
		}
	}

	/**
	 * Gets the database.
	 * 
	 * @return mDbHelper
	 */
	public DatabaseHelper getMDbHelper() {

		return mDbHelper;
	}

	/**
	 * Sets the database
	 * 
	 * @param mDbHelper
	 */
	public void setMDbHelper(DatabaseHelper mDbHelper) {

		this.mDbHelper = mDbHelper;
	}

	/**
	 * Gets the context of the activity
	 * 
	 * @return mCtx the context of the activity
	 */
	public Context getMCtx() {

		return mCtx;
	}

	/**
	 * Sets the context.
	 * 
	 * @param mCtx
	 *            the context of the activity
	 */
	public void setMCtx(Context mCtx) {

		this.mCtx = mCtx;
	}

	/**
	 * Open the entries database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public DatabaseAdapter open(DatabaseAdapter databaseAdapter)
			throws SQLException {

		mDbHelper = new DatabaseHelper(mCtx);
		databaseAdapter.setMDb(mDbHelper.getWritableDatabase());
		return databaseAdapter;
	}

	/**
	 * Closes the database
	 */
	public void close() {
		mDbHelper.close();
	}

}
