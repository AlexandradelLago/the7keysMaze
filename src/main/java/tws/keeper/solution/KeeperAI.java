package tws.keeper.solution;

import tws.keeper.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static tws.keeper.model.Action.*;

public class KeeperAI implements Keeper {

    private static final List<Action> availableActions = Arrays.asList(GO_UP, GO_RIGHT, GO_DOWN, GO_LEFT);
    private static List<Action> backtrackedActions;
    private static List<Position> walkedPositions = new ArrayList<Position>();
    private static Action lastAction;
    private static Boolean tracking = false;
    private static Position inicio = new Position(0,0);
    private static List<Position> keysFoundPosition =  Arrays.asList(inicio);


    /**
     * This Keeper acts according to the Tramoux algorithm
     * @param maze
     * @return
     */
    public Action act(Observable maze) {

        if (maze.getKeysFound()>1){
            System.out.println("solo para ver");
        }
        // actual position del keeper
        Position current = maze.getKeeperPosition();

        // añadire por donde voy pasando
        walkedPositions.add(current);
        int pasos = walkedPositions.size();

        // Listado de acciones
        List<Action> tempDir = new ArrayList<>();
        tempDir.addAll(availableActions);    // celdas a donde puedo ir ( GO_UP, GO_RIGHT, GO_DOWN, GO_LEFT, );

        // que tengo en cada celda alrededor
        List<Cell> adyacentes = adyacents(maze);

        if (isDoor(adyacentes)){
            maze.getKeysFound();

            if(maze.isMazeCompleted()){
                // 1. SI ES PUERTA Y TENGO TODAS LAS LLAVES TERMINO
                return availableActions.get(adyacentes.indexOf(Cell.DOOR));
            } else{

                // 2. ME QUITO LOS MUROS
                removeWall(adyacentes, tempDir);
                // VER LAS POSICIONE
                if ((tempDir.size()>1)&&(pasos>1)){ // con esto me quito deadends
                    tempDir = removeLastMovement(tempDir,lastAction); //  3. ME QUITO DE DONDE VENGO  -- HEC
                }
                lastAction=decideMove(tempDir,current,adyacentes,maze);
                if (tracking) {
                    tracking(lastAction);
                }else{
                    tracking =true;
                }
                return lastAction;
            }
        }else {
            removeWall(adyacentes, tempDir);
            // VER LAS POSICIONES
            if ((tempDir.size()>1)&&(pasos>1)){ // con esto me quito deadends
                tempDir = removeLastMovement(tempDir,lastAction); //  3. ME QUITO DE DONDE VENGO  -- HEC
            }
            lastAction=decideMove(tempDir,current,adyacentes,maze);

            if (tracking){
                tracking(lastAction);
            }
            return lastAction;
        }

    }




    /*************************** FUNCIONES ******************************************************************/


    private Action decideMove(List<Action> temporaryDirections, Position current, List<Cell> adyacentes, Observable maze){
          /*
             miro si hay llaves y si las llaves no estan en mi array de llaves encontradas- meto las llaves que haya en posiciones encontradas-
              // ME FALTA COMTEMPLAR que haya mas de una llaves a mano y guardarme la posicion para ir luego
               si ya estan en posiciones encontradas no hago nada
                 // voy a la que no haya pisado y a la primera que encuentro- TENGO QUE AÑADIR ALEATORIEDAD -- minSteppedCell
               7. miro si he pisado las casillas - si no he pisado ninguna aleatorio
               8. si he pisado alguna voy a la que no he pisado y si hay varias ue no he pisado entonces aleatorio
        */
          if (temporaryDirections.size()==1){ // 5. SI SOLO TENGO UNA VOY A ESA
              return temporaryDirections.get(0);
          }else {
              //  posiciones de mis adyacentes
              List<Position> tempPosList = getPositionList(temporaryDirections,current);


              int keyIndex=0;
              int keysAround = 0;
              for (int i= 0; i<adyacentes.size();i++){
                  if (adyacentes.get(i) == Cell.KEY) {
                      Position keyPosition = getPosition(temporaryDirections.get(i), current);
             //         if (!keysFoundPosition.contains(keyPosition)) {
                          keysFoundPosition.add((Position) keyPosition);
                          keysAround += 1;
                          keyIndex = i;
              //        }
                  }
              }

              if (keysAround!=0){
                  return temporaryDirections.get(keyIndex);
              }else{

                  return temporaryDirections.get(minSteppedCell(tempPosList,current));
              }
          }
        // return availableActions.get(ThreadLocalRandom.current().nextInt(availableActions.size()));
    }

    // mira las adyacentes y ve que CELDA es
    private List<Cell> adyacents(Observable maze){
        List<Cell> adyacentes= new ArrayList<Cell>();
        // ( GO_UP, GO_RIGHT, GO_DOWN, GO_LEFT, );
        adyacentes.add(maze.lookUp());
        adyacentes.add(maze.lookRight());
        adyacentes.add(maze.lookDown());
        adyacentes.add(maze.lookLeft());
        return adyacentes;
    }

