package com.herbst.vortexbank.util;

public enum TransactionStatusEnum {
    PENDING("Pending"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    PROCESSING("Processing"),
    REFUNDED("Refunded");

    private String value;

    TransactionStatusEnum (String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public static TransactionStatusEnum fromValue(String value){
        for (TransactionStatusEnum transactionStatus : TransactionStatusEnum.values()){
            if(value.equalsIgnoreCase(transactionStatus.getValue())){
                return transactionStatus;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

}
