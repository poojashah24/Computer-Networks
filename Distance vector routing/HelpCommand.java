import java.io.IOException;


public class HelpCommand extends Command {

	@Override
	public void execute() throws IOException {
		for(CommandEnum cmdEnum : CommandEnum.values())
		{
			if(!cmdEnum.desc().isEmpty())
				System.out.println(cmdEnum.name() + ":" + cmdEnum.desc());
		}

	}

}
