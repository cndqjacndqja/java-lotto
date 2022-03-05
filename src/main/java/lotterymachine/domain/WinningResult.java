package lotterymachine.domain;

import lotterymachine.domain.vo.Money;
import lotterymachine.utils.LotteryCalculator;

import java.util.EnumMap;
import java.util.Map;

public class WinningResult {
    private final Map<WinningLotteryRank, Integer> result = new EnumMap<>(WinningLotteryRank.class);
    private final LotteryTickets lotteryTickets;
    private final WinningLottery winningLottery;

    public WinningResult(LotteryTickets lotteryTickets, WinningLottery winningLottery) {
        this.lotteryTickets = lotteryTickets;
        this.winningLottery = winningLottery;
        createResult();
    }

    public Map<WinningLotteryRank, Integer> getResult() {
        return this.result;
    }

    public double getTotalProfitRate(Money money) {
        int totalProfit = totalProfit();
        return LotteryCalculator.calculateProfitRate(totalProfit, money.getValue());
    }

    private void createResult() {
        initResult();
        for (LotteryTicket lotteryTicket : lotteryTickets.getLotteryTickets()) {
            WinningLotteryRank winningLotteryRank = winningLottery.getWinningLotteryRank(lotteryTicket);
            result.put(winningLotteryRank, result.get(winningLotteryRank) + 1);
        }
    }

    private void initResult() {
        for (WinningLotteryRank winningLotteryRank : WinningLotteryRank.values()) {
            result.put(winningLotteryRank, 0);
        }
    }

    private int totalProfit() {
        int sum = 0;
        for (WinningLotteryRank winningLotteryRank : result.keySet()) {
            sum += winningLotteryRank.getPrice() * result.get(winningLotteryRank);
        }
        return sum;
    }
}