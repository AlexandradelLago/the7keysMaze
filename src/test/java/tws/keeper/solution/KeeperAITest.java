package tws.keeper.solution;

import org.junit.Assert;
import org.junit.Test;
import tws.keeper.model.Action;
import tws.keeper.model.Cell;
import tws.keeper.model.Position;

import java.util.ArrayList;
import java.util.List;

public class KeeperAITest {


    @Test
    public void steppedTest() {

        System.out.println("TEST 1");
        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.clear();
        keeper.walkedPositions.add(new Position (3,2));
        keeper.walkedPositions.add(new Position (3,3));
        keeper.walkedPositions.add(new Position (3,4));
        keeper.walkedPositions.add(new Position (2,4));
        keeper.walkedPositions.add(new Position (1,4));
        keeper.walkedPositions.add(new Position (1,3));
        keeper.walkedPositions.add(new Position (1,4));


        Assert.assertEquals(true, keeper.stepped( new Position(2,4),keeper.walkedPositions));
        Assert.assertEquals(true, keeper.stepped( new Position(1,3),keeper.walkedPositions));

    }


    @Test
    public void decideMoveTest1ShortEnd() {

        System.out.println("TEST 1");
        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.clear();
        keeper.walkedPositions.add(new Position (1,2));
        keeper.backtrackCount=0;
        List<Action> tempAvailableActions = new ArrayList<Action>();
        tempAvailableActions.add(Action.GO_RIGHT);
        tempAvailableActions.add(Action.GO_DOWN);

        Action x = keeper.decideMove(tempAvailableActions , new Position(1,2),keeper.walkedPositions);

       Assert.assertTrue( x == Action.GO_RIGHT||x== Action.GO_DOWN);

    }

    @Test
    public void decideMoveTest2ShortEnd() {

        System.out.println("TEST 2");

        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.clear();
        keeper.walkedPositions.add(new Position (1,2));
        keeper.walkedPositions.add(new Position (1,3));

        keeper.backtrackCount=0;
        List<Action> tempAvailableActions = new ArrayList<Action>();
        tempAvailableActions.add(Action.GO_LEFT);

        Action x = keeper.decideMove(tempAvailableActions , new Position(1,3),keeper.walkedPositions);

        Assert.assertTrue( x == Action.GO_LEFT);

    }

    @Test
    public void decideMoveTest3ShortEnd() {

        System.out.println("TEST 3");
        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.clear();
        keeper.walkedPositions.add(new Position (1,2));
        keeper.walkedPositions.add(new Position (1,3));
        keeper.walkedPositions.add(new Position (1,2));

        keeper.backtrackCount=1;
        List<Action> tempAvailableActions = new ArrayList<Action>();

        tempAvailableActions.add(Action.GO_RIGHT);
        tempAvailableActions.add(Action.GO_DOWN);

        Action x = keeper.decideMove(tempAvailableActions , new Position(1,2),keeper.walkedPositions);

        Assert.assertTrue( x == Action.GO_DOWN);

    }


    @Test
    public void allSteppedFalse() {

        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.clear();
        keeper.walkedPositions.add(new Position (1,2));
        keeper.walkedPositions.add(new Position (1,3));
        keeper.walkedPositions.add(new Position (1,2));
        List<Position> adyPosition = new ArrayList<Position>();
        adyPosition.add(new Position(1,3));
        adyPosition.add(new Position(2,2));

        Assert.assertEquals(false, keeper.allStepped(adyPosition));

    }



    @Test
    public void decideMoveTestDeadEnd() {

        System.out.println("TEST 1");

        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.clear();
        keeper.walkedPositions.add(new Position (3,2));
        keeper.walkedPositions.add(new Position (3,3));
        keeper.walkedPositions.add(new Position (3,4));
        keeper.walkedPositions.add(new Position (2,4));
        keeper.walkedPositions.add(new Position (1,4));
        keeper.walkedPositions.add(new Position (1,3));

        keeper.backtrackCount=0;
        List<Action> tempAvailableActions = new ArrayList<Action>();
        tempAvailableActions.add(Action.GO_RIGHT);
        Assert.assertEquals(Action.GO_RIGHT, keeper.decideMove(tempAvailableActions , new Position(1,3),keeper.walkedPositions));

    }

