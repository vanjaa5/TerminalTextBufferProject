package org.example;

public class Cell {
    private char character;
    private TerminalColor foregroundColor;
    private TerminalColor backgroundColor;
    private int styles;

    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int UNDERLINED = 4;

    public Cell() {
        character = ' ';
        foregroundColor = null;
        backgroundColor = null;
        styles = 0;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public TerminalColor getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(TerminalColor foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public TerminalColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(TerminalColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getStyles() {
        return styles;
    }

    public void setStyles(int styles) {
        this.styles = styles;
    }

    public boolean isBold(){
        return (styles & BOLD) == BOLD;
    }

    public boolean isItalic(){
        return (styles & ITALIC) == ITALIC;
    }
    public boolean isUnderline(){
        return (styles & UNDERLINED) == UNDERLINED;
    }

    public void setBold(boolean bold) {
        if (bold) {
            styles |= BOLD;
        }
        else {
            styles &= ~BOLD;
        }
    }

    public void setItalic(boolean italic) {
        if (italic) {
            styles |= ITALIC;
        }
        else {
            styles &= ~ITALIC;
        }
    }
    public void setUnderline(boolean underline) {
        if (underline) {
            styles |= UNDERLINED;
        }
        else {
            styles &= ~UNDERLINED;
        }
    }


    public void reset(){
        styles = 0;
        backgroundColor = null;
        foregroundColor = null;
        character = ' ';
    }

    public void copyFrom(Cell cell) {
        character = cell.getCharacter();
        foregroundColor = cell.getForegroundColor();
        backgroundColor = cell.getBackgroundColor();
        styles = cell.getStyles();
    }


}
