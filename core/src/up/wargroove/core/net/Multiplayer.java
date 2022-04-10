package up.wargroove.core.net;

import up.wargroove.utils.Log;

public class Multiplayer {

	private boolean hosting = false;
	private Network network;

	public Multiplayer() {

		network = new Network(51235, 46589);
		network.run();

	}

	public void host() {
	
		hosting = true;

	}

	public void join(Node node) {
	
		if(hosting) return;
	
		byte [] header = Protocol.forgePacketHead(Protocol.RPC.JOIN);
		int status = network.send(node.getAddress(), node.getPort(), header);

		if(status == -1) {

			Log.print(Log.Status.ERROR, "Erreur lors de l'envoie du paquet!");

		}

	}

	public Network getNetwork() {

		return network;

	}

}
