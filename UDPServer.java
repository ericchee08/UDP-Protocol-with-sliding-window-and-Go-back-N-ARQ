import java.io.*;
import java.net.*;
import java.util.Random;

class UDPServer {
	public static void main(String args[]) throws Exception {

		int nextAck = 0;

		DatagramSocket serverSocket = new DatagramSocket(9876);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		while (nextAck < 8) {
			// Get the received packet
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);

			// Get the message from the packet
			String sentence = new String(receivePacket.getData());
			int SN = Integer.valueOf(sentence.substring(5, 6));

			// random number generator in order to simulate a timeout event
			Random random = new Random();
			int timeout = random.nextInt(100);
//			&& Integer.valueOf(sentence.substring(5, 6)) == SN)
			if ((timeout > 30)) {
//
				if (SN == nextAck) {
					System.out.println("\n\nReceived packet " + sentence);
					System.out.println("ACK Sent");
					nextAck++;
				} else {
					System.out.println("Resent Packets Received: " + sentence.substring(5, 6));
				}
//				
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				String capitalizedSentence = sentence.toUpperCase();
				sendData = capitalizedSentence.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);

			} else {
				// package drop due to timeout, no acknowledgement sent.
				System.out.println("\n\nPacket with " + sentence + " was dropped");
			}
		}
		System.out.println("All data received. exiting.");
		serverSocket.close();
	}
}