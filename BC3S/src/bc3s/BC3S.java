
package bc3s;

/**
 *
 * @author Forrest Swift
 */
public class BC3S {

    public SudokuCell[][] grid;

    /**
     * Creates a new solver with a blank grid.
     */
    public BC3S() {
        grid = new SudokuCell[9][9];
        initGrid();
    }

    /**
     * Creates a new solver with a pre-created grid.
     * @param grid A 9x9 grid of SudokuCells
     */
    public BC3S(SudokuCell grid[][]) {
        this.grid = grid;
    }

    /**
     * A less intensive way to remove possible options from related cells. 
     * Only works when adding a new number, for all other purposes use 
     * calcConstraints.
     * <p>
     * This method works by checking the value at the location provided, and 
     * removing said value from the constraint list of each cell in the same 
     * row, column, and square.
     * @param y The y value of the location of the added cell in the grid array.
     * @param x The x value of the location of the added cell in the grid array.
     */
    public void addConstraints(int y, int x) {
        // find the value to be removed from each constraint list.
        int removalVal = grid[y][x].value - 1;
        // for each cell in our given row.
        for (int i = 0; i < 9; i++) {
            // remove the value from the possible values of said cell.
            grid[y][i].csList[removalVal] = false;
        }
        // for each cell in our given column.
        for (int i = 0; i < 9; i++) {
            // remove the value from the possible values of said cell.
            grid[i][x].csList[removalVal] = false;
        }
        // Find the start of the square we are in
        int rowBoxStart = y - y % 3;
        int colBoxStart = x - x % 3;
        // for each row of our box
        for (int i = rowBoxStart; i < rowBoxStart + 3; i++) {
            // for each column of our box
            for (int j = colBoxStart; j < colBoxStart + 3; j++) {
                // remove the value from the possible values of said cell.
                grid[i][j].csList[removalVal] = false;
            }
        }
    }
    
