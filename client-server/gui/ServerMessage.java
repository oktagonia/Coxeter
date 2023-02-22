package gui;

import euclid.ShapeType;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A message from the server to the client
 */
public class ServerMessage extends ArrayList<ServerInstruction> {
    public ServerMessage(String msg) {
        super();
        if (!msg.equals("")) {
            String[] tmp = msg.split(";");
            for (String string: tmp) {
                String[] instruction = string.split(" ");
                if (instruction.length == 0) continue;
                add(new ServerInstruction(instruction));
            }
        }
    }

    public ServerMessage() {
        super();
    }

    public String toString() {
        String string = "";
        for (ServerInstruction inst: this) {
            string += inst.toString() + ";";
        }
        return string;
    }
}
