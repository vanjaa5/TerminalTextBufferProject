package org.example;

import java.util.ArrayList;
import java.util.List;

public class TerminalBuffer {

    private int width;
    private int height;
    private int scrollbackSize;
    private List<Line> scrollback = new ArrayList<>();
    private List<Line> screen = new ArrayList<>();
    private int cursorRow;
    private int cursorColumn;
    private TerminalColor foregroundColor;
    private TerminalColor backgroundColor;
    private int styles;


    public TerminalBuffer(int width, int height, int scrollbackSize) {
        this.width = width;
        this.height = height;
        this.scrollbackSize = scrollbackSize;
        for (int i = 0; i < height; i++) {
            screen.add(new Line(width));
        }
        this.cursorRow = 0;
        this.cursorColumn = 0;
        foregroundColor = null;
        backgroundColor = null;
        styles = 0;

    }

    public int getCursorRow() {
        return cursorRow;
    }

    public int getCursorColumn() {
        return cursorColumn;
    }

    public void setCursorPosition(int row, int column) {
        cursorRow = Math.max(0, Math.min(height - 1, row));
        cursorColumn = Math.max(0, Math.min(width - 1, column));
    }

    public void moveCursor(int dRow, int dColumn) {

        setCursorPosition(cursorRow + dRow, cursorColumn + dColumn);

    }

    public void setForegroundColor(TerminalColor foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public void setBackgroundColor(TerminalColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBold(boolean bold) {
        if (bold) {
            styles |= Cell.BOLD;
        } else {
            styles &= ~Cell.BOLD;
        }
    }

    public void setItalic(boolean italic) {
        if (italic) {
            styles |= Cell.ITALIC;
        } else {
            styles &= ~Cell.ITALIC;
        }
    }

    public void setUnderline(boolean underline) {
        if (underline) {
            styles |= Cell.UNDERLINED;
        } else {
            styles &= ~Cell.UNDERLINED;
        }
    }

    public Line getLine(int row) {
        return screen.get(row);
    }

    public Line getScrollbackLine(int row) {
        return scrollback.get(row);
    }

    public int getScrollbackSize() {
        return scrollback.size();
    }

    //editing

    public void writeText(String text) {
        for (char c : text.toCharArray()) {
            Line line = screen.get(cursorRow);
            Cell cell = line.getCell(cursorColumn);

            cell.setCharacter(c);
            cell.setForegroundColor(foregroundColor);
            cell.setBackgroundColor(backgroundColor);
            cell.setStyles(styles);

            if(Cell.isWideChar(c)){
                if(cursorColumn++ == width-1){
                    cell.setCharacter(' ');
                    cursorColumn = 0;
                    cursorRow++;
                    writeText(String.valueOf(c));
                    continue;
                }
                else cell.setWide(true);

               // cursorColumn++;
                Cell placeholder = line.getCell(cursorColumn++);
                placeholder.setPlaceholder(true);
                placeholder.setCharacter(' ');

            }
            else cursorColumn++;


            if (cursorColumn >= width) {
                cursorColumn = 0;
                cursorRow++;
                if (cursorRow >= height) {
                    insertEmptyLine();
                }
            }

        }
    }




    public void insertText(String text) {
        Line line = screen.get(cursorRow);
        int n = text.length();
        int k = cursorColumn;
        for (int j = 0; j < n; j++) {

            if(k >= width) {
                cursorColumn = k = 0;
                cursorRow++;
                if (cursorRow >= height) {
                    insertEmptyLine();
                }
                line = screen.get(cursorRow);

            }

            for (int i = width - 1; i > k; i--) {
                line.getCell(i).copyFrom(line.getCell(i - 1));
            }
            Cell cell = line.getCell(k++);
            cell.setCharacter(text.charAt(j));
            cell.setForegroundColor(foregroundColor);
            cell.setBackgroundColor(backgroundColor);
            cell.setStyles(styles);
        }

        cursorColumn = k;

    }


    public void fillLine(int row, char c) {
        Line line = screen.get(row);
        for (int i = 0; i < width; i++) {
            Cell cell = line.getCell(i);
            cell.setCharacter(c);
            cell.setForegroundColor(foregroundColor);
            cell.setBackgroundColor(backgroundColor);
            cell.setStyles(styles);
        }
    }

//operations that do not depend on the cursor

    public void insertEmptyLine() {
        Line top = screen.remove(0);
        if (scrollback.size() >= scrollbackSize) {
            scrollback.remove(0);
        }
        scrollback.add(top);
        screen.add(new Line(width));
        cursorRow = Math.max(0, cursorRow - 1);
    }

    public void clearScreen() {
        for (Line line : screen) {
            line.clear();
        }

        cursorRow = 0;
        cursorColumn = 0;
    }

    public void clearScreenAndScrollback() {
        clearScreen();
        scrollback.clear();

    }

    //content access

    public char getCharAtScreen(int row, int column) {
        return screen.get(row).getCell(column).getCharacter();
    }


    public char getCharAtScrollback(int row, int column) {
        return scrollback.get(row).getCell(column).getCharacter();
    }

    public Cell getAttributeAtScreen(int row, int column) {
        return screen.get(row).getCell(column);
    }

    public Cell getAttributeAtScrollback(int row, int column) {
        return scrollback.get(row).getCell(column);
    }

    public String getLineToStringScreen(int row){
        return screen.get(row).getLineValue();
    }

    public String getLineToStringScrollback(int row){
        return scrollback.get(row).getLineValue();
    }

    public String getScreenToString(){
        StringBuilder ret = new StringBuilder();
        for(Line line : screen){
            ret.append(line.getLineValue()).append("\n");
        }

        return ret.toString();
    }

    public String getScreenAndScrollbackToString(){
        StringBuilder ret = new StringBuilder();
        for(Line s : scrollback){
            ret.append(s.getLineValue()).append("\n");
        }
        ret.append(getScreenToString());
        return ret.toString();
    }

}
