package com.fishbowl.auditTrail.dao;

public interface AuditTrailDAO<T> {
	
	public T insertAuditTrailDetails(T model);
	public int updateAuditTrailDetails(T model);

}
