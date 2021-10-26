package me.iron.WarpSpace.Mod.beacon;

import api.ModPlayground;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.register.RegisterAddonsEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.addon.SimpleAddOn;
import api.utils.game.SegmentControllerUtils;
import me.iron.WarpSpace.Mod.WarpMain;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.controller.elements.power.reactor.tree.ReactorElement;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 26.10.2021
 * TIME: 19:31
 */
public class WarpBeaconAddon extends SimpleAddOn {
    private  float powerCost = 10000;
    public static final String UIDName = "WarpBeaconSimple";
    public static ElementInformation beaconChamber;
    public static void registerChamberBlock() {
        //short rootID = (short) ElementKeyMap.getInfo().chamberRoot;
         StarMod mod = WarpMain.instance;
        beaconChamber = BlockConfig.newChamber(mod, "Warp Beacon", ElementKeyMap.REACTOR_CHAMBER_JUMP);
        beaconChamber.chamberCapacity = 0.5f;
        beaconChamber.setTextureId(ElementKeyMap.getInfo(ElementKeyMap.REACTOR_CHAMBER_JUMP).getTextureIds());
        beaconChamber.setDescription("Shift the closest warp droppoint to this sector.");
        BlockConfig.add(beaconChamber);
    }
    public static void init() {
        StarLoader.registerListener(RegisterAddonsEvent.class, new Listener<RegisterAddonsEvent>() {
            @Override
            public void onEvent(RegisterAddonsEvent event) {
                event.addModule(new WarpBeaconAddon(event.getContainer(),WarpMain.instance));
            }
        }, WarpMain.instance);
    }

    private BeaconObject beacon;
    public WarpBeaconAddon(ManagerContainer<?> managerContainer, StarMod starMod) {
        super(managerContainer, ElementKeyMap.REACTOR_CHAMBER_JUMP, starMod, UIDName);
    }

    @Override
    public boolean isPlayerUsable() { //called every frame (or often)
        ReactorElement warpBeaconChamber = SegmentControllerUtils.getChamberFromElement(getManagerUsableSegmentController(), beaconChamber);
        if (warpBeaconChamber == null)// || !(this.segmentController instanceof SpaceStation))
            return false;
        return super.isPlayerUsable();
    }

    @Override
    public float getChargeRateFull() { //in seconds
        return 5;
    }

    @Override
    public double getPowerConsumedPerSecondResting() {
        return powerCost;
    }

    @Override
    public double getPowerConsumedPerSecondCharging() {
        return powerCost;
    }

    @Override
    public float getDuration() {
        return -1;
    }

    @Override
    public boolean onExecuteServer() {
        ModPlayground.broadcastMessage("warp beacon activated by " + this.segmentController.getName());
        beacon = new BeaconObject(this.segmentController);
        WarpMain.instance.beaconManager.addBeacon(beacon);
        return true;
    }

    @Override
    public boolean onExecuteClient() {
        return true;
    }

    private int timer;
    @Override
    public void onActive() { //called every frame
        if (timer++%100==0 && beacon != null)
            beacon.update();
    }

    @Override
    public void onInactive() { //called when?
        if (beacon != null) {
            beacon.setFlagForDelete();
            WarpMain.instance.beaconManager.updateBeacon(beacon);
            beacon = null;
        }
    }

    @Override
    public String getName() {
        return "Warp Beacon";
    }
}