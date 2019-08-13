package sweeper;

public class Game {
    private Bomb bomb;
    private Flag flag;
    private GameState state;

    public Game(int cols, int rows, int bombs) {
        Ranges.setSize(new Coord(cols, rows));
        bomb = new Bomb(bombs);
        flag = new Flag();
        state = GameState.PLAYED;
    }

    public void start() {
        bomb.start();
        flag.start();
    }

    public Box getBox(Coord coord) {
        if (Box.OPENED == flag.get(coord))
            return bomb.get(coord);
        else
            return flag.get(coord);
    }
    public void pressLeftButton (Coord coord){
        if (isGameOver()) return;
            openBox (coord);
            checkWinner ();
    }

    private void checkWinner() {
        if (GameState.PLAYED == state )
            if (flag.getTotalClosed()==bomb.getTotalBombs()){
                state = GameState.WINNER;
                flag.setFlagedToLastCloseBoxes();
            }

    }

    public int getTotalBombs(){
        return bomb.getTotalBombs();
    }

    public int getTotalFlaged(){
        return flag.getTotalFlaged();
    }

    private void openBox(Coord coord) {
        switch (flag.get(coord)){
            case OPENED : setOpenedToClosedBoxesAroundNumber (coord); break;
            case FLAGED : break;
            case CLOSED :
                switch (bomb.get(coord)){
                    case ZERO : openBoxesAroundZero(coord); break;
                    case BOMB : openBombs(coord); break;
                    default   : flag.setOpenedToBox(coord); break;
                }
        }
    }

    private void setOpenedToClosedBoxesAroundNumber(Coord coord) {
        if (Box.BOMB != bomb.get(coord))
            if (bomb.get(coord).getNumber() == flag.getCountOfFlagedBoxesAround(coord))
                for (Coord around : Ranges.getCoordsAround(coord))
                    if (flag.get(around) == Box.CLOSED) openBox(around);

    }

    private void openBombs(Coord bombedCoord) {
        flag.setBombedToBox (bombedCoord);
        for (Coord coord : Ranges.getAllCoords())
            if (bomb.get(coord) == Box.BOMB)
               flag.setOpenedToCloseBox (coord);
            else
                flag.setNobombToFlagedBox (coord);
        state = GameState.BOMBED;
    }

    private void openBoxesAroundZero(Coord coord) {
        flag.setOpenedToBox(coord);
        for (Coord around : Ranges.getCoordsAround(coord)) openBox(around);
    }

    public void pressRightButton (Coord coord){
        if (isGameOver()) return;
        flag.toggleFlagedToBox(coord);
    }
    private boolean isGameOver(){
        if (GameState.PLAYED != state){
            start ();
            state = GameState.PLAYED;
            return true;
        } return false;
    }

    public GameState getState() {
        return state;
    }
}
