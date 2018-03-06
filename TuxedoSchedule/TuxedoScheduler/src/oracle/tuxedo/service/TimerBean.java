package oracle.tuxedo.service;

import commonj.timers.Timer;
import commonj.timers.TimerManager;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import oracle.tuxedo.batch.TuxedoTask;

import javax.servlet.ServletContext;

import javax.servlet.jsp.JspPage;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

public class TimerBean {

    private boolean tm = true;

    private boolean batchRunTimerIsRunning = true;
    private Timer batchRunTimer = null;
    
     
    public TimerBean() {

    }

    public void setTimerManager(boolean status) {
        this.tm = status;
    }

    public void setBatchStatus(boolean status) {
        this.batchRunTimerIsRunning = status;
    }
    
    public String getTmStatus () {
        if (tm) {
            return "TimerManager is running";  
        } else {
           return "TimerManager is stopped";
        }
    }

    public String getBatchStatus () {
       Long time = batchRunTimer.getScheduledExecutionTime();
       Date date = new Date(time);
       if ( batchRunTimerIsRunning ) { 
         return "TuxedoTask scheduled time "+date.toString();
       } {
         return "TuxedoTask stopped";
       }
    }
    
    public void setBatchRunTimer(JspPage jsp ){
      this.batchRunTimer = (Timer) jsp.getServletConfig().getServletContext().getAttribute("TuxedoTask");
    }
}
