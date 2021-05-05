package com.uparis.ppd.service;

import org.springframework.stereotype.Service;

@Service
public class RegexService {

    private static final String ADDRESS = "^[^<>%$§!?°\"£+\\€`=*:;,()\\[\\]]*$";
    private static final String BIRTH_DATE = "[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}";
    private static final String CREDIT_CARD = "^(?:4[0-9]{12}(?:[0-9]{3})?|(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12})$";
    private static final String CRYPTOGRAM = "[0-9]{3}";
    private static final String EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final String EXPIRATION_DATE = "[0-9]{2}/[0-9]{2}";
    private static final String LEVEL = "^(true|false)$";
    private static final String OURASSO_PRICE = "^[1-4]$";
    private static final String PHONE_NUMBER = "(0|\\+33)[1-9]( *[0-9]{2}){4}";
    private static final String POSTAL_CODE = "[0-9]{5}";
    private static final String PRICE = "^[0-9 \\,.€]*$";
    private static final String GENDER = "^(homme|femme|autre)$";
    private static final String WORD = "^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$";

    public String getAddress() {
        return ADDRESS;
    }

    public String getBirthDate() {
        return BIRTH_DATE;
    }

    public String getCreditCard() {
        return CREDIT_CARD;
    }

    public String getCryptogram() {
        return CRYPTOGRAM;
    }

    public String getEmail() {
        return EMAIL;
    }

    public String getExpirationDate() {
        return EXPIRATION_DATE;
    }

    public String getLevel() {
        return LEVEL;
    }

    public String getOurassoPrice() {
        return OURASSO_PRICE;
    }

    public String getPhoneNumber() {
        return PHONE_NUMBER;
    }

    public String getPostalCode() {
        return POSTAL_CODE;
    }

    public String getPrice() {
        return PRICE; }

    public String getGender() {
        return GENDER;
    }

    public String getWord() {
        return WORD;
    }
}
