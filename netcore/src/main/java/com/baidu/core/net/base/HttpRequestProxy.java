package com.baidu.core.net.base;


import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import java.io.UnsupportedEncodingException;

/**
 * Http Request Proxy
 * Created by chenrensong on 15/5/19.
 */
/*Package*/ class HttpRequestProxy extends JsonRequest<String> {

    private static final String PROTOCOL_CONTENT_TYPE = "application/x-www-form-urlencoded";
    /**
     * Creates a new request.
     *
     * @param method        the HTTP method to use
     * @param url           URL to fetch the JSON from
     * @param requestBody   Request Body
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public HttpRequestProxy(int method, String url, String requestBody,
                            Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener,
                errorListener);

    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     *
     * @see #HttpRequestProxy(int, String, String, Response.Listener, Response.ErrorListener)
     */
    public HttpRequestProxy(String url, String requestBody, Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        this(requestBody == null ? Method.GET : Method.POST, url, requestBody,
                listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(jsonString,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }


}
