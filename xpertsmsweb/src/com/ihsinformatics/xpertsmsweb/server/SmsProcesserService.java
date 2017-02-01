package com.ihsinformatics.xpertsmsweb.server;

import java.util.Timer;

public class SmsProcesserService {
	public static final int DELAY = 100000;
	private static SmsProcesserService smsInstance;

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public static boolean instantiate() {
		if (smsInstance == null) {
			smsInstance = new SmsProcesserService();
			Timer responseReaderTimer = new Timer();
			responseReaderTimer
					.schedule(new ResponseReader(), DELAY, DELAY * 3);
			return true;
		} else {
			throw new InstantiationError(
					"An instance of SMS Service already exists");
		}
	}

	public static SmsProcesserService getInstance() {
		if (smsInstance == null) {
			throw new InstantiationError("SMS Service not instantiated");
		}
		return smsInstance;
	}
}
