package PCS.DispatcherHandler.Emulator;

import AppKickstarter.misc.Msg;
import PCS.DispatcherHandler.DispatcherHandler;
import PCS.PCSStarter;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class DispatcherEmulator extends DispatcherHandler {
    private final PCSStarter pcsStarter;
    private final String id;
    private Stage myStage;
    private DispatcherEmulatorController dispatcherEmulatorController;

    public DispatcherEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id;
    }

    /**
     * Function to start UI
     *
     * @throws Exception Exception for starting Dispatcher Emulator.
     */
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "DispatcherEmulator.fxml";
        loader.setLocation(DispatcherEmulator.class.getResource(fxmlName));
        root = loader.load();
        dispatcherEmulatorController = loader.getController();
        dispatcherEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 420, 470));
        myStage.setTitle("Dispatcher Emulator");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    }

    /**
     * Function for message process
     *
     * @param msg message from msg queue
     */
    protected final boolean processMsg(Msg msg) {
        boolean quit = false;
        quit = super.processMsg(msg);
        return quit;
    }

    /**
     * Function for print ticket
     */
    protected void printTicket() {
        logFine("Ticket was printed");
    }

    /**
     * Function for ticket was taken
     */
    protected void takeTicket() {
        logFine("Ticket was Taken");
    }

    /**
     * Function for new ticket number comes to the handler
     *
     * @param ticketNumber ticket number
     */
    protected void handleNewTicket(String ticketNumber) {
        logFine("New Ticket With Number " + ticketNumber);
    }

    private final void logFine(String logMsg) {
        dispatcherEmulatorController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine

    //------------------------------------------------------------
    // logInfo
    private final void logInfo(String logMsg) {
        dispatcherEmulatorController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    } // logInfo

    //------------------------------------------------------------
    // logWarning
    private final void logWarning(String logMsg) {
        dispatcherEmulatorController.appendTextArea("[WARNING]: " + logMsg);
        log.warning(id + ": " + logMsg);
    } // logWarning

    //------------------------------------------------------------
    // logSevere
    private final void logSevere(String logMsg) {
        dispatcherEmulatorController.appendTextArea("[SEVERE]: " + logMsg);
        log.severe(id + ": " + logMsg);
    } // logSevere
}
