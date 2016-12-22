import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

/**
 * The RMI client main class, launches a client
 * @author Johan & Luther
 */
public class RmiClientMain {

    //=================
    //=== VARIABLES ===
    //=================

    private static final String CALLBACK_NAME = "johanlutherbikes";
    private static final String DEFAULT_SERVER_IP = "localhost";
    private static final int DEFAULT_RMI_PORT = 1099;

    //============
    //=== MAIN ===
    //============

    /**
     * Launches the client
     * @param args
     */
    public static void main(String args[]) {
        // Sets some system properties
        Properties props = System.getProperties();
        props.setProperty("java.security.policy", "client.policy");
        // Set up the security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            // Sets the connection variables, default or args
            String serverIP = DEFAULT_SERVER_IP;
            if(args.length >= 1){
                serverIP = args[0];
            }

            int RMIPortNum = DEFAULT_RMI_PORT;
            if(args.length >= 2){
                RMIPortNum = Integer.parseInt(args[1]);
            }

            // Attempts to connect to the server with the given connection variables
            System.out.println("Attempts to connect to " + serverIP + " on port " + RMIPortNum);
            //String registryURL = "rmi://" + serverIP + ":" + RMIPortNum + "/" + CALLBACK_NAME;

            // Find the remote server and cast it to an interface object
            Registry registry = LocateRegistry.getRegistry(serverIP, RMIPortNum);
            IServer h = (IServer) registry.lookup(CALLBACK_NAME);
            //IServer h = (IServer) Naming.lookup(registryURL);
            System.out.println("Lookup completed");

            // Launch the rmi client
            new RmiClient(h);
        }
        catch (Exception e) {
            System.out.println("Exception in Client: " + e.getMessage());
            System.exit(1);
        }
    }
}
