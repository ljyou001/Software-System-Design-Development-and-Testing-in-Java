package PCS.CollectorHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.logging.Logger;

public class CollectorEmulatorController {
    public TextArea ticketId;
    public TextArea collectorTextArea;
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private CollectorEmulator collectorEmulator;
    private MBox collectorHandlerMBox;
    private int lineNo = 0;

    /**
     * constructor for initialize
     *
     * @param id                message from msg queue
     * @param appKickstarter    appKickstarter
     * @param log               log
     * @param collectorEmulator dispatcherEmulator
     */
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CollectorEmulator collectorEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.collectorEmulator = collectorEmulator;
        this.collectorHandlerMBox = appKickstarter.getThread("CollectorHandler").getMBox();
    }

    /**
     * Function for button pressed
     *
     * @param actionEvent key event
     */
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "Insert":
                if (ticketId.getText().length() != 0) {
                    appendTextArea("Ticket: " + ticketId.getText());
                    collectorHandlerMBox.send(new Msg(id, null, Msg.Type.CollectorInsertTicket, ticketId.getText()));
                } else {
                    appendTextArea("Insert Your Card");
                }
                break;
            case "Admin Pass":
                collectorHandlerMBox.send(new Msg(id, null, Msg.Type.AdminOpen, ticketId.getText()));
                appendTextArea("Admin have asked for ack signal");
                break;
            default:
                log.warning(id + "unknown");
                break;
        }
    }

    /**
     * Function for append to text area
     *
     * @param status word log
     */
    public void appendTextArea(String status) {
        Platform.runLater(() -> collectorTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    }
}
