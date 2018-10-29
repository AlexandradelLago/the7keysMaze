package tws.keeper.solution;

import tws.keeper.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static tws.keeper.model.Action.*;

public class KeeperAI implements Keeper {
    private static final List<Action> availableActions = Arrays.asList(GO_UP, GO_RIGHT, GO_DOWN, GO_LEFT);
    private static List<Action> backtrackedActions = new ArrayList<Action>();
    public static List<Position> walkedPositions = new ArrayList<Position>();
    private static Action lastAction;
    public static Integer backtrackCount = 0;
    private static Boolean tracking = false;
    public KeeperAI() {
        backtrackedActions.add(DO_NOTHING);
        lastAction = DO_NOTHING;
    }

    /**
     * This Keeper acts according to the Tramoux algotythm
     * Checkes the surrounding cells and if no stepped goes to them randomly
     * In case all of them are alreadby been walked then goes to the less used
     *
     * @param maze the maze
     * @return something
     */

    public Action act(Observable maze) {

        // Actual position
        Position current = maze.getKeeperPosition();

        // I include it in the walked
        walkedPositions.add(current);

        // I make a copy of available actions to be able to remove them etc
        List<Action> tempAvailableActions = new ArrayList<>();
        tempAvailableActions.addAll(availableActions);    // celdas a donde puedo ir ( GO_UP, GO_RIGHT, GO_DOWN, GO_LEFT, );

        // What is every cell I am surrounded by
        List<Cell> adyacentes = adyacents(maze);


        // if I found all keys and I have already crossed by the door before then backtrack
        if (isDoor(adyacentes)&&maze.getKeysFound()==maze.getTotalNumberOfKeys()){
            return availableActions.get(adyacentes.indexOf(Cell.DOOR));

        }else if (maze.getKeysFound()==maze.getTotalNumberOfKeys()&&tracking){
            return backtrackToDoor();

        }else if (maze.getKeysFound()==maze.getTotalNumberOfKeys()&& isDoor(adyacentes)){
            removeCell(adyacentes, Cell.WALL, tempAvailableActions);
            lastAction = decideMove(tempAvailableActions, current, walkedPositions);
            // if I am tracking I put it 0 if not yet started I do
            setOrClearTracking();
            return lastAction;

        } else {
            removeCell(adyacentes, Cell.WALL, tempAvailableActions);
            // If there is a cell with a key I go to that cell if no I check where to go

            if (keyIndex(adyacentes)!=-1) {
                lastAction= tempAvailableActions.get(keyIndex(adyacentes));
            }else{
               lastAction = decideMove(tempAvailableActions, current, walkedPositions);
            }

            if (tracking) { backtrackedActions.add(lastAction); }


            return lastAction;
        }

    }

    /*************************** FUNCIONES ******************************************************************/


    /**
     *  TESTED
     * @param current
     * @param nextPos
     * @return
     */

    public Action convertPositiontoAction (Position current, Position nextPos){
        int xDif = current.getVertical() - nextPos.getVertical();
        int yDif = current.getHorizontal()-nextPos.getHorizontal();
        switch (xDif){
            case 0:
                if (Math.abs(yDif)==1) return  (yDif==1) ?  GO_LEFT : GO_RIGHT;
                return DO_NOTHING;
            case -1:
                return GO_DOWN;

            case 1:
                return GO_UP;
                default:

            return DO_NOTHING;
        }
    }


    /**
     * TESTED  Function that gives the position given and action
     *
     * @param tempAvailableActions
     * @param current
     * @return
     */
    public Position getPositionFromAction(Action tempAvailableActions, Position current) {
        switch (tempAvailableActions) {
            case GO_UP:
                return new Position(current.getVertical() - 1, current.getHorizontal());
            case GO_DOWN:
                return new Position(current.getVertical() + 1, current.getHorizontal());
            case GO_RIGHT:
                return new Position(current.getVertical(), current.getHorizontal() + 1);
            case GO_LEFT:
                return new Position(current.getVertical(), current.getHorizontal() - 1);
            case DO_NOTHING:
                return new Position(current.getVertical(), current.getHorizontal());
        }
        return current;
    }
    private List<Position> getPositionList(List<Action> tempAvailableActions, Position current) {
        List<Position> tempPosList = new ArrayList<Position>();
        for (int i = 0; i < tempAvailableActions.size(); i++) {
            tempPosList.add(getPositionFromAction(tempAvailableActions.get(i), current));
        }
        return tempPosList;
    }


    /**
     *  TESTED - Functions that calculate the opposite action
     * @param act  Action received
     * @return   Oppposite actions
     */

    public Action switchAction(Action act) {


        switch (act) {
            case GO_UP:
                return GO_DOWN;
            case GO_DOWN:
                return GO_UP;
            case GO_RIGHT:
                return GO_LEFT;
            case GO_LEFT:
                return GO_RIGHT;
            default:
                return DO_NOTHING;
        }

    }

    /**
     *  TESTED - function that tells if the wizard has and adyacent door
     * @param adyacentCells
     * @return - true if an adyacent door and flase if not
     */
    public Boolean isDoor(List<Cell> adyacentCells) {
        return adyacentCells.indexOf(Cell.DOOR) != -1;
    }

    /**
     *  TESTED - funcion that returns the index of the cell with a key
     *
     * @param adyacentCells -- List of the adyacents cells to the wizard
     * @return -1 if no Cell is KEY
     */
    public int keyIndex(List<Cell> adyacentCells) {
        return adyacentCells.indexOf(Cell.KEY);
    }

