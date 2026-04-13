import java.util.*;

public class Reversi {
    static final int SIZE = 8;
    static char[][] board = new char[SIZE][SIZE];
    static char currentPlayer = 'B'; // B = Black, W = White
    static int[] pieces = countPieces(board) ;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        printRules();
        initBoard();
        int orderCase = pregame() ; // choose the order in which the game is going to be played
        
        int deapth = DFSdeapth() ; // this is the method that is going to determine the deapth of the 

        while (true) {

            printBoard();
            int r = -1; 
            int c = -1;

            int[] move = {-1,-1};

            List<int[]> moves = getValidMoves(board , currentPlayer);
            if (moves.isEmpty()){
                moves = getValidMoves(board,opponent(currentPlayer));
                if ( moves.isEmpty()){
                    System.out.println("There are no valid moves for any player");
                    System.out.println("The match is finished!!!");
                    declareWinner(board);
                    break ;
                }
                currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
                continue;

            }

            // battle of algorithms cause what you gonna do
           
            if (orderCase == 0){
                System.out.println("=====AI vs AI=====") ;
                if ( currentPlayer == 'B'){
                    System.out.println("AI is playing as Black");
                    move = aiPlayerMove(board,r , c,'B',deapth) ;
                }else{
                    System.out.println("AI is playing as White");
                    move = aiPlayerMove(board,r, c,'W',deapth);
                }
            }
            if(orderCase == 1){
                if ( currentPlayer == 'B'){
                    printValidMoves(moves);
                    System.out.print("You are playing as " + currentPlayer + "\nMove (row col): ");
                    move = playerMove() ;
                }else{
                    System.out.println("AI is playing as White");
                    move = aiPlayerMove(board,r, c,'W',deapth);
                }
            }else if(orderCase == 2){
                if(currentPlayer == 'B'){ 
                    System.out.println("AI is playing as Black");
                    move = aiPlayerMove(board,r, c,'B',deapth);
                }else{
                    printValidMoves(moves) ;
                    System.out.print("You are playing as " + currentPlayer + "\nMove (row col): ");
                    move = playerMove();
                }
            }

           
                

            r = move[0] ;
            c = move[1] ;
            
            if (r == -1 && c == -1 ){ //end case of the
                System.out.println("The match is finished!!!"); 
                return ;
            }
            
            if (isValidMove(board ,r, c, currentPlayer)) {
                makeMove(r, c, currentPlayer);
                currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
            } else {
                System.out.println("Invalid move: " + r + " " + c + "\n Please try again : ");
            }
        }
    }


    static void printRules() {
    System.out.println(
        "\n===== REVERSI RULES =====\n" +
        "1. Players take turns placing one piece on an empty square (Black is the starting player).\n" +
        "2. A move is valid only if:\n" +
        "   - The chosen square is empty, AND\n" +
        "   - By placing your piece, you sandwich at least one opponent piece\n" +
        "     between the new piece and another of your pieces.\n" +
        "3. Sandwiched opponent pieces (in any direction: horizontal, vertical,\n" +
        "   diagonal) are flipped to your color.\n" +
        "4. If a player has no valid moves, they skip their turn.\n" +
        "5. The game ends when neither player can move.\n" +
        "6. The winner is the player with the most pieces on the board.\n" +
        "7. Enter two numbers between 0-7 (with a blank space in between), one for the row and one for the colum.\n" +
        "==========================\n"
    );
}

    public static int pregame() {
        System.out.print("To play as Black (1st)? Press '1'\nTo play as White (2nd)? Press '2'\nOption : ");
        int input = sc.nextInt();

        if (input == 1) {
            System.out.println("You are playing as Black");
            return 1;
        }else if(input == 0){ // this is for testing reasons to us ai against ai
            return 0 ;
        }

        System.out.println("You are playing as White");
        return 2;
    }

    public static int DFSdeapth(){
        System.out.print("Input a search deapth number for the computer algorithm: ");
        int input2 = sc.nextInt();
        System.out.print("\n");

        while (input2 < 1 || input2 >30){
            System.out.print("Invalid entry...Please try again!\nInput a search deapth number for the computer algorith: ");
            input2 = sc.nextInt();
            System.out.print("\n");
        }
        return input2 ;
    }

    static int[] playerMove() { 
        int r;
        int c;
        while (true) {
            String input = sc.next();
            if (input.equals("000") ){ return new int[]{-1,-1} ;} // instant kill switch for developer reasons 

            if (input.matches("[0-7]")) {
                r = Integer.parseInt(input); // Convert to int only after validation
                break; // Exit the loop
            } else {
                System.out.println("Invalid row number entered" );
            }
        }

        while (true) {
            // System.out.println("Enter col:"); // Optional prompt
            String input = sc.next();

            if (input.matches("[0-7]")) {
                c = Integer.parseInt(input);
                break; 
            } else {
                System.out.println("Invalid column number entered" );
            }
        }

        return new int[]{r, c};
    }

    static int[] aiPlayerMove(char[][] state,int r, int c,char aiChar,int deapth){
        
        int initial_alpha = Integer.MIN_VALUE; // min value for black and max value for white ( black is max , white is min)
        int initial_beta =  Integer.MAX_VALUE;
        int[] currentMove = miniMaxRoot(state,initial_alpha, initial_beta, aiChar,deapth);
        System.out.println("Move: " + currentMove[0] + " " + currentMove[1] + "\n");
        return currentMove ; 
    }

    static void initBoard() {
        for (char[] row : board) Arrays.fill(row, '.');
        board[3][3] = board[4][4] = 'W';
        board[3][4] = board[4][3] = 'B';
    }

    static void printBoard() {
        pieces = countPieces(board) ;
        System.out.println("======Score===== \nBlack: " + pieces[0] + " White: " + pieces[1] + "\n");
        System.out.print("  ");
        for (int i = 0; i < SIZE; i++) System.out.print(i + " ");
        System.out.println();
        for (int i = 0; i < SIZE; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < SIZE; j++) System.out.print(board[i][j] + " ");
            System.out.println();
        }
        System.out.print("\n");
    }

    static boolean validMoveExists(){
        for(int r = 0; r < SIZE; r++)
            for (int c=0; c < SIZE ; c++)
                if (board[r][c] == '.' && isValidMove(board,r,c,currentPlayer)) 
                    return true;
        return false;
    }

    static boolean isValidMove(char[][] state, int r, int c, char player) {

        if (r < 0 || c < 0 || r >= SIZE || c >= SIZE || state[r][c] != '.') {
            return false;
        }

        char opponent = (player == 'B') ? 'W' : 'B';
        
        int[][] dirs = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

        for (int[] d : dirs) {
            int i = r + d[0]; 
            int j = c + d[1]; 
            boolean foundOpponent = false;

            // 2. Αναζήτηση για κομμάτια αντιπάλου
            while (i >= 0 && j >= 0 && i < SIZE && j < SIZE && state[i][j] == opponent) {
                foundOpponent = true;
                i += d[0]; 
                j += d[1];
            }

            if (foundOpponent && i >= 0 && j >= 0 && i < SIZE && j < SIZE && state[i][j] == player) {
                return true;
            }
        }

        return false;
    }

    static void makeMove(int r, int c, char player) {
        char opponent = (player == 'B') ? 'W' : 'B';
        board[r][c] = player;
        int[][] dirs = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};
        for (int[] d : dirs) {
            int i = r + d[0], j = c + d[1];
            List<int[]> flips = new ArrayList<>();
            while (i >= 0 && j >= 0 && i < SIZE && j < SIZE && board[i][j] == opponent) {
                flips.add(new int[]{i, j});
                i += d[0]; j += d[1];
            }
            if (i >= 0 && j >= 0 && i < SIZE && j < SIZE && board[i][j] == player)
                for (int[] f : flips) board[f[0]][f[1]] = player;
        }
    }

    public static int[] countPieces(char[][] board) {
    int black = 0;
    int white = 0;

    for (int r = 0; r < board.length; r++) {
        for (int c = 0; c < board[r].length; c++) {
            if (board[r][c] == 'B') black++;
            else if (board[r][c] == 'W') white++;
        }
    }


    return new int[]{black, white}; // [0] = black, [1] = white
    }

    public static int calculateScore(char[][] board){
        int[] pieces = countPieces(board) ;
        int sum = pieces[0] - pieces[1] ; // pieces one is black which is always max since he is the first player and pieces[1] is white since he is the second player playing he is min
        return sum ;
    }
    
    public static int MiniMax(char[][] state,int alpha ,int beta,char player , int deapth){
        int eval ;
        if (deapth == 0 ) return calculateScore(state) ;

        List <int[]> validMoves = getValidMoves(state,player);

        // if there are no moves left return the score
        if (validMoves.isEmpty()){ 
            if (getValidMoves(state,opponent(player)).isEmpty()){
                return calculateScore(state) ;
            }      
        }

        if (player == 'B'){
            int maxEval = Integer.MIN_VALUE ;    
            for ( int[] move : validMoves){
                char[][] tempState = exploreMove( move[0],move[1],player ,state );
                eval = MiniMax(tempState ,alpha , beta , opponent(player),deapth - 1); 
                maxEval = Math.max(eval,maxEval);
                if ( maxEval >= beta) { return maxEval ;} // pruning
                alpha = Math.max(maxEval , alpha );

            }

            return maxEval ;

        }else{
            int minEval = Integer.MAX_VALUE ; 
            for (int[] move : validMoves){
                char[][] tempState = exploreMove( move[0],move[1],player ,state );
                eval = MiniMax(tempState,alpha , beta , opponent(player), deapth - 1 ); 
                minEval = Math.min(minEval , eval );      
                if ( minEval <= alpha  ){ return minEval;}
                beta = Math.min(minEval, beta) ;

            }
            return minEval ;
        }
    } // mini max

    public static int[] miniMaxRoot(char[][] state,int alpha ,int beta, char player, int deapth){
        int bestValue = (player == 'B') ? Integer.MIN_VALUE : Integer.MAX_VALUE; // min value for black and max value for white ( black is max , white is min)
        int[] bestMove = {-1,-1} ; // we might change this

        

        List <int[]> validMoves = getValidMoves(state,player);

        if (validMoves.isEmpty()){ 
            if (getValidMoves(state,opponent(player)).isEmpty()){
                return bestMove ; // in this case the match is going to end
            }
        }

        for ( int[] move : validMoves){
            
                


            // apply the move and save the state of the board if its played
            char[][] nextState = exploreMove(move[0],move[1],player,state);
            // evaluate using the minimax algo
            int value = MiniMax(nextState,alpha,beta ,opponent(player), deapth -1 );

            // for which ever player if the nextState we are currently looking at is a better value than the one we consider the best value keep it
        
            if ( player == 'B'){ // MAX
                if (value >= bestValue ){
                    bestValue = value ;
                    bestMove = new int[] {move[0],move[1]} ;
                }
            }else { // MIN
                if ( value <= bestValue){
                    bestValue = value ;
                            bestMove = new int[] {move[0],move[1]};
                    }
            }
        }

        return bestMove ;
    }

    static char[][] exploreMove(int r, int c, char player, char[][] board) {

    // Copy the board
    char[][] newBoard = new char[SIZE][SIZE];
    for (int i = 0; i < SIZE; i++) {
        newBoard[i] = board[i].clone();
    }

    char opponent = (player == 'B') ? 'W' : 'B';
    newBoard[r][c] = player;

    int[][] dirs = {
        {-1,-1}, {-1,0}, {-1,1},
        {0,-1},          {0,1},
        {1,-1},  {1,0},  {1,1}
    };

    for (int[] d : dirs) {
        int i = r + d[0];
        int j = c + d[1];

        List<int[]> flips = new ArrayList<>();

        // Follow the direction to find opponent pieces
        while (i >= 0 && j >= 0 && i < SIZE && j < SIZE && newBoard[i][j] == opponent) {
            flips.add(new int[]{i, j});
            i += d[0];
            j += d[1];
        }

        // If we end on a friendly piece, flip collected pieces
        if (i >= 0 && j >= 0 && i < SIZE && j < SIZE && newBoard[i][j] == player) {
            for (int[] f : flips) {
                newBoard[f[0]][f[1]] = player;
            }
        }
    }

    return newBoard;
}

    static char opponent(char player){
        if (player == 'B'){ return 'W' ;}
        return 'B';
    }

    public static List<int[]> getValidMoves(char [][] state,char player){
        List<int[]> moves = new ArrayList<>() ;
        for (int r = 0 ; r < SIZE ; r ++){
            for (int c = 0 ; c < SIZE ; c ++ ){
                if ( state[r][c] == '.' && isValidMove(state ,r ,c ,player)){
                    moves.add(new int[]{r,c}) ;
                }
            }
        }
        return moves ;
    }

    static public void declareWinner(char[][] board){
        int score = calculateScore(board) ;
        if ( score > 0 ){ 
            System.out.println("Black wins!!!");
        }
        else if ( score < 0){
            System.out.println("White wins!!!");
        }else {
            System.out.println("Is's a tie!!!");
        }
    
    }

    static public void printValidMoves(List<int[]> moves){
        System.out.println("Aveliable valid moves :") ;
        for (int[] move : moves){
            System.out.println("Row: " + move[0] + " Collumn: " + move[1]);
        }
        System.out.print("\n");
    }

}

    