package com.herbst.vortexbank.util;

public enum NotificationTypeEnum {
    TRANSACTION("Transaction"),
    SECURITY("Security"),
    PRODUCTS("Products"),
    LOAN("Loan"),
    SERVICE("Service");

    private String value;

    NotificationTypeEnum(String value) {
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public static NotificationTypeEnum fromValue(String value){
        for (NotificationTypeEnum notificationType : NotificationTypeEnum.values()){
            if(value.equalsIgnoreCase(notificationType.getValue())){
                return notificationType;
            }
        }
        throw new IllegalArgumentException("Unknown value Enum: " + value);
    }
}
