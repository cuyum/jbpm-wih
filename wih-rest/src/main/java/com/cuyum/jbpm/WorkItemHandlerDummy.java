package com.cuyum.jbpm;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;



public class WorkItemHandlerDummy implements WorkItemHandler {

	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		System.out.println("Finalizando WIH");

	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		try {
			System.out.println("WIH ID:"+workItem.getId());
			System.out.println("WIH NAME:"+workItem.getName());
			System.out.println("WIH PID:"+workItem.getProcessInstanceId());
			System.out.println("WIH STATE:"+workItem.getState());
			System.out.println("WIH Parameters:"+workItem.getParameters());
			
			manager.completeWorkItem(workItem.getId(), new HashMap<String, Object>());
		} catch (Exception e) {
			e.printStackTrace();
			manager.abortWorkItem(workItem.getId());
		}

	}

}
