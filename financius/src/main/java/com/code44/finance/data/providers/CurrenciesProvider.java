package com.code44.finance.data.providers;

import android.net.Uri;

import com.code44.finance.data.db.Tables;
import com.code44.finance.utils.MoneyFormatter;

public class CurrenciesProvider extends BaseModelProvider {
    public static Uri uriCurrencies() {
        return uriModels(CurrenciesProvider.class, Tables.Currencies.TABLE_NAME);
    }

    public static Uri uriCurrency(long currencyId) {
        return uriModel(CurrenciesProvider.class, Tables.Currencies.TABLE_NAME, currencyId);
    }

    @Override
    protected String getModelTable() {
        return Tables.Currencies.TABLE_NAME;
    }

    @Override
    protected String getQueryTables() {
        return getModelTable();
    }

    @Override
    protected void onAfterBulkInsert() {
        super.onAfterBulkInsert();
        MoneyFormatter.invalidateCache();
    }

    @Override
    protected void notifyOtherProviders() {
        super.notifyOtherProviders();

        ProviderUtils.notifyChangeIfNecessary(getContext(), AccountsProvider.uriAccounts());
        ProviderUtils.notifyChangeIfNecessary(getContext(), TransactionsProvider.uriTransactions());
    }
}
