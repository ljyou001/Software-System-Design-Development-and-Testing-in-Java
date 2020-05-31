package PCS.PCSCore;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;
import PCS.Ticket;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


//======================================================================
// PCSCore
public class PCSCore extends AppThread {
    private final int pollTime;
    private final int PollTimerID = 1;
    private final int openCloseGateTime;        // for demo only!!!
    private final int OpenCloseGateTimerID = 2;        // for demo only!!!
    private final Map<Integer, Ticket> ticketMap = new HashMap<>();
    private MBox gateMBox;
    private MBox payMachineMBox;
    private MBox dispatcherMBox;
    private MBox collectorMBox;
    private MBox vacancyDispMBox;
    private boolean gateIsClosed = true;        // for demo only!!
    private boolean isDemo = false;


    //------------------------------------------------------------
    // PCSCore
    public PCSCore(String id, AppKickstarter appKickstarter) throws Exception {
        super(id, appKickstarter);
        this.pollTime = Integer.parseInt(appKickstarter.getProperty("PCSCore.PollTime"));
        this.openCloseGateTime = Integer.parseInt(appKickstarter.getProperty("PCSCore.OpenCloseGateTime"));        // for demo only!!!
        if (appKickstarter.getProperty("demo").equals("on")) {
            this.isDemo = true;
        }
    } // PCSCore


