package com.mp.servlet;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "OC3MD5UtilServlet", urlPatterns = "/paySign")
public class OC3MD5UtilServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {

    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, String> map = new HashMap();
        if (null != request.getParameter("payer")) {
            map.put("payer", request.getParameter("payer"));
        }

        if (null != request.getParameter("amount")) {
            map.put("amount", request.getParameter("amount"));
        }

        if (null != request.getParameter("bizSysFlag")) {
            map.put("bizSysFlag", request.getParameter("bizSysFlag"));
        }

        if (null != request.getParameter("orgId")) {
            map.put("orgId", request.getParameter("orgId"));
        }

        if (null != request.getParameter("orderNo")) {
            map.put("orderNo", request.getParameter("orderNo"));
        }

        if (null != request.getParameter("payCode")) {
            map.put("payCode", request.getParameter("payCode"));
        }

        if (null != request.getParameter("payAcctNo")) {
            map.put("payAcctNo", request.getParameter("payAcctNo"));
        }

        if (null != request.getParameter("receptAcctNo")) {
            map.put("receptAcctNo", request.getParameter("receptAcctNo"));
        }

        if (null != request.getParameter("receptAcctName")) {
            map.put("receptAcctName", request.getParameter("receptAcctName"));
        }

        String valuesBySort = this.getValuesByAscSort(map);
        valuesBySort = new String(valuesBySort.getBytes("ISO-8859-1"), "GBK");
        String digest = this.getKeyedDigest(valuesBySort, "ShanDongXML");
        response.setCharacterEncoding("GBK");
        response.setContentType("application/json; charset=GBK");
        PrintWriter out = response.getWriter();
        out.write("{sign:'" + digest + "'}");
        out.close();
    }

    public String getKeyedDigest(String strSrc, String key) {
        String charSet = "GBK";

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(strSrc.getBytes(charSet));
            StringBuffer result = new StringBuffer();
            byte[] temp = md5.digest(key.getBytes(charSet));

            for (int i = 0; i < temp.length; ++i) {
                result.append(Integer.toHexString(255 & temp[i] | -256).substring(6));
            }

            return result.toString();
        } catch (NoSuchAlgorithmException var8) {
            var8.printStackTrace();
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return null;
    }

    private String getValuesBySort(Map<String, String> map, String rejectKey, String link) {
        String result = null;

        try {
            link = this.isEmpty(link) ? "&" : link;
            result = this.getValuesBySort_asc(map, rejectKey, link);
        } catch (Exception var6) {
            ;
        }

        return result;
    }

    private boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    private String getValuesBySort_asc(Map<String, String> map, String rejectKey, String link) {
        if (map != null && map.size() >= 1) {
            String[] keyArray = (String[]) map.keySet().toArray(new String[0]);
            Arrays.sort(keyArray);
            StringBuilder result = new StringBuilder();
            String[] arr$ = keyArray;
            int len$ = keyArray.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                if (key == null || !key.equals(rejectKey)) {
                    result.append(key).append("=").append((String) map.get(key)).append(link);
                }
            }

            return result.length() > 0 ? result.subSequence(0, result.length() - link.length()).toString() : null;
        } else {
            return null;
        }
    }

    public String getValuesByAscSort(Map<String, String> map) {
        return this.getValuesBySort(map, (String) null, (String) null);
    }
}
