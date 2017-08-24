package com.jay.csp.pCloud;

import org.pcloud.*;
import java.util.*;

public class Test{
  public static void main(String args[]){
	  System.setProperty("java.net.useSystemProxies", "true");
    try{
      PCloudAPI conn=new PCloudAPI(true);
      Hashtable <String, Object> params=new Hashtable <String, Object> ();
      params.put("auth", "ORdOWsH8s1QuqNavEF82hk4T6rCy");
      params.put("folderid", 0);
//      conn.sendCommand("diff", params);
      PCloudAPIDebug.print(conn.sendCommand("diff", params));
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
}
