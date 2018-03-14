package com.mp.service;

import com.alibaba.fastjson.JSON;
import com.mp.bean.ReturnBean;
import com.openapi.sdk.service.DataExchangeService;
import com.openapi.sdk.service.TransCode;
import redis.clients.jedis.Jedis;


public class MainService {
    private static String apiUrl = "https://zhiyunopenapi.95155.com/apis";//联调测试环境接口地址

    private static String apiUser = "45c738c6-977a-4b2e-bbe0-748ff4197898";//这里需要替换成：您的API账号
    private static String password = "t604U2s25JQW43qVdQW2674lk212a9";//这里需要替换成：您的API账号密码
    private static String client_id = "8a7183bd-ee1f-4b21-9d3f-9f278b789297";//这里需要替换成：您的客户端ID

    private static Jedis jedis = RedisService.getJedis();
    private static int EXPIRE_TIME = 2 * 24 * 60 * 60;

    /**
     * API用户登陆
     * 用户首次调用接口，须先登录，认证通过后生成令牌。
     * 令牌有效期默认为3天，登录后之前的令牌将立即失效，多服务调用业务接口时，建议由统一服务调用登录接口将令牌缓存起来，多个服务统一从共享缓存中获取令牌。
     * 令牌失效后再调用登录接口获取令牌，避免频繁调用登录接口，建议一天内登录次数不超过10次，超过10次将触发安全系统报警。
     */
    public static void login() {
        try {
            System.out.println("API用户登陆 ");
            String p = "user=" + apiUser + "&pwd=" + password;
            System.out.println("参数:" + p);
            p = TransCode.encode(p);//DES加密
            String url = apiUrl + "/login/" + p + "?client_id=" + client_id;
            DataExchangeService des = new DataExchangeService(5000, 5000);// 请求访问超时时间,读取数据超时时间
            System.out.println("请求地址:" + url);
            String res = des.accessHttps(url, "POST");
            res = TransCode.decode(res);//DES解密
            System.out.println("返回:" + res);
            ReturnBean bean = JSON.parseObject(res, ReturnBean.class);
            jedis.setex("zhiyun_auth_token", EXPIRE_TIME, bean.getResult().toString());
            analysisStatus(bean);//解析接口返回状态
            System.out.println("------------------------------------------------------");
        } catch (Exception e) {
            System.out.println("e:" + e.getMessage());
        }
    }

    /**
     * 获取token
     *
     * @return
     */
    private static String getToken() {
        String token = jedis.get("zhiyun_auth_token");
        if (token != null) {
            System.out.println("redis token:" + token);
            return token;
        }
        login();
        token = jedis.get("zhiyun_auth_token");
        System.out.println("new token:" + token);
        return token;
    }


    /**
     * 车辆最新位置查询（车牌号）接口
     * 本接口提供指定车牌号的车辆最新位置查询。
     * 返回值示例：
     * {"result":{"adr":"安徽省安庆市怀宁县长琳塑业，向西方向，148米","drc":"225","lat":"18451089","lon":"70094469","spd":"73.0","utc":"1496826420000","province":"安徽省","city":"安庆市","country":"怀宁县"},"status":1001}
     */
    public static ReturnBean vLastLocationV3(String vclN) {
        try {
            System.out.println(" 车辆最新位置查询（车牌号）接口");
            String token = getToken();
            if (token == null) {
                ReturnBean bean = new ReturnBean();
                bean.setStatus("1011");
                bean.setResult("登录智运失败");
                return bean;
            }
            String p = "token=" + token + "&vclN=" + vclN + "&timeNearby=24";
            System.out.println("参数:" + p);
            p = TransCode.encode(p);//DES加密
            String url = apiUrl + "/vLastLocationV3/" + p + "?client_id=" + client_id;
            DataExchangeService des = new DataExchangeService(5000, 5000);// 请求访问超时时间,读取数据超时时间
            System.out.println("请求地址:" + url);
            String res = des.accessHttps(url, "POST");
            res = TransCode.decode(res);//DES解密
            System.out.println("返回:" + res);
            ReturnBean bean = JSON.parseObject(res, ReturnBean.class);
            analysisStatus(bean);//解析接口返回状态
            System.out.println("------------------------------------------------------");
            return bean;
        } catch (Exception e) {
            System.out.println("e:" + e.getMessage());
            return null;
        }
    }

