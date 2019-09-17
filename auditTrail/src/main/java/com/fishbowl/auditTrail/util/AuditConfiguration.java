package com.fishbowl.auditTrail.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



//import org.apache.log4j.Logger;

public class AuditConfiguration {
	
	
	 private final static AuditConfiguration INSTANCE = new AuditConfiguration();
	 
	    public static AuditConfiguration getInstance() {
	        return INSTANCE;
	    }
	 
	    private static Properties configuration = new Properties();
	 
	    private static Properties getConfiguration() {
	        return configuration;
	    }
	 
	    public void initilize(final String file) {
	        InputStream in = null;
	        try {
	            in = new FileInputStream(new File(file));
	            configuration.load(in);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	 
	    public String getConfiguration(final String key) {
	        return (String) getConfiguration().get(key);
	    }
	 
	    public String getConfigurationWithDefaultValue(final String key,
	            final String defaultValue) {
	        return (String) getConfiguration().getProperty(key, defaultValue);
	    }
	    
	    public int getIntProperty(String key, int def) {
			int ret = def;
			try{
			ret = Integer.valueOf(getConfiguration(key));
			}catch(Exception e){
				ret = def;
			}
			return ret;
		}
	    
	   
}
