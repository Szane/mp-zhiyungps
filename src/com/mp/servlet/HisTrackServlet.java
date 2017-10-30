package com.mp.servlet;

import com.mp.bean.ReturnBean;
import com.mp.service.MainService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "HisTrackServlet", urlPatterns = "/hisTrack")
public class HisTrackServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String vclN = new String(request.getParameter("vclN").getBytes("ISO-8859-1"), "UTF-8");
        String qryBtm = new String(request.getParameter("qryBtm").getBytes("ISO-8859-1"), "UTF-8");
        String qryEtm = new String(request.getParameter("qryEtm").getBytes("ISO-8859-1"), "UTF-8");
        ReturnBean bean = MainService.vHisTrack24(vclN, qryBtm, qryEtm);
        response.setCharacterEncoding("utf8");
        response.setContentType("application/json; charset=utf8");
        PrintWriter out = response.getWriter();
        if (bean != null)
            out.write("{result:" + bean.getResult() + ",status:" + bean.getStatus() + "}");
        else
            out.write("{result:null}");
        out.close();
    }
}
