package com.fishbowl.auditTrail.model;

import java.io.Serializable;

public class AuditEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String action;
	private int brandId;
	private AuditTrail auditTrail;
	
	public AuditEvent() {}
	
	public AuditEvent(int brandId, AuditTrail auditTrail) {
		this.brandId = brandId;
		this.auditTrail = auditTrail;
	}

	public AuditEvent(String action, int brandId, AuditTrail auditTrail) {
		this.action = action;
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return "AuditEvent [action=" + action + ", brandId=" + brandId
				+ ", auditTrail=" + auditTrail + "]";
	}

}
