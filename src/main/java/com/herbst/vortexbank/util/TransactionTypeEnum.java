package com.herbst.vortexbank.util;

public enum TransactionTypeEnum {
    PAYMENT("Payment"),
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    TRANSFER("Transfer"),
    CHARGE("Charge"),
    REFUND("Refund");
    private String value;

    TransactionTypeEnum(String value) {
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public static TransactionTypeEnum fromValue(String value){
        for (TransactionTypeEnum transactionType : TransactionTypeEnum.values()){
            if(value.equalsIgnoreCase(transactionType.getValue())){
                return transactionType;
            }
        }
        throw new IllegalArgumentException("Unknown value Enum: " + value);
    }
}
