package up.wargroove.core.net;

import java.util.Vector;

public class Bucket {

	public static final int K_SIZE = 3;
	private Vector<Node> nodes;

	public Bucket() {
	
		nodes = new Vector<>(K_SIZE);

	}

	public boolean addNode(Node n) {

		if(nodes.size() > K_SIZE) return false;

		nodes.add(n);
		return true;

	}

	public boolean delNode(Node n) {

		if(nodes.size() == 0) return false;

		nodes.remove(n);
		return true;

	}

}
