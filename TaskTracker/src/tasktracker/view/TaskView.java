package tasktracker.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tasktracker.controller.DatabaseAdapter;
import tasktracker.model.Preferences;
import tasktracker.model.elements.Notification;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

/**
 * And activity that displays an existing task. From here, a user may fulfill a
 * task after completing the requirements.
 * 
 * @author Jeanine Bonot
 * 
 */
public class TaskView extends Activity {

	// The current user
	private String _user;

	// Activity Items
	private Button _fulfillmentButton;
	private Button _expandButton;
	private Button _collapseButton;
	private Button _photoButton;
	private TextView _voteLink;
	private TextView _voteInfo;
	private EditText _textFulfillment;
	private LinearLayout _fulfillmentList;
	private ScrollView _scrollview;

	// Task Info
	private long _taskID;
	private String _taskName;
	private String _taskCreator;
	private boolean _requiresText;
	private boolean _requiresPhoto;
	private boolean _voted;
	private int _voteCount;

	// DB stuff
	private DatabaseAdapter _dbHelper;
	private Cursor _cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_view);

		_taskID = getIntent().getLongExtra("TASK_ID", -1);

		if (_taskID == -1)
			return;

		_dbHelper = new DatabaseAdapter(this);
		_user = Preferences.getUsername(this);

		_fulfillmentButton = (Button) findViewById(R.id.fulfillButton);
		_photoButton = (Button) findViewById(R.id.button_photo);
		_expandButton = (Button) findViewById(R.id.button_expand);
		_collapseButton = (Button) findViewById(R.id.button_collapse);

		_fulfillmentList = (LinearLayout) findViewById(R.id.list_fulfillments);
		_scrollview = (ScrollView) findViewById(R.id.scrollview);
		_textFulfillment = (EditText) findViewById(R.id.edit_textFulfillment);

		_voteInfo = (TextView) findViewById(R.id.vote_info);
		_voteLink = (TextView) findViewById(R.id.text_vote);
		_voteLink.setOnClickListener(new VoteButtonSetup());

		_expandButton.setOnClickListener(new ExpandButtonSetup());
		_collapseButton.setOnClickListener(new CollapseButtonSetup());
		_fulfillmentButton.setOnClickListener(new FulfillButtonSetup());

		setupToolbarButtons();

		_textFulfillment.addTextChangedListener(new TextFulfillmentSetup());

		_photoButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startActivity(PhotoPicker.class);
			}

		});

	}

	protected void onStart() {
		super.onStart();

		_dbHelper.open();

		setTaskInfo();
		setMembersList();
		setFulfillmentsList();
		setVoteInfo();
	}

	protected void onStop() {
		super.onStop();

		_dbHelper.close();
		_cursor.close();
	}

	private void setVoteInfo() {

		_cursor = _dbHelper.countAllVotes(_taskName);

		if (_cursor.moveToFirst()) {
			_voteCount = _cursor.getInt(0);
			_voteInfo.setText(_voteCount + " likes");
			Log.d("TaskView", "Vote count = " + _voteCount);
		}

		_cursor = _dbHelper.fetchVote(_taskName, _user);
		if (_cursor.moveToFirst()) {
			_voted = true;
			_voteLink.setText("Unlike");
		} else {
			_voted = false;
			_voteLink.setText("Like");
		}
	}

	private void setupToolbarButtons() {
		// Assign Buttons
		Button buttonMyTasks = (Button) findViewById(R.id.buttonMyTasks);
		Button buttonCreate = (Button) findViewById(R.id.buttonCreateTask);
		Button buttonNotifications = (Button) findViewById(R.id.buttonNotifications);

		buttonMyTasks.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startActivity(TaskListView.class);
			}
		});

		buttonCreate.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startActivity(CreateTaskView.class);
			}
		});

		buttonNotifications.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startActivity(NotificationListView.class);
			}
		});
	}

	/**
	 * Fills the layout views with information on the task.
	 */
	private void setTaskInfo() {
		_cursor = _dbHelper.fetchTask(_taskID);

		if (!_cursor.moveToFirst())
			return;

		TextView name = (TextView) findViewById(R.id.taskName);
		TextView description = (TextView) findViewById(R.id.description);
		TextView creationInfo = (TextView) findViewById(R.id.creationInfo);
		TextView privacy = (TextView) findViewById(R.id.private_task);

		CheckBox textRequirement = (CheckBox) findViewById(R.id.checkbox_text);
		CheckBox photoRequirement = (CheckBox) findViewById(R.id.checkbox_photo);

		_taskCreator = _cursor.getString(_cursor
				.getColumnIndex(DatabaseAdapter.USER));
		String date = _cursor.getString(_cursor
				.getColumnIndex(DatabaseAdapter.DATE));

		_requiresText = _cursor.getInt(_cursor
				.getColumnIndex(DatabaseAdapter.REQS_TEXT)) == 1;
		_requiresPhoto = _cursor.getInt(_cursor
				.getColumnIndex(DatabaseAdapter.REQS_PHOTO)) == 1;
		_taskName = _cursor.getString(_cursor
				.getColumnIndex(DatabaseAdapter.TASK));

		if (_cursor.getInt(_cursor.getColumnIndex(DatabaseAdapter.PRIVATE)) == 1)
			privacy.setVisibility(View.VISIBLE);

		name.setText(_taskName);
		description.setText(_cursor.getString(_cursor
				.getColumnIndex(DatabaseAdapter.TEXT)));
		creationInfo
				.setText("Created on " + date + " by " + _taskCreator + ".");

		textRequirement.setChecked(_requiresText);
		photoRequirement.setChecked(_requiresPhoto);

	}

	/**
	 * Sets the fulfillment list with data from the SQL database.
	 */
	private void setFulfillmentsList() {
		_cursor = _dbHelper.fetchFulfillment(_taskName);

		// startManagingCursor(_cursor);
		//
		// String[] from = new String[] { DatabaseAdapter.USER,
		// DatabaseAdapter.TEXT, DatabaseAdapter.DATE };
		// int[] to = new int[] { R.id.task_name, R.id.task_creator,
		// R.id.fulfillment_date };
		//
		// SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
		// R.layout.list_item, _cursor, from, to);
		// int count = adapter.getCount();
		// _hasFulfillments = count > 0;
		// Log.d("Task Fulfillment",
		// "number of fulfillments = " + Integer.toString(count));

		// adapter.setViewBinder(new ViewBinder() {
		//
		// public boolean setViewValue(View view, Cursor cursor,
		// int columnIndex) {
		//
		// if (true) {
		// String fulfiller = cursor.getString(cursor
		// .getColumnIndex(DatabaseAdapter.USER));
		// String date = cursor.getString(cursor
		// .getColumnIndex(DatabaseAdapter.DATE));
		// TextView textView = (TextView) view;
		// textView.setText("Fulfilled by " + fulfiller + " on "
		// + date);
		// return true;
		// }
		//
		// return false;
		// }
		// });

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int fulfillerIndex = _cursor.getColumnIndex(DatabaseAdapter.USER);
		int textIndex = _cursor.getColumnIndex(DatabaseAdapter.TEXT);
		int dateIndex = _cursor.getColumnIndex(DatabaseAdapter.DATE);
		while (_cursor.moveToNext()) {
			// _hasFulfillments = true;
			View view = inflater.inflate(R.layout.list_item, _fulfillmentList,
					false);

			view.findViewById(R.id.item_date_bottom).setVisibility(View.GONE);
			view.findViewById(R.id.item_vote_count).setVisibility(View.GONE);
			TextView fulfiller = (TextView) view.findViewById(R.id.item_title);
			TextView text = (TextView) view.findViewById(R.id.item_text);
			TextView date = (TextView) view.findViewById(R.id.item_top_right);

			fulfiller.setText("Fulfilled by "
					+ _cursor.getString(fulfillerIndex));
			text.setText(_cursor.getString(textIndex));
			text.setTextSize(12);
			date.setText(_cursor.getString(dateIndex));

			_fulfillmentList.addView(view);
		}

		// _fulfillmentList.setAdapter(adapter);
		// stopManagingCursor(_cursor);

	}

	/**
	 * Sets the member list with data from the SQL database.
	 */
	private void setMembersList() {
		ListView members = (ListView) findViewById(R.id.membersList);
		_cursor = _dbHelper.fetchTaskMembers(_taskName);

		startManagingCursor(_cursor);

		String[] from = new String[] { DatabaseAdapter.USER };
		int[] to = new int[] { R.id.text };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.simple_list_item, _cursor, from, to);
		int count = adapter.getCount();
		members.setAdapter(adapter);
		stopManagingCursor(_cursor);
		TextView headline = (TextView) findViewById(R.id.heading_Members);

		if (count == 1) {
			headline.setText(count + " Member:");
		} else {
			headline.setText(count + " Members:");
		}
	}

	/**
	 * Send a notification report of the task fulfillment to the creator.
	 */
	private void sendFulfillmentNotification(String message) {
		_dbHelper.createNotification(_taskName, _taskCreator, message);
	}

	/**
	 * Send an email report of the task fulfillment to the creator.
	 */
	private void sendFulfillmentEmail(String message, String textFulfillment) {

		_cursor = _dbHelper.fetchUser(_taskCreator);

		if (!_cursor.moveToFirst()) {
			ToastCreator.showLongToast(this,
					"Could not find creator information");
			return;
		}

		String email = _cursor.getString(_cursor
				.getColumnIndexOrThrow(DatabaseAdapter.EMAIL));

		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
		i.putExtra(Intent.EXTRA_SUBJECT,
				"TaskTracker : Task Fulfillment Report");
		i.putExtra(Intent.EXTRA_TEXT, message + "\n\n" + textFulfillment);
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			ToastCreator.showShortToast(this,
					"There are no email clients installed.");
		}
	}

	/**
	 * Soon to be deleted. Makes sure that the requirements have been completed
	 * before fulfilling a task.
	 * 
	 * @return True if the requirements we fulfilled; otherwise false.
	 */
	private boolean requirementsFulfilled() {

		boolean ready = true;

		if (_requiresPhoto) {
			if (true) {
				ToastCreator
						.showLongToast(this,
								"You must add a photo before marking this task as fulfilled.");
				ready = false;
			}
		}

		return ready;
	}

	/**
	 * Start a new activity.
	 * 
	 * @param destination
	 *            The activity class destination.
	 */
	private <T extends Activity> void startActivity(Class<T> destination) {
		Intent intent = new Intent(getApplicationContext(), destination);
		startActivity(intent);
	}

	class TextFulfillmentSetup implements TextWatcher {
		public void afterTextChanged(Editable s) {
			_fulfillmentButton.setEnabled(s.length() > 0);
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// Do nothing.

		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// Do nothing.

		}
	}

	/**
	 * OnClickListener for the expand button. When the user clicks the expand
	 * button, the fields that are required for task fulfillment are displayed,
	 * as well as the fulfillment button.
	 */
	class ExpandButtonSetup implements OnClickListener {

		/**
		 * Sets views a visible if they are required for fulfillment.
		 */
		public void onClick(View v) {
			_collapseButton.setVisibility(View.VISIBLE);
			_expandButton.setVisibility(View.GONE);

			_fulfillmentList.setVisibility(View.VISIBLE);

			if (_requiresText) {
				_textFulfillment.setVisibility(View.VISIBLE);
				_textFulfillment.requestFocus();
			}

			if (_requiresPhoto)
				_photoButton.setVisibility(View.VISIBLE);

			if (!(_requiresText || _requiresPhoto))
				_fulfillmentButton.setEnabled(true);

			_fulfillmentButton.setVisibility(View.VISIBLE);

			// Scroll down so the user can quickly complete the task
			// requirements.
			_scrollview.post(new Runnable() {

				public void run() {
					_scrollview.fullScroll(ScrollView.FOCUS_DOWN);
				}

			});
		}
	}

	/**
	 * OnClickListener for the collapse button. Hides all views that are related
	 * to task fulfillment.
	 */
	class CollapseButtonSetup implements OnClickListener {

		public void onClick(View v) {
			_expandButton.setVisibility(View.VISIBLE);
			_collapseButton.setVisibility(View.GONE);
			_fulfillmentList.setVisibility(View.GONE);
			_fulfillmentButton.setVisibility(View.GONE);
			_textFulfillment.setVisibility(View.GONE);
			_photoButton.setVisibility(View.GONE);
		}
	}

	class FulfillButtonSetup implements OnClickListener {

		public void onClick(View v) {
			if (requirementsFulfilled()) {

				String textFulfillment = _textFulfillment.getText().toString();
				String date = new SimpleDateFormat("MMM dd, yyyy | HH:mm")
						.format(Calendar.getInstance().getTime());
				long id = _dbHelper.createFulfillment(_taskName, date, _user,
						textFulfillment);

				Log.d("Task Fulfillment",
						"fulfillment id: " + Long.toString(id));
				if (!_user.equals(_taskCreator)) {
					String message = Notification.getMessage(_user, _taskName,
							Notification.Type.FulfillmentReport);
					sendFulfillmentNotification(message);
					sendFulfillmentEmail(message, textFulfillment);
				}

				ToastCreator.showLongToast(TaskView.this, "\"" + _taskName
						+ "\" was fulfilled!");

				finish();
			}
		}
	}

	class VoteButtonSetup implements OnClickListener {

		public void onClick(View v) {
			if (_voted) {
				_dbHelper.deleteVote(_taskName, _user);
				_voteCount--;
				_voteLink.setText("Like");
			} else {
				_dbHelper.createVote(_taskName, _user);
				_voteCount++;
				_voteLink.setText("Unlike");
			}

			_voted = !_voted;

			_voteInfo.setText(_voteCount + " likes");
		}

	}

}
