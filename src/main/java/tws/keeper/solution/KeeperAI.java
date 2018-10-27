package tws.keeper.solution;

import tws.keeper.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static tws.keeper.model.Action.*;

public class KeeperAI implements Keeper {
    private static final List<Action> availableActions = Arrays.asList(GO_UP, GO_RIGHT, GO_DOWN, GO_LEFT);
    private static List<Action> backtrackedActions= new ArrayList<Action>();
    private static List<Position> walkedPositions = new ArrayList<Position>();
    private static Action lastAction;
    private static Boolean tracking = false;
    private static List<Position> keysFoundPosition = new ArrayList<Position>();

    public KeeperAI() {
        Position inicio = new Position(0,0);
        keysFoundPosition.add(inicio);
        backtrackedActions.add(DO_NOTHING);
    }

    /**
     * This Keeper acts according to the Tramoux algorithm
     * @param maze
     * @return
     */
    public Action act(Observable maze) {
        System.out.println(backtrackedActions);

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

        // if I found all keys and I have already crossed by the door before then backtrack
        if (maze.getKeysFound()==maze.getTotalNumberOfKeys()&&tracking){
            return backtrack();
            // if I find the door and
        }else if(isDoor(adyacentes) && (maze.getKeysFound()==maze.getTotalNumberOfKeys())){
            // 1. SI ES PUERTA Y TENGO TODAS LAS LLAVES TERMINO
            System.out.println("aqui esta el maze complete y estoy al lado de la puerta");
            return availableActions.get(adyacentes.indexOf(Cell.DOOR));
        }else if (isDoor(adyacentes)){

            removeCell(adyacentes,Cell.WALL,tempDir);
            removeCell(adyacentes,Cell.DOOR,tempDir);
        //    lastAction=decideMoveRightHand(tempDir,current,adyacentes,maze, lastAction);
            lastAction=decideMove(tempDir,current,adyacentes,maze);

           if (tracking) {
                backtrackedActions.clear();
                backtrackedActions.add(lastAction);
            }else{
                tracking =true;
                backtrackedActions.add(lastAction);
            }
            return lastAction;

        }else {
        removeCell(adyacentes,Cell.WALL, tempDir);

             if ((tempDir.size()>1)&&(pasos>1)){ // con esto me quito deadends -no me quito de donde vengo entonces no me quedo en dead ends
              tempDir = removeLastMovement(tempDir,adyacentes,lastAction); //  3. ME QUITO DE DONDE VENGO  -- HEC
            }

           lastAction=decideMove(tempDir,current,adyacentes,maze);
            //lastAction=decideMoveRightHand(tempDir,current,adyacentes,maze, lastAction);

            if (tracking){
                backtrackedActions.add(lastAction);
            }

            return lastAction;
        }

    }


    /*************************** FUNCIONES ******************************************************************/

    private Action  backtrack(){
        Action backtrackAction = switchAction(backtrackedActions.get(backtrackedActions.size()-1));
        backtrackedActions.remove(backtrackedActions.size()-1);
        if (backtrackedActions.size()==1){
            tracking=false;
        }
        return backtrackAction;
    }

    // in case i wanna do the mano en la derecha
    private Action decideMoveRightHand(List<Action> temporaryDirections, Position current, List<Cell> adyacentes, Observable maze,Action act){
        if (temporaryDirections.size()==1){ // 5. SI SOLO TENGO UNA VOY A ESA
            return temporaryDirections.get(0);
        }else {
            return temporaryDirections.get(moveRight(temporaryDirections,lastAction));
        }

    }
    private Integer moveRight(List<Action> tempDir, Action dir){
        switch (dir){
            case GO_UP:
                if (tempDir.contains(GO_RIGHT)){
                    dir=GO_RIGHT;
                }else if (tempDir.contains(GO_UP)){
                    dir=GO_UP;
                }else if (tempDir.contains(GO_LEFT)){
                    dir=GO_LEFT;
                }
                break;
            case GO_RIGHT:
                if (tempDir.contains(GO_DOWN)){
                    dir=GO_DOWN;
                }else if (tempDir.contains(GO_RIGHT)){
                    dir=GO_RIGHT;
                }else if (tempDir.contains(GO_UP)){
                    dir=GO_UP;
                }
                break;
            case GO_DOWN:
                if (tempDir.contains(GO_LEFT)){
                    dir=GO_LEFT;
                }else if (tempDir.contains(GO_DOWN)){
                    dir=GO_DOWN;
                }else if (tempDir.contains(GO_RIGHT)){
                    dir=GO_RIGHT;
                }
                break;
            case GO_LEFT:
                if (tempDir.contains(GO_UP)){
                    dir=GO_UP;
                }else if (tempDir.contains(GO_LEFT)){
                    dir=GO_LEFT;
                }else if (tempDir.contains(GO_DOWN)){
                    dir=GO_DOWN;
                }
                break;
        }
        return tempDir.indexOf(dir);
    }


