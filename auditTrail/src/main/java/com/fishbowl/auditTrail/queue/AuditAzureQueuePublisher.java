package com.fishbowl.auditTrail.queue;

import org.apache.log4j.Logger;

import com.fishbowl.auditTrail.constant.AuditConstant;
import com.fishbowl.auditTrail.model.AuditEvent;
import com.fishbowl.auditTrail.queue.impl.QueuePublisherImpl;
import com.google.gson.Gson;


public class AuditAzureQueuePublisher {
	
	private static Logger logger = Logger.getLogger(AuditAzureQueuePublisher.class);
	
	public void sendEventToQueue(AuditEvent event){
		try{
			QueuePublisher queuePublisher = new QueuePublisherImpl();
			String queueName = AuditConstant.AUDIT_QUEUE+event.getBrandId();
			logger.debug("queueName : " + queueName);
			Gson gson = new Gson();
			String json = gson.toJson(event);
			logger.debug("json : " + json);
			String queueURL = queuePublisher.createQueue(queueName);
			logger.debug("queueURL : " + queueURL);
			queuePublisher.sendEventToQueue(json);
		}catch(Exception ex){
			logger.debug("Exception occured while sendinf event to queue");
		}
	}

}
