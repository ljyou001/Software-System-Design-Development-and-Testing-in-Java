package PCS.CollectorHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;


public class CollectorHandler extends AppThread {
    protected final MBox pcsCore;
    private CollectorStatus status;
    private boolean alarm;

    /**
     * Constructor for the Handler
     *
     * @param id             id of this device
     * @param appKickstarter app kickstarter
     */
    public CollectorHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        status = CollectorStatus.Idle;
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
            case CollectorInsertTicket:
                String tID = msg.getDetails();
                log.info(id + ": Ticket Inserted.");
                switch (status) {
                    case Idle:
                        log.info(id + ": Sending ticket information to PCS");
                        pcsCore.send(new Msg(id, mbox, Msg.Type.CollectorInsertTicket, tID));
                        status = CollectorStatus.CollectorInsertTicket;
                        break;
                    default:
                        log.warning(id + ": Ignored");
                }
                break;
            case AdminOpen:
                log.info(id + ": Admin Pressed the Button");
                pcsCore.send(new Msg(id, mbox, Msg.Type.AdminOpen, msg.getDetails()));
                switch (status) {
                    case RingingAlarm:
                        log.info(id + ": Admin stopped alarm and opened the door");
                        alarm = false;
                        alarmSignal(alarm);
                        status = CollectorStatus.Idle;
                        break;
                    default:
                        log.warning(id + ": Ignored");
                }
                break;
            case PAck:
                log.info(id + ": positive acknowledgement received");
                switch (status) {
                    case CollectorInsertTicket:
                        log.info(id + ": valid ticket.");
                        status = CollectorStatus.Idle;
                        break;
                    case RingingAlarm:
                        log.warning(id + ": Collector is ringing alarm now!!");
                        break;
                    default:
                        log.warning(id + ": Ignored");
                }
                break;
            case NAck:
                log.info(id + ": Alert! Admin! ");
                switch (status) {
                    case CollectorInsertTicket:
                        log.info(id + ": invalid ticket.");
                        alarm = true;
                        alarmSignal(alarm);
                        status = CollectorStatus.RingingAlarm;
                        break;
                    default:
                        log.warning(id + ": Ignored");
                }
                break;
            case CollectorError:
                switch (status) {
                    case Idle:
                        log.info(id + ": Collector idle now.");
                        break;
                    case CollectorInsertTicket:
                        log.warning(id + ": Wrong Ticket");
                        status = CollectorStatus.Idle;
                        break;
                    default:
                        log.warning(id + ": Ignored");
                }
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
     * Function for alarm signal
     *
     * @param alarmSignal alarm status
     */
    protected void alarmSignal(boolean alarmSignal) {
        if (alarmSignal) {
            log.info(id + ": Ring the Alarm.");
        } else {
            log.info(id + ": Stop the Alarm.");
        }
    }

    /**
     * status in this class
     */
    private enum CollectorStatus {
        Idle,
        CollectorInsertTicket,
        RingingAlarm,
    }
}