    private Action decideMove(List<Action> temporaryDirections, Position current, List<Cell> adyacentes, Observable maze){
          /*
             miro si hay llaves y si las llaves no estan en mi array de llaves encontradas- meto las llaves que haya en posiciones encontradas-

              // ME FALTA COMTEMPLAR que haya mas de una llaves a mano y guardarme la posicion para ir luego

               si ya estan en posiciones encontradas no hago nada
                 // voy a la que no haya pisado y a la primera que encuentro- TENGO QUE AÑADIR ALEATORIEDAD -- minSteppedCell
               7. miro si he pisado las casillas - si no he pisado ninguna ALEATORIO - por ahora va ala  primera que no he pisado
               8. si he pisado alguna voy a la que no he pisado y si hay varias ue no he pisado entonces aleatorio
        */
          if (temporaryDirections.size()==1){ // 5. SI SOLO TENGO UNA VOY A ESA
              return temporaryDirections.get(0);
          }else {
              //  posiciones de mis adyacentes
              List<Position> tempPosList = getPositionList(temporaryDirections,current);
              int keyIndex=0;
              int keysAround = 0;
              // REVISAR -- las llaves aqui cojo solo la primera -- optimizar
              for (int i= 0; i<adyacentes.size();i++){
                  if (adyacentes.get(i) == Cell.KEY) {
                      Position keyPosition = getPosition(temporaryDirections.get(i), current);
             //         if (!keysFoundPosition.contains(keyPosition)) {
                          keysFoundPosition.add(keyPosition);
                          keysAround += 1;
                          keyIndex = i;
              //        }
                  }
              }

              if (keysAround!=0){
                  System.out.println("las adyacentes " + adyacentes);
                  System.out.println("this is keys around " + keysAround);
                  System.out.println("las direcciones posibles " +temporaryDirections);
                  System.out.println("la direccion elegida " + keyIndex);
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
        switch (tempDir){
            case GO_UP:
                return new Position(current.getVertical()- 1, current.getHorizontal());
            case GO_DOWN:
                return new Position(current.getVertical() + 1, current.getHorizontal());
            case GO_RIGHT:
                return new Position(current.getVertical(), current.getHorizontal() + 1);
            case GO_LEFT:
                return new Position(current.getVertical() , current.getHorizontal() - 1);
            case DO_NOTHING:
                return new Position(current.getVertical(), current.getHorizontal());
        }
        return current;
    }
    private List<Position> getPositionList(List<Action> temporaryDirections , Position current){
        List<Position> tempPosList = new ArrayList<Position>();
        for (int i=0;i<temporaryDirections.size();i++ ){
            tempPosList.add(getPosition(temporaryDirections.get(i),current));
        }
        return tempPosList;
    }

    // esta me mira cuantas veces he pasado - por ahora me coge el primer minimo - osea va normalmente
    // arriba si no derecha si no abajo si no izquierda - poner aleatoriedad de los que sean iguales
    private Integer minSteppedCell(List<Position> tempPosList, Position current){
        Position pos = tempPosList.get(0);
        ArrayList<Integer> stepsPerCell = new ArrayList<Integer>();
        stepsPerCell.clear();
        int min = timesStepped(pos,walkedPositions);
        stepsPerCell.add(min);

        int tempDirIndex=0;
        for (int i=1 ;i<tempPosList.size();i++ ){
            int newMin = timesStepped(tempPosList.get(i),walkedPositions);
            stepsPerCell.add(newMin);
            if (stepsPerCell.get(i) < min){
                min = newMin;
                tempDirIndex=i;
            }
        }
        // tengo array con el número de veces que se ha pisado cada celda de alrededor [1,2,1]
        // tengo el indice de la celda con menos pisadas
        ArrayList<Integer> cellWithsameMinSteps = new ArrayList<Integer>();
        for (int i= 0; i< stepsPerCell.size();i++){
            if (stepsPerCell.get(i)==stepsPerCell.get(tempDirIndex)){
                cellWithsameMinSteps.add(i);
            }
        }
        int index;
      //  System.out.println(min);
      //  System.out.println(stepsPerCell);
      //  System.out.println(cellWithsameMinSteps);
        index = cellWithsameMinSteps.get(ThreadLocalRandom.current().nextInt(cellWithsameMinSteps.size()));
        //System.out.println(index);
        return index;

       // return tempDirIndex;
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
        }
        return stepped;
    }
    // funcion que mira elimina de donde vengo si tengo mas opciones
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
    private void removeCell(List<Cell> adyacentCells , Cell celltoremove, List<Action> indexDir){

        int size = adyacentCells.size();
        ArrayList<Integer> indexDirec = new ArrayList<>();
        for(int i=0; i<size; i++){
            if (adyacentCells.get(i).equals(celltoremove)){
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







