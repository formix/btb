package org.formix.btb;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Sufficiently Unique Identifier
 * 
 * @author formix
 *
 */
final class SUID {

	private static final int SHORT_MAX = 65536;
	private static Random rnd = new SecureRandom();
	private static int counter = -1;

	private SUID() {
	}

	/**
	 * Creates a unique 64 bits ID by aggregating the current time in
	 * milliseconds since epoch (Jan. 1, 1970) with a 16 bits counter. The
	 * counter is initialized at a random number. This generator can create up
	 * to 65536 different id per millisecond.
	 * 
	 * @return a new id.
	 */
	public static synchronized long nextId() {
		long now = System.currentTimeMillis();
		if (counter == -1) {
			counter = rnd.nextInt(SHORT_MAX);
		}
		long id = (now << 16) | counter;
		counter = (counter + 1) % SHORT_MAX;
		return id;
	}
}
