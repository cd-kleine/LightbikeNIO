import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashSet;

/**
 * This is the most advanced AI (that I wrote for this problem). Basically, it looks at the 200 earliest possible moves, and selects the one that leads to the greatest distance in the long run.
 * To be perfect, it should look at 10000 moves (the whole grid) and account for other players moves, but it would be freaking slow and it's still god darn good as it is.
 * @author Sam
 */
public class HardAI implements IAI {
    
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
                    //Get the next tile to reach after the move, and its freshness (although the latter is not really used here. I wonder why there's no warning)
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
                    {
                        to_add = true;
                    }
                    //No else, it tries its best, this time
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
						//Now, we force it to take a decision at every step (that's why analyzing the whole grid at each step is not an option)
            boolean bKeepOnTrack = false;

            if(bKeepOnTrack == false)
            {

								//This is where the core of the AI is, practically speaking.
								//
								//It works by builing a tree of all possible moves for the current player
								//It considers that all other players won't move, which does not change much since
								//we rapidly end up in our own region.
								//
								//The tree is built using a breadth first search, that we interrupt after 200 nodes for speed purpose.
								//
								//To build the tree, we use two structures:
								//- A LinkedList, where we pop elements from the head, and add new elements to the tail, that contains nodes to process
								//- A hashset, where we keep the track of already visited nodes
								//
								//At each step, 
								//- we take a node from the linked list
								//- compare the reached distance, from the starting node to this node, to the best distance so far
								//- if the distance is better, we consider this node as the best node so far
								//- create at most 3 new nodes (one for each possible direction)
								//- Look in the hashset if this node has already been seen
								//- If not, add this node into the hashset and into the linked list
								//
								//When we finished (because the linked list is empty of because we considered 200 nodes)
								//- we take the best node and look at its parent
								//- as long as the parent is not the root node, we climb up the hierarchy
								//- the last node before the root is the first move that lead to the best distance, so we pick this one
                
                //We first create the root node, which starts where the player currently is
                Node root = new Node();
                root.x = x;																//X position (column)
                root.y = y;																//Y position (row)
                root.distance = 0;												//Distance (0, in this case, since it's the root node)
                root.orientation = (currentMove+1)%4;     //Current orientation
                root.Parent = null;												//Its parent (no-one for root)
                root.Sons = new Node[4];									//An array for its 4 sons (1 will never be used)
                root.isFreeTile = new boolean[100][100];	//The representation of the grid at this node (we just need to know it the tile is free or not, not the player id, that's why the boolean)
                
                //Fill the "node grid" with the current situation
                for(int j = 0; j < 100; j++)
                {
                    for(int k = 0; k < 100; k++)
                    {
                        if(iGrid[j][k] == 0)
                            root.isFreeTile[j][k] = true;
                        else
                            root.isFreeTile[j][k] = false;
                    }
                }

								//The best node so far is the root node, but that will rapidly change
                Node bestNode = root;

								//(Very) Useful structures
                LinkedList<Node> to_process = new LinkedList<Node>();
                HashSet<Node> seen = new HashSet<Node>();
                
                //The first node to process is the root node, and we add it in the "seen" set
                to_process.add(root);
                seen.add(root);
                
                //This is used to count the number of nodes that we already processed
                long processed = 0;

								//As long as we processed fewer than 200 nodes and there's still something to process
                while(processed < 200 && to_process.size() > 0)
                {

                    //Extract the node from the linked list and increase the counter
                    Node currentNode = to_process.poll();
                    processed++;
                    
										//If the distance is better, count this node as the current best
                    if(bestNode.distance < currentNode.distance)
                    {
                        bestNode = currentNode;
                    }

										//Where could we be after we move?
                    int[][] possibleMoves = new int[4][2];
                    possibleMoves[0][0] = currentNode.x-1; possibleMoves[0][1] = currentNode.y;  //Left
                    possibleMoves[1][0] = currentNode.x; possibleMoves[1][1] = currentNode.y-1;  //Up
                    possibleMoves[2][0] = currentNode.x+1; possibleMoves[2][1] = currentNode.y;  //Right
                    possibleMoves[3][0] = currentNode.x; possibleMoves[3][1] = currentNode.y+1;  //Down

										//Process all 4 possibilities (while one is the half-turn and will obviously be put away
                    for(int j = 0; j < 4; j++)
                    {
                        //If this move makes us stay in the play area
                        if(possibleMoves[j][0] >= 0 && possibleMoves[j][0] <= 99 && possibleMoves[j][1] >= 0 && possibleMoves[j][1] <= 99 && currentNode.isFreeTile[possibleMoves[j][0]][possibleMoves[j][1]]) 
                        {

														//And this move leads to an empty tile
                            if(iGrid[possibleMoves[j][0]][possibleMoves[j][1]] == 0)
                            {
                                
                                //Then we consider it as a valid option, and build a new node.
                                Node newNode = new Node();
                                newNode.x = possibleMoves[j][0];
                                newNode.y = possibleMoves[j][1];
                                newNode.distance = currentNode.distance+1;	//We increased the distance by 1
                                newNode.orientation = j;
                                newNode.Sons = new Node[4];
                                newNode.Parent = currentNode;
                                
                                //Copy the state of the grid from the previous node, except the current position, which is now occupied
                                newNode.isFreeTile = Arrays.copyOf(currentNode.isFreeTile, 100*100);
                                newNode.isFreeTile[newNode.x][newNode.y] = false;

                                //Link this node to its parent
                                currentNode.Sons[j] = newNode;
																
																//Check if it's really a new node, and only add it if so.
																if(!seen.contains(newNode))
																{
                                	to_process.add(newNode);
                                	seen.add(newNode);
                                }

                            }
                            else
                            {
                                //Invalid move : leads to an occupied space
                                currentNode.Sons[j] = null;
                            }
                        }
                        else
                        {
                            //Invalid move : leads over the edge
                            currentNode.Sons[j] = null;
                        }
                    }
                }

								//At this point, the best node is in "bestNode"
								//We take its parent
                Node parentNode = bestNode.Parent;
                
                //While the parent is not the root
                while(parentNode.distance > 0)
                {
                    //The best one is the parent
                    bestNode = parentNode;
                    //We take the parent of the parent
                    parentNode = bestNode.Parent;
                }
                
                //At this point, the best first move is in "bestNode"
                //We simply look at the index of the son in the array of the parent to get the best direction to take
                if(parentNode.Sons[0] == bestNode)
                    cCarDir = 'L';
                else if(parentNode.Sons[1] == bestNode)
                    cCarDir = 'U';
                else if(parentNode.Sons[2] == bestNode)
                    cCarDir = 'R';
                else if(parentNode.Sons[3] == bestNode)
                    cCarDir = 'D';
            }
        }
        
        //Update the direction (or keep the existing one if that's still the best option) 
        rc = cCarDir;
        return rc;
     }
}
