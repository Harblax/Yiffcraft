package de.doridian.yiffcraft;

import org.lwjgl.input.Keyboard;

public class KeyMacro {

   public String keyMacro;
   public int keyCode;
   public boolean keyRepeatable;

   public KeyMacro(String macro, int code) {
	   this.keyMacro = macro;
	  	this.keyCode = code;
	  	this.keyRepeatable = false;
   }
   
   public KeyMacro(String macro, int code, boolean repeatable) {
		this.keyMacro = macro;
		this.keyCode = code;
		this.keyRepeatable = repeatable;
   }
   
   public void run() {
	   if(Macros.disableMacros) return;
	   
	   if(keyMacro.startsWith(".") || keyMacro.startsWith("-irc") || keyMacro.startsWith("-pm"))
	   {
		   Chat.addChat("Warning you cannot send irc messages inside of macros");
		   //Yiffcraft.jabberClient.sendLogMessage("Attempted to spam Jabber with "+keyMacro);
		   return;
	   }
	   Chat.emitChatMsg(keyMacro);
   }
   
   public String keyName() {
	   return Keyboard.getKeyName(keyCode);
   }
   
   public String toString() {
	   return keyCode + "=" + keyRepeatable + "=" + keyMacro;
   }
}