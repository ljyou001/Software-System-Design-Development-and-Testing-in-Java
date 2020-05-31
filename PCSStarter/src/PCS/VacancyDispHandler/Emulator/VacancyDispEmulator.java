package PCS.VacancyDispHandler.Emulator;

import AppKickstarter.misc.Msg;
import PCS.PCSStarter;
import PCS.VacancyDispHandler.VacancyDispHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class VacancyDispEmulator extends VacancyDispHandler {
    private final String id;
    private final PCSStarter pcsStarter;
    private VacancyDispEmulatorController vacancyDispEmulatorController;
    private Stage myStage;

    /**
     * Constructor for an appThread
     *
     * @param id         name of the appThread
     * @param pcsStarter a reference to our PCSStarter
     */
    public VacancyDispEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.id = id + "Emulator";
        this.pcsStarter = pcsStarter;
    }

    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "VacancyDispEmulator.fxml";
        loader.setLocation(VacancyDispEmulator.class.getResource(fxmlName));
        root = loader.load();
        vacancyDispEmulatorController = loader.getController();
        vacancyDispEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 500, 200));
        myStage.setTitle("Vacancy Display Emulator");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    }

    //------------------------------------------------------------
    // processMsg
    protected boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {
            case UpdateDisplay:
                handleUpdateDisplay(msg);
                break;

            default:
                super.processMsg(msg);
        }
        return quit;
    } // processMsg

    protected void handleUpdateDisplay(Msg msg) {
        Platform.runLater(() -> {
            log.info(id + ": " + msg.getDetails() + " and update display.");

            String[] details = msg.getDetails().split("-");
            String currentFloor = details[1];
            String direction = details[2];
            vacancyDispEmulatorController.handleNumberUpdate(currentFloor, direction, myStage.getScene());

        });
    }

}
