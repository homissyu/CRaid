package com.jay.util;

import java.util.ArrayList;

public class CaseManipulation {

   public static String toUpperCase(String inputString) {
       String result = "";
       for (int i = 0; i < inputString.length(); i++) {
           char currentChar = inputString.charAt(i);
           char currentCharToUpperCase = Character.toUpperCase(currentChar);
           result = result + currentCharToUpperCase;
       }
       return result;
   }

   public static String toLowerCase(String inputString) {
       String result = "";
       for (int i = 0; i < inputString.length(); i++) {
           char currentChar = inputString.charAt(i);
           char currentCharToLowerCase = Character.toLowerCase(currentChar);
           result = result + currentCharToLowerCase;
       }
       return result;
   }

   public static String toToggleCase(String inputString) {
       String result = "";
       for (int i = 0; i < inputString.length(); i++) {
           char currentChar = inputString.charAt(i);
           if (Character.isUpperCase(currentChar)) {
               char currentCharToLowerCase = Character.toLowerCase(currentChar);
               result = result + currentCharToLowerCase;
           } else {
               char currentCharToUpperCase = Character.toUpperCase(currentChar);
               result = result + currentCharToUpperCase;
           }
       }
       return result;
   }
   
   public static String toToggleCaseInPostion(String inputString, ArrayList<Integer> positions, ArrayList<Integer> lengths) {
       StringBuffer retBuf = null;
       String aTempStr = inputString;
       int iTempPosition = 0;
       int iTempLength = 0;
       String sOldTempStr = null;
       String sNewTempStr = null;
       
       for(int i=0;i<positions.size();i++) {
    	   retBuf = new StringBuffer();
    	   iTempLength = lengths.get(i);
    	   iTempPosition = positions.get(i);
    	   sOldTempStr = aTempStr.substring(iTempPosition, iTempPosition+iTempLength);
    	   sNewTempStr = CaseManipulation.toToggleCase(sOldTempStr);
    	   
    	   retBuf.append(aTempStr.substring(0, iTempPosition));
    	   retBuf.append(sNewTempStr);
    	   retBuf.append(aTempStr.substring(iTempPosition+iTempLength));
    	   aTempStr = retBuf.toString();
       }
       return aTempStr;
   }
   
   
   

   public static String toCamelCase(String inputString) {
       String result = "";
       if (inputString.length() == 0) {
           return result;
       }
       char firstChar = inputString.charAt(0);
       char firstCharToUpperCase = Character.toUpperCase(firstChar);
       result = result + firstCharToUpperCase;
       for (int i = 1; i < inputString.length(); i++) {
           char currentChar = inputString.charAt(i);
           char previousChar = inputString.charAt(i - 1);
           if (previousChar == ' ') {
               char currentCharToUpperCase = Character.toUpperCase(currentChar);
               result = result + currentCharToUpperCase;
           } else {
               char currentCharToLowerCase = Character.toLowerCase(currentChar);
               result = result + currentCharToLowerCase;
           }
       }
       return result;
   }

   public static String toSentenceCase(String inputString) {
       String result = "";
       if (inputString.length() == 0) {
           return result;
       }
       char firstChar = inputString.charAt(0);
       char firstCharToUpperCase = Character.toUpperCase(firstChar);
       result = result + firstCharToUpperCase;
       boolean terminalCharacterEncountered = false;
       char[] terminalCharacters = {'.', '?', '!'};
       for (int i = 1; i < inputString.length(); i++) {
           char currentChar = inputString.charAt(i);
           if (terminalCharacterEncountered) {
               if (currentChar == ' ') {
                   result = result + currentChar;
               } else {
                   char currentCharToUpperCase = Character.toUpperCase(currentChar);
                   result = result + currentCharToUpperCase;
                   terminalCharacterEncountered = false;
               }
           } else {
               char currentCharToLowerCase = Character.toLowerCase(currentChar);
               result = result + currentCharToLowerCase;
           }
           for (int j = 0; j < terminalCharacters.length; j++) {
               if (currentChar == terminalCharacters[j]) {
                   terminalCharacterEncountered = true;
                   break;
               }
           }
       }
       return result;
   }

}