    @Test
    public void decideMoveBackTrack1() {

        System.out.println("TEST 2");

        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.clear();
        keeper.walkedPositions.add(new Position (3,2));
        keeper.walkedPositions.add(new Position (3,3));
        keeper.walkedPositions.add(new Position (3,4));
        keeper.walkedPositions.add(new Position (2,4));
        keeper.walkedPositions.add(new Position (1,4));
        keeper.walkedPositions.add(new Position (1,3));
        keeper.walkedPositions.add(new Position (1,4));

        keeper.backtrackCount=1;

        List<Action> tempAvailableActions = new ArrayList<Action>();
        // in the order of looking up, right, down, left
        tempAvailableActions.add(Action.GO_DOWN);
        tempAvailableActions.add(Action.GO_LEFT);
        Assert.assertEquals(Action.GO_DOWN, keeper.decideMove(tempAvailableActions , new Position(1,4),keeper.walkedPositions));
    }


    @Test
    public void decideMoveBackTrack2() {
        System.out.println("TEST 3");

        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.clear();
        keeper.walkedPositions.add(new Position (3,2));
        keeper.walkedPositions.add(new Position (3,3));
        keeper.walkedPositions.add(new Position (3,4));
        keeper.walkedPositions.add(new Position (2,4));
        keeper.walkedPositions.add(new Position (1,4));
        keeper.walkedPositions.add(new Position (1,3));
        keeper.walkedPositions.add(new Position (1,4));
        keeper.walkedPositions.add(new Position (2,4));

        keeper.backtrackCount=2;
        List<Action> tempAvailableActions = new ArrayList<Action>();
        // in the order of looking up, right, down, left
        tempAvailableActions.add(Action.GO_UP);
        tempAvailableActions.add(Action.GO_DOWN);
        Assert.assertEquals(Action.GO_DOWN, keeper.decideMove(tempAvailableActions , new Position(2,4),keeper.walkedPositions));
    }


    @Test
    public void decideMoveBackTrack3() {

        System.out.println("TEST 4");

        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.clear();
        keeper.walkedPositions.add(new Position (3,2));
        keeper.walkedPositions.add(new Position (3,3));
        keeper.walkedPositions.add(new Position (3,4));
        keeper.walkedPositions.add(new Position (2,4));
        keeper.walkedPositions.add(new Position (1,4));
        keeper.walkedPositions.add(new Position (1,3));
        keeper.walkedPositions.add(new Position (1,4));
        keeper.walkedPositions.add(new Position (2,4));
        keeper.walkedPositions.add(new Position (3,4));

        keeper.backtrackCount=3;
        List<Action> tempAvailableActions = new ArrayList<Action>();
        // in the order of looking up, right, down, left
        tempAvailableActions.add(Action.GO_RIGHT);
        tempAvailableActions.add(Action.GO_LEFT);
        Assert.assertEquals(Action.GO_RIGHT, keeper.decideMove(tempAvailableActions , new Position(3,4),keeper.walkedPositions));
    }



    @Test
    public void decideMoveBackTrackFinish() {
        System.out.println("TEST 5");

        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.clear();
        keeper.walkedPositions.add(new Position (3,2));
        keeper.walkedPositions.add(new Position (3,3));
        keeper.walkedPositions.add(new Position (3,4));
        keeper.walkedPositions.add(new Position (2,4));
        keeper.walkedPositions.add(new Position (1,4));
        keeper.walkedPositions.add(new Position (1,3));
        keeper.walkedPositions.add(new Position (1,4));
        keeper.walkedPositions.add(new Position (2,4));
        keeper.walkedPositions.add(new Position (3,4));
        keeper.walkedPositions.add(new Position (3,5));

        keeper.backtrackCount=4;
        List<Action> tempAvailableActions = new ArrayList<Action>();
        // in the order of looking up, right, down, left
        tempAvailableActions.add(Action.GO_RIGHT);
        tempAvailableActions.add(Action.GO_LEFT);
        Assert.assertEquals(Action.GO_RIGHT, keeper.decideMove(tempAvailableActions , new Position(3,5),keeper.walkedPositions));
    }



