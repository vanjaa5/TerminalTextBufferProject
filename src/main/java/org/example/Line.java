package org.example;

public class Line {
    private int width;
    private Cell[] cells;

    public Line(int width) {
        this.width = width;
        cells = new Cell[width];
        for (int i = 0; i < width; i++) {
            cells[i] = new Cell();
        }
    }

    public Cell getCell(int col){
        return cells[col];
    }

    public String getLineVlue(){
        StringBuilder line = new StringBuilder();
        for (Cell cell : cells) {
            line.append(cell.getCharacter());
        }


        return line.toString();
    }

    public void clear(){
        for (Cell cell : cells) {
            cell.reset();
        }
    }



}