    /**
     * This method calculates all constraints of all cells given current values 
     * of the grid. When possible (when adding new values), addConstraints 
     * should be used.
     * This method starts by looping through each cell in the Sudoku grid and 
     * resetting all constraints. Next, it loops through each cell, calculating 
     * the constraints based on the values of cells in the same row, column, and 
     * square.
     */
    public void calcConstraints() {
        // for each cell in the grid
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                // for each value in the constraint list
                for (int i = 0; i < 9; i++) {
                    // set it back to being a valid option
                    grid[y][x].csList[i] = true;
                }
            }
        }
        // for each cell in the grid
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                // for each cell in the same row
                for (int i = 0; i < 9; i++) {
                    // if the cell in the same row is assigned
                    if (grid[y][i].value != 0) {
                        // remove the value its assigned to from our possibles
                        grid[y][x].csList[grid[y][i].value - 1] = false;
                    }
                }
                // for each cell in the same column
                for (int i = 0; i < 9; i++) {
                    // if the cell in the same column is assigned
                    if (grid[i][x].value != 0) {
                        // remove the value its assigned to from our possibles
                        grid[y][x].csList[grid[i][x].value - 1] = false;
                    }
                }
                // find the start of our box
                int rowBoxStart = y - y % 3;
                int colBoxStart = x - x % 3;
                // for each cell in our box
                for (int i = rowBoxStart; i < rowBoxStart + 3; i++) {
                    for (int j = colBoxStart; j < colBoxStart + 3; j++) {
                        // if the cell in our box is assigned
                        if (grid[i][j].value != 0) {
                            // remove the value its assigned to from our cs-list
                            grid[y][x].csList[grid[i][j].value - 1] = false;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * This method calculates the heuristic of a given cell by counting the 
     * number of possible values the cell can be assigned to. The lower the 
     * number of remaining values, the higher the likelihood we will guess the 
     * correct value to assign the cell.
     * @param possibles A constraint satisfaction list to count from.
     * @return The number of values the cell can still be assigned to.
     */
    public int calcHeuristic(boolean[] possibles){
        int heuristic = 0;
        // For each possible value the cell can be assigned to, add 1 to h.
        for (int i = 0; i<9; i++) {
            if (possibles[i]) {
                heuristic++;
            }
        }
        return heuristic;
    }

    /**
     * This method recursively solves any valid Sudoku grid.
     * This method starts with a loop for each possible heuristic value from 1-9. 
     * Inside this loop, it loops through each cell in the grid. If it finds an 
     * unassigned value, it checks to see if the cell's heuristic is below the 
     * bar to be assigned. If it is both unassigned and below the heuristic bar, 
     * the solver assigns the cell to the first valid value it finds on the 
     * cell's constraint satisfaction list, and calls solver recursively. If 
     * the recursive solver call returns success, the solver that called it also 
     * returns success until we exit the recursion. If the recursive solver call 
     * returns failure, we have exhausted our options down this branch of the 
     * tree and must remove the assigned value, recalculate constraints, and 
     * continue through the constraint satisfaction list trying possible values. 
     * If we make it to the end of the CS-list with no valid option for our cell, 
     * we must have messed up with a previous assignment and must return a fail 
     * call. If we make it through every loop that must mean every cell is 
     * assigned and therefore we have succeeded, the method returns success.
     * @return Returns true or false based on success of the solver and the 
     * children of the solver.
     */
    public boolean solver() {
        // For each possible heuristic value
        for(int h = 1; h<10; h++){
            // for each row in the grid
            for (int y = 0; y < 9; y++) {
                // for each column in the grid
                for (int x = 0; x < 9; x++) {
                    // if the cell's value isn't yet assigned
                    if (grid[y][x].value == 0) {
                        // if the heuristic of the cell is below the bar
                        if(calcHeuristic(grid[y][x].csList) <= h) {
                            // for each possible value in the cs-list
                            for (int csVal = 0; csVal < 9; csVal++) {
                                // if the slot in the cs-list is still valid
                                if (grid[y][x].csList[csVal]) {
                                    // set the value of the cell to our choice
                                    grid[y][x].value = csVal + 1;
                                    // calc new constraints
                                    addConstraints(y, x);
                                    // check if children can succeed
                                    if (solver()) {
                                        // if they did, we're done
                                        return true;
                                    } else {
                                        // otherwise, remove the value we set
                                        grid[y][x].value = 0;
                                        // remove the constraints we set as well
                                        calcConstraints();
                                        // continue through cs-list
                                    }
                                }
                            }
                            // every possible value in cs-list failed, we must
                            // have made a mistake previously, go back a step
                            return false;
                        }
                    }
                }
            }
        }
        // If we made it through the whole grid, everything must be assigned
        return true;
    }

    /**
     * Used to display the Sudoku grid to console.
     * No longer necessary due to BC3SGUI
     */
    public void displayGrid() {
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("----------------------------------\n");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print(" | ");
                }
                System.out.print(" " + grid[i][j].value + " ");

            }
            System.out.println();
        }
        System.out.println("\n\n__________________________________________\n\n");
    }

    /**
     * This method initializes a grid of SudokuCells.
     */
    public void initGrid() {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                grid[y][x] = new SudokuCell();
            }
        }
    }

    public static void main(String args[]) {
        // Outdated method of entering a puzzle because of SudokuCells
        // Useful for visualization
        /*int[][] puzzle = {
      0  { 1, 0, 0, 0, 0, 7, 0, 9, 0 },
      1  { 0, 3, 0, 0, 2, 0, 0, 0, 8 },
      2  { 0, 0, 9, 6, 0, 0, 5, 0, 0 },
      3  { 0, 0, 5, 3, 0, 0, 9, 0, 0 },
      4  { 0, 1, 0, 0, 8, 0, 0, 0, 2 },
      5  { 6, 0, 0, 0, 0, 4, 0, 0, 0 },
      6  { 3, 0, 0, 0, 0, 0, 0, 1, 0 },
      7  { 0, 4, 0, 0, 0, 0, 0, 0, 7 },
      8  { 0, 0, 7, 0, 0, 0, 3, 0, 0 }
           0  1  2  3  4  5  6  7  8 
        };*/
        
        // Uncomment below to run without the GUI
        /*BC3S s = new BC3S();
        s.grid[0][0].value = 1;
        s.grid[0][5].value = 7;
        s.grid[0][7].value = 9;
        s.grid[1][1].value = 3;
        s.grid[1][4].value = 2;
        s.grid[1][8].value = 8;
        s.grid[2][2].value = 9;
        s.grid[2][3].value = 6;
        s.grid[2][6].value = 5;
        s.grid[3][2].value = 5;
        s.grid[3][3].value = 3;
        s.grid[3][6].value = 9;
        s.grid[4][1].value = 1;
        s.grid[4][4].value = 8;
        s.grid[4][8].value = 2;
        s.grid[5][0].value = 6;
        s.grid[5][5].value = 4;
        s.grid[6][0].value = 3;
        s.grid[6][7].value = 1;
        s.grid[7][1].value = 4;
        s.grid[7][8].value = 7;
        s.grid[8][2].value = 7;
        s.grid[8][6].value = 3;
        s.calcConstraints();
        s.solver();
        s.displayGrid();*/
        
    }
}