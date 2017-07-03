package com.jay.util;

import java.sql.SQLException;
import java.util.Vector;

public class CommonConst{
    public static final String CURRENT_DIR = ".";
    public static final String WHITE_SPACE = " ";
    public static final String CONN_INFO_STR="ConnInnfo";
    public static final String CONN_STR="conn";
    public static final String ENTRY_STR="entry";
    public static final String LIB_DIR = "lib";
    public static final String DAT_DIR = "dat";
    public static final String NOT_MATCHED = "Not Matched";
    
    public static final String TAB = "\t";
    public static final String TAB_WHITE = "  ";
    public static final String TAB_STR = "tab";    
    public static final String ENCODING = "UTF-8";
    public static final String PASSWORD_ENC_STRING = "PWD_ENC";
    public static final String ASTERISK = "*";
    
    public static final String EXCEPTION_FILE_PATH = "c:\\";
    public static final String AES = "AES";
    public static final String DES = "DES";
    public static final String DESede = "DESede";
    public static final int ENCRYPT = 1;
    public static final int DECRYPT = 0;
    public static final int TEST = 2;
    public static final String T_DES = "TrippleDES";
    public static final String KEY_FILE = "jayk.dat";

    public static final String USER_DIR_PROP_KEY = "user.dir";
    public static final String JAVA_HOME_PROP_KEY = "java.home";
    public static final String SEPARATOR_PROP_KEY = "system.separator";



    public static final String LABEL_STRING = "Label";


    public static final String GEN_USER_PROMPT = "$";
    public static final String GEN_ROOT_PROMPT = "#";


    public static final long SHOW_OBJ_INFO_DURATION = 500L;
    public static final String  XML_STRING = "xml";

    private static String[] mColsName = {"FileName","AbsolutePath","CanRead","CanWrite","CanExecute","isHidden","Encoding","LastModified"};
	
	private static String[] mTxtFileType = {"txt","log","inc","ini","conf","cfg","csv"};
	private static String[] mMarkupFileType = {"html","htm","mhtm","mhtml","xml","vml","vrml","sgml"};
	private static String[] mLanguageFileType = {"java","c","cc","cxx","cpp","cob","pc","f","hdr","h","cs"};
	private static String[] mClientSideScriptLanguageFileType = {"js","as","css"};
	private static String[] mServerSideScriptLanguageFileType = {"jsp","asp","php","py","pl","cgi","sh","bat"};
	
	private static String[][] mAvailFileType = {mTxtFileType,mMarkupFileType,mLanguageFileType,mClientSideScriptLanguageFileType,mServerSideScriptLanguageFileType};
	
	public static Vector getColumnNames() throws SQLException{
		Vector<String> ret = new Vector<String>();
		for(int i=0;i<mColsName.length;i++){
			ret.addElement(mColsName[i]);
		}
		return ret;
	}
	
	public static String[][] getAvailFileType(){
		return mAvailFileType;
	}
    
    
    public static final String TIME_ZONE = "TimeZone";
    public static final String TIME_ZONE_DEFAULT = "GMT";
    public static final String TIME_ZONE_KR = "GMT+9";
    
    public static final String DATE_FORMAT = "yyyyMMdd";
    public static final String TIME_FORMAT = "HHmm";
    public static final String SEC_FORMAT = "HHmmss";
    public static final String DATETIME_FORMAT = "yyyyMMddHHmmss";
    
    public static final String DATETIME_STRING = "^^YYYYMMDDHH24MISS^^";
    public static final String DATE_STRING = "^^YYYYMMDD^^";
    
    
    public static final String JAVA_SPEC_VERSION = "java.specification.version";
    public static final String JAVA6 = "1.6";

    public static final String EXCEPTION = " Exception occured";
    public static final String INFORM = " Infromation ";

    public static final String HOST_STRING = "Server";

    public static final String PORT_STRING = "Port";

    public static final String USER_STRING = "User";

    public static final String PASSWORD_STRING = "Password";
    
    public static final String TEST_STRING = "Test";

    
    public static final char[] sAlphabetChar = {'A','a','B','b','C','c','D','d','E','e','F','f',
                                                'G','g','H','h','I','i','J','j','K','k','L','l',
                                                'M','m','N','n','O','o','P','p','Q','q','R','r',
                                                'S','s','T','t','U','u','V','v','W','w','X','x',
                                                'Y','y','Z','z'};
    public static final char[] sAlphabetDecimalChar = {'A','a','B','b','C','c','D','d','E','e','F','f',
									            'G','g','H','h','I','i','J','j','K','k','L','l',
									            'M','m','N','n','O','o','P','p','Q','q','R','r',
									            'S','s','T','t','U','u','V','v','W','w','X','x',
									            'Y','y','Z','z','1','2','3','4','5','6','7','8','9','0'};
    
    public static final String NO_PERMISSION = " (No permission)";
	public static final String SPLIT_STRING = "SPLIT";
	public static final String SALT_POSITION = "SALT_POSITION";
	public static final String SALT_LENGTH = "SALT_LENGTH";
	public static final int ASCII = 0;
	public static final int BINARY = 1;
	public static final String FILE_TYPE = "FILE_TYPE";
	public static final String FILE_PATH = "FILE_PATH";
	public static final String ID = "ID";
	public static final String TEMP = "TEMP";
	public static final String ORIGIN_STRING = "ORIGIN";
	public static final Object ORIGIN_FILE_PATH = ORIGIN_STRING +"_"+ FILE_PATH;
	public static final Object SPLIT_FILE_NAMES = "SPLIT_FILE_NAMES";
	public static final String MERGE_STR = "merge_";
}