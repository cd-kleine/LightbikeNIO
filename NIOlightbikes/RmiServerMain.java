import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

/**
 * The RMI server main class, launches the server
 * @author Johan & Luther
 */
public class RmiServerMain {

    //=================
    //=== VARIABLES ===
    //=================

    private static final String CALLBACK_NAME = "johanlutherbikes";
    private static final String DEFAULT_SERVER_IP = "localhost";
    private static final int DEFAULT_RMI_PORT = 1099;

    //===================
    //=== SERVER MAIN ===
    //===================

    /**
     * Launches the RMI server
     * @param args Server IP & RMI port
     */
    public static void main(String args[]) {

        // Sets the connection variables, default or args
        String serverIP = DEFAULT_SERVER_IP;
        if(args.length >= 1){
            serverIP = args[0];
        }
        int RMIPortNum = DEFAULT_RMI_PORT;
        if(args.length >= 2){
            RMIPortNum = Integer.parseInt(args[1]);
        }

        // Set the server policy
        Properties props = System.getProperties();
        props.setProperty("java.security.policy", "server.policy");
        System.setProperty("java.rmi.server.hostname", serverIP);

        // Set up the security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        String registryURL;
        try{
            // Try to start the registry
            System.out.println("Setup server on " + serverIP + " on port " + RMIPortNum);
            // Create the RMI server instance to be given as stub to the clients
            //startRegistry(RMIPortNum);
            //RmiServer exportedObj = new RmiServer();

            IServer stub = new RmiServer();
            //IServer stub = (IServer) UnicastRemoteObject.exportObject(server, 0);
            Registry registry;
            try {
                registry = LocateRegistry.getRegistry(RMIPortNum);
                registry.list( );
                // This call will throw an exception
                // if the registry does not already exist
            }
            catch (RemoteException e) {
                // No valid registry at that port.
                registry = LocateRegistry.createRegistry(RMIPortNum);
            }
            registry.rebind(CALLBACK_NAME, stub);

            // Rebind
            //registryURL = "rmi://" + serverIP + ":" + RMIPortNum + "/" + CALLBACK_NAME;
            //Naming.rebind(registryURL, exportedObj);
            System.out.println("Server ready");
        }
        catch (Exception e) {
            System.out.println("Exception in Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //====================
    //=== RMI Registry ===
    //====================

    /**
     * This method starts a RMI registry on the local host, if
     * it does not already exists at the specified port number.
     * @param RMIPortNum The RMI port number
     * @throws RemoteException
     */
    private static void startRegistry(int RMIPortNum) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list( );
            // This call will throw an exception
            // if the registry does not already exist
        }
        catch (RemoteException e) {
            // No valid registry at that port.
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
        }
    }

}
