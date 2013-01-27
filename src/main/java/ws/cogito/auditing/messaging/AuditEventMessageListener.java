package ws.cogito.auditing.messaging;

import java.io.InputStream;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import ws.cogito.auditing.model.AuditEvent;
import ws.cogito.auditing.service.AuditingServices;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Listens for audit events (for Enterprise Integration Agility Demo)
 * @author jeremydeane
 */
public class AuditEventMessageListener implements MessageListener {
	
	private static final Logger logger = LoggerFactory.getLogger
			(AuditEventMessageListener.class);
	
	private final ObjectMapper objectMapper;
	private final JAXBContext jaxbContext;
	
	/**
	 * Default Constructor
	 * @throws Exception
	 */
	public AuditEventMessageListener () throws Exception {
		
		objectMapper = new ObjectMapper();
		jaxbContext = JAXBContext.newInstance(AuditEvent.class);
	}
	
	//private final XmlMapper xmlMapper = new XmlMapper();
	
	@Autowired
	AuditingServices auditingServices;

	@Override
	public void onMessage(Message message) {
		
		AuditEvent auditEvent = null;
		
		try {
			
			TextMessage textMessage = (TextMessage) message;
			
			logger.debug("Audit Event Message ID: " + textMessage.
					getJMSMessageID());
			
			String mimeType = textMessage.getStringProperty("mimetype");
			
			if (mimeType.equals("application/json")) {
				
				auditEvent = jsonToPOJO(textMessage.getText());
				
			} else {
				
				auditEvent = xmlToPOJO(textMessage.getText());
			}
			
			auditingServices.storeAuditEvent(auditEvent);
			
			logger.debug("Stored audit event:" + auditEvent);
			
		} catch (Exception e) {
			
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * Transrept to POJO from JSON
	 * @param json
	 * @return AuditEvent
	 * @throws Exception
	 */
	private AuditEvent jsonToPOJO (String json) throws Exception {
		
		logger.debug("Audit Event Payload: " + json);		
		
		return (AuditEvent)objectMapper.readValue(json, AuditEvent.class);	
	}
	
	/**
	 * Transrept to POJO from xml
	 * @param xml
	 * @return AuditEvent
	 * @throws Exception
	 */
	private AuditEvent xmlToPOJO (String xml) throws Exception {
		
		logger.debug("Audit Event Payload: " + xml);
	
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		InputStream stream = IOUtils.toInputStream (xml);	

		return (AuditEvent) unmarshaller.unmarshal(stream);		
	}
}