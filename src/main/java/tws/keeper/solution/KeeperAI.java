package tws.keeper.solution;

import tws.keeper.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static tws.keeper.model.Action.*;

public class KeeperAI implements Keeper {

    private static final List<Action> availableActions = Arrays.asList( GO_RIGHT,GO_UP, GO_LEFT, GO_DOWN);
    private static List<Position> walkedPositions = new ArrayList<Position>();
    private static Action lastAction;

    /**
     * This Keeper Artificial Inteligence simply acts randomly
     * @param maze
     * @return
     */
  int count=0;
    public Action act(Observable maze) {
        // actual position del keeper
        Position current = maze.getKeeperPosition();
        // añadire por donde voy pasando
        walkedPositions.add(current);
        Integer pasos = walkedPositions.size();
        System.out.println("estos son los pasos " + pasos + "y este es el contador " + count + " y esto es los walked " + walkedPositions);
        // celdas a donde puedo ir // ir a la derecha , arriba , izquierda o abajo NO SERIA MEJOR USR EL ENUM CELL EN LUGAR DE ESTO???
        List<Integer> possibleDirections = new ArrayList<>(Arrays.asList(0,1,2,3));
        List<Cell> adyacentes = adyacents(maze);
        // AQUI ME QUITO LOS MUROS Y DEBERIA METER AHI DENTRO QQUITAAR LA CELDA DE DONDE VENGO
        toPossible(adyacentes,Cell.WALL,possibleDirections);


        // 1. HACER FUNCION PRA QUITARME LA CEDLDA DE DONDE VENGO
        Integer indexOfPredecessor = adyacentes.indexOf(walkedPositions.get(walkedPositions.size()-1));


        /*  1. SI ES PUERTA Y TENGO TODAS LAS LLAVES TERMINO
            2. ME QUITO LOS MUROS  - HECHO
            3. ME QUITO DE DONDE VENGO
            4. MIRO SI TENGO CELDAAS QUE NO HAYA PASADO -  SI YA HE PASADO ME LA QUITO - FUNCION QUE CALCULA LA POSITION DE LAS CELDAS A LAS QUE PUEDO OPTAR
               5. SI SOLO TENGO UNA VOY A ESA
               6. SI TENGO DOS Y UNA ES LLAVE VOY A ESA
               7. SI NINGUNA ES LLAVE ENTONCES ALEATORIO
               8. SI ES LLAVE - GUARDO POSITION DE LLAVE ENCONTRADA Y AÑADO NUMERO DE LLAVES ENCONTRADAS
               9. GUARDO LA DIRECCION DE LA LLAVE ENCONTRADA.

        */

       if ( isDoor(adyacentes) && maze.isMazeCompleted()){
            return availableActions.get(adyacentes.indexOf(Cell.DOOR));
        }


        if (count==0){
            count++;
            return availableActions.get(0);
        }else if (count==1){
            count++;
            return availableActions.get(1);
        }else if (count==2){
            count++;
            return availableActions.get(2);
        }else{
            count++;
            return availableActions.get(3);
        }
       // return availableActions.get(ThreadLocalRandom.current().nextInt(availableActions.size()));
    }


    /*************************** FUNCIONES ******************************************************************/

    // funcion que me elimina los muros y tb debo decirle que elimine la direccion de donde vengo
    private void toPossible(List<Cell> possiblePositions,Cell celltypetoRemove,List<Integer> directions){
        int size = possiblePositions.size();
        ArrayList<Integer> indexDirec = new ArrayList<>();
        for(int i=0; i<size; i++){
            if (possiblePositions.get(i).equals(celltypetoRemove)){
                indexDirec.add(i);
            }
        }
        // elimino al reves para no crear excepciones nunca
        for (int i=(indexDirec.size()-1);i>=0;i--){
           directions.remove( (int) indexDirec.get(i));
       }

    }
    private Boolean isDoor(List<Cell> possiblePositions){
        return possiblePositions.indexOf(Cell.DOOR)!=-1;
    }
    // me dice la celda anterior para no repetirla y no ir para atras- redundante-solo ir a una celda
    // que ya se ha pisado si no se puede ir a ningun otro sitio
    // ++++++++++++++++
    private Position direction(List<Position> walked){
        System.out.println("this is the walked size" + walked.size());
        return walked.get(walked.size()-1);
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
    private Boolean steppedTwice(Position position, List<Position> walked){
        int stepped=0;
        for (Position item : walked) {
            if (item.equals(position)){
                stepped++;
            }
            System.out.println(item);
        }
        return stepped==2;
    }
    // funcion que mira alrededor
    private List<Cell> adyacents(Observable maze){
        List<Cell> adyacentes= new ArrayList<Cell>();
        adyacentes.add(maze.lookRight());
        adyacentes.add(maze.lookUp());
        adyacentes.add(maze.lookLeft());
        adyacentes.add(maze.lookDown());
        return adyacentes;
    }
}