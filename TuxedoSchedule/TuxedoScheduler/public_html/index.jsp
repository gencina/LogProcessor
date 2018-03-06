<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="oracle.tuxedo.service.TimerBean"%>
<jsp:useBean id="timerBean" class="oracle.tuxedo.service.TimerBean"
             scope="application"></jsp:useBean>
<% timerBean.setBatchRunTimer(this); %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=windows-1252"/>
    <title>Tuxedo Scheduler</title>
  </head>
  <body>
    <p>
      <font face="Arial">
        <strong>
          <u>TuxedoLogProcessor</u></strong>
      </font>
    </p>
    <form method="POST" action="TuxedoSchedule">
      <table cellspacing="2" cellpadding="3" border="0" width="100%">
        <tr>
          <th width="100" align="left">
            <input type="text" name="tmStatus" value="${timerBean.tmStatus}"
                   size="70" readonly="readonly"/>
          </th>
          <th width="500" align="left">
            <input type="submit" value="TimerManager On / Off" name="tm"/>
          </th>
        </tr>
      </table>
      <table cellspacing="2" cellpadding="3" border="0" width="100%">
        <tr>
          <th width="100" align="left">
            <input type="text" name="batchStatus"
                   value="${timerBean.batchStatus}" size="70"
                   readonly="readonly"/>
          </th>
          <th width="500" align="left">
            <input type="submit" value="TuxedoTask On / Off" name="bt"/>
          </th>
        </tr>
      </table>
    </form>
  </body>
</html>