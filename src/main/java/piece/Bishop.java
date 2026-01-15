package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece{

	public Bishop(int color, int col, int row) {
		super(color, col, row);

		type=Type.BISHOP;

		if (color==GamePanel.WHITE) {
			image=getImage("/piece/chess-bishop-white");
		}
		else {
			image=getImage("/piece/chess-bishop-black");
		}
	}
	
	public boolean canMove (int targetCol, int targetRow) {
		
		if (isWithinBoard (targetCol,targetRow) && isSameSquare (targetCol, targetRow)==false) {
			
			// quando si muove l'alfiere la colonna e la riga hanno sempre lo stesso valore assoluto dopo la sottrazione tra dove si trovava e dove vuole mettersi
			if (Math.abs(targetCol-preCol)==Math.abs(targetRow-preRow) && pieceIsOnDiagonalLine (targetCol, targetRow) == false) {
					if (isValidSquare(targetCol, targetRow)) {
						return true;
					}
			}
		}
		
		return false;
	}

}
