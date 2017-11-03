package com.vuta.helpers;

/**
 * Created by vuta on 03/11/2017.
 */
public class test {

//    public static void main (String[] args) {
//        PhotoTools.moveFile("C:\\Users\\vuta\\Pictures", "C:\\Users\\vuta\\Pictures\\plmea", "hello.jpg");
//    }
public static void main (String[] args) {
   Logger logger = new Logger();
   logger.logEvent("This is a venet message");
   logger.logDebug("This is a DEBUG MESSAGE");
   logger.logError("This is a ERROR MESSAGE");

    try{
       System.out.println(341 / 0);
    } catch (Exception e) {
       e.printStackTrace(logger.printStream());
    }
}
}
