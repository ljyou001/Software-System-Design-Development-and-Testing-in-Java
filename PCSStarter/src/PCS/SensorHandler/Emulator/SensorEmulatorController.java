package PCS.SensorHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.util.logging.Logger;

public class SensorEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private SensorEmulator sensorEmulator;
    private MBox sensorMBox;

    public void initialize(String id, AppKickstarter appKickstarter, Logger log, SensorEmulator sensorEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.sensorEmulator = sensorEmulator;
        if (appKickstarter.getThread("SensorHandler") != null) {
            this.sensorMBox = appKickstarter.getThread("SensorHandler").getMBox();
        }
    }

    public void buttonPressed(ActionEvent actionEvent) {
        Button bu = (Button) actionEvent.getSource();
        String buTxt = bu.getText();
        String buID = bu.getId();
        if (!buTxt.isEmpty()) {
            sensorMBox.send(new Msg(id, sensorMBox, Msg.Type.CarPassThrough, buID));
        }
    }
}
