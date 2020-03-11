package com.bsd.user.server.utils.geetest;

import com.bsd.user.server.model.dto.CaptchaValidateDTO;
import com.google.common.collect.Maps;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Java SDK
 */
public class GeetestLib {
    protected final String verName = "4.0";
    protected final String sdkLang = "java";

    protected final String apiUrl = "http://api.geetest.com";

    protected final String registerUrl = "/register.php";
    protected final String validateUrl = "/validate.php";

    protected final String json_format = "1";

    /**
     * 极验验证二次验证表单数据 chllenge
     */
    public static final String fn_geetest_challenge = "geetest_challenge";

    /**
     * 极验验证二次验证表单数据 validate
     */
    public static final String fn_geetest_validate = "geetest_validate";

    /**
     * 极验验证二次验证表单数据 seccode
     */
    public static final String fn_geetest_seccode = "geetest_seccode";

    /**
     * 公钥
     */
    private String captchaId = "";

    /**
     * 私钥
     */
    private String privateKey = "";

    /**
     * 是否开启新的failback
     */
    private boolean newFailback = false;

    /**
     * 返回字符串
     */
    private String responseStr = "";

    /**
     * 调试开关，是否输出调试日志
     */
    public boolean debugCode = true;

    /**
     * 极验验证API服务状态Session Key
     */
    public String gtServerStatusSessionKey = "gt_server_status";

    /**
     * 带参数构造函数
     *
     * @param captchaId
     * @param privateKey
     */
    public GeetestLib(String captchaId, String privateKey, boolean newFailback) {
        this.captchaId = captchaId;
        this.privateKey = privateKey;
        this.newFailback = newFailback;
    }

    /**
     * 获取本次验证初始化返回字符串
     *
     * @return 初始化结果
     */
    public String getResponseStr() {
        return responseStr;
    }

    public String getVersionInfo() {
        return verName;
    }

    /**
     * 预处理失败后的返回格式串
     *
     * @return
     */
    private String getFailPreProcessRes() {
        Long rnd1 = Math.round(Math.random() * 100);
        Long rnd2 = Math.round(Math.random() * 100);
        String md5Str1 = md5Encode(rnd1 + "");
        String md5Str2 = md5Encode(rnd2 + "");
        String challenge = md5Str1 + md5Str2.substring(0, 2);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("success", 0);
            jsonObject.put("gt", this.captchaId);
            jsonObject.put("challenge", challenge);
            jsonObject.put("new_captcha", this.newFailback);
        } catch (JSONException e) {
            gtlog("json dumps error");
        }

