import java.net.*;
import java.util.Random;

class UDPServer {
	public static void main(String args[]) throws Exception {
		
		//newAck variable keeps track of only new acknowledge/sequence numbers to be received to ensure reliable transfer 
		int newAck_Seq = 0;
		 //Max variable used to exit while loop - receives the string length from the client.
		int max = 99999;
		
		//Create server socket
		DatagramSocket serverSocket = new DatagramSocket(9876);
		System.out.println("Server Ready: waiting for packets...");
		System.out.println("------------------------------------");
		
		//create byte data 
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		while (newAck_Seq < (max-1)) {
			
			//Get the transmitted packet from the client 
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			
			//Get packet's IP and port
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();

			//Get the message from the packet
			String sentence = new String(receivePacket.getData());
			int SN = Integer.valueOf(sentence.substring(5, 6));

			//Random number generator in order to simulate a timeout event
			Random random = new Random();
			int timeout = random.nextInt(100);

			//25% timeout rate
			if ((timeout > 25)) {
				//Extract word length from the packet and set as max variable for while loop exit.
				max = Integer.valueOf(sentence.substring(9, 10));
				
				//Compare the sequence number with the ack/SN tracker, if the same increment by 1 for the next packet comparison.
				if (SN == newAck_Seq) {
					System.out.println("\n\nReceived packet " + sentence.substring(0,8));
					System.out.println("ACK Sent");
					newAck_Seq++;
					
				//If the sequence number comparison is not the same on receiver and server, do not increment ack/seq tracker
				} else {
					System.out.println("        Retransmitted Frames Received: " + sentence.substring(5, 6));
				}
				
				//Convert the packet containing both sequence number and string character into byte as ACK information.
				String data = sentence;
				sendData = data.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
				
			//failure of receiving transmission from client results in packet drop/lost.
			} else {
				//Extract word length from the packet and set as max variable for while loop exit.
				max = Integer.valueOf(sentence.substring(9, 10));
				// package drop due to timeout, no acknowledgement sent.
				System.out.println("\n\n        Packet with " + sentence.substring(0,8) + " was dropped");
			}
		}
		//While loop exited, close server session.
		System.out.println("\n\n--------------------------------------");
		System.out.println("All data received. exiting. Session Closed");
		serverSocket.close();
	}
}