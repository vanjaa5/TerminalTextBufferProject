package org.example;

import org.junit.jupiter.api.Test;

import static org.example.TerminalColor.*;
import static org.junit.jupiter.api.Assertions.*;

public class TerminalBufferTests {


    @Test
    void writeTextWrapsCorrectly() {
        TerminalBuffer buffer = new TerminalBuffer(10, 5, 100);
        buffer.writeText("abcdefghijklmnopqrstuvwxyz");
        assertEquals("abcdefghij", buffer.getLineToStringScreen(0).stripTrailing());
        assertEquals("klmnopqrst", buffer.getLineToStringScreen(1).stripTrailing());
        assertEquals("uvwxyz", buffer.getLineToStringScreen(2).stripTrailing());
        assertEquals(2, buffer.getCursorRow());
        assertEquals(6, buffer.getCursorColumn());
    }

    @Test
    void insertTextWrapsCorrectly() {
        TerminalBuffer buffer = new TerminalBuffer(10, 5, 100);
        buffer.writeText("1234567890");
        buffer.setCursorPosition(0, 5);
        buffer.insertText("abcdefghij");
        assertEquals("12345abcde", buffer.getLineToStringScreen(0).stripTrailing());
        assertEquals("fghij", buffer.getLineToStringScreen(1).stripTrailing());
        assertEquals(1, buffer.getCursorRow());
        assertEquals(5, buffer.getCursorColumn());
    }

    private void assertCellAttributes(Cell cell, TerminalColor bg, TerminalColor fg,
                                      boolean bold, boolean italic, boolean underline) {
        assertEquals(bg, cell.getBackgroundColor());
        assertEquals(fg, cell.getForegroundColor());
        assertEquals(bold, cell.isBold());
        assertEquals(italic, cell.isItalic());
        assertEquals(underline, cell.isUnderline());
    }

    @Test
    void colorTest() {
        TerminalBuffer buffer = new TerminalBuffer(10, 5, 100);
        buffer.setBold(true);
        buffer.writeText("abc");
        buffer.setBackgroundColor(TerminalColor.GREEN);
        buffer.setForegroundColor(MAGENTA);
        buffer.setBold(false);
        buffer.setItalic(true);
        buffer.setUnderline(true);
        buffer.writeText("def");


        assertCellAttributes(buffer.getLine(0).getCell(0), BLACK, WHITE, true, false, false);
        assertCellAttributes(buffer.getLine(0).getCell(1), BLACK, WHITE, true, false, false);
        assertCellAttributes(buffer.getLine(0).getCell(2), BLACK, WHITE, true, false, false);

        assertCellAttributes(buffer.getLine(0).getCell(3), GREEN, MAGENTA, false, true, true);
        assertCellAttributes(buffer.getLine(0).getCell(4), GREEN, MAGENTA, false, true, true);
        assertCellAttributes(buffer.getLine(0).getCell(5), GREEN, MAGENTA, false, true, true);

    }

