package PCS.DispatcherHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;


public class DispatcherHandler extends AppThread {
    protected final MBox pcsCore;

    /**
     * Constructor for the Handler
     *
     * @param id             id of this device
     * @param appKickstarter appKickstarter
     */
    public DispatcherHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
    }

    /**
     * Function for running the thread
     */
    public void run() {
        Thread.currentThread().setName(id);
        log.info(id + ": starting...");
        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();
            log.fine(id + ": message received: [" + msg + "].");
            quit = processMsg(msg);
        }
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    }

    /**
     * Function for message process
     *
     * @param msg message from msg queue
     * @return the flag of quit
     */
    protected boolean processMsg(Msg msg) {
        boolean quit = false;
        switch (msg.getType()) {
            case DispatcherCreateTicket:
                pcsCore.send(new Msg(id, mbox, Msg.Type.DispatcherCreateTicket, msg.getDetails()));
                printTicket();
                break;
            case DispatcherTakeTicket:
                pcsCore.send(new Msg(id, mbox, Msg.Type.DispatcherTakeTicket, msg.getDetails()));
                takeTicket();
                break;
            case DispatcherGetNewTicketNumber:
                handleNewTicket(msg.getDetails());
                break;
            case Poll:
                pcsCore.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is up!"));
                break;
            case Terminate:
                quit = true;
                break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
        return quit;
    }

    /**
     * Function for print ticket
     */
    //Dispatcher prepares the ticket
    protected void printTicket() {
        log.info(id + ": Ticket Printed");
    }

    /**
     * Function for ticket was taken
     */
    //Driver collects the ticket from the dispatcher, and enters the parking lot
    protected void takeTicket() {
        log.info(id + ": Ticket was taken and door opened");
    }

    /**
     * Function for new ticket number comes to the handler
     *
     * @param ticketNumber ticket number
     */
    protected void handleNewTicket(String ticketNumber) {
        log.info(id + ": New Ticket With Number " + ticketNumber);
    }
}
