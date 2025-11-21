package client.model;

public class Card {
    private String rank; // "3".."10","J","Q","K","A","2"
    private String suit; // "C","D","H","S"

    public Card(String code) {
        // ví dụ code = "10H", "AS"
        this.rank = code.replaceAll("[CDHS]", "");
        this.suit = code.substring(code.length() - 1);
    }

    public String getRank() { return rank; }
    public String getSuit() { return suit; }
    public String getCode() { return rank + suit; }

    @Override
    public String toString() {
        return getCode();
    }
}
