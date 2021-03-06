package nl.gogognome.gogochess.logic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import java.util.List;

import static nl.gogognome.gogochess.logic.Square.*;

public class Squares {

	public static final Square A1 = new Square("a1");
	public static final Square A2 = new Square("a2");
	public static final Square A3 = new Square("a3");
	public static final Square A4 = new Square("a4");
	public static final Square A5 = new Square("a5");
	public static final Square A6 = new Square("a6");
	public static final Square A7 = new Square("a7");
	public static final Square A8 = new Square("a8");
	public static final Square B1 = new Square("b1");
	public static final Square B2 = new Square("b2");
	public static final Square B3 = new Square("b3");
	public static final Square B4 = new Square("b4");
	public static final Square B5 = new Square("b5");
	public static final Square B6 = new Square("b6");
	public static final Square B7 = new Square("b7");
	public static final Square B8 = new Square("b8");
	public static final Square C1 = new Square("c1");
	public static final Square C2 = new Square("c2");
	public static final Square C3 = new Square("c3");
	public static final Square C4 = new Square("c4");
	public static final Square C5 = new Square("c5");
	public static final Square C6 = new Square("c6");
	public static final Square C7 = new Square("c7");
	public static final Square C8 = new Square("c8");
	public static final Square D1 = new Square("d1");
	public static final Square D2 = new Square("d2");
	public static final Square D3 = new Square("d3");
	public static final Square D4 = new Square("d4");
	public static final Square D5 = new Square("d5");
	public static final Square D6 = new Square("d6");
	public static final Square D7 = new Square("d7");
	public static final Square D8 = new Square("d8");
	public static final Square E1 = new Square("e1");
	public static final Square E2 = new Square("e2");
	public static final Square E3 = new Square("e3");
	public static final Square E4 = new Square("e4");
	public static final Square E5 = new Square("e5");
	public static final Square E6 = new Square("e6");
	public static final Square E7 = new Square("e7");
	public static final Square E8 = new Square("e8");
	public static final Square F1 = new Square("f1");
	public static final Square F2 = new Square("f2");
	public static final Square F3 = new Square("f3");
	public static final Square F4 = new Square("f4");
	public static final Square F5 = new Square("f5");
	public static final Square F6 = new Square("f6");
	public static final Square F7 = new Square("f7");
	public static final Square F8 = new Square("f8");
	public static final Square G1 = new Square("g1");
	public static final Square G2 = new Square("g2");
	public static final Square G3 = new Square("g3");
	public static final Square G4 = new Square("g4");
	public static final Square G5 = new Square("g5");
	public static final Square G6 = new Square("g6");
	public static final Square G7 = new Square("g7");
	public static final Square G8 = new Square("g8");
	public static final Square H1 = new Square("h1");
	public static final Square H2 = new Square("h2");
	public static final Square H3 = new Square("h3");
	public static final Square H4 = new Square("h4");
	public static final Square H5 = new Square("h5");
	public static final Square H6 = new Square("h6");
	public static final Square H7 = new Square("h7");
	public static final Square H8 = new Square("h8");

	static final List<Square> ALL;

	static {
		Builder<Square> builder = new Builder<>();
		for (int file = FILE_A; file<= FILE_H; file++) {
			for (int rank=RANK_1; rank<=RANK_8; rank++) {
				builder.add(new Square(file, rank));
			}
		}
		ALL = builder.build();
	}
}
