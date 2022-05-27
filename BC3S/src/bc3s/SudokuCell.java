package bc3s;

/**
 *
 * @author Forrest Swift
 */
public class SudokuCell {
    /**
     * The number the cell currently holds.
     */
    public int value;
    /**
     * The list of possible values the cell could hold.
     * Each value is located at index value-1.
     */
    public boolean[] csList;
    
    public SudokuCell() {
        value = 0;
        csList = new boolean[9];
    }
}
