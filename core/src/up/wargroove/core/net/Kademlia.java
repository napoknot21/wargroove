package up.wargroove.core.net;

import java.util.Queue;
import java.util.LinkedList;

public class Kademlia {

	private int size;

	static enum Code {

		PING,
		STORE,
		FIND_NODE,
		FIND_VALUE

	}

	private Queue<Bucket> buckets;

	public Kademlia(int size) {

		this.size = size;
		buckets = new LinkedList<>();

	}

}
