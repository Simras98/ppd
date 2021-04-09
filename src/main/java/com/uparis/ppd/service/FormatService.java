package com.uparis.ppd.service;

import org.springframework.stereotype.Service;

@Service
public class FormatService {

    public String formatWord(String word) {
        char[] charWord = word.toCharArray();
        for (int i = 0; i < word.length(); i++) {
            if (i == 0 && charWord[i] != ' ' || charWord[i] != ' ' && charWord[i - 1] == ' ') {
                if (charWord[i] >= 'a' && charWord[i] <= 'z') {
                    charWord[i] = (char) (charWord[i] - 'a' + 'A');
                }
            } else if (charWord[i] >= 'A' && charWord[i] <= 'Z') {
                charWord[i] = (char) (charWord[i] + 'a' - 'A');
            }
        }
        return new String(charWord);
    }

    public double formatPrice(String price) {
        String priceWithoutEuro = price.replace("€", "");
        String priceWithoutEuroAndSpace = priceWithoutEuro.replace(" ", "");
        String priceWithoutEuroAndSpaceAndComma = priceWithoutEuroAndSpace.replace(",", ".");
        return Double.parseDouble(priceWithoutEuroAndSpaceAndComma);
    }
}
