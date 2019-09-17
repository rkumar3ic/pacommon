package com.fishbowl.auditTrail.dao.impl;

import com.fishbowl.auditTrail.dao.AuditTrailDAO;
import com.fishbowl.auditTrail.model.AuditTrail;


public class AuditTrailDAOImpl implements AuditTrailDAO<AuditTrail> {

	public AuditTrail insertAuditTrailDetails(AuditTrail model) {
		// TODO Auto-generated method stub
		return null;
	}

	public int updateAuditTrailDetails(AuditTrail model) {
		// TODO Auto-generated method stub
		return 0;
	}
	

	/*@Override
	public AuditTrail insertAuditTrailDetails(AuditTrail auditTrail) {
		auditTrail.setOperationDbCall(Date.from(Instant.now()));
		Session session =  entityManager.unwrap(Session.class);
		int auditId = (int)session.save(auditTrail);
		AuditTrail auditTrail1 = session.get(AuditTrail.class,auditId);
		return auditTrail1;
	}

	@Override
	public int updateAuditTrailDetails(AuditTrail auditTrail) {
		return 0;
	}*/

}
