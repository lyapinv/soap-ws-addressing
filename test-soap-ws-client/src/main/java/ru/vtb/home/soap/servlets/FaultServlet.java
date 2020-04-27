package ru.vtb.home.soap.servlets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.vtb.home.soap.RunClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FaultServlet extends HttpServlet {

    private static final Log logger = LogFactory.getLog(FaultServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        logger.info("!!! Client handling web service POST fault");
        RunClient.latch.countDown();
    }
}