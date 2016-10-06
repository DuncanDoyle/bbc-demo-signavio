package org.jboss.ddoyle.jbpm.workitem;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demo implementation of the Send Task that is called by the BPMN2 Message End Event.
 * 
 * @author <a href="mailto:duncan.doyle@redhat.com">Duncan Doyle</a>
 */
public class DemoSendWorkItemHandler implements WorkItemHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DemoSendWorkItemHandler.class);
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		LOGGER.info("Sending message.");
		
		//Complete the workitem after the message has been sent.	
		workItemManager.completeWorkItem(workItem.getId(), null);
		
	}
	
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		throw new UnsupportedOperationException("This workitem cannot be aborted.");
	}

}
