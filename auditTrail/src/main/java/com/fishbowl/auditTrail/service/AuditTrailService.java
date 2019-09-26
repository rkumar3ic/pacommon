package com.fishbowl.auditTrail.service;

import java.util.Map;


public interface AuditTrailService<T1, T2> {
	public T1 getAuditTrailInstance();
	public T1 doPreAudit(String userId, String remoteAddr,String tenantId, Object attribute, String method, String className, T2 t2);
	public T1 doPreAudit(String userId, String remoteAddr,String tenantId, Object attribute, String method, Map<String, String[]> requestParams);
	public T1 doPostAudit(String className, T2 t2);
}
