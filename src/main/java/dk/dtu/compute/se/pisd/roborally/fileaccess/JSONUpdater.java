package dk.dtu.compute.se.pisd.roborally.fileaccess;

public class JSONUpdater extends Thread {

    private ClientController clientController;

    public JSONUpdater(ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    public void run() {
        while (true) {
            try {
                clientController.updateJSON("playerData.json");
                clientController.getJSON("playerData.json");

                // Sleep for 100 ms
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // This exception will be thrown if the thread is interrupted.
                // It's good practice to handle the exception, possibly by breaking the loop and stopping the thread.
                System.out.println("Thread was interrupted, Failed to complete operation");
                break;
            }
        }
    }
}