    @Test
    public void returnsRandomIndexOfCells2and4NoStepped() {

        KeeperAI keeper = new KeeperAI();
        List<Position> adyPosition = new ArrayList<Position>();

        adyPosition.add(new Position(2,8));
        adyPosition.add(new Position(0,6));
        adyPosition.add(new Position(4,8));
        adyPosition.add(new Position(10,8));

        keeper.walkedPositions.add(new Position (5,7));
        keeper.walkedPositions.add(new Position (5,6));
        keeper.walkedPositions.add(new Position (5,8));
        keeper.walkedPositions.add(new Position (4,8));
        keeper.walkedPositions.add(new Position (3,8));
        keeper.walkedPositions.add(new Position (2,8));


        int x = keeper.randomOftheNonSteppedCell(adyPosition);
        Assert.assertTrue( x==1 || x==3);

    }

    @Test
    public void returnsRandomIndexOf2CellsNoStepped() {

        KeeperAI keeper = new KeeperAI();
        List<Position> adyPosition = new ArrayList<Position>();

        adyPosition.add(new Position(1,3));
        adyPosition.add(new Position(2,2));

        keeper.walkedPositions.clear();
        keeper.walkedPositions.add(new Position (1,2));



        int x = keeper.randomOftheNonSteppedCell(adyPosition);
        Assert.assertTrue( x==0 || x==1);

    }

    @Test
    public void returnsRandomIndexOf3CellsNoStepped() {

        KeeperAI keeper = new KeeperAI();
        List<Position> adyPosition = new ArrayList<Position>();

        adyPosition.add(new Position(10,8));
        adyPosition.add(new Position(0,6));
        adyPosition.add(new Position(2,8));
        adyPosition.add(new Position(20,8));

        keeper.walkedPositions.add(new Position (5,7));
        keeper.walkedPositions.add(new Position (5,6));
        keeper.walkedPositions.add(new Position (5,8));
        keeper.walkedPositions.add(new Position (4,8));
        keeper.walkedPositions.add(new Position (3,8));
        keeper.walkedPositions.add(new Position (2,8));


        int x = keeper.randomOftheNonSteppedCell(adyPosition);
        Assert.assertTrue( x==1 || x==0||x==3);

    }

    @Test
    public void returnsIndexWhenFirstCellNoStepped() {

        KeeperAI keeper = new KeeperAI();
        List<Position> adyPosition = new ArrayList<Position>();

        adyPosition.add(new Position(10,8)); // this is the not stepped
        adyPosition.add(new Position(5,6));
        adyPosition.add(new Position(2,8));
        adyPosition.add(new Position(4,8));

        keeper.walkedPositions.add(new Position (5,7));
        keeper.walkedPositions.add(new Position (5,6));
        keeper.walkedPositions.add(new Position (5,8));
        keeper.walkedPositions.add(new Position (4,8));
        keeper.walkedPositions.add(new Position (3,8));
        keeper.walkedPositions.add(new Position (2,8));


        int x = keeper.randomOftheNonSteppedCell(adyPosition);
        Assert.assertTrue( x==0);

    }

    @Test
    public void returnsIndexWhenSecondCellNoStepped() {

        KeeperAI keeper = new KeeperAI();
        List<Position> adyPosition = new ArrayList<Position>();

        adyPosition.add(new Position(5,8));
        adyPosition.add(new Position(10,6));// this is the not stepped
        adyPosition.add(new Position(2,8));
        adyPosition.add(new Position(4,8));

        keeper.walkedPositions.add(new Position (5,7));
        keeper.walkedPositions.add(new Position (5,6));
        keeper.walkedPositions.add(new Position (5,8));
        keeper.walkedPositions.add(new Position (4,8));
        keeper.walkedPositions.add(new Position (3,8));
        keeper.walkedPositions.add(new Position (2,8));


        int x = keeper.randomOftheNonSteppedCell(adyPosition);
        Assert.assertTrue( x==1);

    }

