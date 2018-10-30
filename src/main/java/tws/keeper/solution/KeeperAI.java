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
     //   backtrackedActions.add(DO_NOTHING);
        lastAction = DO_NOTHING;
    }

    /**
     * This Keeper acts according to the Tramoux algotythm
     * Removes walls
     * Check if I pass next to the door and If I have all keys then unlocks
     * If doesnt have all keys starts tracking the cells to know how to go to the door on the way back
     * If the wizards passes again by the door , reset of the backtracking
     * TO DECIDE wher to go:
     * Checkes the surrounding cells and if no stepped goes to them randomly
     * In case all of them are alreadby been walked then goes to a randomly chosen among the same with min steps
     *
     *
     * @param maze the maze
     * @return something
     */

    public Action act(Observable maze) {

        System.out.println(backtrackedActions);
        // Actual position
        Position current = maze.getKeeperPosition();

        // keep track of the positions the wizard passed by
        walkedPositions.add(current);

        // I make a copy of available actions to be able to remove them etc
        List<Action> tempAvailableActions = new ArrayList<>();
        tempAvailableActions.addAll(availableActions);    // copy of available actions to be able to remove not possible

        // What is in every cell I am surrounded by
        List<Cell> adyacentes = adyacents(maze);


        // if I found all keys and I have already crossed by the door before then backtrack
        if (isDoor(adyacentes)&&maze.getKeysFound()==maze.getTotalNumberOfKeys()){
            return availableActions.get(adyacentes.indexOf(Cell.DOOR));

        }else if (maze.getKeysFound()==maze.getTotalNumberOfKeys()&&tracking){
            return backtrackToDoor();

        }else if (isDoor(adyacentes)){
            removeCell(adyacentes, Cell.WALL, tempAvailableActions);

            lastAction = decideMove(tempAvailableActions, current, walkedPositions);
            // if I am tracking I put it 0 if not yet started I do
            setOrClearTracking();
            return lastAction;

        } else {
            removeCell(adyacentes, Cell.WALL, tempAvailableActions);

            if ((tempAvailableActions.size()>1)&&(walkedPositions.size()>1)){
                tempAvailableActions = removeLastMovement(tempAvailableActions,adyacentes,lastAction);
            }

            // If there is a cell with a key I go to that cell if no I check where to go
            if (keyIndex(adyacentes)!=-1) {
                // if there is a key adyacent then It goes first there
                lastAction= tempAvailableActions.get(keyIndex(adyacentes));
            }else{
                lastAction = decideMove(tempAvailableActions, current, walkedPositions);
            }

            if (tracking) { backtrackedActions.add(lastAction); }

            return lastAction;
        }

    }

    /*********************************************************************************************/


    /**
     *  Function gives the action to do between to positions
     * @param current - position at the moment
     * @param nextPos - position where you wanna go
     * @return - Action to do
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
     * Function that calculates the position of the cells according to the action indicate
     * from the current position
     * @param action -- action to do
     * @param current -- from this position
     * @return - destination position
     */
    public Position getPositionFromAction(Action action, Position current) {
        switch (action) {
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

    /**
     * Makes an array with the adyacents position to the wizard related to the action needed
     * @param tempAvailableActions -- possible actions to do
     * @param current - starting position
     * @return - array of positions
     */
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

    /**
     *  Function that gives the adyacent cells of the current position
     * @param maze - uring the observable interface
     * @return -- list of cell adyacents in order UP-RIGHT-DOWN-LEFT
     *
     */
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
     * Function that checkes if we already passed by a door to track the steps or to reset the
     * backtrack path to the door
     */
    private void setOrClearTracking(){
        // I already was tracking so I clean the previous path
        if (tracking) {
            backtrackedActions.clear();
           // backtrackedActions.add(lastAction);
        } else {
            tracking = true;
            backtrackedActions.add(lastAction);
        }
    }

    /**
     *  Function that choosed the opposite action of the list to backtrack to reach the door
     *  removes the last one movement also
     * @return -the opposite action of what is saved in the list
     */
    private Action backtrackToDoor() {
        Action backtrackAction = switchAction(backtrackedActions.get(backtrackedActions.size() - 1));
        backtrackedActions.remove(backtrackedActions.size() - 1);
        if (backtrackedActions.size() == 1) {
            tracking = false;
        }
        return backtrackAction;
    }

    /**
     *  Function that returns how many times a provided position has been stepped by
     *  checking the walked positions list
     * @param position - position to check
     * @param walked - list of positions where the wizard has being
     * @return - times the wizard has been in that position
     */
    public Integer timesStepped(Position position, List<Position> walked) {
        int stepped = 0;
        for (Position item : walked) {
            if (item.equals(position)) {
                stepped++;
            }
        }
        return stepped;
    }
    /**
     * Function that says if a position has been ever visited
     * @param position
     * @param walked
     * @return boolean
     */
    public Boolean stepped(Position position, List<Position> walked) {
        if (walked.indexOf(position) == -1) {
            return false;
        } else {
            return true;
        }
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
     * @return - index of a cell to go not stepped yet from the adyacent Positions
     */

    public int randomOftheNonSteppedCell(List<Position> adyPositions) {

        List<Integer> cellsNoStepped = new ArrayList<Integer>();
        for (int i = 0; i < adyPositions.size(); i++) {
            // first checks cells that are not stepped yet
            if (timesStepped(adyPositions.get(i),walkedPositions) == 0) {
                cellsNoStepped.add(i);
            }
        }

        // in only one i return that
        if (cellsNoStepped.size()==1){
            return cellsNoStepped.get(0);
        }else{

            // if not random position among the ones with same 0 steps
            int index = (int) Math.floor(Math.random()*cellsNoStepped.size());
            return  cellsNoStepped.get(index);
        }
    }

    /**
     * Function that returns the index of the position to go that has minimum number of s
     * @param adyPositions
     * @return
     */
    public int randomMinStepped(List<Position> adyPositions) {
        if (adyPositions.size()==1){
            return 0;
        }else{
            List<Integer> stepsInAdyCell = new ArrayList<Integer>();
            List<Integer> lastTimeStepped = new ArrayList<Integer>();
            // check how many times have been stepped the ady cells
            for (int i = 0; i < adyPositions.size(); i++) {
                stepsInAdyCell.add(timesStepped(adyPositions.get(i),walkedPositions));
            }
            // look for the min steps
            int minIndex = stepsInAdyCell.indexOf(Collections.min(stepsInAdyCell));
            int minSteps = stepsInAdyCell.get(minIndex);

            // check the cells that have the same min number of steppes
            List<Integer> cellsOfMinSteps =new ArrayList<Integer>();
            for (int i=0;i<adyPositions.size();i++){
                if (stepsInAdyCell.get(i)==minSteps){
                   cellsOfMinSteps.add(i);
                }
            }
            // if only one cell I give it straight
            if (cellsOfMinSteps.size()==1){
                return cellsOfMinSteps.get(0);
            } else{
                // returs a random position among the min stepped ones
               int index = (int) Math.floor(Math.random()*cellsOfMinSteps.size());
                return  cellsOfMinSteps.get(index);
            }
        }
    }

    /**
     * Funtion that removes the last movement to not go backwards
     * @param temporaryDirections -- availableActions (UO,RIGHT;DOWN;LEFT)
     * @param ady -- adyCells
     * @param direction -- last Action that tells me the direction from where I come
     * @return availableActions removing the last cell
     */
    private List<Action> removeLastMovement(List<Action> temporaryDirections, List<Cell> ady, Action direction ){
        switch (direction){
            case GO_UP:
                if (temporaryDirections.indexOf(GO_DOWN)!=-1){
                    ady.remove(temporaryDirections.indexOf(GO_DOWN));
                    temporaryDirections.remove(temporaryDirections.indexOf(GO_DOWN));
                }
                break;
            case GO_DOWN:
                if (temporaryDirections.indexOf(GO_UP)!=-1){
                    ady.remove(temporaryDirections.indexOf(GO_UP));
                    temporaryDirections.remove(temporaryDirections.indexOf(GO_UP));
                }
                break;
            case GO_RIGHT:
                if (temporaryDirections.indexOf(GO_LEFT)!=-1){
                    ady.remove(temporaryDirections.indexOf(GO_LEFT));
                    temporaryDirections.remove(temporaryDirections.indexOf(GO_LEFT));
                }
                break;
            case GO_LEFT:
                if (temporaryDirections.indexOf(GO_RIGHT)!=-1) {
                    ady.remove(temporaryDirections.indexOf(GO_RIGHT));
                    temporaryDirections.remove(temporaryDirections.indexOf(GO_RIGHT));
                }
                break;
        }
        return temporaryDirections;
    }



    /*+++++++++++++++++ +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/


    /**
     * Function that decides what next action to do
     * It decides :
     *   If there is no stepped adyacents cell I go there and if more than one then random among those.
     *   If all are stepped then I go to the randomly selected among the cells with the min num of steps among the ady
     *   cells.
     * @param tempAvailableActions - available actions after removing walls and origin
     * @param current - current position
     * @param walkedPos - list of all walked positions
     * @return - next action to do
     */

    public Action decideMove(List<Action> tempAvailableActions, Position current, List<Position> walkedPos) {
        List<Position> adyPositions = getPositionList(tempAvailableActions, current);
        Action move;
        int index;
        if (allStepped(adyPositions)) {
            index = randomMinStepped(adyPositions);

        }else{
            index= randomOftheNonSteppedCell(adyPositions);

        }

        move = tempAvailableActions.get(index);
        return move;

    }



}







