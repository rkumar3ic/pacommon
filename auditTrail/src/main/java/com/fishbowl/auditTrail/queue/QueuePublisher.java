package com.fishbowl.auditTrail.queue;

public interface QueuePublisher {
	public void initEventQueue();
	public String createQueue(String queueName);
	public void sendEventToQueue(String message);
	
}
