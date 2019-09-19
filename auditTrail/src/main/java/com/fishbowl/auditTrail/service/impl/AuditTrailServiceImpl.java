package com.fishbowl.auditTrail.service.impl;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fishbowl.auditTrail.model.AuditEvent;
import com.fishbowl.auditTrail.model.AuditTrail;
import com.fishbowl.auditTrail.queue.AuditAzureQueuePublisher;
import com.fishbowl.auditTrail.service.AuditTrailService;
import com.google.gson.Gson;

public class AuditTrailServiceImpl implements AuditTrailService<AuditTrail, Map<String,Object>> {
	
	private static Logger logger = LoggerFactory.getLogger(AuditTrailServiceImpl.class);
	
	private AuditTrail auditTrail;
	
	 @PostConstruct
	  public void init(){
	     this.auditTrail = new AuditTrail();
	  }

	public AuditTrail doPreAudit(Map<String,Object> auditDetails) {
		logger.info("Inside doPreAudit");
		String auditDetailsToJson;
		try {
			auditDetailsToJson = ObjectToJson(auditDetails);
			
			logger.info(auditDetailsToJson);
			//auditTrail.setPreOperation(integrateClassToJsonValue(className,objectToJsonValue));
			this.auditTrail.setPreOperation(auditDetailsToJson);
			//this.auditTrail = auditTrail;
			logger.info(this.auditTrail.getPreOperation());
			logger.info(this.auditTrail.toString());
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
		return this.auditTrail;
	}

	public AuditTrail doPostAudit(Map<String,Object> auditDetails) {
		logger.info("Inside doPostAudit");
		String auditDetailsToJson;
		try {
			auditDetailsToJson = ObjectToJson(auditDetails);
			logger.info(auditDetailsToJson);
			this.auditTrail.setPostOperation(concatenateObjectToJsonValues(auditTrail.getPreOperation(),auditDetailsToJson,"|"));
			//this.auditTrail = auditTrail;
			logger.info(this.auditTrail.getPostOperation());
			logger.info(this.auditTrail.toString());
			AuditEvent auditEvent = new AuditEvent(auditTrail.getBrandId(),auditTrail);
			new AuditAzureQueuePublisher().sendEventToQueue(auditEvent);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
		return this.auditTrail;
	}
	
	public AuditTrail setAuditDetails(String userId, String ipAddress, String brandId, Object apiUrl, String httpMethod){
		logger.info("Inside setAuditDetails");
		this.auditTrail.setUserId(Integer.parseInt(userId));
		this.auditTrail.setIpAddress(ipAddress);
		this.auditTrail.setBrandId(Integer.parseInt(brandId));
		this.auditTrail.setApiUrl((String)apiUrl);
		this.auditTrail.setHttpMethod(httpMethod);
		return this.auditTrail;
	}
	
	public String ObjectToJson(Object model) throws JsonProcessingException{
		Gson objectToJson = new Gson();
		return objectToJson.toJson(model);
	}
	
	public String concatenateObjectToJsonValues(String v1, String v2, String delimiter) throws JsonProcessingException{
		return v1 + " " +delimiter+ " "+v2 ;
	}
	
	/*public String integrateClassToJsonValue(String className, String objectToJsonValue) throws JsonProcessingException{
		return className+" ["+objectToJsonValue+"]";
	}*/
	
	@Override
	public AuditTrail getAuditTrailInstance() {
		return this.auditTrail;
	}
	
	public AuditTrail getAuditTrail() {
		return auditTrail;
	}

	public void setAuditTrail(AuditTrail auditTrail) {
		this.auditTrail = auditTrail;
	}

}
