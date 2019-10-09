package com.fishbowl.auditTrail.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fishbowl.auditTrail.constant.AuditConstant;
import com.fishbowl.auditTrail.model.AuditEvent;
import com.fishbowl.auditTrail.queue.impl.QueuePublisherImpl;
import com.google.gson.Gson;


public class AuditAzureQueuePublisher{
	
	private static Logger logger = LoggerFactory.getLogger(AuditAzureQueuePublisher.class);
	
	public void sendEventToQueue(AuditEvent event){
		ExecutorService executor = null;
		try{
			executor = Executors.newSingleThreadExecutor();
			executor.submit(() ->{
				QueuePublisher queuePublisher = new QueuePublisherImpl();
				String queueName = AuditConstant.AUDIT_QUEUE+event.getBrandId();
				logger.debug("queueName : " + queueName);
				Gson gson = new Gson();
				String json = gson.toJson(event);
				logger.debug("json : " + json);
				String queueURL = queuePublisher.createQueue(queueName);
				logger.debug("queueURL : " + queueURL);
				queuePublisher.sendEventToQueue(json);
			});
		}catch(Exception e){
			logger.debug("Exception occured while sendinf event to queue");
			logger.error(e.getMessage(),e.fillInStackTrace());
		}finally{
			executor.shutdown();
		}
	}

}
