package com.fishbowl.auditTrail.service;

public interface AuditTrailService<T1, T2> {
	public T1 doPreAudit(T2 t2);
	public T1 doPostAudit(T2 t2);
	public T1 setAuditDetails(String userId, String ipAddress, String brandId, Object apiUrl, String httpMethod);
	public T1 getAuditTrailInstance();
}
