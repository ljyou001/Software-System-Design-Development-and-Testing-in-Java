package PCS.PayMachineHandler.Emulator;

import PCS.PCSStarter;
import PCS.PayMachineHandler.PayMachineHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class PayMachineEmulator extends PayMachineHandler {
    private final String id;
    private final PCSStarter pcsStarter;
    private PayMachineEmulatorController payMachineEmulatorController;
    private Stage myStage;
    private String[] tickDetails;

    /**
     * Constructor for an appThread
     *
     * @param id         name of the appThread
     * @param pcsStarter a reference to our PCSStarter
     */
    public PayMachineEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.id = id + "Emulator";
        this.pcsStarter = pcsStarter;
    }

    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "PayMachineEmulator.fxml";
        loader.setLocation(PayMachineEmulator.class.getResource(fxmlName));
        root = loader.load();
        payMachineEmulatorController = loader.getController();
        payMachineEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 350, 470));
        myStage.setTitle("Pay Machine Emulator");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });

        myStage.show();
    }

    /**
     * This method is used to check whether the inserted tick exists or not
     */

    // ------------------------------------------------------------
    // handleTicketInsert
    protected void handleTicketInsert() {
        super.handleTicketInsert();
        payMachineEmulatorController.appendTextArea("Ticket Inserted\n");
    } // handleTicketInsert

    /**
     * This method is used to remove the ticket from pay machine
     */

    // ------------------------------------------------------------
    // handleTicketRemove
    protected void handleTicketRemove() {
        super.handleTicketRemove();
        payMachineEmulatorController.appendTextArea("Please collect your ticket\n");
    } // handleTicketRemove

    /**
     * This method is used to handle the invalid ticket
     */
    // ------------------------------------------------------------
    // handleErrorTicket
    protected void handleErrorTicket() {
        super.handleErrorTicket();
        payMachineEmulatorController.appendTextArea("Ticket does not exist!\n");
    } // handleErrorTicket

    /**
     * This method is used to process the valid ticket
     */
    // ------------------------------------------------------------
    // handleErrorTicket
    protected void handleSuccessTicket() {
        super.handleSuccessTicket();
        payMachineEmulatorController.appendTextArea("Pay Successfully!\n");
    } // handleSuccessTicket

    /**
     * This method is used to print the ticket information that received from PCS,
     * Then print the information on screen
     */
    // ------------------------------------------------------------
    // handlePrintTicketInfo
    protected void handlePrintTicketInfo(String ticketinfo) {
        super.handlePrintTicketInfo(ticketinfo);
        if (ticketinfo != "") {
            payMachineEmulatorController.appendTextArea("Total parking fee is: " + ticketinfo);
        }
    } // handlePrintTicketInfo


} // PayMachineEmulator

