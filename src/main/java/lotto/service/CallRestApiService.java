package lotto.service;

import com.google.gson.*;
import lotto.dao.UserLottoDAO;
import lotto.dao.WinnerDAO;
import lotto.domain.*;
import lotto.domain.autocreatelotto.DefaultAutoCreateLotto;
import lotto.dto.WinnerDTO;
import spark.Request;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author heebg
 * @version 1.0 2019-06-10
 */
public class CallRestApiService {
    private static Money money;
    private static Lotteries lotteries;

    /**
     *
     * @param req
     * @return
     */
    public String lottoBuyCount(Request req) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(req.body());
        long reqMoney = jsonElement.getAsJsonObject().get("money").getAsLong();
        money = new Money(reqMoney);

        return new Gson().toJson(money);
    }

    /**
     *
     * @param req
     * @return
     */
    public String detailLotteries(Request req) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(req.body());

        long manualCount = jsonElement.getAsJsonObject().get("manual_count").getAsLong();
        long autoCount = money.calculateAutoCount(manualCount);

        lotteries = generateLotteries(jsonElement, manualCount, autoCount);

        JsonObject jsonObject = generateResponseDetailLotteries(manualCount, autoCount, lotteries);
        return new Gson().toJson(jsonObject);
    }

    // TODO : request를 이용해 domain 객체를 '직접' 만드는것보다 다른 방법이 없을까
    private Lotteries generateLotteries(JsonElement jsonElement, long manualCount, long autoCount) {
        lotteries = generateManualLotteries(manualCount, jsonElement);
        lotteries.addAutoLotteries(autoCount, new DefaultAutoCreateLotto());
        return lotteries;
    }

    private Lotteries generateManualLotteries(long manualCount, JsonElement jsonElement) {
        lotteries = new Lotteries();
        if (manualCount != 0) {
            JsonArray array = jsonElement.getAsJsonObject().get("lotteries").getAsJsonArray();
            lotteries = addManualLotteries(array);
        }
        return lotteries;
    }

    private Lotteries addManualLotteries(JsonArray array) {
        List<Lotto> lottos = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonArray lotto = array.get(i).getAsJsonArray();
            lottos.add(generateLotto(lotto));
        }
        System.out.println(array.get(0).getAsJsonArray().get(0).getAsInt());

        return new Lotteries(lottos);
    }

    // TODO : response를 JsonObject가 아닌 객체로 만드는게 어떨까
    private JsonObject generateResponseDetailLotteries(long manualCount, long autoCount, Lotteries lotteries) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("lotteries", generateResponseLotteries(lotteries));
        jsonObject.addProperty("manual_count", manualCount);
        jsonObject.addProperty("auto_count", autoCount);
        return jsonObject;
    }

    private JsonArray generateResponseLotteries(Lotteries lotteries) {
        JsonArray jsonArray = new JsonArray();
        for (Lotto lotto : lotteries) {
            jsonArray.add(generateResponseLotto(lotto));
        }

        return jsonArray;
    }

    private JsonArray generateResponseLotto(Lotto lotto) {
        JsonArray jsonArray = new JsonArray();
        for (LottoNumber lottoNumber : lotto) {
            jsonArray.add(lottoNumber.toString());
        }
        return jsonArray;
    }

    /**
     *
     * @param req
     * @return
     * @throws SQLException
     */
    public String detailResult(Request req) throws SQLException {
        // TODO : JsonElement를 여기서 사용하지 않고 바로 객체로 만들 수 있지 않을까
        JsonElement jsonElement = new JsonParser().parse(req.body());
        int lottoNumber = jsonElement.getAsJsonObject().get("bonus").getAsInt();

        Winner winner = new Winner(generateLotto(jsonElement.getAsJsonObject().get("winLotto").getAsJsonArray()), new LottoNumber(lottoNumber));
        RankResult rankResult = new RankResult(lotteries, winner, money);

        WinnerDAO.addWinner(new WinnerDTO(rankResult));
        int recentTurn = WinnerDAO.findRecentTurn();

        UserLottoDAO.addUserLotteries(rankResult.getLotteries(), recentTurn);
        JsonObject jsonObject = generateResponseDetailResult(rankResult, recentTurn);

        return new Gson().toJson(jsonObject);
    }

    private Lotto generateLotto(JsonArray lotto) {
        List<LottoNumber> lottoNumbers = new ArrayList<>();
        for (int i = 0; i < lotto.size(); i++) {
            lottoNumbers.add(new LottoNumber(lotto.get(i).getAsInt()));
        }
        return Lotto.createLotto(lottoNumbers);
    }

    // TODO : response를 JsonObject가 아닌 객체로 만드는게 어떨까
    private JsonObject generateResponseDetailResult(RankResult rankResult, int turn) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rate",rankResult.getRate());
        jsonObject.add("result",generateResponseRank(rankResult.getRankResult()));
        jsonObject.addProperty("turn", turn);
        return jsonObject;
    }

    private JsonArray generateResponseRank(Map<Rank, Integer> rankResult) {
        JsonArray jsonArray = new JsonArray();
        for (Map.Entry<Rank, Integer> entry : rankResult.entrySet()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("rank",entry.getKey().getRank());
            jsonObject.addProperty("match_count", entry.getValue());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 
     * @param req
     * @return
     * @throws SQLException
     */
    // TODO : response를 JsonObject가 아닌 객체로 만드는게 어떨까
    // TODO : JsonElement를 여기서 사용하지 않고 바로 객체로 만들 수 있지 않을까
    public String showHistory(Request req) throws SQLException {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(req.body());
        int turn = jsonElement.getAsJsonObject().get("turn").getAsInt();
        JsonObject jsonObject = new JsonObject();
        JsonObject winner = WinnerDAO.findWinnerByTurn(turn);
        jsonObject.add("winner", winner);
        jsonObject.add("userLotto",UserLottoDAO.findLotteriesByTurn(turn));
        return new Gson().toJson(jsonObject);
    }
}