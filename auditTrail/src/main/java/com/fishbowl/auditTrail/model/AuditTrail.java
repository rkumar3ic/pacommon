package com.fishbowl.auditTrail.model;

import java.util.Date;

public class AuditTrail {
	
	private long auditId;
	private int userId;
	private String ipAddress;
	private int brandId;
	private String apiUrl;
	private String httpMethod;
	private String preOperation;
	private String postOperation;
	private Date apiCallStart;
	private Date apiCallEnd;
	private Date operationDbCall;
	
	public long getAuditId() {
		return auditId;
	}
	public void setAuditId(long auditId) {
		this.auditId = auditId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public int getBrandId() {
		return brandId;
	}
	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}
	public String getApiUrl() {
		return apiUrl;
	}
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	public String getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	public String getPreOperation() {
		return preOperation;
	}
	public void setPreOperation(String preOperation) {
		this.preOperation = preOperation;
	}
	public String getPostOperation() {
		return postOperation;
	}
	public void setPostOperation(String postOperation) {
		this.postOperation = postOperation;
	}
	public Date getApiCallStart() {
		return apiCallStart;
	}
	public void setApiCallStart(Date apiCallStart) {
		this.apiCallStart = apiCallStart;
	}
	public Date getApiCallEnd() {
		return apiCallEnd;
	}
	public void setApiCallEnd(Date apiCallEnd) {
		this.apiCallEnd = apiCallEnd;
	}
	public Date getOperationDbCall() {
		return operationDbCall;
	}
	public void setOperationDbCall(Date operationDbCall) {
		this.operationDbCall = operationDbCall;
	}
	
	@Override
	public String toString() {
		return "AuditTrail [auditId=" + auditId + ", userId=" + userId
				+ ", ipAddress=" + ipAddress + ", brandId=" + brandId
				+ ", apiUrl=" + apiUrl + ", httpMethod=" + httpMethod
				+ ", preOperation=" + preOperation + ", postOperation="
				+ postOperation + ", apiCallStart=" + apiCallStart
				+ ", apiCallEnd=" + apiCallEnd + ", operationDbCall="
				+ operationDbCall + "]";
	}
	
}
