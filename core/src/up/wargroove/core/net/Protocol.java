package up.wargroove.core.net;

import up.wargroove.utils.Pair;

public class Protocol {

	static enum RPC {

		JOIN,
		LEAVE

	}

	public static byte [] forgePacketHead(RPC code, Pair<String, String> ... options) {
	
		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append(code.toString() + "\n");
		for(Pair<String, String> option : options) {

			strBuilder.append(option.first + ":" + option.second);

		}

		return strBuilder.toString().getBytes();

	}

}
