package com.jay.util;

/**
 * @ï¿½ï¿½ï¿½Ï¸ï¿½      JayException.java
 * @author  Jay
 * @project mySingleAgent
 * @ï¿½Û¼ï¿½ï¿½ï¿½ï¿½ï¿½   2004. 9. 10.
 * TODO
 */
public class JayException extends Exception{
    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    /**
     * @param string
     */
    public JayException(String msg)
    {
        super(msg);
        // TODO Auto-generated constructor stub
    }

    public JayException(Exception e) {
        super(e.getLocalizedMessage());
    }    
}