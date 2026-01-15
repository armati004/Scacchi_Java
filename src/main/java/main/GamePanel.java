package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.Piece;
import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Queen;
import piece.Rook;


public class GamePanel extends JPanel implements Runnable{

	//TODO: definire regole 50 mosse, ripetizione
	//TODO: invertire scacchiera quando tocca all'altro giocatore
	//TODO: implementare online
	//TODO: implementare computer
	
	public static final int WIDTH=1100;
	public static final int HEIGHT = 800;
	final int FPS=60;
	Thread gameThread;
	Board board=new Board();
	Mouse mouse=new Mouse();
	
	//Pezzi
	public static ArrayList<Piece> pieces = new ArrayList<>(); //backup list
	public static ArrayList<Piece> simPieces = new ArrayList<>();
	ArrayList<Piece> promoPieces = new ArrayList<>();
	Piece activeP, checkingP;
	public static Piece castlingP;
	
	//Colori
	public static final int WHITE=0;
	public static final int BLACK=1;
	int currentColor=WHITE;
	
	//booleani
	boolean canMove;
	boolean validSquare;
	boolean promotion;
	boolean gameOver;
	boolean stalemate;
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.black);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		setPieces();
		copyPieces(pieces,simPieces);
	}
	
	public void launchGame () {
		gameThread = new Thread (this);
		gameThread.start();
	}
	
	public void setPieces() {
		
		//bianchi
		pieces.add(new Pawn(WHITE,0,6));
		pieces.add(new Pawn(WHITE,1,6));
		pieces.add(new Pawn(WHITE,2,6));
		pieces.add(new Pawn(WHITE,3,6));
		pieces.add(new Pawn(WHITE,4,6));
		pieces.add(new Pawn(WHITE,5,6));
		pieces.add(new Pawn(WHITE,6,6));
		pieces.add(new Pawn(WHITE,7,6));
		pieces.add(new Rook(WHITE,0,7));
		pieces.add(new Knight(WHITE,1,7));
		pieces.add(new Bishop(WHITE,2,7));
		pieces.add(new Queen(WHITE,3,7));
		pieces.add(new King(WHITE,4,7));
		pieces.add(new Bishop(WHITE,5,7));
		pieces.add(new Knight(WHITE,6,7));
		pieces.add(new Rook(WHITE,7,7));

		//neri
		pieces.add(new Pawn(BLACK,0,1));
		pieces.add(new Pawn(BLACK,1,1));
		pieces.add(new Pawn(BLACK,2,1));
		pieces.add(new Pawn(BLACK,3,1));
		pieces.add(new Pawn(BLACK,4,1));
		pieces.add(new Pawn(BLACK,5,1));
		pieces.add(new Pawn(BLACK,6,1));
		pieces.add(new Pawn(BLACK,7,1));
		pieces.add(new Rook(BLACK,0,0));
		pieces.add(new Knight(BLACK,1,0));
		pieces.add(new Bishop(BLACK,2,0));
		pieces.add(new Queen(BLACK,3,0));
		pieces.add(new King(BLACK,4,0));
		pieces.add(new Bishop(BLACK,5,0));
		pieces.add(new Knight(BLACK,6,0));
		pieces.add(new Rook(BLACK,7,0));
	}
	
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		target.clear();
		for (int i=0; i<source.size(); i++) {
			target.add(source.get(i));
		}
	}
	
	
	//viene creato un loop tale che il gioco continui ad andare e venga aggiornato costantemente
	@Override
	public void run() {
		
		// GAME LOOP
		double drawInterval = 1000000000/FPS;
		double delta =0;
		long lastTime=System.nanoTime();
		long currentTime;
		
		while (gameThread != null) {
			
			currentTime = System.nanoTime();
			
			delta += (currentTime-lastTime)/drawInterval;
			lastTime=currentTime;
			
			if (delta >=1) {
				update();
				repaint();
				delta--;
			}
		}
		 
	}
	
	private void update() {
		
		if (promotion) {
			promoting();
		} else if (gameOver != true && stalemate != true){
			///// MOUSE PREMUTO /////
			if (mouse.pressed) {
				//se non è selezionato alcun pezzo, controlla se si possano prendere dei pezzi
				if (activeP==null) {
					for (Piece piece:simPieces) {
						// se il mouse è su un pezzo alleato, lo prende come activeP
						if(piece.color==currentColor &&
								piece.col==mouse.x/Board.SQUARE_SIZE &&
								piece.row==mouse.y/Board.SQUARE_SIZE) {
							activeP=piece;
						}
					}
				}
				else {
					// se il giocatore sta tenendo il pezzo, ne simula il movimento
					simulate();
				}
			}
			
			///// MOUSE RILASCIATO /////
			if (mouse.pressed==false) {
				if (activeP != null) {
					
					if (validSquare) {
						
						// MOSSA CONFERMATA
						
						//aggiorna la lista dei pezzi in caso un pezzo sia stato catturato e rimosso durante la simulazione
						copyPieces(simPieces, pieces);
						activeP.updatePosition();
						if (castlingP != null) {
							castlingP.updatePosition();
						}
						
						if (isKingInCheck() && isCheckmate()) {
							gameOver=true;
						}
						else { // la partita non è finita
							if (canPromote()) {
								promotion=true;
							}
							else {
								changePlayer();	
								
								if (isStalemate() || isInsufficientMaterial()) {
									stalemate=true;
								}
							}	
						}	
					}
					else {
						// la mossa è illegale, quindi tutto viene resettato
						copyPieces (pieces, simPieces);
						activeP.resetPosition();
						activeP=null;
					}
				}
			}
		}
		

	}
	private void simulate () {
		
		canMove=false;
		validSquare=false;
		
		// resetta la lista dei pezzi ogni loop, rimettendo i pezzi rimossi durante la simulazione
		copyPieces(pieces,simPieces);
		
		//resetta la posizione del pezzo d'arrocco
		if (castlingP != null) {
			castlingP.col=castlingP.preCol;
			castlingP.x=castlingP.getX(castlingP.col);
			castlingP=null;
		}
		
		// se il pezzo è mantenuto, modifica la sua posizione
		activeP.x=mouse.x-Board.HALF_SQUARE_SIZE;
		activeP.y=mouse.y-Board.HALF_SQUARE_SIZE;
		activeP.col=activeP.getCol(activeP.x);
		activeP.row=activeP.getRow(activeP.y);
		
		// controlla se i pezzi si possano muovere in una casella raggiungibile
		// per ciascun pezzo chiama il rispettivo canMove in modo tale da verificare le loro possibili mosse
		if (activeP.canMove(activeP.col, activeP.row)) {
			
			canMove=true;
			
			// se colpisce un pezzo, lo cattura, rimuovendolo dalla lista
			if (activeP.hittingP != null) {
				simPieces.remove(activeP.hittingP.getIndex());
			}
			
			checkCastling();
			
			if (isIllegal (activeP)==false && opponentCanCaptureKing()==false) {
				validSquare=true;
			}
		}
	}
	
	// cambia il turno all'altro giocatore
	private void changePlayer() {
		
		if (currentColor == WHITE) {
			currentColor = BLACK;
			//resetta lo stato di 2 passi
			for (Piece piece : pieces) {
				if (piece.color==BLACK) {
					piece.twoStepped=false;
				}
			}
		}
		else {
			currentColor = WHITE;
			for (Piece piece : pieces) {
				if (piece.color==WHITE) {
					piece.twoStepped=false;
			}
		}
		
		activeP=null;
		}
	}
	
	private boolean isIllegal (Piece king) {
		
		if (king.type==Type.KING) {
			for (Piece piece : simPieces) {
				if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean opponentCanCaptureKing () {
		
		Piece king = getKing(false);
		
		for (Piece piece :simPieces) {
			if (piece.color != king.color && piece.canMove(king.col, king.row)) {
				return true;
			}
		}
		return false;
	}
	private boolean isInsufficientMaterial() {

	    int whiteMinor = 0;
	    int blackMinor = 0;

	    Piece whiteBishop = null;
	    Piece blackBishop = null;

	    for (Piece p : simPieces) {

	        // Se esiste almeno un pedone, torre o regina → NON è patta
	        if (p.type == Type.PAWN ||
	            p.type == Type.ROOK ||
	            p.type == Type.QUEEN) {
	            return false;
	        }

	        // Conta alfieri e cavalli
	        if (p.type == Type.BISHOP || p.type == Type.KNIGHT) {
	            if (p.color == WHITE) {
	                whiteMinor++;
	                if (p.type == Type.BISHOP) whiteBishop = p;
	            } else {
	                blackMinor++;
	                if (p.type == Type.BISHOP) blackBishop = p;
	            }
	        }
	    }

	    // Re vs Re
	    if (whiteMinor == 0 && blackMinor == 0) {
	        return true;
	    }

	    // Re + (Alfiere o Cavallo) vs Re
	    if ((whiteMinor == 1 && blackMinor == 0) ||
	        (whiteMinor == 0 && blackMinor == 1)) {
	        return true;
	    }

	    // Re + Alfiere vs Re + Alfiere (stesso colore di casa)
	    if (whiteMinor == 1 && blackMinor == 1 &&
	        whiteBishop != null && blackBishop != null) {

	        boolean whiteSquare =
	            (whiteBishop.col + whiteBishop.row) % 2 == 0;
	        boolean blackSquare =
	            (blackBishop.col + blackBishop.row) % 2 == 0;

	        if (whiteSquare == blackSquare) {
	            return true;
	        }
	    }

	    return false;
	}
	
	private boolean isKingInCheck () {
		
		Piece king = getKing(true);
		
		// controlla se il pezzo può andare sulla casella del re
		if (activeP.canMove(king.col, king.row)) {
			checkingP=activeP;
			return true;
		}
		else {
			checkingP=null;
		}
		
		return false;
	}
	
	private Piece getKing (boolean opponent) {
		Piece king=null;
		
		for (Piece piece : simPieces) {
			if (opponent) {	
				if (piece.type == Type.KING && piece.color != currentColor) {
					king=piece;
				}
			}
			else {
				if (piece.type == Type.KING && piece.color == currentColor) {
					king=piece;
				}
			}
		}
		return king;
	}
	
	private boolean isCheckmate () {
		
		Piece king = getKing(true);
		
		if (kingCanMove(king)) {
			return false;
		}
		else {
			// controlla se lo scacco sia bloccabile con un altro pezzo
			// controlla se un pezzo può mettersi tra la colonna e la riga dei pezzi che fanno scacco al re
			int colDiff = Math.abs(checkingP.col - king.col);
			int rowDiff = Math.abs(checkingP.row- king.row);
			
			if (colDiff==0) {
				// attacco in verticale
				
				if (checkingP.row < king.row) {
					//attacco da sopra
					
					for (int row=checkingP.row; row<king.row;row++) {
						for (Piece piece : simPieces) {
							if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
				
				if (checkingP.row>king.row) {
					//attacco da sotto
					
					for (int row=checkingP.row; row>king.row;row--) {
						for (Piece piece : simPieces) {
							if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
			}
			else if (rowDiff==0) {
				//attacco in orizzontale
				
				if (checkingP.col<king.col) {
					// attacco da sinistra
					
					for (int col=checkingP.col; col<king.col;col++) {
						for (Piece piece : simPieces) {
							if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
					
				}
				if (checkingP.col>king.col) {
					// attacco da destra
					
					for (int col=checkingP.col; col>king.col;col--) {
						for (Piece piece : simPieces) {
							if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
			}
			else if (rowDiff==colDiff) {
				//attacco in diagonale
				if (checkingP.row<king.row) {
					//attacco da sopra il re
					if (checkingP.col<king.col) {
						// l'attacco è da alto sinistra
						for (int col = checkingP.col, row = checkingP.row; col<king.col; col++,row++) {
							for (Piece piece : simPieces) {
								if (piece != king && piece.color != currentColor && piece.canMove (col, row)) {
									return false;
								}
							}
						}
					}
					if (checkingP.col>king.col) {
						// l'attacco è da alto destra
						for (int col = checkingP.col, row = checkingP.row; col>king.col; col--,row++) {
							for (Piece piece : simPieces) {
								if (piece != king && piece.color != currentColor && piece.canMove (col, row)) {
									return false;
								}
							}
						}
					}
				}
				if (checkingP.row>king.row) {
					//attacco da sotto il re
					if (checkingP.col<king.col) {
						// l'attacco è da basso sinistra
						for (int col = checkingP.col, row = checkingP.row; col<king.col; col++,row--) {
							for (Piece piece : simPieces) {
								if (piece != king && piece.color != currentColor && piece.canMove (col, row)) {
									return false;
								}
							}
						}
					}
					if (checkingP.col>king.col) {
						// l'attacco è da basso destra
						for (int col = checkingP.col, row = checkingP.row; col>king.col; col--,row--) {
							for (Piece piece : simPieces) {
								if (piece != king && piece.color != currentColor && piece.canMove (col, row)) {
									return false;
								}
							}
						}
					}
				}
			}

		}
		
		return true;
	}
	
	private boolean kingCanMove (Piece king) {
		
		// simula se ci sono altre caselle dove il re possa muoversi
		// se nella casella ipotetica si può muovere allora è true (es -1,-1 è in alto a sx rispetto al re)
		if (isValidMove(king, -1,-1)) {return true;}
		if (isValidMove(king, -1,0)) {return true;}
		if (isValidMove(king, -1,1)) {return true;}
		if (isValidMove(king, 0,-1)) {return true;}
		if (isValidMove(king, 0,1)) {return true;}
		if (isValidMove(king, 1,-1)) {return true;}
		if (isValidMove(king, 1,0)) {return true;}
		if (isValidMove(king, 1,1)) {return true;}

		return false;
	}
	
	private boolean isValidMove (Piece king, int colPlus, int rowPlus) {
		
		boolean isValidMove = false;
		
		//modifica la posizione del re per 1 sec
		king.col += colPlus;
		king.row += rowPlus;
		
		if (king.canMove(king.col, king.row)) {
			if (king.hittingP != null) {
				simPieces.remove(king.hittingP.getIndex());
			}
			if (isIllegal(king) == false) {
				isValidMove=true;
			}
		}

		// imposta la posizione del re e rimette al suo posto il pezzo rimosso
		king.resetPosition();
		copyPieces (pieces, simPieces);
		
		return isValidMove;
	}
	
	private boolean isStalemate() {

	    // 1. Il re NON deve essere sotto scacco
	    Piece king = getKing(true); // re del giocatore che deve muovere
	    if (opponentCanCaptureKing()) {
	        return false;
	    }

	    // 2. Per ogni pezzo del giocatore che deve muovere
	    for (Piece piece : simPieces) {

	        if (piece.color != currentColor) {

	            // prova tutte le caselle della scacchiera
	            for (int col = 0; col < 8; col++) {
	                for (int row = 0; row < 8; row++) {

	                    if (piece.canMove(col, row)) {

	                        // salva stato
	                        int prevCol = piece.col;
	                        int prevRow = piece.row;
	                        Piece captured = piece.hittingP;

	                        // simula mossa
	                        piece.col = col;
	                        piece.row = row;
	                        if (captured != null) {
	                            simPieces.remove(captured.getIndex());
	                        }

	                        boolean illegal = isIllegal(king);

	                        // ripristina
	                        piece.col = prevCol;
	                        piece.row = prevRow;
	                        copyPieces(pieces, simPieces);

	                        // se esiste almeno UNA mossa legale → NON è stallo
	                        if (!illegal) {
	                            return false;
	                        }
	                    }
	                }
	            }
	        }
	    }

	    // nessuna mossa legale trovata
	    return true;
	}
	
	private void checkCastling() {
		if (castlingP != null) {
			if (castlingP.col==0) {
				castlingP.col+=3;
			}
			else if (castlingP.col==7) {
				castlingP.col-=2;
			}
			castlingP.x=castlingP.getX(castlingP.col);
		}
	}
	
	private boolean canPromote() {
		
		if (activeP.type==Type.PAWN) {
			if (currentColor==WHITE && activeP.row==0 || currentColor == BLACK && activeP.row==7) {
				promoPieces.clear();
				promoPieces.add(new Rook(currentColor,9,2));
				promoPieces.add(new Knight(currentColor,9,3));
				promoPieces.add(new Bishop(currentColor,9,4));
				promoPieces.add(new Queen(currentColor,9,5));
				return true;
				
			}
		}
		return false;
	}
	
	private void promoting () {
		
		if (mouse.pressed) {
			for (Piece piece : promoPieces) {
				if (piece.col==mouse.x/Board.SQUARE_SIZE && piece.row==mouse.y/Board.SQUARE_SIZE) {
					switch (piece.type) {
						case ROOK: simPieces.add(new Rook (currentColor, activeP.col, activeP.row)); break;
						case KNIGHT: simPieces.add(new Knight (currentColor, activeP.col, activeP.row)); break;
						case BISHOP: simPieces.add(new Bishop (currentColor, activeP.col, activeP.row)); break;
						case QUEEN: simPieces.add(new Queen (currentColor, activeP.col, activeP.row)); break;
						default: break;
					}
					simPieces.remove(activeP.getIndex());
					copyPieces (simPieces, pieces);
					activeP=null;
					promotion =false;
					changePlayer();
				}
			}
		}
		
	}
	
	//utilizzato per disegnare oggetti sul panel
	public void paintComponent (Graphics g) {
		 super.paintComponent(g);
		 
		 Graphics2D g2= (Graphics2D)g;
		 
		 //board
		 board.draw(g2);

		 //pieces
		 for (Piece p : simPieces) {
			 p.draw(g2);
			 
		 }
		 if (activeP!=null) {
			 if (canMove) {
				 if(isIllegal (activeP) || opponentCanCaptureKing()) {
					 g2.setColor(Color.red);
					 g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					 g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					 g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				 }else {
					 g2.setColor(Color.white);
					 g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					 g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					 g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				 }

			 }

			 //mostra il pezzo affichè non venga oscurato dalla board o dal quadratico colorato
			 activeP.draw(g2);
		 }
		 
		 //Messaggio di stato
		 g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		 g2.setFont(new Font ("Book Antiqua", Font.PLAIN, 30));
		 g2.setColor(Color.white);
		 
		 if (promotion) {
			 g2.drawString("Promuovi a", 840, 150);
			 for (Piece piece : promoPieces) {
				 g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			 }
		 }else {
			 if (currentColor == WHITE) {
				 g2.drawString("Turno del bianco", 840, 550);
				 if (checkingP != null && checkingP.color == BLACK) {
					 g2.setColor(Color.red);
					 g2.drawString("Il re", 840, 200);
					 g2.drawString("è sotto scacco", 840, 250);
				 }
			 }
			 else {
				 g2.drawString("Turno del nero", 840, 250);
				 if (checkingP != null && checkingP.color == WHITE) {
					 g2.setColor(Color.red);
					 g2.drawString("Il re", 840, 500);
					 g2.drawString("è sotto scacco", 840, 550);
				 }
			 }
		 }
		 
		 if (gameOver) {
			 String s = "";
			 if (currentColor==WHITE) {
				 s="Vince il bianco";
			 }
			 else {
				 s="Vince il nero";
			 }
			 g2.setFont(new Font ("Arial", Font.PLAIN, 90));
			 g2.setColor(Color.GREEN);
			 g2.drawString(s, 200, 420);
			 
		 }
		 
		 if (stalemate) {
			 g2.setFont(new Font ("Arial", Font.PLAIN, 90));
			 g2.setColor(Color.GRAY);
			 g2.drawString("Stallo", 200, 420);
		 }
		 
	}


}
