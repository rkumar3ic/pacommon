package com.fishbowl.auditTrail.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;


import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fishbowl.auditTrail.constant.AuditConstant;
import com.fishbowl.auditTrail.model.AuditEvent;
import com.fishbowl.auditTrail.model.AuditTrail;
import com.fishbowl.auditTrail.queue.AuditAzureQueuePublisher;
import com.fishbowl.auditTrail.service.AuditTrailService;
import com.google.gson.Gson;

public class AuditTrailServiceImpl implements AuditTrailService<AuditTrail, Object> {
	
	private static Logger logger = Logger.getLogger(AuditTrailServiceImpl.class);
	
    private static final String CORRELATION_ID_LOG_VAR_NAME = "correlationId";
 
	
	private AuditTrail auditTrail;
	private String correlationId;
	
	@PostConstruct
	public void init(){
		configureLogging(AuditConstant.CONFIGURE_LOGGING);
		this.auditTrail = new AuditTrail();
	}

	public AuditTrail doPreAudit(String userId, String remoteAddr,String tenantId, Object attribute, String method, String className, Object classObject, String userAgent, String serverIp) {
		configureCorrelationId();
		logger.debug("Inside doPreAudit");
		String auditDetailsToJson;
		try {
			setAuditDetails(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), userId,remoteAddr,tenantId,attribute,method,userAgent,serverIp);
			auditDetailsToJson = setClassObjectDetailsToJson(className,classObject);
			this.auditTrail.setPreOperation(auditDetailsToJson);
			logger.debug(this.auditTrail.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
		return this.auditTrail;
	}
	
	@Override
	public AuditTrail doPreAudit(String userId, String remoteAddr, String tenantId, Object attribute, String method, Map<String, String[]> requestParams, String userAgent, String serverIp) {
		configureCorrelationId();
		logger.debug("Inside doPreAudit");
		String auditDetailsToJson;
		try {
			setAuditDetails(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),userId,remoteAddr,tenantId,attribute,method,userAgent,serverIp);
			
			auditDetailsToJson = ObjectToJson(getRequestQueryParameters(requestParams));
			
			this.auditTrail.setPreOperation(auditDetailsToJson);
			//logger.debug(this.auditTrail.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
		return this.auditTrail;
	}
	
	public AuditTrail doPostAudit(String className, Object classObject) {
		logger.debug("Inside doPostAudit");
		String auditDetailsToJson;
		try {
			this.auditTrail.setApiCallEnd(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
			auditDetailsToJson = setClassObjectDetailsToJson(className,classObject);
			this.auditTrail.setPostOperation(concatenateObjectToJsonValues(auditTrail.getPreOperation(),auditDetailsToJson,"|"));
			AuditEvent auditEvent = new AuditEvent(AuditConstant.AUDIT_EVENT,auditTrail.getBrandId(),auditTrail);
			logger.debug("auditEvent : "+auditEvent);
			new AuditAzureQueuePublisher().sendEventToQueue(auditEvent);
			cleanupCorrelationId();
			
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
		return this.auditTrail;
	}
	
	public String setClassObjectDetailsToJson(String className, Object classObject) throws JsonProcessingException{
		Map<String,Object> auditDetails = new HashMap<String, Object>();
		auditDetails.put(className, classObject);
		return ObjectToJson(auditDetails);
	}
	
	public AuditTrail setAuditDetails(String apiStartDate, String userId, String ipAddress, String brandId, Object apiUrl, String httpMethod, String userAgent, String serverIp){
		logger.debug("Inside setAuditDetails");
		try {
			this.auditTrail.setAuditId(this.correlationId);
			this.auditTrail.setApiCallStart(apiStartDate);
			this.auditTrail.setUserId(Integer.parseInt(userId));
			this.auditTrail.setIpAddress(ipAddress);
			this.auditTrail.setBrandId(Integer.parseInt(brandId));
			this.auditTrail.setApiUrl((String)apiUrl);
			this.auditTrail.setHttpMethod(httpMethod);
			this.auditTrail.setUserAgent(userAgent);
			this.auditTrail.setServerIpAddress(serverIp);
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
		}
		return this.auditTrail;
	}
	
	public Map<String,Object> getRequestQueryParameters(Map<String, String[]> requestParams){
		Map<String,Object> auditDetails = null;
		try {
			if(requestParams != null){
				auditDetails = new HashMap<String, Object>();
				if(requestParams != null){
					Set<String> requestParameters = requestParams.keySet();
					for(String requestParametersKey : requestParameters){
						auditDetails.put(requestParametersKey, requestParams.get(requestParametersKey));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
		}
		return auditDetails;
	}
	
	public String ObjectToJson(Object model) throws JsonProcessingException{
		Gson objectToJson = new Gson();
		return objectToJson.toJson(model);
	}
	
	public String concatenateObjectToJsonValues(String v1, String v2, String delimiter) throws JsonProcessingException{
		return v1 + " " +delimiter+ " "+v2 ;
	}
	
	@Override
	public AuditTrail getAuditTrailInstance() {
		return this.auditTrail;
	}
	
	private void configureLogging(String logPropFile){
		try(InputStream logStream = new FileInputStream(new File(System.getProperty("user.dir")+logPropFile));){
			Properties prop = new Properties();
			if (logStream.available() > 0) {
				prop.load(logStream);
				PropertyConfigurator.configure(prop);
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
		}
	}
	
	private void configureCorrelationId(){
		correlationId = (String) MDC.get(CORRELATION_ID_LOG_VAR_NAME);
		if (correlationId == null) {
			correlationId = UUID.randomUUID().toString();
			logger.debug("No correlationId found. Generated : " + correlationId);
		} else {
			logger.debug("Found correlationId : " + correlationId);
		}
		MDC.put(CORRELATION_ID_LOG_VAR_NAME, correlationId);
	}
	
	private void cleanupCorrelationId(){
		logger.debug("Removing correlationId : " + correlationId);
		MDC.remove(CORRELATION_ID_LOG_VAR_NAME);
	}
	
	
	public AuditTrail getAuditTrail() {
		return auditTrail;
	}

	public void setAuditTrail(AuditTrail auditTrail) {
		this.auditTrail = auditTrail;
	}

}
