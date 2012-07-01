package ws.cogito.auditing.messaging;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ws.cogito.auditing.model.AuditEvent;
import ws.cogito.auditing.model.AuditEventURL;
import ws.cogito.auditing.model.AuditEvents;
import ws.cogito.auditing.service.AuditingServices;

import com.thoughtworks.xstream.XStream;


/**
 * Listens for audit events (for Enterprise Integration Agility Demo)
 * @author jeremydeane
 */
public class AuditEventMessageListener implements MessageListener {
	
	private static final Logger logger = LoggerFactory.getLogger
			(AuditEventMessageListener.class);
	
	private XStream xstream = null;
	
	@Autowired
	AuditingServices auditingServices;

	@Override
	public void onMessage(Message message) {
		
		try {
			
			TextMessage textMessage = (TextMessage) message;
			
			if (logger.isDebugEnabled()) {
				logger.debug("Audit Event Message ID: " + textMessage.
						getJMSMessageID());
			}
			
			AuditEvent auditEvent = toPOJO(textMessage.getText());
			
			auditingServices.storeAuditEvent(auditEvent);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Stored audit event:" + auditEvent);
			}
			
		} catch (Exception e) {
			
			throw new RuntimeException(e);
		}
		
	}

	/**
	 * Converts XML to POJO
	 * @param xml
	 * @return AuditEvent
	 * @throws Exception
	 */
	private AuditEvent toPOJO (String xml) throws Exception {
		
		if (xstream == null) {
			initializeXStream();
		}
		
		return (AuditEvent) xstream.fromXML(xml);
	}
	
	/**
	 * Configure XStream
	 */
	private void initializeXStream() {
		
    	xstream = new XStream();
    	xstream.alias("event", AuditEventURL.class);
    	xstream.alias("audit-event", AuditEvent.class);
    	xstream.alias("audit-events", AuditEvents.class);
	}
}
