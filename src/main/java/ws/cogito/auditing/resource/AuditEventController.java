package ws.cogito.auditing.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ws.cogito.auditing.model.AuditEvent;
import ws.cogito.auditing.model.AuditEvents;
import ws.cogito.auditing.service.AuditingServices;

/**
 * Handles requests for audit event resources
 * @author jeremydeane
 */
@Controller
@RequestMapping("/")
public class AuditEventController {
	
	private static final Logger logger = LoggerFactory.getLogger
			(AuditEventController.class);
	
	@Autowired
	AuditingServices auditingServices;
	
	/**
	 * Returns audit events information for a given application
	 * @param application
	 * @return AuditEvents
	 * @throws Exception
	 */
	@RequestMapping(value = "{application}/events", method = RequestMethod.GET)
	@ResponseBody
	public AuditEvents getAuditEvents (@PathVariable String application, 
			HttpServletRequest request) throws Exception {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Application: " + application);
		}

		AuditEvents auditEvents = auditingServices.retrieveAuditEvents
				(application, request.getLocalName(), request.getLocalPort(),
						request.getContextPath());
		
		return auditEvents;
	}	
	
	/**
	 * Returns audit event information given a key
	 * @param auditEventKey
	 * @return AuditEvent
	 * @throws Exception
	 */
	@RequestMapping(value = "audit/event/{auditEventKey}", method = RequestMethod.GET)
	@ResponseBody
	public AuditEvent getAuditEvent (@PathVariable String auditEventKey ) 
			throws Exception {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Audit Event Key: " + auditEventKey);
		}
		
		AuditEvent event = auditingServices.retrieveAuditEvent(auditEventKey);
		
		return event;
	}
	
	/**
	 * Creates an audit event via POST
	 * @param auditEvent
	 * @param response
	 * @return AuditEvent
	 * @throws Exception
	 */
	@RequestMapping(value = "audit/event", method=RequestMethod.POST)
	public @ResponseBody AuditEvent postAuditEvent
		(@RequestBody AuditEvent auditEvent, HttpServletResponse response) 
				throws Exception {
		
		//audit event was created
		if (auditingServices.storeAuditEvent(auditEvent) == null) {
			
			response.setStatus(HttpStatus.CREATED.value());
		
		} else {
			
			//audit event was updated
			response.setStatus(HttpStatus.OK.value());
		}
		
		//set the resource uri
		response.setHeader("Location",
				"http://localhost:8080/spring-auditor/audit/event/"+ auditEvent.
				getAuditEventKey());
		
		if (logger.isDebugEnabled()) {
			logger.debug("Stored audit event: " + auditEvent);
		}
		
		return auditEvent;
	}
	
	/**
	 * Creates an audit event via PUT
	 * @param auditEvent
	 * @param auditEventKey
	 * @param response
	 * @return AuditEvent
	 * @throws Exception
	 */
	@RequestMapping(value = "audit/event/{auditEventKey}", method=RequestMethod.PUT)
	public @ResponseBody AuditEvent putAuditEvent (@RequestBody AuditEvent auditEvent, 
			@PathVariable String auditEventKey, HttpServletResponse response) 
					throws Exception {
		
		//audit event was created
		if (auditingServices.storeAuditEvent(auditEvent, auditEventKey) == null) {
			
			response.setStatus(HttpStatus.CREATED.value());
		
		} else {
			
			//audit event was updated
			response.setStatus(HttpStatus.OK.value());
		}
		
		//set the resource uri
		response.setHeader("Location","/audit/event/"+ auditEventKey);
		
		if (logger.isDebugEnabled()) {
			
			logger.debug("Stored audit event: " + auditEvent + " with key: "
					+ auditEventKey);
		}
		
		return auditEvent;
	}
	
	@RequestMapping(value = "audit/event/{auditEventKey}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void deleteAuditEvent (@PathVariable String auditEventKey) throws Exception {
		
		//delete the audit event
		auditingServices.deleteAuditEvent(auditEventKey);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Deleted audit event: " + auditEventKey);
		}
	}
}