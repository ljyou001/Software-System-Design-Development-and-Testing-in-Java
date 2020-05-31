package PCS.SensorHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

public class SensorHandler extends AppThread {
    /**
     * Constructor for an appThread
     *
     * @param id             name of the appThread
     * @param appKickstarter a reference to our AppKickstarter
     */
    public SensorHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    }

    // run
    public void run() {
        MBox pcsCore = appKickstarter.getThread("PCSCore").getMBox();
        log.info(id + ": starting...");

        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                case CarPassThrough:
                    pcsCore.send(new Msg(id, mbox, Msg.Type.CarPassThrough, msg.getDetails()));
                    break;
                default:
                    log.warning(id + ": unknown message type: [" + msg + "]");
                    break;
            }
        }

        appKickstarter.unregThread(this);
        log.info(id + ": Total Cars Number is increase");
    }
}
