package com.fishbowl.auditTrail.service;

public interface AuditTrailService<T1, T2, T3> {
	//public void doAudit(T1 t1, T2 t2, T3 t3);
	public void doPreAudit(T1 t1, T2 t2, T3 t3);
	public void doPostAudit(T1 t1, T2 t2, T3 t3);
}
