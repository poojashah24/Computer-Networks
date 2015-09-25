JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	 $(JC) $(JFLAGS) $*.java

CLASSES = \
	ACKReceiver.java \
	BitHelper.java \
	ChecksumHelper.java \
	Constants.java \
	DataProvider.java \
	LogManager.java \
	RTTEstimator.java \
	Sender.java \
	Serializer.java \
	TCPHeader.java \
	Receiver.java \
	TCPSegment.java \
	TCPSender.java \
	Utils.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

