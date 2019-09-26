package com.fishbowl.auditTrail.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fishbowl.auditTrail.constant.AuditConstant;
import com.fishbowl.auditTrail.model.AuditEvent;
import com.fishbowl.auditTrail.model.AuditTrail;
import com.fishbowl.auditTrail.queue.AuditAzureQueuePublisher;
import com.fishbowl.auditTrail.service.AuditTrailService;
import com.google.gson.Gson;

public class AuditTrailServiceImpl implements AuditTrailService<AuditTrail, Object> {
	
	private static Logger logger = LoggerFactory.getLogger(AuditTrailServiceImpl.class);
	
	private AuditTrail auditTrail;
	
	@PostConstruct
	public void init(){
	   this.auditTrail = new AuditTrail();
	}

	public AuditTrail doPreAudit(String userId, String remoteAddr,String tenantId, Object attribute, String method, String className, Object classObject) {
		logger.debug("Inside doPreAudit");
		String auditDetailsToJson;
		try {
			this.auditTrail.setApiCallStart(new Date());
			setAuditDetails(userId,remoteAddr,tenantId,attribute,method);
			auditDetailsToJson = setClassObjectDetailsToJson(className,classObject);
			logger.debug(auditDetailsToJson);
			this.auditTrail.setPreOperation(auditDetailsToJson);
			logger.debug(this.auditTrail.getPreOperation());
			logger.debug(this.auditTrail.toString());
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
		return this.auditTrail;
	}
	
	@Override
	public AuditTrail doPreAudit(String userId, String remoteAddr, String tenantId, Object attribute, String method, Map<String, String[]> requestParams) {
		logger.debug("Inside doPreAudit");
		String auditDetailsToJson;
		try {
			this.auditTrail.setApiCallStart(new Date());
			setAuditDetails(userId,remoteAddr,tenantId,attribute,method);
			
			auditDetailsToJson = ObjectToJson(getRequestQueryParameters(requestParams));
			
			logger.debug(auditDetailsToJson);
			this.auditTrail.setPreOperation(auditDetailsToJson);
			logger.debug(this.auditTrail.getPreOperation());
			logger.debug(this.auditTrail.toString());
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
		return this.auditTrail;
	}
	
	public AuditTrail doPostAudit(String className, Object classObject) {
		logger.debug("Inside doPostAudit");
		String auditDetailsToJson;
		try {
			this.auditTrail.setApiCallEnd(new Date());
			auditDetailsToJson = setClassObjectDetailsToJson(className,classObject);
			logger.debug(auditDetailsToJson);
			this.auditTrail.setPostOperation(concatenateObjectToJsonValues(auditTrail.getPreOperation(),auditDetailsToJson,"|"));
			logger.debug(this.auditTrail.getPostOperation());
			logger.debug(this.auditTrail.toString());
			AuditEvent auditEvent = new AuditEvent(AuditConstant.AUDIT_EVENT,auditTrail.getBrandId(),auditTrail);
			logger.debug("auditEvent : "+auditEvent);
			new AuditAzureQueuePublisher().sendEventToQueue(auditEvent);
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
	
	public AuditTrail setAuditDetails(String userId, String ipAddress, String brandId, Object apiUrl, String httpMethod){
		logger.debug("Inside setAuditDetails");
		try {
			this.auditTrail.setUserId(Integer.parseInt(userId));
			this.auditTrail.setIpAddress(ipAddress);
			this.auditTrail.setBrandId(Integer.parseInt(brandId));
			this.auditTrail.setApiUrl((String)apiUrl);
			this.auditTrail.setHttpMethod(httpMethod);
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
	
	public AuditTrail getAuditTrail() {
		return auditTrail;
	}

	public void setAuditTrail(AuditTrail auditTrail) {
		this.auditTrail = auditTrail;
	}

}
