package com.fishbowl.auditTrail.model;

public class AuditEvent {
	private int brandId;
	private AuditTrail auditTrail;
	
	public AuditEvent() {}
	
	public AuditEvent(int brandId, AuditTrail auditTrail) {
		this.brandId = brandId;
		this.auditTrail = auditTrail;
	}
	
	public int getBrandId() {
		return brandId;
	}
	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}
	public AuditTrail getAuditTrail() {
		return auditTrail;
	}
	public void setAuditTrail(AuditTrail auditTrail) {
		this.auditTrail = auditTrail;
	}

}