        return jsonObject.toString();
    }

    /**
     * 预处理成功后的标准串
     */
    private String getSuccessPreProcessRes(String challenge) {
        gtlog("challenge:" + challenge);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("success", 1);
            jsonObject.put("gt", this.captchaId);
            jsonObject.put("challenge", challenge);
        } catch (JSONException e) {
            gtlog("json dumps error");
        }

        return jsonObject.toString();
    }

    /**
     * 验证初始化预处理
     *
     * @return 1表示初始化成功，0表示初始化失败
     */
    public int preProcess(HashMap<String, String> data) {
        if (registerChallenge(data) != 1) {
            this.responseStr = this.getFailPreProcessRes();
            return 0;
        }

        return 1;
    }

    /**
     * 用captchaID进行注册，更新challenge
     *
     * @return 1表示注册成功，0表示注册失败
     */
    private int registerChallenge(HashMap<String, String> data) {
        try {
            String userId = data.get("user_id");
            String clientType = data.get("client_type");
            String ipAddress = data.get("ip_address");

            String getUrl = apiUrl + registerUrl + "?";
            String param = "gt=" + this.captchaId + "&json_format=" + this.json_format;

            if (userId != null) {
                param = param + "&user_id=" + URLEncoder.encode(userId, "utf-8");
            }
            if (clientType != null) {
                param = param + "&client_type=" + URLEncoder.encode(clientType, "utf-8");
            }
            if (ipAddress != null) {
                param = param + "&ip_address=" + URLEncoder.encode(ipAddress, "utf-8");
            }

            gtlog("GET_URL:" + getUrl + param);
            String resultStr = readContentFromGet(getUrl + param);
            if (resultStr.equals("fail")) {
                gtlog("gtServer register challenge failed");
                return 0;
            }

            gtlog("result:" + resultStr);
            JSONObject jsonObject = new JSONObject(resultStr);
            String returnChallenge = jsonObject.getString("challenge");

            gtlog("return_challenge:" + returnChallenge);

            if (returnChallenge.length() == 32) {
                this.responseStr = this.getSuccessPreProcessRes(this.md5Encode(returnChallenge + this.privateKey));
                return 1;
            } else {
                gtlog("gtServer register challenge error");
                return 0;
            }
        } catch (Exception e) {
            gtlog(e.toString());
            gtlog("exception:register api");
        }
        return 0;
    }

    /**
     * 判断一个表单对象值是否为空
     *
     * @param gtObj
     * @return
     */
    protected boolean objIsEmpty(Object gtObj) {
        if (gtObj == null) {
            return true;
        }

        return gtObj.toString().trim().length() == 0;
    }

    /**
     * 检查客户端的请求是否合法,三个只要有一个为空，则判断不合法
     *
     * @param challenge
     * @param validate
     * @param seccode
     * @return
     */
    private boolean requestIsLegal(String challenge, String validate, String seccode) {
        if (objIsEmpty(challenge)) {
            return false;
        }

        if (objIsEmpty(validate)) {
            return false;
        }

        if (objIsEmpty(seccode)) {
            return false;
        }

        return true;
    }

    public int enhancedValidateRequest(CaptchaValidateDTO captchaValidateDTO) throws UnsupportedEncodingException {
        String challenge = captchaValidateDTO.getChllenge();
        String validate = captchaValidateDTO.getValidate();
        String seccode = captchaValidateDTO.getSeccode();
        HashMap<String, String> params = createCustomerParam(captchaValidateDTO.getUserId(), captchaValidateDTO.getClientType(), captchaValidateDTO.getIp());
        return enhancedValidateRequest(challenge, validate, seccode, params);
    }


    /**
     * 创建自定义参数
     *
     * @param userId     用户ID
     * @param clientType 客户端类型
     * @param ip         IP
     * @return
     */
    public HashMap<String, String> createCustomerParam(String userId, String clientType, String ip) {
        //自定义参数,可选择添加
        HashMap<String, String> param = Maps.newHashMap();
        param.put("user_id", userId); //网站用户id
        param.put("client_type", clientType); //客户端类型
        param.put("ip_address", ip); //传输用户请求验证时所携带的IP
        return param;
    }

    /**
     * 服务正常的情况下使用的验证方式,向gt-server进行二次验证,获取验证结果
     *
     * @param challenge
     * @param validate
     * @param seccode
     * @return 验证结果, 1表示验证成功0表示验证失败
     */
    public int enhancedValidateRequest(String challenge, String validate, String seccode, HashMap<String, String> data) throws UnsupportedEncodingException {
        if (!requestIsLegal(challenge, validate, seccode)) {
            return 0;
        }

        gtlog("request legitimate");

        String userId = data.get("user_id");
        String clientType = data.get("client_type");
        String ipAddress = data.get("ip_address");

        String postUrl = this.apiUrl + this.validateUrl;
        String param = String.format("challenge=%s&validate=%s&seccode=%s&json_format=%s", challenge, validate, seccode, this.json_format);

        if (userId != null) {
            param = param + "&user_id=" + URLEncoder.encode(userId, "utf-8");
        }
        if (clientType != null) {
            param = param + "&client_type=" + URLEncoder.encode(clientType, "utf-8");
        }
        if (ipAddress != null) {
            param = param + "&ip_address=" + URLEncoder.encode(ipAddress, "utf-8");
        }

        gtlog("param:" + param);

        String response = "";
        try {
            if (validate.length() <= 0) {
                return 0;
            }

            if (!checkResultByPrivate(challenge, validate)) {
                return 0;
            }

            gtlog("checkResultByPrivate");

            response = readContentFromPost(postUrl, param);

            gtlog("response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String returnSeccode = "";

        try {
            JSONObject return_map = new JSONObject(response);
            returnSeccode = return_map.getString("seccode");
            gtlog("md5: " + md5Encode(returnSeccode));

            if (returnSeccode.equals(md5Encode(seccode))) {
                return 1;
            } else {
                return 0;
            }
        } catch (JSONException e) {
            gtlog("json load error");
            return 0;
        }
    }

    /**
     * failback使用的验证方式
     *
     * @param challenge
     * @param validate
     * @param seccode
     * @return 验证结果, 1表示验证成功0表示验证失败
     */
    public int failbackValidateRequest(String challenge, String validate, String seccode) {
        gtlog("in failback validate");

        if (!requestIsLegal(challenge, validate, seccode)) {
            return 0;
        }
        gtlog("request legitimate");

        return 1;
    }

    /**
     * 输出debug信息，需要开启debugCode
     *
     * @param message
     */
    public void gtlog(String message) {
        if (debugCode) {
            System.out.println("gtlog: " + message);
        }
    }

    protected boolean checkResultByPrivate(String challenge, String validate) {
        String encodeStr = md5Encode(privateKey + "geetest" + challenge);
        return validate.equals(encodeStr);
    }

    /**
     * 发送GET请求，获取服务器返回结果
     *
     * @param url
     * @return 服务器返回结果
     * @throws IOException
     */
    private String readContentFromGet(String url) throws IOException {
        URL getUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();

        connection.setConnectTimeout(2000);// 设置连接主机超时（单位：毫秒）
        connection.setReadTimeout(2000);// 设置从主机读取数据超时（单位：毫秒）

        // 建立与服务器的连接，并未发送数据
        connection.connect();

        if (connection.getResponseCode() == 200) {
            // 发送数据到服务器并使用Reader读取返回的数据
            StringBuilder sBuffer = new StringBuilder();

            InputStream inStream = null;
            byte[] buf = new byte[1024];
            inStream = connection.getInputStream();
            for (int n; (n = inStream.read(buf)) != -1; ) {
                sBuffer.append(new String(buf, 0, n, StandardCharsets.UTF_8));
            }
            inStream.close();
            connection.disconnect();// 断开连接

            return sBuffer.toString();
        } else {
            return "fail";
        }
    }

    /**
     * 发送POST请求，获取服务器返回结果
     *
     * @param url
     * @return 服务器返回结果
     * @throws IOException
     */
    private String readContentFromPost(String url, String data) throws IOException {
        gtlog(data);
        URL postUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();

        connection.setConnectTimeout(2000);// 设置连接主机超时（单位：毫秒）
        connection.setReadTimeout(2000);// 设置从主机读取数据超时（单位：毫秒）
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // 建立与服务器的连接，并未发送数据
        connection.connect();

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
        outputStreamWriter.write(data);
        outputStreamWriter.flush();
        outputStreamWriter.close();

        if (connection.getResponseCode() == 200) {
            // 发送数据到服务器并使用Reader读取返回的数据
            StringBuilder sBuffer = new StringBuilder();

            InputStream inStream = null;
            byte[] buf = new byte[1024];
            inStream = connection.getInputStream();
            for (int n; (n = inStream.read(buf)) != -1; ) {
                sBuffer.append(new String(buf, 0, n, StandardCharsets.UTF_8));
            }
            inStream.close();
            connection.disconnect();// 断开连接

            return sBuffer.toString();
        } else {
            return "fail";
        }
    }

    /**
     * md5 加密
     *
     * @param plainText
     * @return
     * @time 2014年7月10日 下午3:30:01
     */
    private String md5Encode(String plainText) {
        String reMd5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] b = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte value : b) {
                i = value;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }

            reMd5 = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return reMd5;
    }
}
