package lotterymachine.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LotteryTickets {
    private final List<LotteryTicket> tickets;

    public LotteryTickets() {
        tickets = new ArrayList<>();
    }

    public Map<WinningLottery, Integer> getLotteriesResult(List<Integer> numbers, int bonusNumber) {
        final Map<WinningLottery, Integer> lotteriesResult = WinningLottery.getWinningLotteries();
        for (LotteryTicket lotteryTicket : tickets) {
            int matchingNumbers = lotteryTicket.countMatchingNumbers(numbers);
            boolean containsBonus = lotteryTicket.containsNumber(bonusNumber);
            WinningLottery winningLottery = WinningLottery.find(matchingNumbers, containsBonus);
            lotteriesResult.put(winningLottery, lotteriesResult.getOrDefault(winningLottery, 0) + 1);
        }
        return Collections.unmodifiableMap(lotteriesResult);
    }

    public List<LotteryTicket> getLotteryTickets() {
        return Collections.unmodifiableList(tickets);
    }

    public void add(List<Integer> numbers) {
        tickets.add(new LotteryTicket(numbers));
    }
}
