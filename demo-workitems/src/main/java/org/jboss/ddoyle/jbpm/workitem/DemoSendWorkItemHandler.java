package org.jboss.ddoyle.jbpm.workitem;

import java.lang.reflect.Method;
import java.util.Map;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.email.Connection;
import org.jbpm.process.workitem.email.Email;
import org.jbpm.process.workitem.email.Message;
import org.jbpm.process.workitem.email.Recipient;
import org.jbpm.process.workitem.email.Recipients;
import org.jbpm.process.workitem.email.SendHtml;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demo implementation of the Send Task that is called by the BPMN2 Message End Event.
 * <p/>
 * Problem with the current implementation is that it's generic and works on data-types defined in the process. Hence, it uses a lot of reflection.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public class DemoSendWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DemoSendWorkItemHandler.class);
	
	private static final String SEND_MAIL_PROPERTY_NAME = "com.signavio.demo.sendmail";

	private static final String PARAMS_MESSAGE = "Message";
	
	private final boolean sendMail;
	
	private final Connection connection;
	
	private static final String FROM_ADDRESS = "jbossdemocentral@gmail.com";
	
	//TODO: Because we get the type of Message, we could select an actual message processor implementation based on this type.
	private static final String PARAMS_MESSAGE_TYPE = "MessageType";

	
	public DemoSendWorkItemHandler() {
		connection = new Connection();
		//We could (and should) parameterize these settings.
		connection.setHost("smtp.gmail.com");
		connection.setPort("587");
		connection.setUserName("jbossdemocentral@gmail.com");
		connection.setPassword("rbantztpqywjyajj");
		connection.setStartTls(true);
		
		//Determine if we're actually going to send a mail.
		sendMail = Boolean.parseBoolean(System.getProperty(SEND_MAIL_PROPERTY_NAME, "false"));
	}
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		LOGGER.info("Sending message.");

		Map<String, Object> params = workItem.getParameters();
		// We expect only a message. The problem is that the message type, atm, is defined in the process project, so we need to do some
		// reflection magic here.
		Object message = params.get(PARAMS_MESSAGE);
		
		if (message != null) {
			try {
				Method getProspectMethod = message.getClass().getDeclaredMethod("getProspect");
				Object prospect = getProspectMethod.invoke(message);
				Method getRejectionMethod = message.getClass().getDeclaredMethod("getRejection");
				Object rejection = getRejectionMethod.invoke(message);
				Method getOfferMethod = message.getClass().getDeclaredMethod("getOffer");
				Object offer = getOfferMethod.invoke(message);
				
				if (rejection != null) {
					handleRejection(prospect, rejection);
				} else if (offer != null) {
					handleOffer(prospect, offer);
				} else {
					LOGGER.error("Unable to send message. Unknown message type.");
				}
			
			} catch (Exception e) {
				LOGGER.error("Error sending message.", e);
				//handleException(e);
			}
		} else {
			String errorMessage = "We don't have a message. Not sending a message is evil and should never happen, so we throw an exception.";
			LOGGER.error(errorMessage);
			//throw new IllegalArgumentException(errorMessage);
		}

		// Complete the workitem after the message has been sent.
		/*
		 * BPMSuite 6.3 contains a bug where the engine throws an illegalstateexecption on a Message End Event. We therefore do not complete
		 * the WIH (which is nasty, but currently the only workaround.
		 */
		// workItemManager.completeWorkItem(workItem.getId(), null);
	}
	
	private void handleRejection(Object prospect, Object rejection) {
		StringBuilder messageBuilder = new StringBuilder("CreditCard application rejected:\n");
		messageBuilder.append("Prosect: ").append(prospect).append("\n");
		messageBuilder.append("Rejection: " + rejection);
		
		Method getEmail = null;
		String email = null;
		try {
			getEmail = prospect.getClass().getDeclaredMethod("getEmail");
			email = (String) getEmail.invoke(prospect);
		} catch (Exception e) {
			LOGGER.error("Error while retrieving prospect's email address for notification.");
			handleException(e);
		}
		
		String message = messageBuilder.toString();
		LOGGER.info("Sending message: " + message);
		if (sendMail) {
			sendEmail(email, "Rejected: Credit Card Application", messageBuilder.toString());
		}
	}
	
	private void handleOffer(Object prospect, Object offer) {
		StringBuilder messageBuilder = new StringBuilder("CreditCard application accepted:\n");
		messageBuilder.append("Prosect: ").append(prospect).append("\n");
		messageBuilder.append("Offer: ").append(offer);
		
		Method getEmail = null;
		String email = null;
		try {
			getEmail = prospect.getClass().getDeclaredMethod("getEmail");
			email = (String) getEmail.invoke(prospect);
		} catch (Exception e) {
			LOGGER.error("Error while retrieving prospect's email address for notification.");
			handleException(e);
		}
		
		String message = messageBuilder.toString();
		LOGGER.info("Sending message: " + message);
		if (sendMail) { 
			sendEmail(email, "Offer: Credit Card Application", message);
		}
	}

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		throw new UnsupportedOperationException("This workitem cannot be aborted.");
	}
	
	/**
	 * Sends the message as an e-mail. Re-uses functionality from the jBPM e-mail wih.
	 * @param message
	 */
	private void sendEmail(String to, String subject, String body) {
		Email email = new Email();
		email.setConnection(this.connection);
		
		Message message = new Message();
		message.setFrom(FROM_ADDRESS);
		
		Recipients recipients = new Recipients();
		
		Recipient toRecipient = new Recipient();
		toRecipient.setType("To");
		toRecipient.setEmail(to);
		recipients.addRecipient(toRecipient);
		
		message.setRecipients(recipients);
		message.setSubject(subject);
		message.setBody(body);
		
		email.setMessage(message);
		
		SendHtml.sendHtml(email);
	}

}
