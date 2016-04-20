/**
 * @author Jeff DeSain
 */

public class Node<E>{
    private E data; //The configuration for this node
    private int score; //The score for this node

    /**
     * @param data A configuration
     */
    public Node(E data)
    {
        this.data = data;
    }

    /**
     * Getter for data
     *
     * @return The configuration for this node
     */
    public E getData()
    {
        return data;
    }

    /**
     * Setter for score
     *
     * @param score The score for this node
     */
    public void setScore(int score)
    {
        this.score = score;
    }

    /**
     * Getter for score
     *
     * @return The score for this node
     */
    public int getScore()
    {
        return score;
    }

    /**
     *
     * @return hashcode for this object
     */
    public int hashCode()
    {
        return data.hashCode();
    }

    /**
     *
     * @return A string representation for this object
     */
    public String toString()
    {
        return "" + data;
    }


}
