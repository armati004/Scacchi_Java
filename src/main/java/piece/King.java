package piece;

import main.GamePanel;
import main.Type;

public class King extends Piece{

	public King(int color, int col, int row) {
		super(color, col, row);

		type=Type.KING;

		if (color==GamePanel.WHITE) {
			image=getImage("/piece/chess-king-white");
		}
		else {
			image=getImage("/piece/chess-king-black");
		}
	}
	
	// controlla su quale casella è e verifica se si può muovere sulla stessa
	public boolean canMove (int targetCol, int targetRow) {
		
		if (isWithinBoard (targetCol, targetRow)) {
			
			//mosse normali
			//per il movimento su, giù, destra e sinistra, controlla la posizione precedente e la sottrae alla posizione bersaglio, dunque in caso sia 1 la mossa è legale
			if (Math.abs(targetCol-preCol)+Math.abs(targetRow-preRow)==1||
					
					//per il movimento indiagonale, in questo caso fa la moltiplicazione e se essa è =1 allora la mossa è legale
					Math.abs(targetCol-preCol)*Math.abs(targetRow-preRow)==1) {
				
				if (isValidSquare(targetCol, targetRow)) {
					return true;					
				}
			}
			
			//arrocco
			if (moved==false) {
			
				//arrocco a dx
				if (targetCol==preCol+2 && targetRow == preRow && pieceIsOnStraightLine (targetCol, targetRow)==false) {
					for (Piece piece : GamePanel.simPieces) {
						if(piece.col==preCol+3 && piece.row == preRow && piece.moved==false) {
							GamePanel.castlingP=piece;
							return true;
						}
					}
				}
				
				//arrocco a sx
				if (targetCol==preCol-2 && targetRow == preRow && pieceIsOnStraightLine (targetCol, targetRow)==false) {
					Piece p[]=new Piece[2];
					for (Piece piece : GamePanel.simPieces) {
						if(piece.col==preCol-3 && piece.row == preRow) {
							p[0]=piece;
						}
						if (piece.col==preCol-4 && piece.row==targetRow) {
							p[1]=piece;
						}
						if (p[0]==null && p[1] != null && p[1].moved==false) {
							GamePanel.castlingP=p[1];
							return true;
						}
					}
				}
			}
			
		}
		
		return false;
	}

}