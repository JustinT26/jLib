public class Launcher {
    public static void main(String[] args) {
        try {
            // launch server
            ProcessBuilder pb1 = new ProcessBuilder("java", "-jar", "jars/finalProject_server.jar");
            pb1.inheritIO();
            pb1.start();

            // launch client
            ProcessBuilder pb2 = new ProcessBuilder("java", "-jar", "jars/finalProject_client.jar");
            pb2.inheritIO();
            pb2.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
