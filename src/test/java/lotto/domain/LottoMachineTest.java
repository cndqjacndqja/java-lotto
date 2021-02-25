package lotto.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class LottoMachineTest {

    private final LottoMachine lottoMachine = new LottoMachine(() -> Arrays.asList(1, 2, 3, 4, 5, 6));
    private final String[] split = "1, 2, 3, 4, 5, 6".split(", ");
    private final ManualTickets manualTickets = ManualTickets.from(Arrays.asList(split, split));

    @DisplayName("구매 금액에서 수동 티켓 구매하고 남은 돈으로 자동 로또 티켓을 구매한다")
    @Test
    void issueLottoTickets() {
        PurchasingPrice purchasingPrice = new PurchasingPrice(3000);

        LottoTickets lottoTickets = lottoMachine.issueLottoTickets(purchasingPrice, manualTickets);
        int ticketCounts = lottoTickets.getTicketCounts();

        assertThat(ticketCounts).isEqualTo(3);
    }

    @DisplayName("구매 금액이 입력받은 수동 티켓을 구매하기 부족한 경우 예외 발생")
    @Test
    void cannotIssueManualLottoTickets() {
        PurchasingPrice purchasingPrice = new PurchasingPrice(500);

        assertThatCode(() -> {
            lottoMachine.issueLottoTickets(purchasingPrice, manualTickets);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("금액이 부족하여 로또 티켓을 구매할 수 없습니다.");
    }

    @DisplayName("수동을 구매하지 않더라도 자동 티켓 1장 뽑을 금액이 없으면 예외 발생")
    @Test
    void cannotIssueAutomaticLottoTickets() {
        PurchasingPrice purchasingPrice = new PurchasingPrice(900);

        assertThatCode(() -> {
            lottoMachine.issueLottoTickets(purchasingPrice, ManualTickets.from(Collections.emptyList()));
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("금액이 부족하여 로또 티켓을 구매할 수 없습니다.");
    }

    @DisplayName("로또 티켓을 구매하는데 든 금액을 반환한다")
    @Test
    void getPurchasingPrice() {
        LottoTickets lottoTickets = lottoMachine.issueLottoTickets(new PurchasingPrice(4000), ManualTickets.from(Collections.emptyList()));

        int purchasingPrice = lottoMachine.calculatePurchasingPrice(lottoTickets);

        assertThat(purchasingPrice).isEqualTo(4000);
    }
}