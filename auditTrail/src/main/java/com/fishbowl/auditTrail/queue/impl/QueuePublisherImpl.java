package com.fishbowl.auditTrail.queue.impl;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.fishbowl.auditTrail.constant.AuditConstant;
import com.fishbowl.auditTrail.model.AuditEvent;
import com.fishbowl.auditTrail.queue.QueuePublisher;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

public class QueuePublisherImpl implements QueuePublisher {
	private static Logger logger = Logger.getLogger(QueuePublisherImpl.class);
	public CloudStorageAccount azureStorageAccount = null;
	public CloudQueueClient queueClient = null;
	public CloudQueue queue=null;
	public String queueUrl;
	
	public QueuePublisherImpl() {
		super();
		initEventQueue();
	}
	
	private CloudStorageAccount createColudStorageAccount(){
		CloudStorageAccount storageAccount= null;
		try {
			storageAccount = CloudStorageAccount.parse(AuditConstant.storageConnectionString);
        }
        catch (IllegalArgumentException|URISyntaxException e) {
        	logger.debug("AZURE Storage connection Error" +e.getMessage());
        }
        catch (InvalidKeyException e) {
        	logger.debug("AZURE Storage connection Error" +e.getMessage());
        }
		return storageAccount;

	}
	@Override
	public void initEventQueue() {
		azureStorageAccount=this.createColudStorageAccount();
		if(azureStorageAccount !=null){
			queueClient= azureStorageAccount.createCloudQueueClient();
		}
	}

	@Override
	public String createQueue(String queueName) {
		// Create a queue
		try {
		    CloudQueue cloudQueue = queueClient.getQueueReference(queueName.toLowerCase());
		    cloudQueue.createIfNotExists();
		    queue = cloudQueue;
		    queueUrl = queue.getUri().toString();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Queue not created : " + e.fillInStackTrace()); //$NON-NLS-1$
		}
		return queueUrl;
	}

	@Override
	public void sendEventToQueue(String message) {
		try {
			logger.info("sending azure queue message : " +queueUrl+ "  message  "+message);
			queue.addMessage(new CloudQueueMessage(message));
		} catch (Exception e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
		}
	}
	
	private List<AuditEvent> getEventsFromQueueMethod(int numberOfMessages){

		List<AuditEvent> events = new ArrayList<AuditEvent>();
		//Messages messages = new Messages();
		try {
			for (CloudQueueMessage message : queue.retrieveMessages(numberOfMessages, 30, null, null)) {
				logger.debug(message.getMessageContentAsString());
				try {
					JSONObject tempobj = new JSONObject(message.getMessageContentAsString());
					AuditEvent event = null;
					String action = null;
					if(tempobj.has("action")){
					action = tempobj.getString("action");
					}else{
						logger.info("Incoming json do not have valid Action");
						continue;
					}
					int brandId = 0;
					if(tempobj.has("brandid")){
						brandId = tempobj.getInt("brandid");
					}
					String requestedOnBehalf = null;
					if(tempobj.has("requestedOnBehalf")){
						requestedOnBehalf = tempobj.getString("requestedOnBehalf");
					}
					if (event != null){
						events.add(event);
					}
					
				} catch (Throwable e) {
					//e.printStackTrace();
					logger.error("Incoming json not valid: " + message.getMessageContentAsString() //$NON-NLS-1$
							+ " : ", e.fillInStackTrace()); //$NON-NLS-1$
				} finally {
					deleteEventFromQueue(message);
				}
			}
			} catch (StorageException e) {
				logger.error("Error while reading the messages from queue: "+e.getMessage());
			}
			return events;
	
		
	}

	@Override
	public List<AuditEvent> getEventsFromQueue() {
		return this.getEventsFromQueueMethod(1);
	}


	@Override
	public void deleteEventFromQueue(CloudQueueMessage message) {
		// Delete a message
		try {
			queue.deleteMessage(message);
		} catch (StorageException e) {
			logger.error("Error while deleting the message from queue :"+ e.getMessage());
		}	
	}

	
	@Override
	public List<AuditEvent> getEventsFromQueue(int numberOfMessages) {
		return this.getEventsFromQueueMethod(numberOfMessages);
	}

}
