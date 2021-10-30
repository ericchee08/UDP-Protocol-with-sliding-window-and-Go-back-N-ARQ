import java.io.*;
import java.net.*;
import java.util.Scanner;

class UDPClient {
	public static void main(String args[]) throws Exception {

		
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.println("Enter window size: ");
		int N = reader.nextInt();
		reader.close();
		
		//variables
		int nextseqnum = 0;
		int send_base = 0;
		// Window size
		boolean timedOut = true;

		//// Read input
		BufferedReader inFromUser = new BufferedReader(new FileReader("file.txt"));
		// Create client socket
		DatagramSocket clientSocket = new DatagramSocket();
		// get hostname
		InetAddress IPAddress = InetAddress.getByName("localhost");
		clientSocket.setSoTimeout(1000);

		//create byte data 
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];

		//string variable for user input
		StringBuilder sb = new StringBuilder();
	    String word = inFromUser.readLine();

	    while (word != null) {
	        sb.append(word);
	        sb.append(System.lineSeparator());
	        word = inFromUser.readLine();
	    }
	    String sentence = sb.toString();

		int max = sentence.length();

		while ((nextseqnum < (max-1)) && timedOut) {
			try {
				if (nextseqnum < send_base + N) {

					String seq = String.valueOf(nextseqnum + " "); // "0 "
					seq = seq.concat(sentence.substring(nextseqnum, nextseqnum + 1));

					String packet = "SN = " + seq;
					// convert user input into byte
					sendData = packet.getBytes();

					System.out.println("\n\n Sending Frame " + packet);
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
					clientSocket.send(sendPacket);

					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					clientSocket.receive(receivePacket);

					String modifiedSentence = new String(receivePacket.getData());
					System.out.println("\n\nACK RECEIVED");
					System.out.println("FROM SERVER: " + modifiedSentence);
					nextseqnum++;

				} else if (send_base < (max - N)) {
					send_base++;
					System.out.println("Slide position:" + send_base);

				}
			} catch (SocketTimeoutException exception) {
				// ACK not received therefore resend data packet.
				System.out.println("\n\nTimeout (Sequence Number " + nextseqnum + ")");
				System.out.println("Resending Frame: " + nextseqnum);
				nextseqnum = send_base;
			}
		}
		System.out.println("All data sent. exiting.");
		clientSocket.close();
		inFromUser.close();
	}
}
