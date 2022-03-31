package me.iron.WarpSpace.Mod.network;

import api.DebugFile;
import me.iron.WarpSpace.Mod.client.HUD_core;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import me.iron.WarpSpace.Mod.client.WarpProcess;
import org.schema.game.common.data.player.PlayerState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * packet to send a vector3i to a client and set it as the clients navigation waypoint
 * send all important information from server to client
 * edited by Ir0nsight
 * made by jake
 */
public class PacketHUDUpdate extends Packet {
    /**
     * value given process has: 1 happening, 0 not happening
     */
    private long[] arr;

    //TODO allow multiple HUD updates in one packet
    /**
     * constructor
     * @param processArray String list that allows input of extra info to be displayed. currently not used.
     */
    public PacketHUDUpdate(long[] processArray) {
        arr = processArray;
        DebugFile.log("sending HUD package to client: " + this);
    }

    /**
     * default constructor required by starlaoder DO NOT DELETE!
     */
    public PacketHUDUpdate() {

    }

    @Override
    public void writePacketData(PacketWriteBuffer buf) throws IOException {
        buf.writeInt(arr.length);
        for (long l: arr) {
            buf.writeLong(l);
        }
        DebugFile.log("packet writing" + this);
    }

    @Override
    public void readPacketData(PacketReadBuffer buf) throws IOException {
        int length = buf.readInt();
        arr = new long[length];
        for (int i = 0; i < length; i++) {
            arr[i]=buf.readLong();
        }
        DebugFile.log("packet reading" + this);
    }



    @Override
    public void processPacketOnClient() {
        //set players process "map" (enum)
        WarpProcess.update(arr);
        HUD_core.UpdateHUD(); //TODO make listener on specific values or to all
    }

    @Override
    public void processPacketOnServer(PlayerState sender) {
        //not intended in this direction.
    }

    @Override
    public String toString() {
        return "PacketHUDUpdate{" +
                "arr=" + arr +
                '}';
    }
}