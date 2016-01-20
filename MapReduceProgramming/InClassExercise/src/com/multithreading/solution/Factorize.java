package com.multithreading.solution;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

public class Factorize {

	static ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
	// stores square root value
	static BigInteger rn = BigInteger.valueOf(0);
	// stores the value of the input and process the factors of it
	static BigInteger nn;

	public static void main(String[] args) throws InterruptedException {
		if (args.length != 1) {
			System.out.println("Usage: java Factor 123456");
		}

		BigInteger kk = new BigInteger(args[0]);
		// 32281802098926944263
		nn = kk;
		factorize(nn);

		System.out.println("Factors:");
		Collections.sort(factors);
		for (BigInteger xx : factors) {
			System.out.println(xx);
		}

	}

	static void factorize(BigInteger kk) throws InterruptedException {
		// Create three threads:
		// This thread manages square root of nn
		Thread thread1 = new Thread() {
			public void run() {
				synchronized (this) {
					rn = BigMath.sqrt(nn);
				}
			}
		};

		// this thread processes even factors of the nn
		Thread thread2 = new Thread() {
			public void run() {
				realFactorize(nn);
			}
		};

		// this thread processes odd factors of the nn
		Thread thread3 = new Thread() {
			public void run() {
				synchronized (this) {
					BigInteger ii = new BigInteger("3");
					while (ii.compareTo(rn) <= 0) {
						if (nn.mod(ii).equals(BigMath.ZERO)) {
							factors.add(ii);
							nn = nn.divide(ii);
						} else {
							ii = ii.add(BigMath.TWO);
						}
					}

					if (!(nn.equals(new BigInteger("1"))))
						factors.add(nn);

				}
			}
		};

		// Start the threads.
		thread1.start();
		thread2.start();
		thread2.join();
		// Wait for the thread that processes even factors to complete

		// spawn thread that processes odd factors
	    if(!thread2.isAlive())
	       thread3.start();
		// Wait Main thread for the completion of the main thread
		thread1.join();
		thread3.join();
		System.out.println("Input: " + kk);
		System.out.println("Sqrt: " + rn);
	}

	private static synchronized ArrayList<BigInteger> realFactorize(BigInteger kk) {
		// BigInteger rn1 = nn;
		factors = new ArrayList<BigInteger>();

		while (nn.mod(BigMath.TWO).equals(BigMath.ZERO)) {
			factors.add(BigMath.TWO);
			nn = nn.divide(BigMath.TWO);
		}
		return factors;
	}

}

class BigMath {
	public final static BigInteger TWO = new BigInteger("2");
	public final static BigInteger ZERO = new BigInteger("0");

	static BigInteger sqrt(BigInteger nn) {
		return sqrtSearch(nn, TWO, nn);
	}

	static BigInteger sqrtSearch(BigInteger nn, BigInteger lo, BigInteger hi) {
		BigInteger xx = lo.add(hi).divide(TWO);

		if (xx.equals(lo) || xx.equals(hi)) {
			return xx;
		}

		BigInteger dy = nn.subtract(xx.multiply(xx));
		if (dy.compareTo(ZERO) < 0) {
			return sqrtSearch(nn, lo, xx);
		} else {
			return sqrtSearch(nn, xx, hi);
		}
	}
}
