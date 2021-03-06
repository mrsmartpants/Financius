package com.code44.finance.api.currencies;

import android.content.ContentValues;
import android.content.Context;

import com.code44.finance.api.Request;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExchangeRatesRequest extends Request {
    private final Context context;
    private final CurrenciesRequestService requestService;
    private final String[] codes;

    public ExchangeRatesRequest(EventBus eventBus, Context context, CurrenciesRequestService requestService, String... codes) {
        super(eventBus);
        Preconditions.notNull(eventBus, "EventBus cannot be empty.");
        this.context = Preconditions.notNull(context, "Context cannot be null.");
        this.requestService = Preconditions.notNull(requestService, "Request service cannot be null.");
        this.codes = Preconditions.notNull(codes, "Codes cannot be null.");
    }

    @Override protected void performRequest() throws Exception {
        final StringBuilder sb = new StringBuilder();
        sb.append("select * from yahoo.finance.xchange where pair in (");
        for (String code : codes) {
            sb.append("\"").append(code).append("\",");
        }
        sb.append(")");

        final Map<String, Double> result = requestService.getExchangeRates(sb.toString()).getExchangeRates();


        final List<ContentValues> valuesList = new ArrayList<>();
        for (String fromCode : fromCodes) {
            final Currency currency = getCurrencyWithUpdatedExchangeRate(fromCode);
            if (currency != null) {
                valuesList.add(currency.asValues());
            }
        }

        if (!valuesList.isEmpty()) {
            DataStore.bulkInsert().values(valuesList).into(context, CurrenciesProvider.uriCurrencies());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeRatesRequest)) return false;

        final ExchangeRatesRequest that = (ExchangeRatesRequest) o;

        //noinspection RedundantIfStatement
        if (!Arrays.equals(codes, that.codes)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(codes);
    }
}
