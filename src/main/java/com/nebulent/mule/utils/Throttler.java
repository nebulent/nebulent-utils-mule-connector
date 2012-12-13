package com.nebulent.mule.utils;

import org.mule.api.MuleMessage;

public class Throttler {

	private long requestsLimit;
	private long periodInMilis;
	private TimeSlot slot;

	public void delay() throws Exception {
		TimeSlot slot = nextSlot();
		if (!slot.isActive()) {
			Thread.sleep(slot.startTime - System.currentTimeMillis());
		}
	}

	protected synchronized TimeSlot nextSlot() {
		if (slot == null) {
			slot = new TimeSlot();
		}
		if (slot.isFull()) {
			slot = slot.next();
		}
		slot.assign();
		return slot;
	}

	protected class TimeSlot {
		public long capacity;
		public long duration = Throttler.this.periodInMilis;
		public long startTime;

		protected TimeSlot() {
			capacity = Throttler.this.requestsLimit;
			System.out.println("New Time Slot : [time:" + System.currentTimeMillis()+", capacity:"+capacity);
			this.startTime = System.currentTimeMillis();
		}

		protected TimeSlot(long nextStartTime) {
			capacity = Throttler.this.requestsLimit;
			System.out.println("New Time Slot : [time:" + nextStartTime+", capacity:"+capacity);
			this.startTime = nextStartTime;
		}

		protected boolean isFull() {
			return capacity <= 0;
		}

		protected boolean isActive() {
			return startTime <= System.currentTimeMillis();
		}

		protected TimeSlot next() {
			return new TimeSlot(Math.max(System.currentTimeMillis(),
					this.startTime + this.duration));
		}

		protected void assign() {
			capacity--;
		}
	}

	public long getRequestsLimit() {
		return requestsLimit;
	}

	public void setRequestsLimit(long requestsLimit) {
		this.requestsLimit = requestsLimit;
	}

	public long getPeriodInMilis() {
		return periodInMilis;
	}

	public void setPeriodInMilis(long periodInMilis) {
		this.periodInMilis = periodInMilis;
	}

	public TimeSlot getSlot() {
		return slot;
	}

	public void setSlot(TimeSlot slot) {
		this.slot = slot;
	}

	
	public Throttler() {
		
	}
	
	public Throttler(long requestsLimit, long periodInMilis) {
		super();
		this.requestsLimit = requestsLimit;
		this.periodInMilis = periodInMilis;
		
		System.out.println("throttler requestsLimit:"+requestsLimit);
		System.out.println("throttler periodInMilis:"+periodInMilis);
	}

}
