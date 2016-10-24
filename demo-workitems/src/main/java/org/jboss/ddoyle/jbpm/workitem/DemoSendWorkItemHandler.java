package org.jboss.ddoyle.jbpm.workitem;

import java.lang.reflect.Method;
import java.util.Map;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
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

	private static final String PARAMS_MESSAGE = "Message";
	
	//TODO: Because we get the type of Message, we could select an actual message processor implementation based on this type.
	private static final String PARAMS_MESSAGE_TYPE = "MessageType";

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
				LOGGER.error("Error while retrieving message data.");
				handleException(e);
			}
		} else {
			String errorMessage = "We don't have a message. Not sending a message is evil and should never happen, so we throw an exception.";
			LOGGER.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
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
		LOGGER.info(messageBuilder.toString());
	}
	
	private void handleOffer(Object prospect, Object offer) {
		StringBuilder messageBuilder = new StringBuilder("CreditCard application accepted:\n");
		messageBuilder.append("Prosect: ").append(prospect).append("\n");
		messageBuilder.append("Offer: " + offer);
		LOGGER.info(messageBuilder.toString());
	}

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		throw new UnsupportedOperationException("This workitem cannot be aborted.");
	}

}
