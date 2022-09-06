package com.iscloud.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iscloud.common.cst.BaseCst.WechatCst;
import com.iscloud.common.cst.BaseCst.ResultCst;
import com.iscloud.common.cst.BaseCst.CharsetCst;
import com.iscloud.common.cst.BaseCst.WebCst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

/**
 * @Desc: Http请求工具类
 * @Author: Yu.Hua
 * @Date: 2020/1/13 0013 16:15
 */
@Slf4j
@SuppressWarnings("unused")
public class HttpSendUtils implements WebCst, CharsetCst {

    public static final int SLEEP_TIME = 1000, HTTP_TIME_OUT = 300000, repeatCount = 3;
    public static final String LOG_INFO = "===== >> doHttpResponse() Exception is : {}";
    public static final boolean IS_REPEAT = true;

    private static final RequestConfig CFG;

    static {
        CFG = RequestConfig.custom()
                .setRedirectsEnabled(true)
                .setSocketTimeout(HTTP_TIME_OUT)
                .setConnectTimeout(HTTP_TIME_OUT)
                .setAuthenticationEnabled(true)
                .setConnectionRequestTimeout(HTTP_TIME_OUT)
                .build();
    }

    public static HttpGet entityGET(HttpRequestEntity entity) {
        HttpGet http = new HttpGet(entity.getUrl());
        http.setConfig(CFG);
        if (MapUtils.isNotEmpty(entity.getHeadParams())) {
            entity.getHeadParams().forEach(http::addHeader);
        }
        return http;
    }

    public static HttpPost entityPOST(HttpRequestEntity entity) {
        HttpPost http = new HttpPost(entity.getUrl());
        http.setConfig(CFG);
        return http;
    }

    @SuppressWarnings("static-access")
    public static void sleep(long sleep) {
        try {
            Thread.currentThread().sleep(sleep);
        } catch (InterruptedException e1) {
            log.error(LOG_INFO, e1.getLocalizedMessage());
        }
    }

    public static HttpResponse doHttpResponse(HttpClient httpClient, HttpUriRequest http, int c) {
        HttpResponse response = null;
        try {
            response = httpClient.execute(http);
        } catch (Exception e) {
            log.error(LOG_INFO, e.getLocalizedMessage());
            log.error("method->[{}], url->{}", http.getMethod(), http.getURI().toASCIIString());
            if (IS_REPEAT && c < repeatCount) {
                log.info("===== >> will sleep : {} ms", SLEEP_TIME);
                c++;
                sleep(SLEEP_TIME);
                response = doHttpResponse(httpClient, http, c);
            }
        }
        return response;
    }

    @SneakyThrows
    public static HttpResponse post2Response(HttpRequestEntity entity, HttpClient httpClient) {
        HttpPost httpPost = entityPOST(entity);
        // 设置请求头
        for (Entry<String, String> entry : entity.getHeadParams().entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }
        // 设置请求体
        String json = entity.getJson();
        if (StringUtils.isNotBlank(json)) {
            log.info("http post req json :\n{}", json);
            StringEntity requestEntity = new StringEntity(json, UTF8);
            requestEntity.setContentEncoding(UTF8);
            httpPost.setEntity(requestEntity);
        } else {
            Map<String, String> params = entity.getBodyParams();
            log.info("http post req params :\n{}", JSON.toJSONString(params));
            if (MapUtils.isNotEmpty(params)) {
                List<NameValuePair> list = new LinkedList<>();
                params.forEach((k, v) -> list.add(new BasicNameValuePair(k, v)));
                if (list.size() > 0) {
                    UrlEncodedFormEntity fromEntity = new UrlEncodedFormEntity(list, UTF8);
                    fromEntity.setContentType(CONTENT_TYPE_FORM);
                    httpPost.setEntity(fromEntity);
                }
            }
        }
        return doHttpResponse(httpClient, httpPost, 0);
    }

    @SneakyThrows
    public static Map<String, Object> post(HttpRequestEntity entity) {
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpResponse res = post2Response(entity, httpClient);
        return resultMap(res, cookieStore);
    }

    public static Map<String, Object> get(HttpRequestEntity entity) {
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        return resultMap(doHttpResponse(httpClient, entityGET(entity), 0), cookieStore);
    }

    public static Map<String, Object> get(HttpRequestEntity entity, List<Cookie> cookies) {
        CookieStore cookieStore = new BasicCookieStore();
        if (CollectionUtils.isNotEmpty(cookies)) {
            cookies.forEach(cookieStore::addCookie);
        }
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        return resultMap(doHttpResponse(httpClient, entityGET(entity), 0), cookieStore);
    }

    @SneakyThrows
    public static Map<String, Object> resultMap(HttpResponse response, CookieStore cookieStore) {
        if (response != null) {
            List<Cookie> cookies = cookieStore.getCookies();
            Map<String, Object> resultMap = new HashMap<>(cookies.size() + 4);
            cookies.forEach(i -> resultMap.put(i.getName(), i.getValue()));
            resultMap.put(COOKIES_STR, cookies);
            resultMap.put(RES_STR, JSON.parseObject(EntityUtils.toString(response.getEntity(), UTF8)));
            resultMap.put(STS_CODE, response.getStatusLine().getStatusCode());
            if (response.getLastHeader(LOCATION_STR) != null) {
                resultMap.put(LOCATION_STR, response.getLastHeader(LOCATION_STR).getValue());
            }
            return resultMap;
        }
        return Collections.emptyMap();
    }

