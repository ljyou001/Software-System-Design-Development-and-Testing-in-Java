package PCS.DispatcherHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.logging.Logger;

public class DispatcherEmulatorController {
    public TextArea dispatcherTextArea;
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private DispatcherEmulator dispatcherEmulator;
    private MBox dispatcherHandlerMBox;
    private int lineNo = 0;

    /**
     * constructor for initialize
     *
     * @param id                 message from msg queue
     * @param appKickstarter     appKickstarter
     * @param log                log
     * @param dispatcherEmulator dispatcherEmulator
     */
    public void initialize(String id, AppKickstarter appKickstarter,
                           Logger log, DispatcherEmulator dispatcherEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.dispatcherEmulator = dispatcherEmulator;
        this.dispatcherHandlerMBox = appKickstarter.getThread("DispatcherHandler").getMBox();
    }

    /**
     * Function for button pressed
     *
     * @param actionEvent key event
     */
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();
        switch (btn.getText()) {
            case "Give Me a Ticket":
                appendTextArea("Ticket Printed");
                dispatcherHandlerMBox.send(new Msg(id, null, Msg.Type.DispatcherCreateTicket, ""));
                break;
            case "Take the Ticket":
                appendTextArea("Ticket Taken");
                dispatcherHandlerMBox.send(new Msg(id, null, Msg.Type.DispatcherTakeTicket, ""));
                break;
            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    }

    /**
     * Function for append to text area
     *
     * @param status word log
     */
    public void appendTextArea(String status) {
        Platform.runLater(() -> dispatcherTextArea.appendText(String.format("[%04d] %s\n", ++lineNo, status)));
    }

}
