package tws.keeper.solution;

import tws.keeper.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static tws.keeper.model.Action.*;

public class KeeperAI implements Keeper {

    private static final List<Action> availableActions = Arrays.asList(GO_UP, GO_RIGHT, GO_DOWN, GO_LEFT);
    private static List<Position> walkedPositions = new ArrayList<Position>();
    private static Action lastAction;
    private static Boolean backtrack = false;
    /**
     * This Keeper acts according to the Tramoux algorithm
     * @param maze
     * @return
     */

  int count=0;
    public Action act(Observable maze) {

        // actual position del keeper
        Position current = maze.getKeeperPosition();
        // añadire por donde voy pasando
        walkedPositions.add(current);
        int pasos = walkedPositions.size();

        List<Action> tempDir = new ArrayList<>(); // celdas a donde puedo ir
        tempDir.addAll(availableActions);    //( GO_UP, GO_RIGHT, GO_DOWN, GO_LEFT, );
        //  ArrayList<Integer>  = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        List<Cell> adyacentes = adyacents(maze);
        if (isDoor(adyacentes) && maze.isMazeCompleted()) {  // 1. SI ES PUERTA Y TENGO TODAS LAS LLAVES TERMINO
            return availableActions.get(adyacentes.indexOf(Cell.DOOR));
        } else { // si es mi primer movimiento
            // 2. ME QUITO LOS MUROS
            toPossibleMove(adyacentes, Cell.WALL, tempDir);
            // VER LAS POSICIONES
            if (pasos > 1)  tempDir = removeLastMovement(tempDir,lastAction); //  3. ME QUITO DE DONDE VENGO  -- HECHO
        }
        lastAction=decideMove(tempDir,current);
        return lastAction;
    }




    /*************************** FUNCIONES ******************************************************************/
    private Action decideMove(List<Action> temporaryDirections, Position current){
          /*
            4. MIRO SI TENGO CELDAAS QUE NO HAYA PASADO -  SI YA HE PASADO ME LA QUITO - FUNCION QUE CALCULA LA POSITION DE LAS CELDAS A LAS QUE PUEDO OPTAR

               6. SI TENGO DOS Y UNA ES LLAVE VOY A ESA
               7. SI NINGUNA ES LLAVE ENTONCES ALEATORIO
               8. SI ES LLAVE - GUARDO POSITION DE LLAVE ENCONTRADA Y AÑADO NUMERO DE LLAVES ENCONTRADAS
               9. GUARDO LA DIRECCION DE LA LLAVE ENCONTRADA.
        */
          if (temporaryDirections.size()==1){ // 5. SI SOLO TENGO UNA VOY A ESA
              return temporaryDirections.get(0);
          }else {

              //  List<Position> tempPos = new ArrayList<Position>();
              // AÑADIR UNA CAPA FUERA PARA VER SI ES KEY Y GUARDAR ESA POSICION

              // voy a la que no haya pisado y a la primera que encuentro-  AQUI DEBERIA AÑADIR LO DE LAS LLAVES
              // Y ALEATORIEDAD
              for (int i=0;i<temporaryDirections.size();i++ ){
                  Position tempPos = getPosition(temporaryDirections.get(i),current);
                  //tempPos.add();
                  if (!stepped(tempPos,walkedPositions)){
                      return temporaryDirections.get(i);
                  }
              }

              // voy a la que tenga menos pisadas - AQUI LAS LLAVES YA LAS HABRIA COGIDO
              Position pos = getPosition(temporaryDirections.get(0),current);
              int min = timesStepped(pos,walkedPositions);
              int tempDirIndex=0;

              for (int i=0 ;i<temporaryDirections.size();i++ ){
                  Position tempPos = getPosition(temporaryDirections.get(i),current);
                  int newMin = timesStepped(tempPos,walkedPositions);
                  if (newMin < min){
                      pos = tempPos;
                      min = newMin;
                      tempDirIndex=i;
                  }
              }
              return temporaryDirections.get(tempDirIndex);
          }
        // return availableActions.get(ThreadLocalRandom.current().nextInt(availableActions.size()));
    }



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
    // funcion que me elimina los muros y tb debo decirle que elimine la direccion de donde vengo
    private void toPossibleMove(List<Cell> possiblePositions,Cell celltypetoRemove,List<Action> indexDir){

        int size = possiblePositions.size();
        ArrayList<Integer> indexDirec = new ArrayList<>();
        for(int i=0; i<size; i++){
            if (possiblePositions.get(i).equals(celltypetoRemove)){
                indexDirec.add((int) i);
            }
        }
        // elimino al reves para no crear excepciones nunca
        for (int i=(indexDirec.size()-1);i>=0;i--){
            // error de remove no se porqueeee

           indexDir.remove((int)indexDirec.get(i));
       }

    }
    private Boolean isDoor(List<Cell> possiblePositions){
        return possiblePositions.indexOf(Cell.DOOR)!=-1;
    }
    private Boolean isKey(List<Cell> possiblePositions){
        return possiblePositions.indexOf(Cell.KEY)!=-1;
    }
    // me dice la celda anterior para no repetirla y no ir para atras- redundante-solo ir a una celda
    // que ya se ha pisado si no se puede ir a ningun otro sitio
    // ++++++++++++++++
    private Position lastPosition(List<Position> movements){
        return movements.get(movements.size()-1);
    }
    // funcion que me diga si ya he pasado alguna vez
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
    private List<Cell> adyacents(Observable maze){
        List<Cell> adyacentes= new ArrayList<Cell>();
       // ( GO_UP, GO_RIGHT, GO_DOWN, GO_LEFT, );
        adyacentes.add(maze.lookUp());
        adyacentes.add(maze.lookRight());
        adyacentes.add(maze.lookDown());
        adyacentes.add(maze.lookLeft());
        return adyacentes;
    }
}







