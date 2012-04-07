package de.doridian.yiffcraft.overrides;

import de.doridian.yiffcraft.CharPrefixTree;
import de.doridian.yiffcraft.Chat;
import de.doridian.yiffcraft.Yiffcraft;
import de.doridian.yiffcraft.commands.BaseCommand;
import net.minecraft.src.GuiChat;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class YCGuiChat extends GuiChat {
	protected String cmdHintSet;
	protected String cmdHintDraw;
	
	protected String predictedCmd;

	protected static CharPrefixTree commands;
	
	protected final HintingRefreshThread hintingRefreshThread;

	private class HintingRefreshThread extends Thread {
		private boolean gotNewMsg;
		private String lastMsg;

		public boolean running = true;

		public void newMessage() {
			synchronized (hintingRefreshThread) {
				gotNewMsg = true;
				this.notify();
			}
		}

		public void run() {
			while(running) {
				try {
					synchronized (hintingRefreshThread) {
						while(!gotNewMsg) {
							wait();
						}
					}

					gotNewMsg = false;

					if(message.equals(lastMsg)) continue;

					lastMsg = message;
					gotNewMsg();
				}
				catch(Exception e) { }
			}
		}

		private void gotNewMsg() {
			if(!message.isEmpty()) {
				String command = getCommand(message);

				CharPrefixTree.Node node = commands.get(command);
				if(node == null) {
					unsetCommandHint();
				} else if(node.desc == null) {
					unsetCommandHint();

					if(message.length() <= command.length() || message.charAt(command.length()) != ' ') {
						node = commands.getFirstEnd(command);
						if(node != null && node.value != null) {
							StringBuilder sb = new StringBuilder(predictedCmd = node.value);
							sb.insert(command.length(), "\u00a78");
							cmdHintDraw = (sb.toString() + " " + node.desc).trim();
						}
					}
				} else {
					CharPrefixTree.Node tmpNode;
					String tmpCommand = command;
					while((tmpCommand = getCommand(message, tmpCommand.length() + 2, true)) != null) {
						tmpNode = commands.get(tmpCommand);
						if(tmpNode != null && tmpNode.desc != null) {
							node = tmpNode;
							command = tmpCommand;
						}
					}

					setCommandHint(command, node.desc);
				}

				oldCmd = command;
			} else {
				oldCmd = null;
				unsetCommandHint();
			}

			refreshCommandHint();
		}
	}

	public YCGuiChat() {
		super();

		if(commands == null) {
			reloadCommands();
		}
		
		hintingRefreshThread = new HintingRefreshThread();
		hintingRefreshThread.start();
	}

	public static void reloadCommands() {
		reloadCommands(null);
	}

	public static void reloadCommands(HashMap<String, String> additionalCommands) {
		CharPrefixTree newCommands = new CharPrefixTree();

		newCommands.addAll(Yiffcraft.wecui.getLocalPlugin().getPlugin().getCommands(), '/');

		Map<String, String> tmpCommands = new HashMap<String, String>();
		for(Map.Entry<String, BaseCommand> cmdEntry : Chat.commands.entrySet()) {
			tmpCommands.put(cmdEntry.getKey(), cmdEntry.getValue().getUsage() + " - " + cmdEntry.getValue().getHelp());
		}
		newCommands.addAll(tmpCommands, '-');

		if(additionalCommands != null) {
			newCommands.addAll(additionalCommands);
		}

		commands = newCommands;
	}

	@Override
	public void drawScreen(int i, int i1, float f) {
		if(cmdHintDraw != null) {
			drawRect(2, this.height - 28, this.width - 14, this.height - 14, 0x80000000);
			drawString(this.fontRenderer, cmdHintDraw, 4, this.height - 24, 0xe0e0e0);
		}
		super.drawScreen(i, i1, f);
	}

	@Override
	protected void keyTyped(char var1, int var2) {
		super.keyTyped(var1, var2);
		
		if(predictedCmd != null && Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
			this.message = predictedCmd + " ";
			this.cursorPosition = this.message.length();
		}

		hintingRefreshThread.newMessage();
	}



	protected String oldCmd;
	protected void setCommandHint(String cmd, String cmdHint) {
		predictedCmd = null;
		if(oldCmd != null && oldCmd.equals(cmd)) return;

		cmdHintSet = (cmd + " " + cmdHint).trim();

		int cmdUsageSplit = cmdHintSet.indexOf(" - ");
		if(cmdUsageSplit >= 0) {
			cmdHintAppend = cmdHintSet.substring(cmdUsageSplit);
			cmdHintSet = cmdHintSet.substring(0, cmdUsageSplit);
		} else {
			cmdHintAppend = " " + cmdHint;
			cmdHintSet = cmd;
			cmdHintArgs = new String[] { cmd };
			cmdHintOptional = new Boolean[] { false };
			refreshCommandHint();
			return;
		}

		ArrayList<String> argTmp = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		boolean inBracket = false;
		for(int i = 0; i < cmdHintSet.length(); i++) {
			char c = cmdHintSet.charAt(i);
			if(c == '<' || c == '[') {
				inBracket = true;
			} else if(c == '>' || c == ']') {
				inBracket = false;
			} else if(c == ' ' && !inBracket) {
				if(sb.length() > 0) {
					argTmp.add(sb.toString());
					sb = new StringBuilder();
				}
				continue;
			}
			sb.append(c);
		}

		if(sb.length() > 0) argTmp.add(sb.toString());

		cmdHintArgs = argTmp.toArray(new String[argTmp.size()]);
		cmdHintOptional = new Boolean[cmdHintArgs.length];

		for(int i = 0; i < cmdHintArgs.length; i++) {
			cmdHintOptional[i] = (cmdHintArgs[i].charAt(0) == '[');
		}

		refreshCommandHint();
	}

	protected String cmdHintAppend;

	protected String[] cmdHintArgs;
	protected Boolean[] cmdHintOptional;
	
	protected void refreshCommandHint() {
		if(cmdHintSet == null) return;

		String[] args = message.trim().split(" ");
		int argc = args.length;
		if(argc > 2 || (argc > 1 && message.charAt(message.length() - 1) == ' ')) {
			String arg = args[1];
			if(arg.length() > 1 && arg.charAt(0) == '-' && !cmdHintSet.contains(" " + arg + " ")) {
				char c = arg.charAt(1);
				if(c < '0' || c > '9') {
					argc--;
				}
			}
		}
		for(String arg : args) {
			if(arg.isEmpty()) argc--;
		}

		if(message.charAt(message.length() - 1) == ' ') argc++;

		int argToHighlight = argc - 1;

		if(argToHighlight >= cmdHintArgs.length) {
			cmdHintDraw = cmdHintSet + cmdHintAppend;
			return;
		}

		int argsToHighlight = 1;
		boolean hadOptional = false;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < cmdHintArgs.length; i++) {
			boolean wasHighlighted = false;
			if(i < argToHighlight + argsToHighlight) {
				if(cmdHintOptional[i]) {
					argsToHighlight++;
				}
				if(i >= argToHighlight) {
					if(cmdHintOptional[i]) {
						if(!hadOptional) {
							sb.append("\u00a7b");
							hadOptional = true;
						}
					} else {
						sb.append("\u00a7a");
					}
				}
				wasHighlighted = true;
			}
			sb.append(cmdHintArgs[i]);
			if(wasHighlighted) {
				sb.append("\u00a7f");
			}
			sb.append(' ');
		}
		
		cmdHintDraw = sb.deleteCharAt(sb.length() - 1).append(cmdHintAppend).toString();
	}

	protected void unsetCommandHint() {
		cmdHintSet = null;
		cmdHintDraw = null;
		predictedCmd = null;
	}

	protected String getCommand(String text, int spacepos, boolean musthavespace) {
		spacepos = text.indexOf(' ', spacepos);

		text = text.toLowerCase();

		if(spacepos < 0) {
			if(musthavespace)
				return null;
			else
				return text;
		}

		return text.substring(0, spacepos);
	}

	protected String getCommand(String text) {
		return getCommand(text, 0, false);
	}
}
