package com.arkflame.mineclans.api.results;

public class DepositResult {
    private final DepositResultType resultType;
    private final double amountDeposited;

    public DepositResult(DepositResultType resultType, double amountDeposited) {
        this.resultType = resultType;
        this.amountDeposited = amountDeposited;
    }

    public DepositResultType getResultType() {
        return resultType;
    }

    public double getAmountDeposited() {
        return amountDeposited;
    }

    public enum DepositResultType {
        SUCCESS,
        NOT_IN_FACTION,
        NO_PERMISSION,
        ERROR, 
        INVALID_AMOUNT,
    }
}
