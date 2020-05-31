package PCS;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;
import PCS.CollectorHandler.CollectorHandler;
import PCS.DispatcherHandler.DispatcherHandler;
import PCS.GateHandler.GateHandler;
import PCS.PCSCore.PCSCore;
import PCS.PayMachineHandler.PayMachineHandler;
import PCS.SensorHandler.SensorHandler;
import PCS.VacancyDispHandler.VacancyDispHandler;
import javafx.application.Platform;


//======================================================================
// PCSStarter
public class PCSStarter extends AppKickstarter {
    protected Timer timer;
    protected PCSCore pcsCore;
    protected GateHandler gateHandler;
    protected CollectorHandler collectorHandler;
    protected DispatcherHandler dispatcherHandler;
    protected PayMachineHandler payMachineHandler;
    protected SensorHandler sensorHandler;
    protected VacancyDispHandler vacancyDispHandler;


    //------------------------------------------------------------
    // PCSStart
    public PCSStarter() {
        super("PCSStarter", "etc/PCS.cfg");
    } // PCSStart

    //------------------------------------------------------------
    // main
    public static void main(String[] args) {
        new PCSStarter().startApp();
    } // main

    //------------------------------------------------------------
    // startApp
    protected void startApp() {
        // start our application
        log.info("");
        log.info("");
        log.info("============================================================");
        log.info(id + ": Application Starting...");

        startHandlers();
    } // startApp


    //------------------------------------------------------------
    // startHandlers
    protected void startHandlers() {
        // create handlers
        try {
            timer = new Timer("timer", this);
            pcsCore = new PCSCore("PCSCore", this);
            gateHandler = new GateHandler("GateHandler", this);
            collectorHandler = new CollectorHandler("CollectorHandler", this);
            dispatcherHandler = new DispatcherHandler("DispathcerHandler", this);
            payMachineHandler = new PayMachineHandler("PayMachineHandler", this);
            sensorHandler = new SensorHandler("SensorHandler", this);
            vacancyDispHandler = new VacancyDispHandler("VacanctDispHandler", this);
        } catch (Exception e) {
            System.out.println("AppKickstarter: startApp failed");
            e.printStackTrace();
            Platform.exit();
        }

        // start threads
        new Thread(timer).start();
        new Thread(pcsCore).start();
        new Thread(gateHandler).start();
        new Thread(collectorHandler).start();
        new Thread(dispatcherHandler).start();
        new Thread(payMachineHandler).start();
        new Thread(sensorHandler).start();
        new Thread(vacancyDispHandler).start();
    } // startHandlers


    //------------------------------------------------------------
    // stopApp
    public void stopApp() {
        log.info("");
        log.info("");
        log.info("============================================================");
        log.info(id + ": Application Stopping...");
        pcsCore.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        gateHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        collectorHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        dispatcherHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        payMachineHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        sensorHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        vacancyDispHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
        timer.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
    } // stopApp
} // PCS.PCSStarter
