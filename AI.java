import java.util.Map;
import java.util.Random;
import java.util.Arrays.*;
import java.io.*;
import java.util.*;

 class AI implements AIInterface{
	EnvironmentalInterface enviroment;
	ShipInterface ship;
	Map<Integer, Point2D> asteroidsMap;
	int rightBoundaries = 20;
	int tempRightBoundaries;
	public int odstep;

  int[][]G;
  int[][]Gpath;

  int[][]Manualpath;
  PrintWriter out;
  public boolean needPaveTheTrack = false;
  public int IDAsteroidToDestroy = -1;
  public boolean isGoingToMove = false;

  private synchronized void changeNeedPaveTheTrack(boolean value){
    this.needPaveTheTrack = value;
  }

  private synchronized void changeIDAsteroidToDestroy(int value){
    this.IDAsteroidToDestroy = value;
  }

  private synchronized void changeGoingToMove(boolean value){
    this.isGoingToMove = value;
  }


  boolean isThereMoreAsteroids(int x, int y){
  for(int i =0; i< odstep; i++){
    if(asteroidsMap.containsValue(new Point2D(x+i, y))) {return true;}
  }
  return false;
  }

  
  class Navigation implements Runnable{
    int averangeHight;
    int numOfcolumn;
    int numOfrow;
    int altitute;



	public void run(){
      numOfcolumn = enviroment.getNumberOfColumns();
      numOfrow = enviroment.getNumberOfRows();
      int tempRightBoundaries = rightBoundaries;
      long asteroidsMovePeriod = enviroment.getAsteroidMovePeriod();
      long shipMovePeriod = enviroment.getShipMovePeriod();
      odstep =3;
      averangeHight = numOfrow/2; //ew ship.getAltitude();
      G=new int[numOfrow+1][numOfcolumn+1];
      Gpath = new int[numOfrow+1][numOfcolumn+1];

      for(int i = 0; i<1; i++){
        ship.up();
        ship.up();
        ship.down();
        ship.down();
      }


      while(true){
        altitute = ship.getAltitude();

        G=new int[numOfrow+1][numOfcolumn+1];
        Gpath = new int[numOfrow+1][numOfcolumn+1];

        for(int i =0; i<10; i++){
        if(findPath(altitute, 0)){
           createPath();
           earlierChangeDirection();
           nextMove();
           break;
         }
          else{
            rightBoundaries++;
              while (rightBoundaries > numOfcolumn- 12){

                if(findPath(altitute, 0)){
                  createPath();
                  earlierChangeDirection();
                  nextMove();
                  rightBoundaries = tempRightBoundaries;
                  break;
                }
                else{
                  rightBoundaries++;

                }
            }

          }
        }

      }
    }

    public boolean findPath(int y, int x) {
      //System.out.println("findPath y = " + y+ " x = "+x);
       if (y < 0 || y > numOfrow || x < 0 || x > numOfcolumn-rightBoundaries) {
         return false;
       }

         asteroidsMap = enviroment.getAsteroids();

         for(int i =0; i<= odstep; i++){
           if(asteroidsMap.containsValue(new Point2D(x+i, y))) {
             return false;
           }
         }
      
       if (G[y][x] == 2) return false; 
       
       G[y][x] = 2;
       if (x == numOfcolumn-rightBoundaries) {
         return true;}
       if (findPath(y, x+1)) {return true;};


     
         int random = 0;
         random = ((int)(10000*Math.random()) % 2);

         switch (random){
           case 0:
              if (findPath(y-1, x+1)) {return true;};
              if (findPath(y+1, x+1)) {return true;};
           case 1:
              if (findPath(y+1, x+1)) {return true;};
              if (findPath(y-1, x+1)) {return true;};
        }


       G[y][x] = 0;
       return false;
    } 

    public void createPath() {
       int r, c;
       for (r = 0; r < numOfrow; r++) {//?
          for (c = 0; c < numOfcolumn; c++)
            if (r == ship.getAltitude() && c == 0) Gpath[r][c] = 9; 
             else if (G[r][c] == 0) Gpath[r][c] = 0;
                  else Gpath[r][c] = 1;
       }
    }


      public void earlierChangeDirection(){
    int length = 0;
    int yy = ship.getAltitude();


    length = getLengthToChangeDirection(yy);
    int way = getNextMoveDirection(length, yy);

    if(length>odstep){
        switch(way){
          case 1:
            if((yy<enviroment.getNumberOfRows()-1)  && (isAvailableNewPath(length, yy+1)))
              changePath(length, yy+1);
              break;

          case -1:
            if((yy>0) && (isAvailableNewPath(length, yy-1)))
              changePath(length, yy-1);
              break;
        }
    }
}
  private boolean isAvailableNewPath(int licznik, int yy){
    asteroidsMap = enviroment.getAsteroids();

    for(int i =0; i<licznik; i++){
      if(asteroidsMap.containsValue(new Point2D(i, yy))) {
        return false;
      }
    }
      return true;
  }

  private void changePath(int licznik, int yy){
        for(int j = 0; j<licznik;j++){ 
          Gpath[yy][j] = 1;
          if(yy >0)  Gpath[yy-1][j] = 0;
          if(yy < numOfrow -1)Gpath[yy+1][j] = 0; 
        }
       
      }

      int getNextMoveDirection(int length, int y){
        if(Gpath[y+1][length +1] == 1) {
          return 1;
          }
        else if(Gpath[y-1][length +1] == 1) {
          return -1;}
        else
          return 0;
      }

      int getLengthToChangeDirection(int y){
        int length = 0;
        for(int i =1; i<numOfcolumn-rightBoundaries;i++){
            if(Gpath[y][i] == 1) length++;
            else 
            	return length;
        }
      return length;
      }



public void nextMove(){
  int yy = ship.getAltitude();
  if(yy<enviroment.getNumberOfRows()-1) {
      if(Gpath[yy+1][odstep+1] == 1) changeGoingToMove(true);
      else changeGoingToMove(false);}
  if(yy>0){
    if(Gpath[yy-1][odstep+1] == 1) changeGoingToMove(true);
    else changeGoingToMove(false);}


  if(Gpath[yy][odstep] == 1){
   try{
     Thread.sleep(enviroment.getShipMovePeriod());
   }
   catch(InterruptedException e){
     System.out.println("InterruptedException");
   }
  }

  else if((yy<enviroment.getNumberOfRows()-1) && isEmptySectionInGpath(odstep, yy+1, 1)){  //ograniczyc zeby nie szlo do gory poza mape!!!
  changeGoingToMove(true);
    ship.up();
    changeGoingToMove(false);
  }

  else if((yy>0) && isEmptySectionInGpath(odstep, yy-1, 1)){
  changeGoingToMove(true);
    ship.down();
    changeGoingToMove(false);
  }
}

  private boolean isEmptySectionInGpath(int section,int y, int value){
  for(int i =1; i<=section; i++){
      if(Gpath[y][i] != value)
        return false;
      else 
    	  return true;
    }
  return false;
  }



}

class Shoot implements Runnable{
  int y =0;
  long asteroidsMovePeriod = enviroment.getAsteroidMovePeriod();

   public void run (){
     try{
      Thread.sleep(enviroment.getShipMovePeriod());
     }
     catch(InterruptedException e){
      System.out.println("InterruptedException");
     }


     while(true){
    	 while(isGoingToMove){
    		 try{
    			 Thread.sleep(50);
    		 }
    		 catch(InterruptedException e){
    			 System.out.println("InterruptedException");
    		 }
    	 }



      if( (!(isGoingToMove)) && ship.isLaserReady()){
    	  ship.shoot();
    	  asteroidsMovePeriod = enviroment.getAsteroidMovePeriod();
      }
    }
  }
 }

	public void setInterfaceToEnvironment( EnvironmentalInterface ei ){
		enviroment = ei;
	}


	public void setInterfaceToShip( ShipInterface sh ){
		ship = sh;
	}

	public void start(){
		Navigation f1 = new Navigation();
		Shoot s1 = new Shoot();
		Thread t1 = new Thread( f1 );
		Thread t2 = new Thread( s1 );
		t1.start();
		t2.start();
	}
}
