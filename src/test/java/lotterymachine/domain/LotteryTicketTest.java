package lotterymachine.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LotteryTicketTest {

    @ParameterizedTest
    @CsvSource(value = {"1,2,3,4,5,6:0", "1,2,3,4,7,8:2", "1,2,3,7,8,9:3"}, delimiter = ':')
    @DisplayName("당첨번호와 일치하는 로또 숫자의 개수를 반환한다.")
    void countMatchingNumbers(String winningNumbers, int expected) {
        LotteryTicket lotteryTicket = new LotteryTicket(Arrays.asList(7, 8, 9, 10, 11, 12));
        List<Integer> numbers = Arrays.stream(winningNumbers.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        assertThat(lotteryTicket.countMatchingNumbers(numbers)).isEqualTo(expected);
    }


    @ParameterizedTest
    @CsvSource(value = {"1:false", "30:true"}, delimiter = ':')
    @DisplayName("보너스 번호 보유 여부를 확인한다.")
    void matchBonusNumber(int bonusNumber, boolean expected) {
        LotteryTicket lotteryTicket = new LotteryTicket(Arrays.asList(7, 8, 9, 10, 11, 30));
        assertThat(lotteryTicket.containsNumber(bonusNumber)).isEqualTo(expected);
    }
}
