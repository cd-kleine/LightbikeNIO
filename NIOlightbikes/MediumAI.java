import java.util.ArrayList;

/**
 * This AI is a bit smarter than just the random one. Most of the time, it keeps its track, unless it reaches a wall.
 * When it has to take a decision, it goes towards the direction that has the most free space in a straight line.
 * @author Sam
 */
public class MediumAI implements IAI {
    
     /**
      * Method implemented over the interface.
      * @param cCarDir the current direction of the player (0=Up, 1=Right, 2=Down, 3=Left)
      * @param x the x position of the player on the grid (column; 0 = left)
      * @param y the y position of the player on the grid (line; 0 = top)
      * @param iGrid a copy of the current grid representation
      * @param iTimer a copy of the current freshness information
      * @return a character as a movement decision ('U', 'R', 'L', 'D').
      */
     public char getNewDir(char cCarDir, int x, int y, int[][] iGrid, int[][] iTimer)
     {
        //The returned character
        char rc;
        
        //These will contain the possible moves, given the constraints
        ArrayList<Character> vPossibleMoves = new ArrayList<Character>();

        //The four options so far
        char[] cPossibleMoves = new char[4];
        cPossibleMoves[0] = 'U';
        cPossibleMoves[1] = 'R';
        cPossibleMoves[2] = 'D';
        cPossibleMoves[3] = 'L';

			  //change the character representation of the direction into an integer one
        int currentMove = 0;
        switch(cCarDir)
        {
            case 'U' : currentMove = 0; break;
            case 'R' : currentMove = 1; break;
            case 'D' : currentMove = 2; break;
            case 'L' : currentMove = 3; break;
        }
        
        //For each possible new direction
        for(int j = 0; j < 4; j++)
        {
            //Should this direction be added? For the moment, no
            boolean to_add = false;
            
            //We don't accept half-turns as an option, since it's not in the game rule
            if(currentMove != (j+2)%4)
            {
                //If this move leads to go over the border, we forbid it
                if((y > 0 && j == 0) || (x < 99 && j == 1) || (y < 99 && j == 2) || (x > 0 && j == 3))
                {
                    //Get the next tile to reach after the move, and its freshness
                    int targetTile = 0;
                    int targetTimer = 0;
                    switch(j)
                    {
                        case 0 : targetTile = iGrid[x][y-1]; targetTimer = iTimer[x][y-1]; break;
                        case 1 : targetTile = iGrid[x+1][y]; targetTimer = iTimer[x+1][y];break;
                        case 2 : targetTile = iGrid[x][y+1]; targetTimer = iTimer[x][y+1];break;
                        case 3 : targetTile = iGrid[x-1][y]; targetTimer = iTimer[x-1][y];break;
                    }
                    
                    //If this tile is empty, we consider this as a valid move
                    if(targetTile == 0)
                        to_add = true;
                    else
                    {
                        //This is where we trick the AI to be worse than it could be
                        //It has a random chance to consider a fresh path as a free tile
                        //The fresher the path, the better the odds. (between 0% and 0.25%)
                        double rchoice = Math.random();
                        if(rchoice < (double)(targetTimer/20))
                        {
                            rchoice = Math.random();
                            if(rchoice < 0.05)
                                //Don't see that it's already taken (and die)
                                to_add = true;
                        }
                    }
                }
            }
            
            //Add this direction into the array of possible moves if it respects the constraints
            if(to_add == true)
            {
                vPossibleMoves.add(cPossibleMoves[j]);
            }
        }
        
        //If there's something to choose from
        if(vPossibleMoves.size() > 0)
        {
            
            //Is the current direction still an option?
            boolean found = vPossibleMoves.contains(cCarDir);
            boolean bKeepOnTrack = true;
            if(found)
            {
                //More likely to stay on course. Only has a 10% probability to change course
                if(Math.random() < 0.10)
                {
                    bKeepOnTrack = false;
                }
            }
            else
            {
                bKeepOnTrack = false;

            }
            
            //If we decided to choose a (possibly new) direction
            if(bKeepOnTrack == false)
            {
                int iSize = vPossibleMoves.size();
                int[] iWeights = {0,0,0,0};					//Store the distance to the nearest wall in each direction

								//For all possible remaining choices
                for(int j = 0; j < iSize; j++)
                {
                    char cTemp = vPossibleMoves.get(j);
                    int newx = x;
                    int newy = y;
                    switch(cTemp)
                    {
                        case 'U' : newy--; break;
                        case 'D' : newy++; break;
                        case 'L' : newx--; break;
                        case 'R' : newx++; break;
                    }
                    
                    //Now newx and newy are the position after one move in this direction
                    
                    //As long as we don't hit a trail or a border, we keep going in this direction
                    while(newx > 0 && newy > 0 && newx < 99 && newy < 99 && iGrid[newx][newy] == 0)
                    {
                        switch(cTemp)
                        {
                            case 'U' : newy--; break;
                            case 'D' : newy++; break;
                            case 'L' : newx--; break;
                            case 'R' : newx++; break;
                        }
                        iWeights[j]++;
                    }
                }

								//Find the direction where I can keep going for the longest time without any problem
                int iSelected = 0;
                int iMax = -1;
                for(int j = 0; j < 4; j++)
                {
                    if(iWeights[j] > iMax)
                    {
                        iSelected = j;
                        iMax = iWeights[j];
                    }
                }
                cCarDir = vPossibleMoves.get(iSelected);
            }
        }
        
        //Update the direction (or keep the existing one)
        rc = cCarDir;
        return rc;
     }
}

