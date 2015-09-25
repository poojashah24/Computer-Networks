public enum CommandEnum {
	ROUTE_UPDATE(""), LINKDOWN(Constants.LINKDOWN), LINKUP(Constants.LINKUP), SHOWRT(
			Constants.SHOWRT), CLOSE(Constants.CLOSE), HELP("");

	private String desc;

	private CommandEnum(String desc) {
		this.desc = desc;
	}
	
	public String desc()
	{
		return this.desc;
	}

}
