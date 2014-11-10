package io.pallas.core.ws;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */

public class TransactionalExecutor implements Executor {

	@Override
	public void execute(final Runnable command) {

		Executors.newSingleThreadExecutor().execute(command);
	}

}
