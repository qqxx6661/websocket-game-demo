package com.example.websocketgamedemo.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Http请求封装
 * TODO: cookies若传入非法参数报NPE
 * @author: Zhendong Yang
 * @date: 2019-07-08
 **/
public class HttpUtil {

    private CloseableHttpClient httpClient;
    private CookieStore cookieStore;

    public HttpUtil() {
        this.cookieStore = new BasicCookieStore();
        this.httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        // 若需要拦截请求Debug（如Fiddler4）需要设置HttpClients走localhost
        //this.httpClient = HttpClients.custom().setProxy(new HttpHost("localhost",8888)).setDefaultCookieStore(cookieStore).build();
    }

    /**
     * 不带参数的get
     * @param uri
     * @return
     * @throws Exception
     */
    public HttpResult doGet(String uri) throws Exception {
        return this.doGet(uri, null, null);
    }

    /**
     * 带参数的get请求
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public HttpResult doGet(String url, Map<String, Object> params, Map<String, Object> cookies) throws Exception {

        // 1.创建URIBuilder
        URIBuilder uriBuilder = new URIBuilder(url);

        // 2.设置请求参数
        if (params != null) {
            // 遍历请求参数
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                // 封装请求参数
                uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }
        }

        // 2.1 设置cookies
        if (cookies != null) {
            // 遍历cookies
            for (Map.Entry<String, Object> entry : cookies.entrySet()) {
                // 封装cookies
                BasicClientCookie cookie = new BasicClientCookie(entry.getKey(), entry.getValue().toString());
                this.cookieStore.addCookie(cookie);
            }
        }

        // 3.创建请求对象httpGet
        HttpGet httpGet = new HttpGet(uriBuilder.build());

        // 4.使用httpClient发起请求
        CloseableHttpResponse response = this.httpClient.execute(httpGet);

        // 5.解析返回结果，封装返回对象httpResult
        // 5.1获取状态码
        int code = response.getStatusLine().getStatusCode();
        // 5.2 获取响应体
        // 使用EntityUtils.toString方法必须保证entity不为空
        String body = null;
        if (response.getEntity() != null) {
            body = EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        HttpResult result = new HttpResult();
        result.setCode(code);
        result.setBody(body);

        // 6. 打印请求日志
        System.out.println("请求方式：GET");
        System.out.println("URL：" + url);
        if (params != null) {
            System.out.println("参数：" + params.toString());
        }
        if (cookies != null) {
            System.out.println("cookies：" + cookies.toString());
        }
        System.out.println("返回结果：" + result.getBody());
        return result;
    }

    /**
     * 不带参数的post请求
     * @param url
     * @return
     */
    public HttpResult doPost(String url) throws Exception {
        return this.doPost(url, null, null);
    }

    /**
     * 带参数的post请求
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public HttpResult doPost(String url, Map<String, Object> params, Map<String, Object> cookies) throws Exception {

        // 1. 声明httppost
        HttpPost httpPost = new HttpPost(url);

        // 2.封装请求参数，请求数据是表单
        // 声明封装表单数据的容器
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
        if (params != null) {

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                // 封装请求参数到容器中
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
        }
        // 创建表单的Entity类
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");

        // 2.1 设置cookies
        if (cookies != null) {
            // 遍历cookies
            for (Map.Entry<String, Object> entry : cookies.entrySet()) {
                // 封装cookies
                BasicClientCookie cookie = new BasicClientCookie(entry.getKey(), entry.getValue().toString());
                this.cookieStore.addCookie(cookie);
            }
        }

        // 3. 把封装好的表单实体对象设置到HttpPost中
        httpPost.setEntity(entity);

        // 4. 使用Httpclient发起请求
        CloseableHttpResponse response = this.httpClient.execute(httpPost);

        // 5. 解析返回数据，封装HttpResult
        // 5.1状态码
        int code = response.getStatusLine().getStatusCode();
        // 5.2 响应体内容
        String body = null;
        if (response.getEntity() != null) {
            body = EntityUtils.toString(response.getEntity(), "UTF-8");
        }

        HttpResult result = new HttpResult();
        result.setCode(code);
        result.setBody(body);

        // 6. 打印请求日志
        System.out.println("请求方式：POST");
        System.out.println("URL：" + url);
        if (params != null) {
            System.out.println("参数：" + params.toString());
        } else {
            System.out.println("参数：null");
        }
        if (cookies != null) {
            System.out.println("cookies：" + cookies.toString());
        }
        System.out.println("返回结果：" + result.getBody());
        return result;
    }
}