    //------------------------------------------------------------
    // run
    public void run() {
        Thread.currentThread().setName(id);
        Timer.setTimer(id, mbox, pollTime, PollTimerID);
        if (isDemo) {
            Timer.setTimer(id, mbox, openCloseGateTime, OpenCloseGateTimerID);
        }
        log.info(id + ": starting...");

        gateMBox = appKickstarter.getThread("GateHandler").getMBox();
        dispatcherMBox = appKickstarter.getThread("DispatcherHandler").getMBox();
        collectorMBox = appKickstarter.getThread("CollectorHandler").getMBox();
        payMachineMBox = appKickstarter.getThread("PayMachineHandler").getMBox();
        vacancyDispMBox = appKickstarter.getThread("VacancyDispHandler").getMBox();

        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                case TimesUp:
                    handleTimesUp(msg);
                    break;
                /* For message from Gate */
                case GateOpenReply:
                    log.info(id + ": Gate is opened.");
                    gateIsClosed = false;
                    if(!isDemo){
                        gateMBox.send(new Msg(id, mbox, Msg.Type.GateCloseRequest, ""));
                    }
                    break;

                case GateCloseReply:
                    log.info(id + ": Gate is closed.");
                    gateIsClosed = true;
                    break;

                case OpenSignal:
                    log.info(id + ": sending gate open signal to hardware.");
                    break;

                case CloseSignal:
                    log.info(id + ": sending gate close signal to hardware.");
                    break;

                case sendPollSignal:
                    log.info(id + ": poll request received.");
                    break;

                case PollAck:
                    log.info("PollAck: " + msg.getDetails());
                    break;

                /* For message from Dispatcher */
                case DispatcherCreateTicket:
                    log.info(id + ": ready to create a ticket.");
                    handleCreateTicket();
                    break;

                case DispatcherTakeTicket:
                    log.info(id + ": ticket was taken from Dispatcher.");
                    gateMBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, ""));
                    break;

                /* For message from PayMachine */
                case PayMachineInsertTicket:
                    log.info(id + ": ticket is inserted.");
                    handlePayMachineInsertTicket(msg.getDetails());
                    payMachineMBox.send(new Msg(id, mbox, Msg.Type.PrintTicketInfo, ""));
                    break;

                case PayMachinePayment:
                    log.info(id + ": payment is made.");
                    handlePayment(msg.getDetails());
                    break;

                case PayMachineRemoveTicket:
                    log.info(id + ": ticket is removed.");
                    handlePayMachineRemoveTicket(msg.getDetails());
                    break;

                /* For message from Collector */
                case CollectorInsertTicket:
                    log.info(id + ": ticket was taken by Collector.");
                    handleCollectorInsertTicket(msg.getDetails());
                    break;

                case AdminOpen:
                    log.info(id + ": admin operates.");
                    handleAdminOpen(msg.getDetails());
                    break;

                /* For message from Vacancy Display */
                case CarPassThrough:
                    log.info(id + " One car passes through the sensor.");
                    vacancyDispMBox.send(new Msg(id, mbox, Msg.Type.UpdateDisplay, msg.getDetails()));
                    break;

                /**/

                case Terminate:
                    quit = true;
                    break;

                default:
                    log.warning(id + ": unknown message type: [" + msg + "]");
            }
        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run


    //------------------------------------------------------------
    // handleTimesUp
    private void handleTimesUp(Msg msg) {
        log.info("------------------------------------------------------------");
        switch (Timer.getTimesUpMsgTimerId(msg)) {
            case PollTimerID:
                log.info("Poll: " + msg.getDetails());
                gateMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                Timer.setTimer(id, mbox, pollTime, PollTimerID);
                break;

            case OpenCloseGateTimerID:                    // for demo only!!!
                if (gateIsClosed) {
                    log.info(id + ": Open the gate now (for demo only!!!)");
                    gateMBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, ""));
                } else {
                    log.info(id + ": Close the gate now (for demo only!!!)");
                    gateMBox.send(new Msg(id, mbox, Msg.Type.GateCloseRequest, ""));
                }
                Timer.setTimer(id, mbox, openCloseGateTime, OpenCloseGateTimerID);
                break;

            default:
                log.severe(id + ": why am I receiving a timeout with timer id " + Timer.getTimesUpMsgTimerId(msg));
                break;
        }
    } // handleTimesUp

    //------------------------------------------------------------
    // handleCreateTicket

    /**
     * A function of handling the creation of a ticket.
     */
    private void handleCreateTicket() {
        Date enterTime = new Date();
        Ticket newTicket = new Ticket(enterTime);
        ticketMap.put(newTicket.getTicketNumber(), newTicket);
        log.info(id + ": " + newTicket.toString() + " is created");
        dispatcherMBox.send(new Msg(id, mbox, Msg.Type.DispatcherGetNewTicketNumber, String.valueOf(newTicket.getTicketNumber())));
    } // handleCreateTicket

    //------------------------------------------------------------
    // handlePayMachineInsertTicket

    /**
     * A function of handling the insertion
     * of the ticket from Pay Machine Emulator.
     *
     * @param ticketNumber The number for identifying the ticket.
     */
    private void handlePayMachineInsertTicket(String ticketNumber) {
        try {
            Ticket ticket = ticketMap.get(Integer.parseInt(ticketNumber));
            // Validate whether the ticket is exist
            if (ticket != null) {
                // Set the taken flag to false
                ticket.setTaken(false);
                Date paymentTime = new Date();
                ticket.setPaymentTime(paymentTime);
                log.info(id + ": Ticket[" + ticket.getTicketNumber() + "] the payment time is updated");
                double fee = calculateFee(ticket.getEnterTime(), ticket.getPaymentTime());
                ticket.setFee(fee);
                log.info(id + ": Ticket[" + ticket.getTicketNumber() + "] the parking fee is updated");
                String message = String.valueOf(ticket.getFee());
                payMachineMBox.send(new Msg(id, mbox, Msg.Type.PrintTicketInfo, message));
            } else {
                log.warning(id + ": Ticket[" + ticketNumber + "] does not exist");
                payMachineMBox.send(new Msg(id, mbox, Msg.Type.PayMachineError, ticketNumber));
            }
        } catch (NumberFormatException e) {
            log.warning(id + ": Invalid input. " + e.getMessage());
            payMachineMBox.send(new Msg(id, mbox, Msg.Type.PayMachineError, ticketNumber));
        }
    }// handlePayMachineInsertTicket

    //------------------------------------------------------------
    // calculateFee

    /**
     * A function of calculation of parking fee.
     *
     * @param from The datetime of the car entrance time.
     * @param to   The datetime of making payment.
     * @return The parking fee.
     */
    private double calculateFee(Date from, Date to) {
        double fee = 0;
        // Calculate the time period
        long diff = to.getTime() - from.getTime();
        // Express the time period in different way
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        // Calculate the fee
        if (diffSeconds > 0) {
            diffMinutes += 1;
        }
        if (diffMinutes > 0) {
            if (diffMinutes > 30) {
                diffHours += 1;
            } else {
                fee += 10;
            }
        }
        if (diffHours > 0) {
            fee += 20 * diffHours;
        }
        if (diffDays > 0) {
            fee += 300 * diffDays;
        }
        return fee;
    }// calculateFee

    //------------------------------------------------------------
    // handlePayment

    /**
     * A function of update the payment
     * status of the ticket.
     *
     * @param ticketNumber The number for identifying the ticket.
     */
    private void handlePayment(String ticketNumber) {
        try {
            Ticket ticket = ticketMap.get(Integer.parseInt(ticketNumber));
            // Validate whether the ticket is exist
            if (ticket != null) {
                ticket.setPaid(true);
                log.info(id + ": Ticket[" + ticket.getTicketNumber() + "] the payment status is updated");
                payMachineMBox.send(new Msg(id, mbox, Msg.Type.PayMachineSuccess, ticketNumber));
            } else {
                log.warning(id + ": Ticket[" + ticketNumber + "] does not exist");
                payMachineMBox.send(new Msg(id, mbox, Msg.Type.PayMachineError, ticketNumber));
            }
        } catch (NumberFormatException e) {
            log.warning(id + ": Invalid input. " + e.getMessage());
            payMachineMBox.send(new Msg(id, mbox, Msg.Type.PayMachineError, ticketNumber));
        }
    }// handlePayment

    //------------------------------------------------------------
    // handlePayMachineRemoveTicket

    /**
     * A function of handling the ticket
     * removed from Pay Machine Emulator.
     *
     * @param ticketNumber The number for identifying the ticket.
     */
    private void handlePayMachineRemoveTicket(String ticketNumber) {
        try {
            Ticket ticket = ticketMap.get(Integer.parseInt(ticketNumber));
            // Validate whether the ticket is exist
            if (ticket != null) {
                ticket.setTaken(true);
                log.info(id + ": Ticket[" + ticket.getTicketNumber() + "] is taken by the user.");
                // if the user removes the ticket without payment, then clear the payment information
                if (!ticket.getPaid()) {
                    ticket.setPaymentTime(null);
                    ticket.setFee(0);
                }
            } else {
                log.warning(id + ": Ticket[" + ticketNumber + "] does not exist.");
//                payMachineMBox.send(new Msg(id, mbox, Msg.Type.PayMachineError, ticketNumber));
            }
        } catch (NumberFormatException e) {
            log.warning(id + ": Invalid input. " + e.getMessage());
//            payMachineMBox.send(new Msg(id, mbox, Msg.Type.PayMachineError, ticketNumber));
        }
    }// handlePayMachineRemoveTicket

    //------------------------------------------------------------
    // handlePayMachineInsertTicket

    /**
     * A function of handling the insertion
     * of the ticket from Pay Machine Emulator.
     *
     * @param ticketNumber The number for identifying the ticket.
     */
    private void handleCollectorInsertTicket(String ticketNumber) {
        try {
            Ticket ticket = ticketMap.get(Integer.parseInt(ticketNumber));
            // Validate whether the ticket is exist
            if (ticket != null) {
                // validate the ticket
                String msg = handleValidation(ticket);
                if (ticket.getValid()) {
                    log.info(id + ": Ticket[" + ticket.getTicketNumber() + "]" + msg);
                    collectorMBox.send(new Msg(id, mbox, Msg.Type.PAck, ""));
                    log.info(id + ": Open Gate.");
                    gateMBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, ""));
                } else {
                    log.warning(id + ": Ticket[" + ticket.getTicketNumber() + "] is invalid. " + msg);
                    collectorMBox.send(new Msg(id, mbox, Msg.Type.NAck, ""));
                }
            } else {
                log.warning(id + ": Ticket[" + ticketNumber + "] does not exist.");
                collectorMBox.send(new Msg(id, mbox, Msg.Type.CollectorError, ticketNumber));
            }
        } catch (NumberFormatException e) {
            log.warning(id + ": Invalid input. " + e.getMessage());
            collectorMBox.send(new Msg(id, mbox, Msg.Type.CollectorError, ticketNumber));
        }
    }// handlePayMachineInsertTicket

    //------------------------------------------------------------
    // handleValidation

    /**
     * A function of ticket validation.
     *
     * @param ticket ticket The ticket ready to be validate.
     * @return the validation message
     */
    private String handleValidation(Ticket ticket) {
        boolean flag = true;
        String msg = "";
        // Validate Start
        log.info(id + ": start to validate Ticket[" + ticket.getTicketNumber() + "]...");
        // 1. check payment
        flag = flag && ticket.getPaid();
        if (!ticket.getPaid()) {
            msg += "The payment is not finished. ";
        }
        // 2. check the ticket is in user's hand
        flag = flag && ticket.getTaken();
        if (!ticket.getTaken()) {
            msg += "The ticket is not in user's hand. ";
        }
        // Validate finish
        log.info(id + ": finish to validate Ticket[" + ticket.getTicketNumber() + "]...");
        ticket.setValid(flag);
        log.info(id + ": Ticket[" + ticket.getTicketNumber() + "] validation status is updated.");
        if (flag) {
            msg += "Ticket is valid.";
        }
        return msg;
    }// handleValidation

    //------------------------------------------------------------
    // handleAdminOpen

    /**
     * A function of handling admin operation.
     *
     * @param ticketNumber The number for identifying the ticket.
     */
    private void handleAdminOpen(String ticketNumber) {
        try {
            Ticket ticket = ticketMap.get(Integer.parseInt(ticketNumber));
            // Validate whether the ticket is exist
            if (ticket != null) {
                // validate the ticket is paid
                if (ticket.getPaid()) {
                    log.info(id + ": Ticket[" + ticket.getTicketNumber() + "] is paid.");
                    log.info(id + ": can open Gate.");
                    gateMBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, ""));
                } else {
                    log.warning(id + ": Ticket[" + ticket.getTicketNumber() + "] is not paid.");
                    log.warning(id + ": cannot open Gate.");
                }
            } else {
                log.warning(id + ": Ticket[" + ticketNumber + "] does not exist.");
                collectorMBox.send(new Msg(id, mbox, Msg.Type.CollectorError, ticketNumber));
            }
        } catch (NumberFormatException e) {
            log.warning(id + ": Invalid input. " + e.getMessage());
            collectorMBox.send(new Msg(id, mbox, Msg.Type.CollectorError, ticketNumber));
        }
    }// handleAdminOpen

} // PCSCore
