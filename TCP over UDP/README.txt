TCP Segment Structure - 
----------------------------------
short sourcePortNumber - 16 bit source port number
short destinationPortNumber - 16 bit destination port number
int sequenceNumber - 32-bit sequence number	
int ackNumber - 32-bit acknowledgment number
short receiveWindow - 16-bit receive window
byte[] internetChecksum - 16-bit checksum
short offsetAndFlags - Combines the offset (header length), and the flags. Bit operations are used to read / set the flags.

Program Features - 
-------------------------------------
1. The sequence number sent by the sender is the first byte in the packet sent (similar to TCP implementation).
2. The sequence number sent by the received in an ACK is the next byte expected (similar to TCP implementation).
3. Timeout is dynamically calculated using the EWMA calculation.
4. Corruption and packet loss is handled by retransmission.

Additional Features - 
--------------------------------------
1. The sequence numbering is done identical to that followed by TCP
2. ACKs are also TCP segments, with a TCP header with the ACK bit set, and enclosed with a TCP segment.

States visited by sender and receiver - 
--------------------------------------
The typical workflow is as follows - 
1. The receiver starts up and listens for data
2. The sender starts transmitting data to the receiver
3. After transmitting data, the sender blocks, waiting for an ACK from the receiver
4. The receiver receives the data, and checks for sequence number and corruption. If the packet is the expected packet, and is not corrupted, the receiver sends an ACK with the next expected seq number, else it sends an ACK with the previous sequence number again.
5. The sender waits until an ACK acknowledging correct receipt is received, or timeout occurs.
6. In case of correct transmission, the sender sends the next packet.
7. In case of a timeout, the sender retransmits the earlier packet.
8. In case of corruption, the sender waits until timeout, and then retransmits the earlier packet.

Description of the code
-----------------------
The code is divided into 2 parts - the sender and the receiver

The sender application has 2 main source files, and several other supporting files - 

TCP Structure files -
1. TCPHeader.java - This file is used to define the 20 byte TCP header. The offset and flags have been combined.
3. TCPSegment.java - This file is used to define the 596 byte TCP segment (20 byte header + 576 byte data)

Sender files - 
1. TCPSender.java - 
- This contains the main transmission logic. A UDP socket is created to establish a connection to the receiver, and transmit data.
- The entry point in this file takes in data in chunks of 576 bytes, creates a TCP header, adds the header to a TCP segment, encapsulates the TCP segment in a UDP datagram and transmits the data.
- It keeps track of the RTT, and calculates timeout using the RTTEstimator.
- In case of timeout, the packet is retransmitted.
- In case of corruption, no action is taken until timeout occurs, and then the packet is retransmitted.
- After the last chunk of data has been transmitted, a FIN packet is sent in order to indicate end of transmission.
- The sender accepts the next data from the upper layer only after the current data has been successfully transmitted.

2. ACKReceiver.java - This is a seperate thread started to receive the ACK messages from the receiver. The messages are transmitted over TCP. ACK messages are in the same form as other messages i.e. ACK messages also have a TCP header, and enclosed within a TCP segment. ACKs are considered to be cumulative.

3. BitHelper.java - This class helps with bit manipulations needed in order to set/unset flags such as ACK/FIN.

4. DataProvider.java - This class acts as the upper layer application sending data to the TCPSender. It reads the file to be transmitted, and invokes the TCP sender with chunks of data.

5. RTTEstimator.java - This class is used to help with RTT estimationa as per the Estimated Weighted Moving Average calculation.
We start with an initial RTT value of 100ms, which is adjusted as per EWMA. The initial value of timeout is also 100ms.

6. Sender.java - This class is the entry point into the sender application.

Receiver files -
Receiver.java - This class contains the entire receiver business logic. It accepts data via UDP, saves the data, and sends ACKs via TCP. Out-of-order packets are rejected, and ACKs are considered to be cumulative.

Common utility files - 
1. ChecksumHelper.java - This class is used by the sender/receiver to calculate/verify checksum

2. Constants.java - This class contains all the constants defined in the application.

3. LogManager.java - This class provides a general purpose log manager that is used to log to console or file.

4. Serializer.java - This class serializes / deserialized a TCP segment, i.e. converts into bytes. Java serialization is NOT used, ByteBuffer is used in order to retain the header size of 20 bytes.

5. Utils.java - This class provides a general purpose utility to trim a byte buffer, used by both the sender and receiver.

Details of development environment
----------------------------------
- The code has been developed in Java, and is compliant with Java version 1.6.
- It does not use any features of Java7/8.
- Eclipse IDE was used as the development/debugging environment.
- A makefile has been placed alongside the source file. The makefile generates class files for all the java files.
- An additional file File.txt has been used as the file to be sent.

Instructions on how to run the code
-----------------------------------
- There are 2 applications to be run - sender and receiver
- The sender can be started by invoking the command
"java Sender <filename> <remote_IP> <remote_port> <ack_port_num> <log_filename>"
-The receiver can be started by invoking the command
"java Receiver <filename> <listening_port> <sender_IP> <sender_port> <log_filename>", where ip, port are the ipaddress and port of the server resp.
