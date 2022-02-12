package up.wargroove.utils;

public class BitSet {

	private boolean [] bits;
	private int setBits = 0;

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

	public BitSet(Integer i, int size) {

		this(Integer.toBinaryString(i), size);

	}

	public BitSet(int size) {
	
		this(0, size);

	};

	public boolean get(int k) {

		if(k >= bits.length) return false;

		return bits[k];

	}

	public int count() {

		return setBits;	

	}

	public int size() {

		return bits.length;

	}

	public void set(int i, boolean v) {

		bits[i] = v;

	}

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

}
