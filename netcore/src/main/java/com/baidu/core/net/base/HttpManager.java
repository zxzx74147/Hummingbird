package com.baidu.core.net.base;

import android.app.Application;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.baidu.asyncTask.CommonUniqueId;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Http Manager
 * Created by chenrensong on 15/5/21.
 */
public class HttpManager {

    public static final String DEFAULT_CACHE_DIR = "volley";
    private static RequestQueue sRequestQueue;
    private static HurlStack sStack;
    private static Application sApp;
    private static boolean sIsInit;
    private static List<IParameterHandler> sParamsHandler;

    static final Set<HttpRequestBase> sCurrentRequests = new HashSet();

    /**
     * 初始化Http Manager
     *
     * @param app
     */
    public static synchronized void init(Application app) {
        if (sIsInit) {
            return;
        }
        sIsInit = true;
        sApp = app;
        if (sRequestQueue != null) {
            sRequestQueue.stop();
        }
        sRequestQueue = newRequestQueue(app);
    }

    public static void sendRequest(CommonUniqueId id, HttpRequestBase request) {
        sendRequest(id, request, false);
    }

    public static void sendRequest(CommonUniqueId id, HttpRequestBase request, boolean isLoadCache) {
        if (id != null) {
            request.setTag(id);
        }
        request.send(isLoadCache);
    }

    public static void sendRequest(HttpRequestBase request, boolean isLoadCache) {
        sendRequest(null, request, isLoadCache);
    }

    public static void sendRequest(HttpRequestBase request) {
        sendRequest(request, false);
    }

    /**
     * finish
     *
     * @param request
     */
    static void finish(HttpRequestBase request) {
        sCurrentRequests.remove(request);
    }

    public static void cancelAll(Object tag) {
        if (sRequestQueue == null || tag == null) {
            return;
        }
        Iterator iterator = sCurrentRequests.iterator();
        while (iterator.hasNext()) {
            HttpRequestBase request = (HttpRequestBase) iterator.next();
            if (request.getTag() == tag) {
                request.cancel();
                iterator.remove();
            }
        }
        sRequestQueue.cancelAll(tag);
    }

    /**
     * 注册ParamsHandler
     *
     * @param parameterHandler
     */
    public static void registerParamsHandler(IParameterHandler parameterHandler) {
        if (parameterHandler == null) {
            return;
        }
        if (sParamsHandler == null) {
            sParamsHandler = new ArrayList<IParameterHandler>();
        }
        sParamsHandler.add(parameterHandler);
    }

    /**
     * 反注册ParamsHandler
     *
     * @param parameterHandler
     */
    public static void unregisterParamsHandler(IParameterHandler parameterHandler) {
        if (parameterHandler == null) {
            return;
        }
        if (sParamsHandler == null) {
            return;
        }
        sParamsHandler.remove(parameterHandler);
    }


//    /*Package*/ static void paramsBuild(MultipartEntityBuilder builder) {
//        if (sParamsHandler == null) {
//            return;
//        } else {
//            HashMap<String, String> params = new HashMap<String, String>();
//            for (IParameterHandler parameterHandler : sParamsHandler) {
//                parameterHandler.build(params);
//            }
//            Iterator<Map.Entry<String, String>> a = params.entrySet().iterator();
//            while (a.hasNext()) {
//                Map.Entry<String, String> me = a.next();
//                builder.addTextBody(me.getKey(), me.getValue());
//            }
//
//        }
//    }


    /*Package*/
    static void paramsBuild(HashMap<String, String> params) {
        if (sParamsHandler == null) {
            return;
        }
        for (IParameterHandler parameterHandler : sParamsHandler) {
            parameterHandler.build(params);
        }
    }


    /**
     * 获取Request Queue
     *
     * @return
     */
    public static RequestQueue getRequestQueue() {
        return sRequestQueue;
    }

    /**
     * 创建volley的requestQueue
     *
     * @param app
     * @return
     */
    private static RequestQueue newRequestQueue(Application app) {
        File cacheDir = new File(app.getCacheDir(), DEFAULT_CACHE_DIR);
        DiskBasedCache diskBasedCache = new DiskBasedCache(cacheDir);
        sStack = new HurlStack();
        Network network = new BasicNetwork(sStack);
        RequestQueue queue = new RequestQueue(diskBasedCache, network);
        queue.start();
        return queue;
    }

    /**
     * 添加Request
     *
     * @param request
     */
    public static void addRequest(Request request) {
        if (sRequestQueue == null) {
            return;
        }
        sRequestQueue.add(request);
    }

    /**
     * Parse Params
     *
     * @param params
     * @return
     */
    static String parseParams(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=").append(entry.getValue());
            sb.append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 把get后边的参数放入map中
     *
     * @param strUrlParam
     * @param params
     */
    public static void buildParams(String strUrlParam, HashMap<String, String> params) {
        if (null == strUrlParam || "".equals(strUrlParam)) return;
        if (strUrlParam.startsWith("&") || strUrlParam.startsWith("?")) {
            strUrlParam = strUrlParam.substring(1, strUrlParam.length());
        }
        String[] arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                params.put(arrSplitEqual[0], arrSplitEqual[1]);
            }
        }
    }

}
