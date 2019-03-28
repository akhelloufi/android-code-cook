package se.du.jsonparsingvolley;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 Android Volley Library Tutorial
 https://www.captechconsulting.com/blogs/android-volley-library-tutorial

 Offline Caching With Volley Using Cache Controlled Header
 http://mobilewebwizard.in/2015/01/offline-caching-with-volley-using-cache-controlled-header/

 Android Volley + JSONObjectRequest Caching
 http://stackoverflow.com/questions/16781244/android-volley-jsonobjectrequest-caching
 */

public class JSONParsingActivity extends ListActivity {

    public static final String TAG = JSONParsingActivity.class.getSimpleName();

    private static final int FIVE_MINUTES = 1000 * 60 * 5;
    private static final String CONTACT_LIST = "CONTACT_LIST";
    private static final String CONTACT_LIST_AGE = "CONTACT_LIST_AGE";
    private ArrayList<HashMap<String, String>> mContacts;
    private long mContactListAge;

	// url to make request
	public static String URL = "http://api.androidhive.info/contacts/";

	// JSON Node names
	private static final String TAG_NAME = "name";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_PHONE_MOBILE = "mobile";


    // JSON parser and Volley manager
    private ContactsJsonParserVolley mJPV;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// since we do not use the default from ListActivity
		setContentView(R.layout.activity_jsonparsing);

        mJPV = new ContactsJsonParserVolley(JSONParsingActivity.this);


        // if the activity is being resumed or orientation has been changed ...
        if (savedInstanceState != null ) {
            if(savedInstanceState.getSerializable(CONTACT_LIST) != null) {
                Log.d(TAG, "savedInstanceState != null");
                mContacts = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable(CONTACT_LIST);
                mContactListAge = savedInstanceState.getLong(CONTACT_LIST_AGE);
                // we load list at once if list is younger than 5 minutes, otherwise force a network request
                if((System.currentTimeMillis() - mContactListAge) < FIVE_MINUTES)
                    onVolleyLoadFinished(mContacts);
                else
                    onReqest();
            }
        }
        else{
            onReqest();
        }
	}

    private void onReqest() {
        if (isNetworkAvailable(this)){
            mJPV.requestJSON(URL);
            mJPV.requestString(URL);    // testing a cached request
        }
        else
            buildAlertMessageNoNetwork();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putSerializable(CONTACT_LIST, mContacts);
        outState.putLong(CONTACT_LIST_AGE, mContactListAge);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onStop() {
        super.onStop();
        // This will tell to Volley to cancel all the pending requests
        mJPV.onStopVolley();
    }


	// Called when Volley has finished its load.
	public void onVolleyLoadFinished(ArrayList<HashMap<String, String>> contactList) {
        Log.d(TAG, "onVolleyLoadFinished()");
        // save contacts in case of orientation change
        mContacts = contactList;
        mContactListAge = System.currentTimeMillis();
		/**
		 * Updating parsed JSON data into ListView
		 * */
		ListAdapter adapter = new SimpleAdapter(this, contactList,
				R.layout.list_item,
				new String[] { TAG_NAME, TAG_EMAIL, TAG_PHONE_MOBILE }, new int[] {
				R.id.name, R.id.email, R.id.mobile });

		setListAdapter(adapter);
		
		// selecting single ListView item
		ListView lv = getListView();

		// Launching new screen on Selecting Single ListItem
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting values from selected ListItem
                String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
                String cost = ((TextView) view.findViewById(R.id.email)).getText().toString();
                String description = ((TextView) view.findViewById(R.id.mobile)).getText().toString();
				/*                 
                // Starting new intent
                Intent in = new Intent(getApplicationContext(), SingleMenuItemActivity.class);
                in.putExtra(TAG_NAME, name);
                in.putExtra(TAG_EMAIL, cost);
                in.putExtra(TAG_PHONE_MOBILE, description);
                startActivity(in);
				 */
            }
        });
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jsonparsing, menu);
		return true;
	}


    /** requires the ACCESS_NETWORK_STATE permission   */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null, otherwise check if we are connected
        return (networkInfo != null && networkInfo.isConnected());
    }


    private void buildAlertMessageNoNetwork()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.action_data_not_available))
                .setCancelable(false)
                .setPositiveButton("Wi-Fi", new DialogInterface.OnClickListener()
                {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNeutralButton("Wireless", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        // general
                        //startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                })
                .setTitle(getResources().getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher);

        builder.create().show();
    }
}
