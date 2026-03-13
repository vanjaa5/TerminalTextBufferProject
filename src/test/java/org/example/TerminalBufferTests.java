package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TerminalBufferTests {


    @Test
    void writeTextWrapsCorrectly() {
        TerminalBuffer buffer = new TerminalBuffer(10, 5, 100);
        buffer.writeText("abcdefghijklmnopqrstuvwxyz");
        assertEquals("abcdefghij", buffer.getLineToStringScreen(0).stripTrailing());
        assertEquals("klmnopqrst", buffer.getLineToStringScreen(1).stripTrailing());
        assertEquals("uvwxyz",     buffer.getLineToStringScreen(2).stripTrailing());
        assertEquals(2, buffer.getCursorRow());
        assertEquals(6, buffer.getCursorColumn());
    }

    @Test
    void insertTextWrapsCorrectly(){
        TerminalBuffer buffer = new TerminalBuffer(10, 5, 100);
        buffer.writeText("1234567890");
        buffer.setCursorPosition(0,5);
        buffer.insertText("abcdefghij");
        assertEquals("12345abcde", buffer.getLineToStringScreen(0).stripTrailing());
        assertEquals("fghij", buffer.getLineToStringScreen(1).stripTrailing());
        assertEquals(1, buffer.getCursorRow());
        assertEquals(5, buffer.getCursorColumn());
    }


}
