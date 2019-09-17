package com.fishbowl.auditTrail.service.impl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fishbowl.auditTrail.model.AuditEvent;
import com.fishbowl.auditTrail.model.AuditTrail;
import com.fishbowl.auditTrail.queue.AuditAzureQueuePublisher;
import com.fishbowl.auditTrail.service.AuditTrailService;
import com.google.gson.Gson;

public class AuditTrailServiceImpl implements AuditTrailService<AuditTrail,Object,String> {
	
	private static Logger logger = LoggerFactory.getLogger(AuditTrailServiceImpl.class);
	
	public AuditTrail auditTrail;
	
	 @PostConstruct
	  public void init(){
	     this.auditTrail = new AuditTrail();
	  }

	public void doPreAudit(AuditTrail auditTrail, Object obj, String className) {
		String objectToJsonValue;
		try {
			objectToJsonValue = ObjectToJson(obj);
			
			logger.info(objectToJsonValue);
			auditTrail.setPreOperation(integrateClassToJsonValue(className,objectToJsonValue));
			this.auditTrail = auditTrail;
			logger.info(auditTrail.getPreOperation());
			logger.info(auditTrail.toString());
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
	}

	public void doPostAudit(AuditTrail auditTrail, Object obj, String className) {
		String objectToJsonValue;
		try {
			objectToJsonValue = ObjectToJson(obj);
			logger.info(objectToJsonValue);
			auditTrail.setPostOperation(concatenateObjectToJsonValues(auditTrail.getPreOperation(),integrateClassToJsonValue(className,objectToJsonValue),"|"));
			this.auditTrail = auditTrail;
			logger.info(auditTrail.getPostOperation());
			logger.info(auditTrail.toString());
			AuditEvent auditEvent = new AuditEvent(auditTrail.getBrandId(),auditTrail);
			new AuditAzureQueuePublisher().sendEventToQueue(auditEvent);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
	}
	
	public String ObjectToJson(Object model) throws JsonProcessingException{
		Gson objectToJson = new Gson();
		return objectToJson.toJson(model);
	}
	
	public String concatenateObjectToJsonValues(String v1, String v2, String delimiter) throws JsonProcessingException{
		return v1 + " " +delimiter+ " "+v2 ;
	}
	
	public String integrateClassToJsonValue(String className, String objectToJsonValue) throws JsonProcessingException{
		return className+" ["+objectToJsonValue+"]";
	}
	
	public AuditTrail getAuditTrail() {
		return auditTrail;
	}

	public void setAuditTrail(AuditTrail auditTrail) {
		this.auditTrail = auditTrail;
	}


}