    // posicion de una accion dependiendo de la adyacencia
    private Position getPosition(Action tempDir, Position current){
        Position nextPos = null;
        switch (tempDir){
            case GO_UP:
                nextPos= new Position(current.getVertical()- 1, current.getHorizontal());
                break;
            case GO_DOWN:
                nextPos = new Position(current.getVertical() + 1, current.getHorizontal());
                break;
            case GO_RIGHT:
                nextPos = new Position(current.getVertical(), current.getHorizontal() + 1);
                break;
            case GO_LEFT:
                nextPos = new Position(current.getVertical() , current.getHorizontal() - 1);
                break;
            case DO_NOTHING:
                nextPos= new Position(current.getVertical(), current.getHorizontal());
                break;
        }
        return nextPos;
    }
    private List<Position> getPositionList(List<Action> temporaryDirections , Position current){
        List<Position> tempPosList = new ArrayList<Position>();
        for (int i=0;i<temporaryDirections.size();i++ ){
            tempPosList.add(getPosition(temporaryDirections.get(i),current));
        }
        return tempPosList;
    }

    // esta me mira cuantas veces he pasado
    private Integer minSteppedCell(List<Position> tempPosList, Position current){
        Position pos = tempPosList.get(0);
        int min = timesStepped(pos,walkedPositions);
        int tempDirIndex=0;
        for (int i=0 ;i<tempPosList.size();i++ ){
            int newMin = timesStepped(tempPosList.get(i),walkedPositions);
            if (newMin < min){
                pos = tempPosList.get(i);
                min = newMin;
                tempDirIndex=i;
            }
        }
        return tempDirIndex;
    }
    private Boolean stepped(Position position, List<Position> walked){
        if (walked.indexOf(position)==-1){
            return false;
        } else{
            return true;
        }
    }
    // funcion que me diga si ya he pasado 2 veces o mas??
    private Integer timesStepped(Position position, List<Position> walked){
        int stepped=0;
        for (Position item : walked) {
            if (item.equals(position)){
                stepped++;
            }
            System.out.println(item);
        }
        return stepped;
    }
    // funcion que mira alrededor

    private List<Action> removeLastMovement(List<Action> temporaryDirections, Action direction ){
        switch (direction){
            case GO_UP:
                if (temporaryDirections.indexOf(GO_DOWN)!=-1) temporaryDirections.remove(temporaryDirections.indexOf(GO_DOWN));
                break;
            case GO_DOWN:
                if (temporaryDirections.indexOf(GO_UP)!=-1) temporaryDirections.remove(temporaryDirections.indexOf(GO_UP));
                break;
            case GO_RIGHT:
                if (temporaryDirections.indexOf(GO_LEFT)!=-1) temporaryDirections.remove(temporaryDirections.indexOf(GO_LEFT));
                break;
            case GO_LEFT:
                if (temporaryDirections.indexOf(GO_RIGHT)!=-1) {
                    temporaryDirections.remove(temporaryDirections.indexOf(GO_RIGHT));
                }
                break;
        }
        return temporaryDirections;
    }

    // este lo voy a usar en el backtrack
    private void tracking(Action action){
        backtrackedActions.add(action);
        // funcion para ir guardando las posiciones por las que pasa desde que encuentra la puerta hasta que encuentra la última llave
        // si encuentro la puerta despues de haber encontrado la llave no se hace nada de esto
    }
    private Action switchAction (Action act){

        switch (act){
            case GO_UP:
                act = GO_DOWN;
                break;
            case GO_DOWN:
                act = GO_UP;
                break;
            case GO_RIGHT:
                act = GO_LEFT;
                break;
            case GO_LEFT:
                act = GO_RIGHT;
                break;
        }
        return act;
    }

    private Position lastPosition(List<Position> movements){
        return movements.get(movements.size()-1);
    }

    // funcion que me elimina los muros y tb debo decirle que elimine la direccion de donde vengo
    private void removeWall(List<Cell> adyacentCells ,List<Action> indexDir){

        int size = adyacentCells.size();
        ArrayList<Integer> indexDirec = new ArrayList<>();
        for(int i=0; i<size; i++){
            if (adyacentCells.get(i).equals(Cell.WALL)){
                indexDirec.add((int) i);
            }
        }
        // elimino al reves para no crear excepciones nunca
        for (int i=(indexDirec.size()-1);i>=0;i--){
            adyacentCells.remove((int) indexDirec.get(i));
            indexDir.remove((int)indexDirec.get(i));
        }

    }
    private Boolean isDoor(List<Cell> possiblePositions){
        return possiblePositions.indexOf(Cell.DOOR)!=-1;
    }
    private Boolean isKey(List<Cell> possiblePositions){
        return possiblePositions.indexOf(Cell.KEY)!=-1;
    }
}







