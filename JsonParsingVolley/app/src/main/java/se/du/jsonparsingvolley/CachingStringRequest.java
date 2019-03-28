package se.du.jsonparsingvolley;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

public class CachingStringRequest extends StringRequest {

    private Priority mPriority;

	public CachingStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}


	public CachingStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		super(url, listener, errorListener);
	}


	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		}
        catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}


    @Override
    public Priority getPriority(){
        return mPriority == null ? Priority.NORMAL : mPriority;
    }


    public void setPriority(Priority priority){
        mPriority = priority;
    }
}