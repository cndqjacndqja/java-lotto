package lotterymachine.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum WinningLottery {
    THREE(3, 5000),
    FOUR(4, 50000),
    FIVE(5, 150000),
    BONUS_FIVE(5, 30000000),
    SIX(6, 2000000000);

    private static final int INITIAL_NUMBER_OF_MATCHING_TICKET = 0;
    private final int number;
    private final int price;

    WinningLottery(int number, int price) {
        this.number = number;
        this.price = price;
    }

    public static WinningLottery find(int number) {
        return Arrays.stream(values())
                .filter(value -> value.matchNumber(number))
                .findFirst()
                .orElse(null);
    }

    private boolean matchNumber(int number) {
        return this.number == number;
    }

    public int getNumber() {
        return number;
    }

    public int getPrice() {
        return price;
    }

    public static Map<WinningLottery, Integer> getWinningLotteries() {
        Map<WinningLottery, Integer> winningLotteries = new HashMap<>();
        Arrays.stream(values())
                .forEach(value -> winningLotteries.put(value, INITIAL_NUMBER_OF_MATCHING_TICKET));
        return winningLotteries;
    }
}