    @SneakyThrows
    public static JSONObject resJson(HttpResponse response, CookieStore cookieStore) {
        JSONObject res = new JSONObject();
        if (response != null) {
            List<Cookie> cookies = cookieStore.getCookies();
            cookies.forEach(i -> res.put(i.getName(), i.getValue()));
            res.put(COOKIES_STR, cookies);
            res.put(RES_STR, EntityUtils.toString(response.getEntity(), UTF8));
            res.put(STS_CODE, response.getStatusLine().getStatusCode());
            if (response.getLastHeader(LOCATION_STR) != null) {
                res.put(LOCATION_STR, response.getLastHeader(LOCATION_STR).getValue());
            }
            return res;
        }
        return res;
    }

    @SneakyThrows
    public static JSONObject post4Json(HttpRequestEntity entity) {
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpResponse res = post2Response(entity, httpClient);
        return resJson(res, cookieStore);
    }

    public static JSONObject get4JSON(HttpRequestEntity entity) {
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        return resJson(doHttpResponse(httpClient, entityGET(entity), 0), cookieStore);
    }

    public static JSONObject get4JSON(HttpRequestEntity entity, List<Cookie> cookies) {
        CookieStore cookieStore = new BasicCookieStore();
        if (CollectionUtils.isNotEmpty(cookies)) {
            cookies.forEach(cookieStore::addCookie);
        }
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        return resJson(doHttpResponse(httpClient, entityGET(entity), 0), cookieStore);
    }

    /**
     * @Desc:   调用index-smart接口，根据统一返回数据格式校验结果
     * @Params: [entity]
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: HYbrid
     * @Date:   2022/6/1
     */
    public static JSONObject getISmart4Data(HttpRequestEntity entity) {
        if (entity != null) {
            CloseableHttpClient httpClient = HttpClients.custom().build();
            entity.addHead(WebCst.SN, WebCst.SN_1);
            return resISmartJson(doHttpResponse(httpClient, entityGET(entity), 0), entity.getUrl());
        }
        return new JSONObject();
    }

    @SneakyThrows
    public static JSONObject resISmartJson(HttpResponse response, String url) {
        if (response != null) {
            int stsCode = response.getStatusLine().getStatusCode();
            if (ResultCst.STATUS_SUCCESS != stsCode) {
                log.error("调用接口[{}]返回异常状态码[{}]", url, stsCode);
            } else {
                String json = EntityUtils.toString(response.getEntity(), UTF8);
                if (StringUtils.isNotBlank(json)) {
                    JSONObject res = JSON.parseObject(json);
                    String status = res.getString(WebCst.STATUS);
                    if (String.valueOf(ResultCst.STATUS_SUCCESS).equalsIgnoreCase(status)) {
                        return res;
                    } else {
                        log.error("调用接口[{}]返回异常状态码[{}]，错误码[{}]，异常信息->{}", url, status,
                                res.getString(WebCst.ERROR_CODE), res.getString(WebCst.MESSAGE));
                    }
                }
            }
        }
        return new JSONObject();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HttpRequestEntity {
        private String url;
        private Map<String, String> headParams = new HashMap<>(0);
        private Map<String, String> bodyParams = new HashMap<>(0);
        private String json;

        public HttpRequestEntity(String url) {
            this.url = url;
        }

        public HttpRequestEntity(String url, String json) {
            this.json = json;
            this.url = url;
        }

        public HttpRequestEntity(String url, JSONObject json) {
            if (json != null && json.size() > 0) {
                json.forEach((k, v) -> bodyParams.put(k, String.valueOf(v)));
            }
            this.url = url;
        }

        public void addHead(String k, String v) {
            if (headParams == null) {
                headParams = new HashMap<>(0);
            }
            headParams.put(k, v);
        }

        public void addBody(String k, String v) {
            if (bodyParams == null) {
                bodyParams = new HashMap<>(0);
            }
            bodyParams.put(k, v);
        }

        public void addPostBody(String json) {
            if (StringUtils.isNotBlank(json)) {
                this.json = json;
            }
        }
    }

    @SneakyThrows
    public static byte[] getPicRemote(String urlStr) {
        if (StringUtils.isNotBlank(urlStr)) {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            byte[] res = os.toByteArray();
            is.close();
            os.close();
            return res;
        }
        return null;
    }

    /**
     * @Desc: 微信请求 - 信任管理器
     * @Author: Yu.Hua
     * @Date: 2020/1/14 0014 19:42
     */
    public static class MyX509TrustManager implements X509TrustManager {

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    public static JSONObject httpRequest4Wechat(String json, String url, String method) {
        TrustManager[] tm = {new MyX509TrustManager()};
        try {
            SSLContext sslContext = SSLContext.getInstance(WechatCst.SSL, WechatCst.SSL_SUN);
            sslContext.init(null, tm, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) new URL(url).openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            httpUrlConn.setRequestMethod(method);
            if (GET.equalsIgnoreCase(method)) {
                httpUrlConn.connect();
            }
            if (StringUtils.isNotBlank(json)) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                outputStream.write(json.getBytes(StandardCharsets.UTF_8));
                outputStream.close();
            }
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str;
            StringBuilder buffer = new StringBuilder();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            httpUrlConn.disconnect();
            return JSONObject.parseObject(buffer.toString());
        } catch (ConnectException ce) {
            log.error("Weixin server connection timed out.");
        } catch (Exception e) {
            log.error("https request error:\n", e);
        }
        return new JSONObject();
    }

    public static JSONObject get4Wechat(String json, String url) {
        return httpRequest4Wechat(json, url, GET);
    }

    public static JSONObject post4Wechat(String json, String url) {
        return httpRequest4Wechat(json, url, POST);
    }

}