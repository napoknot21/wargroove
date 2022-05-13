package up.wargroove.utils;

public class BitSet {

	private boolean [] bits;
	private int setBits = 0;

	/**
	 * Constructor for Bitset
	 * @param str String to analyse
	 * @param size length for boolean table
	 */
	public BitSet(String str, int size) {

		bits = new boolean[size];
		int strLen = str.length();

		for(int k = 0; k < size; k++) {

			if(k >= str.length()) {

				bits[k] = false;
				continue;

			}

			bits[k] = str.charAt(strLen - 1 - k) == '1' ? true : false;
			if(bits[k]) setBits++;

		}
	}

	/**
	 * constructor for BitSet
	 * @param i Integer in binary format
	 * @param size length of a boolean table
	 */
	public BitSet(Integer i, int size) {

		this(Integer.toBinaryString(i), size);

	}

	/**
	 * constructor for BitSet
	 * @param size length of boolean table
	 */
	public BitSet(int size) {
	
		this(0, size);

	};

	/**
	 * gets a boolean value at a specific position
	 * @param k index
	 * @return the boolean value of bits[k]
	 */
	public boolean get(int k) {

		if(k >= bits.length) return false;

		return bits[k];

	}

	/**
	 * @return length of the boolean table
	 */
	public int size() {

		return bits.length;

	}

	/**
	 * sum of true boolean values
	 * @return the sum
	 */
	public int toInt() {

		int sum = 0;
		int pow = 1;

		for(boolean b : bits) {

			if(b) sum += pow;	
			pow *= 2;
		}

		return sum;

	}

	public BitSet sub(int from, int offset) {

		BitSet bs = new BitSet(offset);

		for(int k = from; k < from + offset; k++) {

			bs.set(k - from, bits[k]);

		}

		return bs;

	}

	/***************** setters and getters *****************/

	public int count() {

		return setBits;

	}

	public void set(int i, boolean v) {

		bits[i] = v;

	}

}
