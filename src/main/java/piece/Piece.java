package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;
import main.Type;

public class Piece {

	public Type type;
	public BufferedImage image;
	public int x,y;
	public int col, row, preCol, preRow;
	public int color;
	public Piece hittingP;
	public boolean moved, twoStepped;
	
	public Piece (int color, int col, int row) {
		this.color=color;
		this.col=col;
		this.row=row;
		x=getX(col);
		y=getY(row);
		preCol=col;
		preRow=row;
	}
	
	// permette di importare le immagini scaricate
	public BufferedImage getImage (String imagePath) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath+".png"));
		}catch (IOException e) {
			e.printStackTrace();
		}
		return image;
		
	}
	
	public int getX(int col) {
		return col * Board.SQUARE_SIZE;
	}
	
	public int getY (int row) {
		return row * Board.SQUARE_SIZE;
	}
	public int getCol(int x) {
		return (x+Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
	}
	public int getRow(int y) {
		return (y+Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
	}
	// fornisce l'indice di che pezzo stia venendo colpito
	public int getIndex () {
		for (int index=0; index<GamePanel.simPieces.size(); index++) {
			if (GamePanel.simPieces.get(index)==this) {
				return index;
			}
		}
		return 0;
	}
	
	// modifica la posizione mettendo al centro del quadrato in cui si trova
	public void updatePosition() {
		
		// per il controllo sull'En Passant
		// se il pedone si muove di 2 righe twoPressed diventa true
		if (type == Type.PAWN) {
			if (Math.abs(row-preRow)==2) {
				twoStepped=true;
			}
		}
		x=getX(col);
		y=getY(row);
		preCol=getCol(x);
		preRow=getRow(y);
		moved=true;
	}
	
	public boolean canMove (int targetCol, int targetRow) {
		return false;
	}
	// controlla se il pezzo è all'interno della scacchiera
	public boolean isWithinBoard (int targetCol, int targetRow) {
		if (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
			return true;
		}
		return false;
	}
	
	// Controlla che il pezzo non si muova nella casella dove si trova
	public boolean isSameSquare (int targetCol, int targetRow) {
		if (targetCol==preCol && targetRow==preRow) {
			return true;
		}
		return false;
	}
	
	// metodo per reimpostare la posizione del pezzo in caso esso si muova in una casella illegale
	public void resetPosition () {
		col=preCol;
		row=preRow;
		x=getX(col);
		y=getY(row);
	}
	
	// metodo per controllare se il pezzo colpisce un altro pezzo, chiaramente eccetto il pezzo attivo stesso
	public Piece getHittingP (int targetCol,int targetRow) {
		for(Piece piece: GamePanel.simPieces) {
			if (piece.col==targetCol && piece.row ==targetRow && piece != this) {
				return piece;
			}
		}
		return null;
	}
	
	public boolean isValidSquare (int targetCol, int targetRow) {
		hittingP=getHittingP(targetCol, targetRow);
		
		if (hittingP==null) { // la casella è libera
			return true;
		}
		else { // la casella è occupata
			if (hittingP.color != this.color) { // se il pezzo è del colore opposto esso può essere catturato
				return true;
			}
			else {
				hittingP=null;
			}
		}
		return false;				
	}
	
	//controlla che il pezzo se "colpisce" un pezzo non possa prendere il pezzo successivo (vedi torrre che incontra il primo pezzo sulla sua linea di movimento)
	public boolean pieceIsOnStraightLine (int targetCol, int targetRow) {
		
		//quando il pezzo si sta muovendo verso sinistra
		//crea un loop che inizia dalla sinistra del pezzo e ne decrementa la colonna (si sposta sulla colonna a sinistra) e verifica finchè non c'è un pezzo sulla riga e in caso lo rende come l'unico pezzo mangiabile
		for (int c=preCol-1;c>targetCol;c--) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col==c && piece.row==targetRow) {
					hittingP=piece;
					return true;
				}
			}
		}
		
		//quando il pezzo si sta muovendo verso destra
		for (int c=preCol+1;c<targetCol;c++) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col==c && piece.row==targetRow) {
					hittingP=piece;
					return true;
				}
			}
		}
		
		//quando il pezzo si sta muovendo verso sopra
		for (int r=preRow-1;r>targetRow;r--) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col==targetCol && piece.row==r) {
					hittingP=piece;
					return true;
				}
			}
		}
		
		//quando il pezzo si sta muovendo verso sotto
		for (int r=preRow+1;r<targetRow;r++) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col==targetCol && piece.row==r) {
					hittingP=piece;
					return true;
				}
			}
		}
		
		
		return false;
	}
	
	//stesso ragionamento della funzione sopra, ma per mosse in diagonale
	public boolean pieceIsOnDiagonalLine (int targetCol, int targetRow) {
		
		if (targetRow<preRow) {

			// alto sx
			// fa lo stesso ragionamento dei for sopra soltanto la differenza è con diff, che corrisponde al movimento diagonale (ove la colonna cambia, ma corrisponde alla differenza con la riga)
			for (int c =preCol-1; c>targetCol;c--) {
				int diff = Math.abs(c-preCol);
				for (Piece piece:GamePanel.simPieces) {
					if (piece.col==c && piece.row==preRow-diff) {
						hittingP=piece;
						return true;
					}
				}
			}
			
			//alto dx
			for (int c =preCol+1; c<targetCol;c++) {
				int diff = Math.abs(c-preCol);
				for (Piece piece:GamePanel.simPieces) {
					if (piece.col==c && piece.row==preRow-diff) {
						hittingP=piece;
						return true;
					}
				}
			}
		}

		if (targetRow >preRow) {
		
			// basso sx
			for (int c =preCol-1; c>targetCol;c--) {
				int diff = Math.abs(c-preCol);
				for (Piece piece:GamePanel.simPieces) {
					if (piece.col==c && piece.row==preRow+diff) {
						hittingP=piece;
						return true;
					}
				}
			}
			
			//basso dx
			for (int c =preCol+1; c<targetCol;c++) {
				int diff = Math.abs(c-preCol);
				for (Piece piece:GamePanel.simPieces) {
					if (piece.col==c && piece.row==preRow+diff) {
						hittingP=piece;
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	//disegna image nel punto x,y, con grandezza board.square_size
	public void draw (Graphics2D g2) {
		g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
	}
}
