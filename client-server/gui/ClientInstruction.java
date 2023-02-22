package gui;

import euclid.*;
import csp.*;

import java.util.Arrays;
import java.util.List;

public class ClientInstruction {
    public Operation operation;
    public String[] arguments;

    // parse the instruction and reconstruct it.
    public ClientInstruction(String msg) {
        String[] tmp = msg.split(" ");
        operation = Operation.valueOf(tmp[0]);
        List<String> a = Arrays.asList(tmp).subList(1,tmp.length);
        arguments = a.toArray(new String[a.size()]);
    }

    // make an instruction
    public ClientInstruction(Operation operation, String[] arguments) {
        this.operation = operation;
        this.arguments = arguments;
    }

    public String toString() {
        String s = "";
        for (String str: arguments) {
            s += str + " ";
        }
        return operation + " " + s;
    }
}
