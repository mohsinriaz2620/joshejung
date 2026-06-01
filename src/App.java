/**
 * App.java
 * This is a "Wrapper" class. 
 * By running THIS file instead of Main.java, we bypass 
 * the requirement for a module-info.java file and many JavaFX module layer errors.
 */
public class App {
    public static void main(String[] args) {
        try {
            System.out.println("[Josh-e-Jung] Initializing Launcher...");
            
            // This direct call to Main.main allows the JVM to treat 
            // JavaFX as a set of regular classpath libraries rather 
            // than strict modules.
            Main.main(args);
            
        } catch (Exception e) {
            System.err.println("[Launcher Error] Could not start the application.");
            e.printStackTrace();
        }
    }
}