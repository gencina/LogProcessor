package test;

import java.sql.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import oracle.tuxedo.logprocessor.TuxedoLogProcessor;


public class TestTuxedo {
    public static void main(String[] args) {
        
        TuxedoLogProcessor testTuxedo = new TuxedoLogProcessor();
              
        String property = System.setProperty("oracle.tuxedo.logprocessor.configfolder","D:\\InformaPago\\BAMProject_2012_08_14\\BAMDemo\\TuxedoLogProcessor");
        Date fecha = null;
        try {
            fecha = new SimpleDateFormat("yyyyMMdd").parse("20120508");
         
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        System.out.println(new Timestamp(System.currentTimeMillis()));
        
        testTuxedo.doProcess(fecha,2);
        
        System.out.println(new Timestamp(System.currentTimeMillis()));
    
    }
    
 
}
