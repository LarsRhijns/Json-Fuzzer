package edu.ucla.cs.bigfuzz.customarray.applicable.BranchMark;

import edu.berkeley.cs.jqf.instrument.tracing.TraceLogger;
import edu.berkeley.cs.jqf.instrument.tracing.events.BranchEvent;
import edu.berkeley.cs.jqf.instrument.tracing.events.TraceEvent;
import edu.ucla.cs.bigfuzz.dataflow.FilterEvent;
import edu.ucla.cs.bigfuzz.dataflow.MapEvent;
import janala.logger.inst.METHOD_BEGIN;
import janala.logger.inst.MemberRef;

import java.util.ArrayList;

public class BranchMarkCustomArray {

	enum EventType {
		Filter, Map, Branch
	}

	private static void throwEvent(EventType eventType) {
		throwEvent(eventType, -1);
	}
	/**
	 * Generate a custom event. Sent by the TraceLogger.
	 *
	 * @param eventType type of event from EventType.
	 * @param arm choice value (default = -1).
	 */
	private static void throwEvent(EventType eventType, int arm) {
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[1];
		int callersLineNumber = stackTraceElement.getLineNumber();
		int iid = BranchMarkCustomArray.class.hashCode(); // random value associated with a program location
		MemberRef method = new METHOD_BEGIN(stackTraceElement.getClassName(), stackTraceElement.getMethodName(),
				"()V"); // containing method

		TraceEvent event;
		switch (eventType) {
			case Filter:
				event = new FilterEvent(iid, method, callersLineNumber, arm);
				break;
			case Map:
				event = new MapEvent(iid, method, callersLineNumber);
				break;
			case Branch:
				event = new BranchEvent(iid, method, callersLineNumber, arm);
				break;
			default:
				throw new IllegalStateException("Unexpected Event Type: " + eventType);
		}

		TraceLogger.get().emit(event);
	}

	/**
	 * Literally just return the values and throw a map event.
	 * @return the unchanged input
	 */
	public static ArrayList<String> mapDoNothing(ArrayList<String> oldResult) {
		// throw map event
		throwEvent(EventType.Map);

		return oldResult;
	}

	/**
	 * Only select the very first string.
	 * @return only first String
	 */
	public static String filterOnlyFirstInput(ArrayList<String> oldResult) {
		// throw filter event with manually chosen arm
		int arm = -199;
		throwEvent(EventType.Filter, arm);

		// only select first input
		return oldResult.get(0);
	}

	/**
	 * Decide branch depending on how many commas the string contains.
	 * @return the unchanged input
	 */
	public static String branchCountCommas(String oldResult) {
		// todo: idea to depend arm based on small steps of ints
		// calculate number of commas in string
		String[] splits = oldResult.split(",");
		int arm = Math.min(40, splits.length);

		// throw filter event
		throwEvent(EventType.Branch, arm);

		return oldResult;
	}
}
