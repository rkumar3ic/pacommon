package com.fishbowl.auditTrail.service.impl;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

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
		logger.info("Inside doPreAudit");
		String auditDetailsToJson;
		try {
			this.auditTrail.setApiCallStart(new Date());
			setAuditDetails(userId,remoteAddr,tenantId,attribute,method);
			auditDetailsToJson = setClassObjectDetailsToJson(className,classObject);
			logger.info(auditDetailsToJson);
			this.auditTrail.setPreOperation(auditDetailsToJson);
			logger.info(this.auditTrail.getPreOperation());
			logger.info(this.auditTrail.toString());
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
		return this.auditTrail;
	}
	
	@Override
	public AuditTrail doPreAudit(String userId, String remoteAddr, String tenantId, Object attribute, String method, HttpServletRequest request) {
		logger.info("Inside doPreAudit");
		String auditDetailsToJson;
		try {
			this.auditTrail.setApiCallStart(new Date());
			setAuditDetails(userId,remoteAddr,tenantId,attribute,method);
			auditDetailsToJson = ObjectToJson(getRequestQueryParameters(request));
			logger.info(auditDetailsToJson);
			this.auditTrail.setPreOperation(auditDetailsToJson);
			logger.info(this.auditTrail.getPreOperation());
			logger.info(this.auditTrail.toString());
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
		return this.auditTrail;
	}
	
	/*public AuditTrail doPreAudit(Map<String,Object> auditDetails, HttpServletRequest request) {
		logger.info("Inside doPreAudit");
		String auditDetailsToJson;
		try {
			this.auditTrail.setApiCallStart(new Date());
			auditDetails = getRequestQueryParameters(request);
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
	}*/

	public AuditTrail doPostAudit(String className, Object classObject) {
		logger.info("Inside doPostAudit");
		String auditDetailsToJson;
		try {
			this.auditTrail.setApiCallEnd(new Date());
			auditDetailsToJson = setClassObjectDetailsToJson(className,classObject);
			logger.info(auditDetailsToJson);
			this.auditTrail.setPostOperation(concatenateObjectToJsonValues(auditTrail.getPreOperation(),auditDetailsToJson,"|"));
			logger.info(this.auditTrail.getPostOperation());
			logger.info(this.auditTrail.toString());
			AuditEvent auditEvent = new AuditEvent(AuditConstant.AUDIT_EVENT,auditTrail.getBrandId(),auditTrail);
			logger.info("auditEvent : "+auditEvent);
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
		logger.info("Inside setAuditDetails");
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
	
	public Map<String,Object> getRequestQueryParameters(HttpServletRequest request){
		Map<String,Object> auditDetails = null;
		try {
			if(request != null){
				auditDetails = new HashMap<String, Object>();
				if(request != null){
					Enumeration requestParameters = request.getParameterNames();
					while (requestParameters.hasMoreElements()) {
						String element = (String) requestParameters.nextElement();
						String value = request.getParameter(element);
						auditDetails.put(element, value);
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
