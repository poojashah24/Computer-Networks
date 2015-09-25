JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	 $(JC) $(JFLAGS) $*.java

CLASSES = \
	Server.java \
	ServerThreadFactory.java \
	ChatUser.java \
	Constants.java \
	Command.java \
	State.java \
	PresenceStatus.java \
	OfflineMessage.java \
	ClientConnectionHandler.java \
	BlockedConnectionCleanupTask.java \
	Reader.java \
	Client.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