    /**
     * 车辆轨迹查询
     *
     * @param vclN
     * @param qryBtm
     * @param qryEtm
     */
    public static ReturnBean vHisTrack24(String vclN, String qryBtm, String qryEtm) {
        try {
            System.out.println("车辆轨迹查询（车牌号）接口");
            String token = getToken();
            if (token == null) {
                ReturnBean bean = new ReturnBean();
                bean.setStatus("1011");
                bean.setResult("登录智运失败");
                return bean;
            }
            String p = "token=" + token + "&vclN=" + vclN + "&qryBtm=" + qryBtm + "&qryEtm=" + qryEtm;//陕YH0009 2017-05-03 01:00:00 2017-05-03 01:59:59
            System.out.println("参数:" + p);
            p = TransCode.encode(p);//DES加密
            String url = apiUrl + "/vHisTrack24/" + p + "?client_id=" + client_id;
            DataExchangeService des = new DataExchangeService(5000, 5000);// 请求访问超时时间,读取数据超时时间
            System.out.println("请求地址:" + url);
            String res = des.accessHttps(url, "POST");
            res = TransCode.decode(res);//DES解密
            System.out.println("返回:" + res);
            ReturnBean bean = JSON.parseObject(res, ReturnBean.class);
            analysisStatus(bean);//解析接口返回状态
            System.out.println("------------------------------------------------------");
            return bean;
        } catch (Exception e) {
            System.out.println("e:" + e.getMessage());
            return null;
        }
    }

    /**
     * 车辆入网验证
     * 提供按车牌号判断指定车辆是否在全国货运平台入网服务
     * 接口返回数据解码后样例：
     * {"result":"yes","status":1001}
     */
    public static ReturnBean checkTruckExist(String vclN) {
        try {
            System.out.println("车辆入网验证");
            String token = getToken();
            if (token == null) {
                ReturnBean bean = new ReturnBean();
                bean.setStatus("1011");
                bean.setResult("登录智运失败");
                return bean;
            }
            String p = "token=" + token + "&vclN=" + vclN;//陕YH0009
            System.out.println("参数:" + p);
            p = TransCode.encode(p);//DES加密
            String url = apiUrl + "/checkTruckExist/" + p + "?client_id=" + client_id;
            DataExchangeService des = new DataExchangeService(5000, 5000);// 请求访问超时时间,读取数据超时时间
            System.out.println("请求地址:" + url);
            String res = des.accessHttps(url, "POST");
            res = TransCode.decode(res);//DES解密
            System.out.println("返回:" + res);
            ReturnBean bean = JSON.parseObject(res, ReturnBean.class);
            analysisStatus(bean);//解析接口返回状态
            return bean;
        } catch (Exception e) {
            System.out.println("e:" + e.getMessage());
            return null;
        }
    }

    /**
     * 车辆行驶证信息查询接口
     * 本接口通过指定车牌号、道路运输证号码，验证道路运输证信息是否准确。
     * 接口返回数据解码后样例：
     * {"result":{"areaName":"陕西省","boxHgt":"--","boxLng":"--","boxWdt":"--","cmpNm":"宋**","ldTn":"1800","prdCdNm":"123999","serviceName":"陕西服务商danwei","servicePhone":"18900000001","vbrndCdNm":"江淮","vclDrwTn":"","vclHgt":"1500","vclLng":"1700","vclTn":"42000","vclTpNm":"农用车","vclWdt":"1600","vclWnrNm":"宋**","vclWnrPhn":"138****8000","vin":"u3221008"},"status":1001}
     */
    public static ReturnBean vQueryLicense(String vclN) {
        try {
            System.out.println("车辆行驶证信息查询接口");
            String token = getToken();
            if (token == null) {
                ReturnBean bean = new ReturnBean();
                bean.setStatus("1011");
                bean.setResult("登录智运失败");
                return bean;
            }
            String p = "token=" + token + "&vclN=" + vclN + "&vco=2";
            System.out.println("参数:" + p);
            p = TransCode.encode(p);//DES加密
            String url = apiUrl + "/vQueryLicense/" + p + "?client_id=" + client_id;
            DataExchangeService des = new DataExchangeService(5000, 5000);
            System.out.println("请求地址:" + url);
            String res = des.accessHttps(url, "POST");
            res = TransCode.decode(res);//DES解密
            System.out.println("返回:" + res);
            ReturnBean bean = JSON.parseObject(res, ReturnBean.class);
            analysisStatus(bean);//解析接口返回状态
            System.out.println("------------------------------------------------------");
            return bean;
        } catch (Exception e) {
            System.out.println("e:" + e.getMessage());
            return null;
        }
    }

