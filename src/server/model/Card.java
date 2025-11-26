package server.model;

public class Card {

    public enum Suit {
        CLUBS, DIAMONDS, HEARTS, SPADES
    }

    public enum Rank {
        THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN,
        JACK, QUEEN, KING, ACE, TWO
    }

    private Rank rank;
    private Suit suit;
    private String code;       // ví dụ: 3C, KD, AS
    private String imagePath;  // đường dẫn ảnh DB

    public Card(String code, String imagePath) {
        this.code = code;
        this.imagePath = imagePath;
        parseCode(code);
    }

    private void parseCode(String code) {
        // Tách rank
        String r = code.substring(0, code.length() - 1);
        switch (r) {
            case "3": rank = Rank.THREE; break;
            case "4": rank = Rank.FOUR; break;
            case "5": rank = Rank.FIVE; break;
            case "6": rank = Rank.SIX; break;
            case "7": rank = Rank.SEVEN; break;
            case "8": rank = Rank.EIGHT; break;
            case "9": rank = Rank.NINE; break;
            case "10": rank = Rank.TEN; break;
            case "J": rank = Rank.JACK; break;
            case "Q": rank = Rank.QUEEN; break;
            case "K": rank = Rank.KING; break;
            case "A": rank = Rank.ACE; break;
            case "2": rank = Rank.TWO; break;
        }

        // Tách suit
        char s = code.charAt(code.length() - 1);
        switch (s) {
            case 'C': suit = Suit.CLUBS; break;
            case 'D': suit = Suit.DIAMONDS; break;
            case 'H': suit = Suit.HEARTS; break;
            case 'S': suit = Suit.SPADES; break;
        }
    }

    public Rank getRank() { return rank; }
    public Suit getSuit() { return suit; }
    public String getCode() { return code; }
    public String getImagePath() { return imagePath; }
    
    @Override
    public String toString() {
        return code;
    }
}
