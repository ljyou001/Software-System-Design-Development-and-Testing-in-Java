package PCS.SensorHandler.Emulator;

import PCS.PCSStarter;
import PCS.SensorHandler.SensorHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


public class SensorEmulator extends SensorHandler {
    private final String id;
    private final PCSStarter pcsStarter;
    private SensorEmulatorController sensorEmulatorController;
    private Stage myStage;

    /**
     * Constructor for an appThread
     *
     * @param id         name of the appThread
     * @param pcsStarter a reference to our PCSStarter
     */
    public SensorEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.id = id + "Emulator";
        this.pcsStarter = pcsStarter;
    }

    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "SensorEmulator.fxml";
        loader.setLocation(SensorEmulator.class.getResource(fxmlName));
        root = loader.load();
        sensorEmulatorController = loader.getController();
        sensorEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 500, 200));
        myStage.setTitle("Sensor Emulator");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    }

}
