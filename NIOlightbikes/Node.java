/**
 * This class is only used by the HardAI class. It implements a node for the best path tree.
 * @author Sam
 */
public class Node {
    int x;										//X position (column)
    int y;										//Y position (row)
    int distance;							//distance from the root node
    int orientation;					//Orientation of the bikes for the last move
    Node[] Sons;							//Array of sons
    Node Parent;    					//Parent node
    boolean[][] isFreeTile;		//Current representation of the grid (availability)
    
    //computes a hash code (needed for the HashSet inclusion)
    //Note that Sons and Parent are not taken into account. It's because I'm considering 
    //the state of the game, not the node itself.
    public int hashCode()
    {
    	int hashCode = 1;
    	hashCode = 31 * hashCode + x;
    	hashCode = 31 * hashCode + y;
    	hashCode = 31 * hashCode + distance;
    	hashCode = 31 * hashCode + orientation;
    	hashCode = 31 * hashCode + java.util.Arrays.hashCode(isFreeTile);
    	return hashCode;
    }
    
    //Returns true if equals, false otherwise. Needed for hash collisions in the hash set
    //Note that Sons and Parent are not taken into account. It's because I'm comparing 
    //the state of the game, not the node itself.
    public boolean equals(Node b)
    {
    	boolean tf = true;
    	
    	if(this.x != b.x)
    	   tf = false;
    	else if(this.y != b.y)
    	   tf = false;
    	else if(this.distance != b.distance)
    	   tf = false;
    	else if(this.orientation != b.orientation)
    	   tf = false;
    	else if(!java.util.Arrays.equals(this.isFreeTile,b.isFreeTile))
    	   tf = false;
    	return tf;
    }
    
}
