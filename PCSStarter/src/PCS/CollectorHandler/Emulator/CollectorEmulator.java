package PCS.CollectorHandler.Emulator;

import AppKickstarter.misc.Msg;
import PCS.CollectorHandler.CollectorHandler;
import PCS.PCSStarter;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class CollectorEmulator extends CollectorHandler {
    private final PCSStarter pcsStarter;
    private final String id;
    private Stage myStage;
    private CollectorEmulatorController collectorEmulatorController;

    public CollectorEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id;
    }

    /**
     * Function to start UI
     *
     * @throws Exception Exception for starting Collector Emulator.
     */
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "CollectorEmulator.fxml";
        loader.setLocation(CollectorEmulator.class.getResource(fxmlName));
        root = loader.load();
        collectorEmulatorController = loader.getController();
        collectorEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 420, 470));
        myStage.setTitle("Collector Emulator");
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
     * Function when card inserted
     *
     * @param tID ticket id
     */
    protected void insertedTicket(String tID) {
        logFine("Ticket Inserted " + tID);
    }

    /**
     * Function to respond to admin open
     */
    protected void adminOpen() {
        logFine("Admin has Pressed");
    }

    /**
     * Function for alarm signal
     *
     * @param alarmSignal alarm status
     */
    protected void alarmSignal(boolean alarmSignal) {
        if (alarmSignal) {
            logWarning("Alarm Ringing");
        }
    }

    /**
     * Function for wrong ticket number
     */
    protected void wrongTicket() {
        logWarning("Wrong Ticket");
    }

    private final void logFine(String logMsg) {
        collectorEmulatorController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine

    //------------------------------------------------------------
    // logInfo
    private final void logInfo(String logMsg) {
        collectorEmulatorController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    } // logInfo

    //------------------------------------------------------------
    // logWarning
    private final void logWarning(String logMsg) {
        collectorEmulatorController.appendTextArea("[WARNING]: " + logMsg);
        log.warning(id + ": " + logMsg);
    } // logWarning

    //------------------------------------------------------------
    // logSevere
    private final void logSevere(String logMsg) {
        collectorEmulatorController.appendTextArea("[SEVERE]: " + logMsg);
        log.severe(id + ": " + logMsg);
    } // logSevere
}
