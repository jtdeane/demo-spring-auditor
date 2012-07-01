package ws.cogito.auditing.service;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ws.cogito.auditing.model.AuditEvent;
import ws.cogito.auditing.model.AuditEvents;

/**
 * Unit test the auditing services
 * @author jeremydeane
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:AuditingServicesTest-context.xml"})
public class AuditingServicesTest {
	
	@Autowired
	AuditingServices auditingServices;

	@Test
	public void storeAuditEvent() throws Exception {
		
		AuditEvent event = new AuditEvent
				("Billing", "201112141530", "Late Payment Notice");
		
		auditingServices.storeAuditEvent(event);
		
		Assert.assertNotNull(auditingServices.retrieveAuditEvent("Billing-201112141530"));
	}
	
	@Test
	public void retrieveAuditEvents() throws Exception {
		
		AuditEvent event1 = new AuditEvent
				("Billing", "201112141530", "Late Payment Notice");
		
		AuditEvent event2 = new AuditEvent
				("Billing", "201112141531", "Late Payment Notice");
		
		AuditEvent event3 = new AuditEvent
				("Claims", "201112141532", "Bodily Injury Alert");		
		
		auditingServices.storeAuditEvent(event1);
		auditingServices.storeAuditEvent(event2);
		auditingServices.storeAuditEvent(event3);
		
		AuditEvents auditEvents = auditingServices.retrieveAuditEvents
				("Billing", "localhost", 8080, "/auditing");
		
		Assert.assertEquals(2, auditEvents.getEvents().size());
		
	}
}