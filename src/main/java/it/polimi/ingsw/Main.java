package it.polimi.ingsw;

import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.server.Server;

/**
 * The class containing the first main method called when running the jar file
 */
public abstract class Main {

   public static final String helpString;
   public static final String errorString;

   static {
      helpString =   "Usage: java -jar MoR.jar [OPTIONS]\n" +
                     "Option   Long option     Meaning" +
                     "-s        --server       start the game server\n" +
                     "-g        --gui          start the game client whit GUI\n" +
                     "-c        --cli          start the game client in CLI\n" +
                     "With no argument will be started the game client in CLI mode.";

      errorString =  "MoR: unrecognized options\n" +
                     "Type 'java -jar MoR.jar -h' for a list of available options.";
   }

   /**
    * The first method called when running the jar file.
    * Checks the program arguments and calls the correct methods.
    *
    * @param args arguments to specify to run the server ("-s"), the client in GUI ("-g") or the client in CLI ("-c")
    */
   public static void main(String[] args) {
      boolean err = true;

      if(args.length == 0) {
         err = false;
         Client.main(new String[]{}); //runs client in GUI mode
      }

      if(args.length == 1) {
         if (args[0].equals("-g") || args[0].equals("--gui")) {
            err = false;
            Client.main(new String[]{}); //run client in GUI mode
         }

         if (args[0].equals("-c") || args[0].equals("--cli")) {
            err = false;
            Client.main(new String[]{"cli"}); //run client in CLI mode
         }

         if (args[0].equals("-s") || args[0].equals("--server")) {
            err = false;
            Server.main(new String[]{}); //run server
         }

         if (args[0].equals("-h") || args[0].equals("--help")) {
            err = false;
            System.out.print(helpString);
         }
      }

      if(err)
         System.out.print(errorString);
   }

}
