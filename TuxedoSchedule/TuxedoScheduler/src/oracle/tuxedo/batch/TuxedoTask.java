package oracle.tuxedo.batch;

import commonj.timers.CancelTimerListener;
import commonj.timers.Timer;
import commonj.timers.TimerListener;

import java.io.Serializable;

import java.sql.Timestamp;

import java.util.Date;

import oracle.tuxedo.logprocessor.TuxedoLogProcessor;

public class TuxedoTask implements Serializable, TimerListener,
                               CancelTimerListener {

    private int processes;
    
    public TuxedoTask(int qThreads){
         processes = qThreads;
    }

    public void timerExpired(Timer timer) {
        System.out.println("TuxedoTask timer expired called on " + timer);
        
        TuxedoLogProcessor tuxedoProcessor = new TuxedoLogProcessor();
              
        Date fecha = new Date(System.currentTimeMillis());
       
        System.out.println(new Timestamp(System.currentTimeMillis()));
        
        tuxedoProcessor.doProcess(fecha,processes);
        
        System.out.println(new Timestamp(System.currentTimeMillis()));
        
    }

    public void timerCancel(Timer timer) {
        System.out.println("TuxedoTask timer cancelled called on " + timer);

    }
}
