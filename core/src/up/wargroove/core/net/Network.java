package up.wargroove.core.net;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;

public class Network {
	
	private Server server;	
	private int lport, sport;

	public Network(int lport, int sport) {
	
		this.lport = lport;
		this.sport = sport;

	}

	public void run() {

		server = new Server(lport);
		server.start();

	}

	public void stop() {

		try {

			server.interrupt();	
			server.join();

		} catch(InterruptedException e) {

			e.printStackTrace();

		}
	}

	public int send(InetAddress address, int port, byte [] data) {

		try {

			DatagramSocket sender = new DatagramSocket(port, address);
			DatagramPacket outcomingPacket = new DatagramPacket(data, data.length);

			sender.send(outcomingPacket);
			sender.close();

		} catch(Exception e) {

			e.printStackTrace();
			return -1;

		}

		return 0;

	}

	public int broadcast(Node [] nodes, byte [] data) {

		int sentBytes = -1;

		for(Node node : nodes) {

			int sb = send(node.getAddress(), node.getPort(), data);	
			sentBytes = sb;

		}

		return sentBytes;

	}

}
