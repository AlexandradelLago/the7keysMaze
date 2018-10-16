package tws.keeper.solution;

import tws.keeper.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static tws.keeper.model.Action.*;

public class KeeperAI implements Keeper {

    private static final List<Action> availableActions = Arrays.asList( GO_RIGHT,GO_UP, GO_LEFT, GO_DOWN);
    private static List<Position> walkedPositions = new ArrayList<Position>();
    private static Action lastAction;

    /**
     * This Keeper Artificial Inteligence simply acts randomly
     *
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
        // celdas a donde puedo ir
        List<Integer> possibleDirections = Arrays.asList(new Integer[]{0,1,2,3 }); // ir a la derecha , arriba , izquierda o abajo
        List<Cell> adyacentes = adyacents(maze);
        // me voy a quitar la celda de donde vengo

        Integer indexOfPredecessor = adyacentes.indexOf(walkedPositions.get(walkedPositions.size()-1));
        direction(walkedPositions);


        if (numberOfCellType(adyacentes,Cell.PATH)>2){
            System.out.println("funcion para ver si llaves y puerta y guardar posicion de puerta");
        }
      /* if ((right==Cell.WALL)&&(up==Cell.WALL)){
            System.out.println("toco muro a la derecha");
            return availableActions.get(1);
        }else if ((right==Cell.WALL)&&(up==Cell.WALL)&&(left==Cell.WALL)){
            return availableActions.get(2);
        }*/
     //   return availableActions.get(3);
        // 1. HACER UN INDEZ EN ACTIONS
        // 2. GUARDAR
        //System.out.println(ThreadLocalRandom.current().nextInt(availableActions.size()));



        if ( isDoor(adyacentes) && maze.isMazeCompleted()){
            return availableActions.get(adyacentes.indexOf(Cell.DOOR));
        }
        return availableActions.get(ThreadLocalRandom.current().nextInt(availableActions.size()));
    }
    private Integer numberOfCellType(List<Cell> possiblePositions,Cell celltype){
        int celltypeCounter=0;
        for (Cell item: possiblePositions){
            if (item.toString().equals(celltype)){
                celltypeCounter++;
            }
        }
        return celltypeCounter;
    }
    private Boolean isDoor(List<Cell> possiblePositions){
        return possiblePositions.indexOf(Cell.DOOR)!=-1;
    }
    // me dice la celda anterior para no repetirla y no ir para atras- redundante-solo ir a una celda
    // que ya se ha pisado si no se puede ir a ningun otro sitio
    private Position direction(List<Position> walked){
        System.out.println("this is the walked size" + walked.size());
        return walked.get(walked.size()-1);
    }
    // funcion que me diga si ya he pasado
    private Boolean stepped(Position position, List<Position> walked){
        if (walked.indexOf(position)==-1){
            return false;
        } else{
            return true;
        }
    }
    // funcion que me diga si ya he pasado 2 veces
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