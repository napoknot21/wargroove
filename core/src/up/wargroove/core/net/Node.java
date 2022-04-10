package up.wargroove.core.net;

import java.net.InetAddress;

public class Node {

	private int uid = 0x0;

	private InetAddress address;
	private int port;

	public Node(InetAddress address, int port) {

		this.address = address;
		this.port = port;

	}

	public Node(int uid, InetAddress address, int port) {
	
		this(address, port);
		this.uid = uid;

	}

	public int getUID() {

		return uid;

	}

	public InetAddress getAddress() {

		return address;

	}

	public int getPort() {

		return port;

	}

	@Override
	public boolean equals(Object o) {

		if(o instanceof Node) {

			Node node = (Node) o;
			return node.uid == uid;

		}

		return false;

	}

}
