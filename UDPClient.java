import java.io.*;
import java.net.*;
import java.util.Scanner;

class UDPClient {
	public static void main(String args[]) throws Exception {

		// Read user input using scanner class
		Scanner inputwindow = new Scanner(System.in);  
		System.out.println("Client Ready.");
		System.out.println("------------------------");
		System.out.println("\nPlease enter window size: ");
		int N = inputwindow.nextInt();
		inputwindow.close(); //close the scanner class
		
		System.out.println("\nWINDOW SIZE = " + N);
		
		//sequence number variable - Pointer 2
		int nextseqnum = 0;
		//window slide tracker - Pointer 1
		int send_base = 0;
		// timeout variable 
		boolean timedOut = true;

		//// Read file input
		BufferedReader inFromUser = new BufferedReader(new FileReader("file.txt"));
		// Create client socket
		DatagramSocket clientSocket = new DatagramSocket();
		// get host name
		InetAddress IPAddress = InetAddress.getByName("localhost");
		// Set timer for SocketException timeout
		clientSocket.setSoTimeout(1000);

		//create byte data 
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];

		//Extract file word in this case "umbrella" into a string variable 
		StringBuilder sb = new StringBuilder();
	    String word = inFromUser.readLine();

	    while (word != null) {
	        sb.append(word);
	        sb.append(System.lineSeparator());
	        word = inFromUser.readLine();
	    }
	    String sentence = sb.toString();
	    
	    //Max variable equal to the length of the word within the file read
		int max = sentence.length();

		// sequence number comparison with max variable used to determine when to close the clientSocket
		while ((nextseqnum < (max-1)) && timedOut) {
			try {
				//Fixed Sliding window size dependent on the user's window size input
				if (nextseqnum < send_base + N) {
					
					//Concatenating the sequence number and the file string character information together to be placed into the same packet
					String seq = String.valueOf(nextseqnum + " "); // 
					seq = seq.concat(sentence.substring(nextseqnum, nextseqnum + 1));
					//length of word in file included in the packet in order for the receiver to extract and use as while loop termination.
					String packet = "SN = " + seq + " " + max;
					
					//Convert the packet containing both sequence number and string character into byte
					sendData = packet.getBytes();
					
					//sending datagram packet to the Server through the client socket
					System.out.println("\n\nSending Frame " + packet.substring(0,8));
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
					clientSocket.send(sendPacket);
					
					//Receiving datagram packet (acknowledgement) from the server socket
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					clientSocket.receive(receivePacket);
					
					//Succesfully received packets are printed on the client side as confirmation
					String data = new String(receivePacket.getData());
					System.out.println("\n\nACK Received");
					System.out.println("From Server: " + data.substring(0,8));
					
					//increment sequence number when successfully received acknowledgement from server
					nextseqnum++;
					
				// if the last frame has been sent on the sliding window, increment the sliding window position by one.
				} else if (send_base < (max - N)) {
					send_base++;
					System.out.println("\n       - Sliding Window Position: " + send_base + " -");
				}
				
				//a timeout is caught if acknowledgement is not received from the server.
			} catch (SocketTimeoutException exception) {
				// ACK not received therefore re-send data packet and already acknowledged frames.
				System.out.println("\n\n        Timeout (Sequence Number " + nextseqnum + ")");
				System.out.println("        Retransmitting Frame(s)");
				// sequence number equal to send base for retransmission of frames within the current window position 
				nextseqnum = send_base;
			}
		}
		//While loop exited, close client and Buffered reader.
		System.out.println("\n\n----------------------------------------------------------");
		System.out.println("All data succesfully sent and acknowledged. Session Closed.");
		clientSocket.close();
		inFromUser.close();
	}
}
