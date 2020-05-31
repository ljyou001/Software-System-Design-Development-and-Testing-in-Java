package PCS.VacancyDispHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.logging.Logger;

public class VacancyDispEmulatorController {

    @FXML
    TextField floor1;
    @FXML
    TextField floor2;
    @FXML
    TextField floor3;
    @FXML
    TextField floor4;
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private VacancyDispEmulator vacancyDispEmulator;
    private MBox vacancyDispMBox;

    /**
     * Initialize the emulator controller of .fxml.
     *
     * @param id                  Thread id
     * @param appKickstarter      A reference to Appkickstarter
     * @param log                 A reference to Logger
     * @param vacancyDispEmulator A reference to VacancyDispEmulator
     */
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, VacancyDispEmulator vacancyDispEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.vacancyDispEmulator = vacancyDispEmulator;
        if (appKickstarter.getThread("VacancyDispHandler") != null) {
            this.vacancyDispMBox = appKickstarter.getThread("VacancyDispHandler").getMBox();
        }
        floor1.setText(this.appKickstarter.getProperty("Park.floorQuota"));
        floor2.setText(this.appKickstarter.getProperty("Park.floorQuota"));
        floor3.setText(this.appKickstarter.getProperty("Park.floorQuota"));
        floor4.setText(this.appKickstarter.getProperty("Park.floorQuota"));
    }

    /**
     * A function of updating the display number
     * of the vacancy display emulator.
     *
     * @param current   The number of current floor.
     * @param direction The moving direction of car
     * @param scene     The scene of the vacancy display emulator.
     */
    protected void handleNumberUpdate(String current, String direction, Scene scene) {
        String currentFloor = "floor" + current;
        String nextFloor = "floor";
        TextField tfCurrent;
        TextField tfNext;
        switch (direction) {
            case "up":
                nextFloor += String.valueOf(Integer.parseInt(current) + 1);
                break;
            case "down":
                nextFloor += String.valueOf(Integer.parseInt(current) - 1);

        }
        tfCurrent = (TextField) scene.lookup("#" + currentFloor);
        tfNext = (TextField) scene.lookup("#" + nextFloor);

        if (Integer.parseInt(tfCurrent.getText()) < Integer.parseInt(this.appKickstarter.getProperty("Park.floorQuota"))) {
            tfCurrent.setText(String.valueOf(Integer.parseInt(tfCurrent.getText()) + 1));
        }
        if (Integer.parseInt(tfNext.getText()) > 0) {
            tfNext.setText(String.valueOf(Integer.parseInt(tfNext.getText()) - 1));
        }
    }

    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getId()) {
            case "resetF1":
                floor1.setText(this.appKickstarter.getProperty("Park.floorQuota"));
                break;

            case "resetF2":
                floor2.setText(this.appKickstarter.getProperty("Park.floorQuota"));
                break;

            case "resetF3":
                floor3.setText(this.appKickstarter.getProperty("Park.floorQuota"));
                break;

            case "resetF4":
                floor4.setText(this.appKickstarter.getProperty("Park.floorQuota"));
                break;

            case "resetAll":
                floor1.setText(this.appKickstarter.getProperty("Park.floorQuota"));
                floor2.setText(this.appKickstarter.getProperty("Park.floorQuota"));
                floor3.setText(this.appKickstarter.getProperty("Park.floorQuota"));
                floor4.setText(this.appKickstarter.getProperty("Park.floorQuota"));
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed
}