    /**
     * 车辆停车查询接口
     * 本接口提供指定车牌号，指定时间段查询车辆停车信息，开始时间和结束时间不能超过24小时。
     * 接口返回数据解码后样例：
     * {"result":{"parkInfo":[{"adr":"天津市蓟县田野农家，向东方向，253米","bte":"2017-07-03 10:37:05","ete":"2017-07-03 11:21:42","lat":"39.82045333333333","lon":"117.29147166666667","parkMins":"44.0"},{"adr":"天津市蓟县田野农家，向东方向，232米","bte":"2017-07-03 11:23:52","ete":"2017-07-03 13:05:32","lat":"39.82045333333333","lon":"117.29171333333333","parkMins":"101.0"},{"adr":"天津市蓟县田野农家，向东方向，253米","bte":"2017-07-03 13:40:13","ete":"2017-07-03 13:56:33","lat":"39.82045333333333","lon":"117.29147166666667","parkMins":"16.0"}],"parkSize":3},"status":1001}
     */
    public static ReturnBean vQueryPark(String vclN, String qryBtm, String qryEtm, String parkMins) {
        try {
            System.out.println("车辆停车查询接口");
            String token = getToken();
            if (token == null) {
                ReturnBean bean = new ReturnBean();
                bean.setStatus("1011");
                bean.setResult("登录智运失败");
                return bean;
            }
            String p = "token=" + token + "&vclN=" + vclN + "&vco=2&qryBtm=" + qryBtm + "&qryEtm=" + qryEtm + "&parkMins=" + parkMins;
            System.out.println("参数:" + p);
            p = TransCode.encode(p);//DES加密
            String url = apiUrl + "/vQueryPark/" + p + "?client_id=" + client_id;
            DataExchangeService des = new DataExchangeService(5000, 5000);
            System.out.println("请求地址:" + url);
            String res = des.accessHttps(url, "POST");
            res = TransCode.decode(res);//DES解密
            System.out.println("返回:" + res);
            ReturnBean bean = JSON.parseObject(res, ReturnBean.class);
            analysisStatus(bean);//解析接口返回状态
            System.out.println("------------------------------------------------------");
            return bean;
        } catch (Exception e) {
            System.out.println("e:" + e.getMessage());
            return null;
        }
    }

    /**
     * 解析接口返回状态
     */
    private static void analysisStatus(ReturnBean bean) {
        if ("1001".equals(bean.getStatus())) {
            System.out.println("√	1001	接口执行成功");
        } else if ("1002".equals(bean.getStatus())) {
            System.out.println("×	1002	参数不正确（参数为空、查询时间范围不正确、参数数量不正确）");
        } else if ("1003".equals(bean.getStatus())) {
            System.out.println("×	1003	车辆调用数量已达上限");
        } else if ("1004".equals(bean.getStatus())) {
            System.out.println("×	1004	接口调用次数已达上限");
        } else if ("1005".equals(bean.getStatus())) {
            System.out.println("×	1005	该API账号未授权指定所属行政区划数据范围");
        } else if ("1006".equals(bean.getStatus())) {
            System.out.println("√	1006	无结果");
        } else if ("1010".equals(bean.getStatus())) {
            System.out.println("×	1010	用户名或密码不正确");
        } else if ("1011".equals(bean.getStatus())) {
            System.out.println("×	1011	IP不在白名单列表");
        } else if ("1012".equals(bean.getStatus())) {
            System.out.println("×	1012	账号已禁用");
        } else if ("1013".equals(bean.getStatus())) {
            System.out.println("×	1013	账号已过有效期");
        } else if ("1014".equals(bean.getStatus())) {
            System.out.println("×	1014	无接口权限");
        } else if ("1015".equals(bean.getStatus())) {
            System.out.println("×	1015	用户认证系统已升级，请使用令牌访问");
        } else if ("1016".equals(bean.getStatus())) {
            System.out.println("√	1016	令牌失效");
        } else if ("1017".equals(bean.getStatus())) {
            System.out.println("×	1017	账号欠费");
        } else if ("1018".equals(bean.getStatus())) {
            System.out.println("×	1018	授权的接口已禁用");
        } else if ("1019".equals(bean.getStatus())) {
            System.out.println("×	1019	授权的接口已过期");
        } else if ("1020".equals(bean.getStatus())) {
            System.out.println("×	1020	该车调用次数已达上限");
        } else if ("9001".equals(bean.getStatus())) {
            System.out.println("×	9001	系统异常");
        }
    }

}
