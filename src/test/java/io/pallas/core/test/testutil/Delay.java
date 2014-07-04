package io.pallas.core.test.testutil;

public class Delay {

	public static final void time(long millis) {
		try {
			Thread.currentThread().sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
