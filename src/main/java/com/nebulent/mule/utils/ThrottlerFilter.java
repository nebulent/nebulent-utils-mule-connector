package com.nebulent.mule.utils;

import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;

public class ThrottlerFilter implements Filter {

	
	Throttler throttler;
	
	long requestsLimit;
	long period;
	
	@Override
	public boolean accept(MuleMessage message) {
		try {
			getThrottler().delay();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return true;
	}

	public Throttler getThrottler() {
		return (this.throttler != null) ? this.throttler : (this.throttler = new Throttler(
				requestsLimit, period));
	}

	public void setThottler(Throttler throttler) {
		this.throttler = throttler;
	}
}
