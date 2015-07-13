package com.baidu.core.net.base;


import com.android.volley.VolleyError;

/**
 * Http Response
 * Created by chenrensong on 15/5/18.
 */
public class HttpResponse<T> {


    public interface Listener<T> {
        void onResponse(HttpResponse<T> response);
    }

    /**
     * Returns a successful response containing the parsed result.
     */
    public static <T> HttpResponse<T> success(HttpRequestBase sender, T result) {
        return new HttpResponse<T>(sender, result);
    }

    /**
     * Returns a failed response containing the given error code and an optional
     * localized message displayed to the user.
     */
    public static <T> HttpResponse<T> error(HttpRequestBase sender, VolleyError error) {
        return new HttpResponse<T>(sender, error);
    }

    /**
     * Parsed response, or null in the case of error.
     */
    public final T result;

    /**
     * Detailed error information if <code>errorCode != OK</code>.
     */
    public final VolleyError error;

    /**
     * True if this response was a soft-expired one and a second one MAY be coming.
     */
    public boolean intermediate = false;

    /**
     * Http Request Sender
     */
    public HttpRequestBase sender;

    /**
     * Returns whether this response is considered successful.
     */
    public boolean isSuccess() {
        return error == null;
    }


    private HttpResponse(HttpRequestBase sender, T result) {
        this.result = result;
        this.error = null;
        this.sender = sender;
    }

    private HttpResponse(HttpRequestBase sender, VolleyError error) {
        this.sender = sender;
        this.result = null;
        this.error = error;
    }
}
