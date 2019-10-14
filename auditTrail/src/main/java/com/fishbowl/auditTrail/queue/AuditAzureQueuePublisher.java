package com.fishbowl.auditTrail.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import org.apache.log4j.Logger;

import com.fishbowl.auditTrail.constant.AuditConstant;
import com.fishbowl.auditTrail.model.AuditEvent;
import com.fishbowl.auditTrail.queue.impl.QueuePublisherImpl;
import com.google.gson.Gson;


public class AuditAzureQueuePublisher{
	
	private static Logger logger = Logger.getLogger(AuditAzureQueuePublisher.class);
	private static ExecutorService executor = Executors.newFixedThreadPool(AuditConstant.AZURE_QUEUE_THREAD_POOL_SIZE);
	
	public void sendEventToQueue(AuditEvent event){
		try{
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
			logger.debug("Exception occured while sending event to queue");
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
	}

}
