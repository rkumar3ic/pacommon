package com.fishbowl.auditTrail.model;

import java.util.Date;

public class AuditTrail {
	
	private String auditId;
	private int userId;
	private String ipAddress;
	private int brandId;
	private String apiUrl;
	private String httpMethod;
	private String userAgent;
	private String serverIpAddress;
	private String preOperation;
	private String postOperation;
	private String apiCallStart;
	private String apiCallEnd;
	private Date operationDbCall;
	
	public String getAuditId() {
		return auditId;
	}
	public void setAuditId(String auditId) {
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
	public String getApiCallStart() {
		return apiCallStart;
	}
	public void setApiCallStart(String apiCallStart) {
		this.apiCallStart = apiCallStart;
	}
	public String getApiCallEnd() {
		return apiCallEnd;
	}
	public void setApiCallEnd(String apiCallEnd) {
		this.apiCallEnd = apiCallEnd;
	}
	public Date getOperationDbCall() {
		return operationDbCall;
	}
	public void setOperationDbCall(Date operationDbCall) {
		this.operationDbCall = operationDbCall;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getServerIpAddress() {
		return serverIpAddress;
	}
	public void setServerIpAddress(String serverIpAddress) {
		this.serverIpAddress = serverIpAddress;
	}
	
	@Override
	public String toString() {
		return "AuditTrail [auditId=" + auditId + ", userId=" + userId
				+ ", ipAddress=" + ipAddress + ", brandId=" + brandId
				+ ", apiUrl=" + apiUrl + ", httpMethod=" + httpMethod
				+ ", userAgent=" + userAgent + ", serverIpAddress="
				+ serverIpAddress + ", preOperation=" + preOperation
				+ ", postOperation=" + postOperation + ", apiCallStart="
				+ apiCallStart + ", apiCallEnd=" + apiCallEnd
				+ ", operationDbCall=" + operationDbCall + "]";
	}
	
}