    @Test
    public void switchActionReturnsOpositeTest() {

        KeeperAI keeper = new KeeperAI();
        Assert.assertEquals(Action.GO_DOWN, keeper.switchAction(Action.GO_UP));
        Assert.assertEquals(Action.GO_LEFT, keeper.switchAction(Action.GO_RIGHT));
        Assert.assertEquals(Action.GO_RIGHT, keeper.switchAction(Action.GO_LEFT));
        Assert.assertEquals(Action.GO_UP, keeper.switchAction(Action.GO_DOWN));

    }

    @Test
    public void switchActionReturnsNothingTest() {

        KeeperAI keeper = new KeeperAI();
        Assert.assertEquals(Action.DO_NOTHING, keeper.switchAction(Action.DO_NOTHING));

    }

    @Test
    public void convertPositiontoActionTest() {

        KeeperAI keeper = new KeeperAI();
        Position current = new Position(1,3);
       // Assert.assertEquals(Action.GO_UP, keeper.convertPositiontoAction(current , new Position(4,7)));
      //  Assert.assertEquals(Action.GO_DOWN, keeper.convertPositiontoAction(current , new Position(6,7)));
      //  Assert.assertEquals(Action.GO_LEFT, keeper.convertPositiontoAction(current , new Position(5,6)));
        Assert.assertEquals(Action.GO_RIGHT, keeper.convertPositiontoAction(current , new Position(1,4)));
      //  Assert.assertEquals(Action.DO_NOTHING, keeper.convertPositiontoAction(current , new Position(5,9)));
    }



    @Test
    public void allSteppedTrueOnlyOneCellAvailable() {

        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.add(new Position (3,2));
        keeper.walkedPositions.add(new Position (3,3));
        keeper.walkedPositions.add(new Position (3,4));
        keeper.walkedPositions.add(new Position (2,4));
        keeper.walkedPositions.add(new Position (1,4));
        keeper.walkedPositions.add(new Position (1,3));

        List<Position> adyPosition = new ArrayList<Position>();
        adyPosition.add(new Position(1,4));

        Assert.assertEquals(true, keeper.allStepped(adyPosition));

    }


    @Test
    public void allSteppedFalse2() {

        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.add(new Position (5,7));
        keeper.walkedPositions.add(new Position (5,6));
        keeper.walkedPositions.add(new Position (5,8));
        keeper.walkedPositions.add(new Position (4,8));
        keeper.walkedPositions.add(new Position (3,8));
        keeper.walkedPositions.add(new Position (2,8));
        List<Position> adyPosition = new ArrayList<Position>();
        adyPosition.add(new Position(2,8));
        adyPosition.add(new Position(5,6));
        adyPosition.add(new Position(1,8));
        adyPosition.add(new Position(10,8));
        Assert.assertEquals(false, keeper.allStepped(adyPosition));

    }

    @Test
    public void onlyOneAdyacentNotSteppped() {

        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.add(new Position (5,7));
        keeper.walkedPositions.add(new Position (5,6));
        keeper.walkedPositions.add(new Position (5,8));
        keeper.walkedPositions.add(new Position (4,8));
        keeper.walkedPositions.add(new Position (3,8));
        keeper.walkedPositions.add(new Position (2,8));
        List<Position> adyPosition = new ArrayList<Position>();
        adyPosition.add(new Position(20,8));

        Assert.assertEquals(false, keeper.allStepped(adyPosition));

    }

    @Test
    public void onlyOneAdyacentAndSteppped() {

        KeeperAI keeper = new KeeperAI();
        keeper.walkedPositions.add(new Position (5,7));
        keeper.walkedPositions.add(new Position (5,6));
        keeper.walkedPositions.add(new Position (5,8));
        keeper.walkedPositions.add(new Position (4,8));
        keeper.walkedPositions.add(new Position (3,8));
        keeper.walkedPositions.add(new Position (2,8));
        List<Position> adyPosition = new ArrayList<Position>();
        adyPosition.add(new Position(3,8));

        Assert.assertEquals(true, keeper.allStepped(adyPosition));

    }


