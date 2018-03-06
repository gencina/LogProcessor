package oracle.tuxedo.scheduler;

import commonj.timers.Timer;
import commonj.timers.TimerManager;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.tuxedo.batch.TuxedoTask;
import oracle.tuxedo.service.TimerBean;


public class TuxedoSchedule extends HttpServlet {
    
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
    private TimerManager tm = null;
    private InitialContext ic = null;
    private ServletConfig config = null;
    private String delay = null;
    private String threads = null;
    
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;
        
        try {
            ic = new InitialContext();
        } catch (NamingException e) {
            e.printStackTrace();
        }

        try {
            tm = (TimerManager)ic.lookup("java:comp/env/tm/TimerManager");
        } catch (NamingException e) {
            e.printStackTrace();
        }
        
        String path = getServletContext().getInitParameter("properties.path");
        delay = getServletContext().getInitParameter("seconds.delay");
        threads = getServletContext().getInitParameter("quantity.threads");
        
        String property = System.setProperty("oracle.tuxedo.logprocessor.configfolder", path);

        boolean batchRunTimerIsRunning = false;

        Timer batchRunTimer = tm.schedule(new TuxedoTask(Integer.valueOf(threads)), 0, Integer.valueOf(delay) * 1000); 
        batchRunTimerIsRunning = true;

        config.getServletContext().setAttribute("TuxedoTask", batchRunTimer);
        config.getServletContext().setAttribute("TuxedoRunning",batchRunTimerIsRunning);

    }

    public void service(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException,
                                                             IOException {

        if (request.getMethod().equals("POST")){
            
            String bTimer = request.getParameter("tm");
            String bTask = request.getParameter("bt");
            TimerBean tb = (TimerBean) request.getSession().getServletContext().getAttribute("timerBean");
            Timer task = (Timer) config.getServletContext().getAttribute("TuxedoTask");
            //tb.setBatchRunTimer(task.getScheduledExecutionTime());
            
            if (bTimer != null){
                if (tm.isSuspended()){
                    tm.resume();
                    tb.setTimerManager(true);
                }
                else {
                    tm.suspend();
                    tb.setTimerManager(false);
                }
            } 
            if(bTask != null){
                boolean taskIsRunning = (Boolean) config.getServletContext().getAttribute("TuxedoRunning");
                if(taskIsRunning){
                    task.cancel();
                    config.getServletContext().setAttribute("TuxedoRunning",false);
                    tb.setBatchStatus(false);
                } else {
                    task = tm.schedule(new TuxedoTask(Integer.valueOf(threads)), 0, Integer.valueOf(delay) * 1000); 
                    config.getServletContext().setAttribute("TuxedoTask", task);
                    config.getServletContext().setAttribute("TuxedoRunning",true);
                    tb.setBatchStatus(true);
                }
            }
        }     
        response.sendRedirect(this.config.getServletContext().getContextPath()+"/index.jsp");
    }

    public void destroy(){
        //Timer task = (Timer) config.getServletContext().getAttribute("TuxedoTask");
        //task.cancel();
        tm.stop();
      /*  try {
            ic.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }*/
    }

}
