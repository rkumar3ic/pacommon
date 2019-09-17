package com.fishbowl.auditTrail.queue;

import java.util.List;

import com.fishbowl.auditTrail.model.AuditEvent;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

public interface QueuePublisher {
	public void initEventQueue();
	public String createQueue(String queueName);
	public void sendEventToQueue(String message);
	public List<AuditEvent> getEventsFromQueue();
	public List<AuditEvent> getEventsFromQueue(int numberOfMessages);
	public void deleteEventFromQueue(CloudQueueMessage message);
	
	
	
}