    @Test
    public void getPositionFromActionTest() {

        KeeperAI keeper = new KeeperAI();
        Assert.assertEquals(new Position (11,10), keeper.getPositionFromAction(Action.GO_DOWN,new Position (10, 10)));
        Assert.assertEquals(new Position (9,10), keeper.getPositionFromAction(Action.GO_UP,new Position (10, 10)));
        Assert.assertEquals(new Position (10,11), keeper.getPositionFromAction(Action.GO_RIGHT,new Position (10, 10)));
        Assert.assertEquals(new Position (10,9), keeper.getPositionFromAction(Action.GO_LEFT,new Position (10, 10)));
        Assert.assertEquals(new Position (10,10), keeper.getPositionFromAction(Action.DO_NOTHING,new Position (10, 10)));

    }


    @Test
    public void keyIndexReturnsIndexofCellKEY(){

        KeeperAI keeper = new KeeperAI();
        List<Cell> adyacentCells = new ArrayList<Cell>();
        adyacentCells.add(Cell.WALL);
        adyacentCells.add(Cell.PATH);
        adyacentCells.add(Cell.KEY);
        adyacentCells.add(Cell.PATH);
        Assert.assertEquals(2,  keeper.keyIndex(adyacentCells));

    }

    @Test
    public void keyIndexReturns_negative1_if_no_adyCellKEY(){

        KeeperAI keeper = new KeeperAI();
        List<Cell> adyacentCells = new ArrayList<Cell>();
        adyacentCells.add(Cell.WALL);
        adyacentCells.add(Cell.PATH);
        adyacentCells.add(Cell.WALL);
        adyacentCells.add(Cell.PATH);
        Assert.assertEquals(-1,  keeper.keyIndex(adyacentCells));

    }

    @Test
    public void isDoorReturnsTrueWhenDoorAdyacent(){

        KeeperAI keeper = new KeeperAI();
        List<Cell> adyacentCells = new ArrayList<Cell>();
        adyacentCells.add(Cell.WALL);
        adyacentCells.add(Cell.DOOR);
        adyacentCells.add(Cell.WALL);
        adyacentCells.add(Cell.PATH);
        Assert.assertEquals(true,  keeper.isDoor(adyacentCells));

    }

    @Test
    public void isDoorReturnsFalseWhenNoDoorAdyacent(){

        KeeperAI keeper = new KeeperAI();
        List<Cell> adyacentCells = new ArrayList<Cell>();
        adyacentCells.add(Cell.WALL);
        adyacentCells.add(Cell.KEY);
        adyacentCells.add(Cell.WALL);
        adyacentCells.add(Cell.PATH);
        Assert.assertEquals(false,  keeper.isDoor(adyacentCells));

    }


    @Test
    public void celltoRemoveWALL(){

        KeeperAI keeper = new KeeperAI();
        List<Cell> adyacentCells = new ArrayList<Cell>();
        // in the order of looking up, right, down, left
        adyacentCells.add(Cell.WALL);
        adyacentCells.add(Cell.KEY);
        adyacentCells.add(Cell.WALL);
        adyacentCells.add(Cell.PATH);
        List<Action> tempAvailableAction = new ArrayList<Action>();
       // GO_UP, GO_RIGHT, GO_DOWN, GO_LEFT
        tempAvailableAction.add(Action.GO_UP);
        tempAvailableAction.add(Action.GO_RIGHT);
        tempAvailableAction.add(Action.GO_DOWN);
        tempAvailableAction.add(Action.GO_LEFT);

        List<Cell> solutionCell = new ArrayList<Cell>();
        solutionCell.add(Cell.KEY);
        solutionCell.add(Cell.PATH);

        List<Action> solutionAction = new ArrayList<Action>();
        solutionAction.add(Action.GO_RIGHT);
        solutionAction.add(Action.GO_LEFT);

        keeper.removeCell(adyacentCells,Cell.WALL,tempAvailableAction);

        Assert.assertTrue(adyacentCells.equals(solutionCell));
        Assert.assertTrue(tempAvailableAction.equals(solutionAction));

    }



}