    @Test
    void cursorClampingBelowZero() {
        TerminalBuffer buffer = new TerminalBuffer(80, 24, 100);
        buffer.setCursorPosition(-1, -1);
        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorColumn());
    }

    @Test
    void cursorClampingAboveMax() {
        TerminalBuffer buffer = new TerminalBuffer(80, 24, 100);
        buffer.setCursorPosition(999, 999);
        assertEquals(23, buffer.getCursorRow());
        assertEquals(79, buffer.getCursorColumn());
    }

    @Test
    void scrollbackGrowsWhenScreenFills() {
        TerminalBuffer buffer = new TerminalBuffer(10, 3, 100);
        buffer.writeText("aaaaaaaaaa");
        buffer.writeText("bbbbbbbbbb");
        buffer.writeText("cccccccccc");
        buffer.writeText("dddddddddd");

        assertEquals(2, buffer.getScrollbackSize());
        assertEquals("aaaaaaaaaa", buffer.getLineToStringScrollback(0).stripTrailing());
        assertEquals("cccccccccc", buffer.getLineToStringScreen(0).stripTrailing());
    }

    @Test
    void scrollbackGrowsWhenScreenFills2() {
        TerminalBuffer buffer = new TerminalBuffer(10, 3, 100);
        //checking what happens when the last cell is not filled
        buffer.writeText("aaaaaaaaaa");
        buffer.writeText("bbbbbbbbbb");
        buffer.writeText("cccccccccc");
        buffer.writeText("ddddddddd");

        assertEquals(1, buffer.getScrollbackSize());
        assertEquals("aaaaaaaaaa", buffer.getLineToStringScrollback(0).stripTrailing());
        assertEquals("bbbbbbbbbb", buffer.getLineToStringScreen(0).stripTrailing());
    }

    @Test
    void scrollbackCapacityTest(){
        TerminalBuffer buffer = new TerminalBuffer(10, 3, 2);
        buffer.writeText("abcdefghij");
        buffer.writeText("a1b2c3d4e5");
        buffer.writeText("test123456");
        buffer.writeText("0123456789");
        buffer.writeText("jihgfedcba");
        buffer.writeText("9876543210");

        assertEquals("jihgfedcba", buffer.getLineToStringScreen(0).stripTrailing());
        assertEquals("9876543210", buffer.getLineToStringScreen(1).stripTrailing());

        assertEquals("test123456", buffer.getLineToStringScrollback(0).stripTrailing());
        assertEquals("0123456789", buffer.getLineToStringScrollback(1).stripTrailing());

    }

    @Test
    void WideCharacterAtLastColumnTest(){
        TerminalBuffer buffer = new TerminalBuffer(10, 3, 2);
        buffer.writeText("abcdefghi你");

        assertEquals(1, buffer.getCursorRow());
        assertEquals("abcdefghi", buffer.getLineToStringScreen(0).stripTrailing());
        assertEquals("你", buffer.getLineToStringScreen(1).stripTrailing());
    }

    @Test
    void wideCharPlaceholderSkippedInOutput() {
        TerminalBuffer buffer = new TerminalBuffer(6, 3, 100);
        buffer.writeText("你好");
        assertEquals("你好", buffer.getLineToStringScreen(0).stripTrailing());
        assertEquals(4,buffer.getCursorColumn());
    }

    @Test
    void wideCharacterLineFill(){
        TerminalBuffer buffer = new TerminalBuffer(7, 3, 100);
        buffer.fillLine(0,'你');
        assertEquals("你你你",buffer.getLineToStringScreen(0).stripTrailing());
        assertEquals(' ',buffer.getLineToStringScreen(0).charAt(3));

    }

    @Test
    void clearScreenTest() {
        TerminalBuffer buffer = new TerminalBuffer(10, 3, 100);
        buffer.writeText("hello");
        buffer.clearScreen();
        assertEquals("", buffer.getLineToStringScreen(0).stripTrailing());
        assertEquals(0, buffer.getCursorRow());
        assertEquals(0, buffer.getCursorColumn());
    }

    @Test
    void clearScreenAndScrollbackTest() {
        TerminalBuffer buffer = new TerminalBuffer(10, 3, 100);
        buffer.writeText("aaaaaaaaaa");
        buffer.writeText("bbbbbbbbbb");
        buffer.writeText("cccccccccc");
        buffer.writeText("dddddddddd");
        buffer.clearScreenAndScrollback();
        assertEquals(0, buffer.getScrollbackSize());
        assertEquals("", buffer.getLineToStringScreen(0).stripTrailing());
    }



    @Test
    void insertTextPushesWideCharOff() {
        TerminalBuffer buffer = new TerminalBuffer(4, 3, 100);
        buffer.writeText("a你b");
        buffer.setCursorPosition(0, 0);
        buffer.insertText("x");
        assertEquals("xa你", buffer.getLineToStringScreen(0).stripTrailing());
    }


}
