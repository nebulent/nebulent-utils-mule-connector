/**
 * This file was automatically generated by the Mule Development Kit
 */
package com.nebulent;

import org.mule.api.MuleEvent;
import org.mule.construct.Flow;
import org.mule.tck.FunctionalTestCase;
import org.mule.tck.AbstractMuleTestCase;

import org.junit.Test;

public class HelloConnectorTest extends FunctionalTestCase {
	@Override
	protected String getConfigResources() {
		return "mule-config.xml";
	}

	@Test
	public void testFlow() throws Exception {
		Flow flow = lookupFlowConstruct("testFlow");
		MuleEvent event = AbstractMuleTestCase
				.getTestEvent("this message is throttled");
		int i=100;
		while (--i>=0) {
			MuleEvent responseEvent = flow.process(event);
		}
	}

	/**
	 * Run the flow specified by name and assert equality on the expected output
	 * 
	 * @param flowName
	 *            The name of the flow to run
	 * @param expect
	 *            The expected output
	 */
	protected <T> void runFlowAndExpect(String flowName, T expect)
			throws Exception {
		Flow flow = lookupFlowConstruct(flowName);
		MuleEvent event = AbstractMuleTestCase.getTestEvent(null);
		MuleEvent responseEvent = flow.process(event);
		assertEquals(expect, responseEvent.getMessage().getPayload());
	}

	/**
	 * Run the flow specified by name using the specified payload and assert
	 * equality on the expected output
	 * 
	 * @param flowName
	 *            The name of the flow to run
	 * @param expect
	 *            The expected output
	 * @param payload
	 *            The payload of the input event
	 */
	protected <T, U> void runFlowWithPayloadAndExpect(String flowName,
			T expect, U payload) throws Exception {
		Flow flow = lookupFlowConstruct(flowName);
		MuleEvent event = AbstractMuleTestCase.getTestEvent(payload);
		MuleEvent responseEvent = flow.process(event);

		assertEquals(expect, responseEvent.getMessage().getPayload());
	}

	/**
	 * Retrieve a flow by name from the registry
	 * 
	 * @param name
	 *            Name of the flow to retrieve
	 */
	protected Flow lookupFlowConstruct(String name) {
		return (Flow) AbstractMuleTestCase.muleContext.getRegistry()
				.lookupFlowConstruct(name);
	}
}
