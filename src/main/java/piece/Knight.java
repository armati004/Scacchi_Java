package piece;

import main.GamePanel;
import main.Type;

public class Knight extends Piece{

	public Knight(int color, int col, int row) {
		super(color, col, row);

		type=Type.KNIGHT;

		if (color==GamePanel.WHITE) {
			image=getImage("/piece/chess-knight-white");
		}
		else {
			image=getImage("/piece/chess-knight-black");
		}
	}
	public boolean canMove (int targetCol, int targetRow) {
		
		if(isWithinBoard (targetCol, targetRow)) {
			// il cavallo può fare le sue mosse con un ratio colonna:riga di 1:2 o 2:1
			if (Math.abs(targetCol-preCol)*Math.abs(targetRow-preRow)==2) {
				if (isValidSquare (targetCol, targetRow)) {
					return true;
				}
			}
		}
		return false;
	}

}
