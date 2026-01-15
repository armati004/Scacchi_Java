package piece;

import main.GamePanel;
import main.Type;

public class Queen extends Piece{

	public Queen(int color, int col, int row) {
		super(color, col, row);

		type=Type.QUEEN;

		if (color==GamePanel.WHITE) {
			image=getImage("/piece/chess-queen-white");
		}
		else {
			image=getImage("/piece/chess-queen-black");
		}
	}
	public boolean canMove (int targetCol, int targetRow) {
		
		if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol,targetRow) ==false) {
			
			//orizzontale e verticale
			if(targetCol==preCol||targetRow==preRow) {
				if (isValidSquare (targetCol,targetRow) && pieceIsOnStraightLine (targetCol, targetRow)==false) {
					return true;
				}
			}
			
			//diagonale
			if (Math.abs(targetCol-preCol)==Math.abs(targetRow-preRow) && pieceIsOnDiagonalLine (targetCol, targetRow) == false) {
				if (isValidSquare(targetCol, targetRow)) {
					return true;
				}
			}
		}
		return false;
	}

}