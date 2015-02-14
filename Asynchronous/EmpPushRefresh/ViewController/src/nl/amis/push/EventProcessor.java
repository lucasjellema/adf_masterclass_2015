package nl.amis.push;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class EventProcessor extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);

        String payload = request.getParameter("payload");
        EventsProcessor.publishEvent(new MyEvent(payload));

        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>EventProcessor</title></head>");
        out.println("<body>");
        out.println("<p>Thank you for the event. We have processed it.</p>");
        out.println("</body></html>");
        out.close();
    }
}
