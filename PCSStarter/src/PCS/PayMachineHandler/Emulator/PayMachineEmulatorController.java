package PCS.PayMachineHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class PayMachineEmulatorController {
    public TextField ticketNumField;
    public TextArea ticketReaderTextArea;
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private PayMachineEmulator payMachineEmulator;
    private LocalDateTime currentTime;
    private MBox PayMachineMBox;
    private boolean insert;

    public void initialize(String id, AppKickstarter appKickstarter, Logger log, PayMachineEmulator payMachineEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.insert = false;
        this.payMachineEmulator = payMachineEmulator;
        if (appKickstarter.getThread("PayMachineHandler") != null) {
            this.PayMachineMBox = appKickstarter.getThread("PayMachineHandler").getMBox();
        }
    }

    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();
        //String buTxt = bu.getText();
        //String buID = bu.getId();

        switch (btn.getText()) {

            //Clean up the input field
            case "Reset":
                ticketNumField.setText("");
                break;

            //Type the ticket num and check whether the ticket exists or not, if yes, ticket can be inserted
            case "Insert Ticket":
                if (ticketNumField.getText().length() != 0 && ticketNumField.getText().length() < 10 && insert == false) {
                    ticketReaderTextArea.appendText("Sending ticket [" + ticketNumField.getText() + "] to PCS for checking." + "\n");
                    PayMachineMBox.send(new Msg(id, PayMachineMBox, Msg.Type.PayMachineInsertTicket, ticketNumField.getText()));
                    insert = true;
                }
                break;

            //Remove the ticket from pay machine
            case "Remove Ticket":
                if (insert == true) {
                    ticketReaderTextArea.appendText("Ticket removed\n");
                    PayMachineMBox.send(new Msg(id, PayMachineMBox, Msg.Type.PayMachineRemoveTicket, ticketNumField.getText()));
                    insert = false;
                }
                break;

            //Process the payment function
            case "Pay":
                if (insert == true) {
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    this.currentTime = now;
                    String paymentTime = df.format(now);
                    ticketReaderTextArea.appendText("Current time is : " + paymentTime + "\n");

                    PayMachineMBox.send(new Msg(id, PayMachineMBox, Msg.Type.PayMachinePayment, ticketNumField.getText()));
                }
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    }

    //Append a text on the screen
    public void appendTextArea(String status) {
        ticketReaderTextArea.appendText(status + "\n");
    } // appendTextArea

}