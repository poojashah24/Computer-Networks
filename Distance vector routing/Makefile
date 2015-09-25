JFLAGS = -g -classpath .:javax.json-1.0.4.jar:javax.json-api-1.0.jar
JC = javac
.SUFFIXES: .java .class
.java.class:
	 $(JC) $(JFLAGS) $*.java

CLASSES = \
	BFClient.java \
	BFNode.java \
	CloseCommand.java \
	Command.java \
	CommandEnum.java \
	Constants.java \
	DVEntry.java \
	DVListener.java \
	DVSender.java \
	IPUtils.java \
	JSONHelper.java \
	JSONTest.java \
	LinkDownCommand.java \
	LinkState.java \
	LinkUpCommand.java \
	Neighbor.java \
	RouteUpdateCommand.java \
	ShowRTCommand.java \
	TimeoutHandler.java \
	ValidationUtils.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

