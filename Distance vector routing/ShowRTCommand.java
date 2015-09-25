
public class ShowRTCommand extends Command {
	public ShowRTCommand()
	{
		commandEnum = CommandEnum.SHOWRT;
	}

	@Override
	public void execute() {
		DVState.printRoutingTable();
	}
}
