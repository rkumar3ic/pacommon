package com.fishbowl.auditTrail.service;

import javax.servlet.http.HttpServletRequest;

public interface AuditTrailService<T1, T2> {
	public T1 doPreAudit(T2 t2);
	public T1 doPreAudit(T2 t2, HttpServletRequest request);
	public T1 doPostAudit(T2 t2);
	public T1 setAuditDetails(String userId, String ipAddress, String brandId, Object apiUrl, String httpMethod);
	public T1 getAuditTrailInstance();
}
