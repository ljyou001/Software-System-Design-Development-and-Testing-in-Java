package PCS.VacancyDispHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.Msg;

public class VacancyDispHandler extends AppThread {

    /**
     * Constructor for an appThread
     *
     * @param id             name of the appThread
     * @param appKickstarter a reference to our AppKickstarter
     */
    public VacancyDispHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    }

    public void run() {
        log.info(id + ": starting...");

        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            quit = processMsg(msg);
        }
        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    }

    //------------------------------------------------------------
    // processMsg
    protected boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {
            case UpdateDisplay:
                handleUpdateDisplay(msg);
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
        return quit;
    } // processMsg

    protected void handleUpdateDisplay(Msg msg) {
//        String[] msgDetail = msg.getDetails().split("-");
//        log.info(id + ": update display -- " + msgDetail[0] + "floor" + (50 - Integer.parseInt(msgDetail[1])) + "places left.");
        log.info(id + ": update display --" + msg.getDetails());
    }
}
