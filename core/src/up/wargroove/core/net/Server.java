package up.wargroove.core.net;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.io.IOException;

public class Server extends Thread {

	private boolean status = true;
	
	private final int bufferSize = 512;

	private final int lport;
	private DatagramSocket socket;

	public Server(int lport) {

		this.lport = lport;

	}

	@Override
	public void run() {

		while(status) {

			byte [] buffer = new byte[bufferSize];

			try {

				socket = new DatagramSocket(lport);
				DatagramPacket incomingPacket = new DatagramPacket(buffer, bufferSize);
		
				socket.receive(incomingPacket);
				socket.close();

			} catch(SocketException e) {

				e.printStackTrace();

			} catch(IOException e) {
			
				e.printStackTrace();
			
			}
		}

	}

	public void interrupt() {

		status = false;

	}

}
