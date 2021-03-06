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
    
    public String mailTemplateGenerator(String to, String message, String from) {
        return "\r\n"
          + "  <body link=\"#00a5b5\" vlink=\"#00a5b5\" alink=\"#00a5b5\" style=background-image:linear-gradient(135deg,#71b7e6,#9b59b6)>\r\n"
          + "\r\n"
          + "<style>\r\n"
          + "@media only screen and (max-width: 600px) {\r\n"
          + "    .main {\r\n"
          + "      width: 320px !important;\r\n"
          + "    }\r\n"
          + "\r\n"
          + "\r\n"
          + "    .top-image {\r\n"
          + "      width: 100% !important;\r\n"
          + "    }\r\n"
          + "    .inside-footer {\r\n"
          + "      width: 320px !important;\r\n"
          + "    }\r\n"
          + "    table[class=\"contenttable\"] {\r\n"
          + "            width: 320px !important;\r\n"
          + "            text-align: left !important;\r\n"
          + "        }\r\n"
          + "        td[class=\"force-col\"] {\r\n"
          + "          display: block !important;\r\n"
          + "      }\r\n"
          + "       td[class=\"rm-col\"] {\r\n"
          + "          display: none !important;\r\n"
          + "      }\r\n"
          + "    .mt {\r\n"
          + "      margin-top: 15px !important;\r\n"
          + "    }\r\n"
          + "    *[class].width300 {width: 255px !important;}\r\n"
          + "    *[class].block {display:block !important;}\r\n"
          + "    *[class].blockcol {display:none !important;}\r\n"
          + "    .emailButton{\r\n"
          + "            width: 100% !important;\r\n"
          + "        }\r\n"
          + "\r\n"
          + "        .emailButton a {\r\n"
          + "            display:block !important;\r\n"
          + "            font-size:18px !important;\r\n"
          + "        }\r\n"
          + "\r\n"
          + "  }\r\n"
          + "</style>\r\n"
          + "<table class=\" main contenttable\" align=\"center\" style=\"font-weight: normal;border-collapse: collapse;border: 0;margin-left: auto;margin-right: auto;padding: 0;font-family: Arial, sans-serif;color: #555559;background-color: white;font-size: 16px;line-height: 26px;width: auto;\">\r\n"
          + "    <tr>\r\n"
          + "        <table style=\"font-weight: normal;border-collapse: collapse;border: 0;margin: 0;padding: 0;font-family: Arial, sans-serif;\">\r\n"
          + "          <tr>\r"
          + "            <td colspan=\"4\" valign=\"top\" class=\"image-section\" style=\"border-collapse: collapse;border: 0;display: block;width: auto;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;background-color: #fff;border-bottom: 4px solid #af52cc\">\r\n"
          + "              <a href=\"https://ourasso.herokuapp.com\"><img class=\"top-image\" src=\"https://i.ibb.co/SwJ6FzK/output-onlinepngtools.png\" style=\"line-height: 1;width: 100px; display: block;\r\n"
          + "    margin-left: auto;\r\n"
          + "    margin-right: auto\" alt=\"OurAsso\"></a>\r\n"
          + "            <br></td>\r"
          + "          </tr>\r\n"
          + "          <tr>\r\n"
          + "            <td valign=\"top\" class=\"side title\" align=\"center\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 20px;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;vertical-align: top;background-color: white;border-top: none;\">\r\n"
          + "              <table style=\"font-weight: normal;border-collapse: collapse;border: 0;margin: 0;padding: 0;font-family: Arial, sans-serif;\">\r\n"
          + "                <tr>\r\n"
          + "                  <td class=\"head-title\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 28px;line-height: 34px;font-weight: bold; text-align: center;\">\r\n"
          + "                    <div class=\"mktEditable\" id=\"main_title\">\r\n"
          + "                      Bonjour "+ to +" ! \r\n"
          + "                    </div>\r\n"
          + "                  </td>\r\n"
          + "                </tr>\r\n"
          + "                <tr>\r\n"
          + "                  <td class=\"sub-title\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;padding-top:5px;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 18px;line-height: 29px;font-weight: bold;text-align: center;\">\r\n"
          + "                  <div class=\"mktEditable\" id=\"intro_title\">\r\n"
          + "                    Message de "+ from + " : \r\n"
          + "                  </div></td>\r\n"
          + "                </tr>\r\n"
          + "                <tr>\r\n"
          + "                  <td class=\"top-padding\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 5px;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;\"></td>\r\n"
          + "                </tr>\r\n"
          + "                <tr>\r\n"
          + "                  <td class=\"grey-block\" style=\"border-collapse: collapse;border: 0;margin: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;background-color: #fff; text-align:center;\">\r\n"
          + "                  </td>\r\n"
          + "                </tr>\r\n"
          + "                <tr>\r\n"
          + "                  <td class=\"top-padding\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 15px 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 21px;\">\r\n"
          + "                    <hr size=\"1\" color=\"#eeeff0\">\r\n"
          + "                  </td>\r\n"
          + "                </tr>\r\n"
          + "                <tr>\r\n"
          + "                  <td class=\"text\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;\">\r\n"
          + "                  <div class=\"mktEditable\" id=\"main_text\">\r\n"
          + "                    " + message + "\r\n"
          + "                  </div>\r\n"
          + "                  </td>\r\n"
          + "                </tr>\r\n"
          + "                <tr>\r\n"
          + "                  <td style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 24px;\">\r\n"
          + "                   &nbsp;<br>\r\n"
          + "                  </td>\r\n"
          + "                </tr>\r\n"
          
          + "              </table>\r\n"
          + "            </td>\r\n"
          + "          </tr>\r\n"
          + "          <tr>\r\n"
          + "            <td style=\"padding:20px; font-family: Arial, sans-serif; -webkit-text-size-adjust: none;\" align=\"center\">\r\n"
          + "              <table>\r\n"
          + "                <tr>\r\n"
          + "                  <td align=\"center\" style=\"font-family: Arial, sans-serif; -webkit-text-size-adjust: none; font-size: 16px;\">\r\n"
      
          + "                    <br/><span style=\"font-size:10px; font-family: Arial, sans-serif; -webkit-text-size-adjust: none;\" >\r\n"
          + "Veuillez ne transmettre cet e-mail qu'aux collègues ou contacts qui seront intéressés à recevoir cet e-mail.</span>\r\n"
          + "                  </td>\r\n"
          + "                </tr>\r\n"
          + "              </table>\r\n"
          + "            </td>\r\n"
          + "          </tr>\r\n"
          + "          <tr>\r\n"
          + "            <td style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 24px; padding: 20px;\">\r\n"
          + "            <div class=\"mktEditable\" id=\"cta_try\">\r\n"
          + "              <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"mobile\" style=\"font-weight: normal;border-collapse: collapse;border: 0;margin: 0;padding: 0;font-family: Arial, sans-serif;\">\r\n"
          + "                  <tr>\r\n"
          + "                    <td class=\"force-col\" valign=\"top\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 24px;\">\r\n"
          + "\r\n"
          + "                          <table class=\"mb mt\" style=\"font-weight: normal;border-collapse: collapse;border: 0;margin: 0;padding: 0;font-family: Arial, sans-serif;margin-bottom: 15px;margin-top: 0;\">\r\n"
          + "                            <tr>\r\n"
          + "\r\n"
          + "                        </td>\r\n"
          + "                      </tr>\r\n"
          + "                          </table>\r\n"
          + "                      </td>\r\n"
          + "                  </tr>\r\n"
          + "              </table>\r\n"
          + "            </div>\r\n"
          + "            </td>\r\n"
          + "          </tr>\r\n"
          + "          <tr>\r\n"
          + "            <td valign=\"top\" align=\"center\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;\">\r\n"
                 
          + "            </td>\r\n"
          + "          </tr>\r\n"
          + "          <tr bgcolor=\"#fff\" style=\"border-top: 4px solid #af52cc;\">\r\n"
          + "            <td valign=\"top\" class=\"footer\" style=\"border-collapse: collapse;border: 0;margin: 0;padding: 0;-webkit-text-size-adjust: none;width : 1000px;color: #555559;font-family: Arial, sans-serif;font-size: 16px;line-height: 26px;background: #fff;text-align: center;\">\r\n"
                 
          + "                    <b>OurAsso</b><br>\r\n"
          + "                            Projet Université de Paris<br>\r\n"
          + "\r\n"
          + "</div>\r\n"
        
          + "            </td>\r\n"
          + "          </tr>\r\n"
          + "        </table>\r\n"
          + "      </td>\r\n"
          + "    </tr>\r\n"
          + "  </table>\r\n"
          + "  </body>\r\n"
          + "";
      }
}
