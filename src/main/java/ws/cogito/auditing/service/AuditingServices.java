package ws.cogito.auditing.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ws.cogito.auditing.model.AuditEvent;
import ws.cogito.auditing.model.AuditEventURL;
import ws.cogito.auditing.model.AuditEvents;

/**
 * Services for storing and retrieving application audit events
 * @author jeremydeane
 */
@Service
public class AuditingServices {

	private static final Logger logger = LoggerFactory.getLogger(AuditingServices.class);
	
	//in memory storage of application audits
	private ConcurrentHashMap<String, AuditEvent> audits = 
			new ConcurrentHashMap<String, AuditEvent>();
	
	/**
	 * Store an application audit event
	 * @param auditEvent
	 * @return AuditEvent
	 */
	public AuditEvent storeAuditEvent (AuditEvent auditEvent) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Storing " + auditEvent.getAuditEventKey());
		}
		
		return audits.putIfAbsent(auditEvent.getAuditEventKey(), auditEvent);
	}
	
	/**
	 * Store an application audit event
	 * @param auditEvent
	 * @param auditEventKey
	 * @return AuditEvent
	 */
	public AuditEvent storeAuditEvent (AuditEvent auditEvent, String auditEventKey) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Storing " + auditEventKey);
		}
		
		return audits.putIfAbsent(auditEventKey, auditEvent);
	}	

	/**
	 * Retrieve a specific audit event
	 * @param auditEventKey
	 * @return AuditEvent
	 */
	public AuditEvent retrieveAuditEvent(String auditEventKey) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieving " + auditEventKey);
		}
		
		return audits.get(auditEventKey);
	}
	
	/**
	 * Delete an audit event
	 * @param auditEventKey
	 */
	public void deleteAuditEvent(String auditEventKey) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Deleting " + auditEventKey);
		}
		
		audits.remove(auditEventKey);
	}
	
	/**
	 * Retrieve all audit events for a given application
	 * @param application
	 * @param host
	 * @param port
	 * @param context
	 * @return AuditEvents
	 */
	public AuditEvents retrieveAuditEvents (String application, String host, 
			int port, String context) {
		
		List<AuditEventURL> auditEvents = new ArrayList<AuditEventURL>();
		
		for (Map.Entry<String,AuditEvent> entry : audits.entrySet()) {
		    
		    if (entry.getKey().contains(application)) {
		    	
		    	auditEvents.add(new AuditEventURL(host, port, context, 
		    			entry.getKey()));
		    }	    
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Returning " + auditEvents.size() + " " + 
					application + " audit events");
		}
		
		return new AuditEvents (application, auditEvents);
	}
}