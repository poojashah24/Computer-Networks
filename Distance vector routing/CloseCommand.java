
public class CloseCommand extends Command {

	public CloseCommand()
	{
		commandEnum = CommandEnum.CLOSE;
	}

	@Override
	public void execute() {
		
		DVState.waitForCompletion();
		DVState.getListener().setDone();
		/*try
		{
			for(Neighbor neighbor : DVState.getNeighbors().values())
			{
				DVSenderUtil.sendLinkDownMessage(neighbor);
			}
			Thread.sleep(1000);
			DVState.waitForCompletion();
			DVState.getListener().setDone();
		}
		catch (InterruptedException ie)
		{
			
		}*/
	}
}
