package se.du.jsonparsingvolley;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class ContactsJsonParserVolley {

    public static final String TAG = ContactsJsonParserVolley.class.getSimpleName();

    private JSONParsingActivity mActivity;
    private ArrayList<HashMap<String, String>> mContactList;

    // Instance which manage Volley requests
    public VolleySingletonHelper volleyHelper = VolleySingletonHelper.getInstance();;

    // JSON Node names
    private static final String TAG_CONTACTS = "contacts";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_PHONE_MOBILE = "mobile";
    private static final String TAG_PHONE_HOME = "home";
    private static final String TAG_PHONE_OFFICE = "office";

    // constructor
    public ContactsJsonParserVolley(Context context) {
        mActivity = (JSONParsingActivity) context;
    }


    /**
     * This is where the bulk of our work is done. This function is called in a background
     * thread and should generate a new set of data to be published when response is received.
     */
    public void requestJSON(String url) {
        Log.d(TAG, "requestJSON");

        CustomJsonRequest request = new CustomJsonRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // if you want to debug: Log.v(TAG, response.toString());
                try {
                    JSONObject jObj = response;

                    // Hashmap for ListView
                    ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
                    // contacts JSONArray and Getting Array of Contacts
                    JSONArray contacts = jObj.getJSONArray(TAG_CONTACTS);

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
                        String email = c.getString(TAG_EMAIL);
                        String address = c.getString(TAG_ADDRESS);
                        String gender = c.getString(TAG_GENDER);

                        // Phone number is agin JSON Object
                        JSONObject phone = c.getJSONObject(TAG_PHONE);
                        String mobile = phone.getString(TAG_PHONE_MOBILE);
                        String home = phone.getString(TAG_PHONE_HOME);
                        String office = phone.getString(TAG_PHONE_OFFICE);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_EMAIL, email);
                        map.put(TAG_PHONE_MOBILE, mobile);

                        // adding HashList to ArrayList
                        contactList.add(map);
                    }
                    // update UI in activity
                    mActivity.onVolleyLoadFinished(contactList);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (Exception e) {
                    txtError(e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                txtError(error);
            }
        });

        request.setPriority(Request.Priority.NORMAL);
        // cache the data
        request.setShouldCache(true);
        volleyHelper.addToRequestQueue(request);
    }


    private void txtError(Exception e) {
        //mTxtError.setVisibility(View.VISIBLE);
        Toast.makeText(mActivity, e.toString(),Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }


    public void onStopVolley() {
        // This will tell to Volley to cancel all the pending requests
        volleyHelper.cancelPendingRequests();
    }


    /*
     Trying the same thing as in requestJSON but with requestString and the request is to be cached
     If it works response.toString() should only be called once. Doing the procedure in a more readable way as well
     */
    public void requestString(String url) {
        Log.d(TAG, "requestString");

        CachingStringRequest stringRequest =
                new CachingStringRequest(Request.Method.GET, url, new ResponseListener(), new ErrorListener());

        stringRequest.setPriority(Request.Priority.HIGH);
        // cache the data
        stringRequest.setShouldCache(true);
        // calling cache test method in VolleySingletonHelper
        // if it works logcat should print "Request is cache-hit"
        volleyHelper.addToRequestQueue(stringRequest, "");
    }


    /** handle the response */
    private class ResponseListener implements Response.Listener<String> {
        @Override
        public void onResponse(String response) {
            Log.d(TAG, response);

            JSONObject jObj = null;
            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(response);

                //
                // same code as in requestJSON after JSONObject jObj = response;
                //

            }
            catch (JSONException e) {
                txtError(e);
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            catch (Exception e) {
                txtError(e);
            }
        }
    }

    /** handle the error */
    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            // TBD error handling
            txtError(error);
        }
    }
}