package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece{

	public Pawn(int color, int col, int row) {
		super(color, col, row);

		type=Type.PAWN;
		
		if (color==GamePanel.WHITE) {
			image=getImage("/piece/chess-pawn-white");
		}
		else {
			image=getImage("/piece/chess-pawn-black");
		}
	}
	
	public boolean canMove (int targetCol, int targetRow) {
		
		if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol,targetRow) == false) {
			
			// definire le sue mosse possibili in base al suo colore
			int moveValue;
			
			// se bianco il valore è -1 altrimenti è 1
			if (color == GamePanel.WHITE) {
				moveValue=-1;
			}
			else {
				moveValue=1;
			}
			
			// controllo sul pezzo che colpisce
			hittingP=getHittingP (targetCol,targetRow);
			
			//movimento di 1 casella
			if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
				return true;
			}
			if(targetCol == preCol && targetRow == preRow + moveValue*2 && hittingP == null && moved==false && pieceIsOnStraightLine(targetCol, targetRow) == false) {
				return true;
			}
			
			//cattura in diagonale del pedone
			if (Math.abs(targetCol-preCol)==1 && targetRow==preRow+moveValue && hittingP != null && hittingP.color != color) {
				return true;
			}
			
			//En passant
			if (Math.abs(targetCol-preCol)==1 && targetRow==preRow+moveValue) {
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col==targetCol && piece.row == preRow && piece.twoStepped==true) {
						hittingP=piece;
						return true;
					}
				}
			}

			
		}
		
		return false;
	}

}