    /**
     *  TESTED - function that calcultes if all cells around are stepped
     * @param adyPositions - position of the available cells around of the keeper
     * @return - returs true if ALL are stepped and false if not
     */
    public Boolean allStepped(List<Position> adyPositions){

        if (adyPositions.size()==1){
            return stepped(adyPositions.get(0),walkedPositions);
        }else{

            int countCellsStepped=0;
            for (int i = 0;i<adyPositions.size();i++){
                if (stepped(adyPositions.get(i),walkedPositions)){
                    countCellsStepped+=1;
                }
            }

            System.out.println("adyPositions size");
            System.out.println(adyPositions.size());
            System.out.println(countCellsStepped);
            return countCellsStepped == adyPositions.size();
        }

    }


    /**
     * TESTED - function the gives the index of the cell not stepped if only one and one of the non stepped chosed
     * randomly if more than 1 not stepped cells
     * @param adyPositions - positions of the possible cells for the wizard to go
     * @return - index a cell to go not stepped yet
     */

    public int randomOftheNonSteppedCell(List<Position> adyPositions) {
        List<Integer> cellsNoStepped = new ArrayList<Integer>();
        for (int i = 0; i < adyPositions.size(); i++) {
            if (timesStepped(adyPositions.get(i),walkedPositions) == 0) {
                cellsNoStepped.add(i);
            }
        }

        if (cellsNoStepped.size()==1){
            return cellsNoStepped.get(0);
        }else{
            int index = (int) Math.floor(Math.random()*cellsNoStepped.size());
            // int index = ThreadLocalRandom.current().nextInt(cellsNoStepped.size()-1);
            return  cellsNoStepped.get(index);
        }
    }

    /**
     * TESTED - function that removes the cell I am telling
     * @param adyacentCells
     * @param celltoremove
     * @param indexDir
     */
    public void removeCell(List<Cell> adyacentCells, Cell celltoremove, List<Action> indexDir) {

        int size = adyacentCells.size();
        ArrayList<Integer> indexDirec = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (adyacentCells.get(i).equals(celltoremove)) {
                indexDirec.add((int) i);
            }
        }
        // elimino al reves para no crear excepciones nunca
        for (int i = (indexDirec.size() - 1); i >= 0; i--) {
            adyacentCells.remove((int) indexDirec.get(i));
            indexDir.remove((int) indexDirec.get(i));
        }

    }

    /*+++++++++++++++++ NON TESTD YET +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/




    public Action decideMove(List<Action> tempAvailableActions, Position current, List<Position> walkedPos) {
        List<Position> adyPositions = getPositionList(tempAvailableActions, current);
        System.out.println("mis posiciones adyacentes "+adyPositions);
        System.out.println("mi actual posicion "+current);
        System.out.println("mis walked positions "+walkedPos);
        Action move;



      /*  if (allStepped(adyPositions)&&adyPositions.size()<3){
            System.out.println("este es mi backtrackcount "+backtrackCount);
            backtrackCount+=1;
            System.out.println("este es mi backtrackcount "+backtrackCount);
            Position pos =  walkedPos.get(walkedPos.size()-backtrackCount*2);
            System.out.println("la posicion a la que tengo que ir de walked positions "+pos);
            move = convertPositiontoAction( current, pos);

            System.out.println("el movimiento "+ move);
            return move;
        }else */if (allStepped(adyPositions)) {
            // if junction of 3 then go to the less stepped
            List<Integer> stepsInAdyCell = new ArrayList<Integer>();
            for (int i=0;i<adyPositions.size();i++){
                stepsInAdyCell.add(timesStepped(adyPositions.get(i),walkedPos));
            }


            int minIndex = stepsInAdyCell.indexOf(Collections.min(stepsInAdyCell));
            Position pos = adyPositions.get(minIndex);

            move = convertPositiontoAction(current,pos);
            return move;
        }else{

            int indexM= randomOftheNonSteppedCell(adyPositions);
             move = tempAvailableActions.get(indexM);
             System.out.println(move);
           // while (walkedPos.get(walkedPos.size()-2)!=current){
           //     walkedPos.remove(walkedPos.size()-1);
           // }
            System.out.println("estoy en el de no back track , con opciones ");
            backtrackCount=0;
            return move;
        }

    }


    public Boolean stepped(Position position, List<Position> walked) {
        if (walked.indexOf(position) == -1) {
            return false;
        } else {
            return true;
        }
    }

    // funcion que me diga si ya he pasado 2 veces o mas??
    public Integer timesStepped(Position position, List<Position> walked) {
        int stepped = 0;
        for (Position item : walked) {
            if (item.equals(position)) {
                stepped++;
            }
        }
        return stepped;
    }

    public Position lastPosition(List<Position> movements) {
        return movements.get(movements.size() - 1);
    }

    // mira las adyacentes y ve que CELDA es
    public List<Cell> adyacents(Observable maze) {
        List<Cell> adyacentes = new ArrayList<Cell>();
        // ( GO_UP, GO_RIGHT, GO_DOWN, GO_LEFT, );
        adyacentes.add(maze.lookUp());
        adyacentes.add(maze.lookRight());
        adyacentes.add(maze.lookDown());
        adyacentes.add(maze.lookLeft());
        return adyacentes;
    }

    /**
     * Function that checkes if we already passed by a door to track the steps
     */
    private void setOrClearTracking(){
        // I already was tracking so I clean the previous path
        if (tracking) {
            backtrackedActions.clear();
            backtrackedActions.add(lastAction);
        } else {
            tracking = true;
            backtrackedActions.add(lastAction);
        }
    }

    /**
     *  Function that choosed the opposite action of the list to backtrack to reach the door
     * @return
     */
    private Action backtrackToDoor() {
        Action backtrackAction = switchAction(backtrackedActions.get(backtrackedActions.size() - 1));
        backtrackedActions.remove(backtrackedActions.size() - 1);
        if (backtrackedActions.size() == 1) {
            tracking = false;
        }
        return backtrackAction;
    }


}







