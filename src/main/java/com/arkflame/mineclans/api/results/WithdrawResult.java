package com.arkflame.mineclans.api.results;

public class WithdrawResult {
    private final WithdrawResultType resultType;
    private final double amountWithdrawn;

    public WithdrawResult(WithdrawResultType resultType, double amountWithdrawn) {
        this.resultType = resultType;
        this.amountWithdrawn = amountWithdrawn;
    }

    public WithdrawResultType getResultType() {
        return resultType;
    }

    public double getAmountWithdrawn() {
        return amountWithdrawn;
    }

    public enum WithdrawResultType {
        SUCCESS,
        NOT_IN_FACTION,
        NO_PERMISSION,
        INSUFFICIENT_FUNDS,
        ERROR,
        INVALID_AMOUNT,
        NO_VAULT, NO_ECONOMY
    }
}
