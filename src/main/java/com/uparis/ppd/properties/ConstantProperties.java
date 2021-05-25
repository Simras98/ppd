package com.uparis.ppd.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("constant")
public class ConstantProperties {

    // Controller constants
    private static final String CONTROLLER_MANAGEMEMBERS = "managemembers";
    private static final String CONTROLLER_MANAGEPAYMENTS = "managepayments";
    private static final String CONTROLLER_BILLINGMEMBER = "billingmember";
    private static final String CONTROLLER_BILLINGSUPERADMIN = "billingsuperadmin";
    private static final String CONTROLLER_CONTACT = "contact";
    private static final String CONTROLLER_INDEX = "index";
    private static final String CONTROLLER_JOIN = "join";
    private static final String CONTROLLER_SIGNUP = "signup";
    private static final String CONTROLLER_LOGIN = "login";
    private static final String CONTROLLER_PASSWORDFORGOTTEN = "passwordforgotten";
    private static final String CONTROLLER_PROFILE = "profile";
    private static final String CONTROLLER_SIGNUPMEMBER = "signupmember";
    private static final String CONTROLLER_SIGNUPSUPERADMIN = "signupsuperadmin";

    // Hcaptcha constant
    private static final String HCAPTCHA_SECRETKEY = "0xfECB063122695C2bEa912c2Ba79b6ccC5e914fab";

    // Attribute constants
    private static final String ATTRIBUTE_NAME_ERROR = "error";
    private static final String ATTRIBUTE_NAME_MEMBERS = "members";
    private static final String ATTRIBUTE_NAME_PRICE = "price";
    private static final String ATTRIBUTE_NAME_SUBSCRIPTION = "subscription";
    private static final String ATTRIBUTE_NAME_STATUS = "status";
    private static final String ATTRIBUTE_NAME_SUCCESS = "success";
    private static final String ATTRIBUTE_NAME_TRANSACTIONS = "transactions";

    // Ourrasso prices constants
    private static final double OURASSO_PRICE1 = 9.99;
    private static final double OURASSO_PRICE2 = 19.99;
    private static final double OURASSO_PRICE3 = 49.99;

    // Ourrasso size constants
    private static final int OURASSO_SIZE1 = 30;
    private static final int OURASSO_SIZE2 = 200;
    private static final int OURASSO_SIZE3 = 500;

    // Ourrasso email constant
    private static final String OURASSO_EMAIL = "contact.ourasso@gmail.com";

    // Ourasso error constants
    private static final String ATTRIBUTE_DESC_ADDRESS = "Vous devez rentrer une adresse valide !";
    private static final String ATTRIBUTE_DESC_ASSOCIATIONNAME = "Vous devez rentrer un nom d'association valide !";
    private static final String ATTRIBUTE_DESC_ASSOCIATIONEXIST = "Le nom d'association est déjà utilisé !";
    private static final String ATTRIBUTE_DESC_ASSOCIATIONNOTEXIST = "L'association n'existe pas !";
    private static final String ATTRIBUTE_DESC_ASSOCIATIONDESCRIPTION = "Vous devez rentrer une description d'association valide !";
    private static final String ATTRIBUTE_DESC_BIRTHDATE = "Vous devez rentrer une date de naissance valide !";
    private static final String ATTRIBUTE_DESC_CHOOSEFILE = "Vous devez choisir un fichier avant de valider !";
    private static final String ATTRIBUTE_DESC_CITY = "Vous devez rentrer une ville valide !";
    private static final String ATTRIBUTE_DESC_CREDITCARD = "Vous devez rentrer un numéro de carte bancaire valide !";
    private static final String ATTRIBUTE_DESC_CRYPTOGRAM = "Vous devez rentrer un cryptogramme valide !";
    private static final String ATTRIBUTE_DESC_EMAIL = "Vous devez rentrer une adresse mail valide !";
    private static final String ATTRIBUTE_DESC_EMAILEXIST = "L'adresse email est déjà utilisée !";
    private static final String ATTRIBUTE_DESC_EMAILNOTFOUND = "L'adresse email n'a pas été trouvée !";
    private static final String ATTRIBUTE_DESC_EMAILSEND = "Un email a été envoyé !";
    private static final String ATTRIBUTE_DESC_ERROR = "Un problème a eu lieu !";
    private static final String ATTRIBUTE_DESC_EXPIRATIONDATE = "Vous devez rentrer une date d'expiration valide !";
    private static final String ATTRIBUTE_DESC_EXPIRATIONDATEEXPIRED = "Votre carte bancaire a exprirée !";
    private static final String ATTRIBUTE_DESC_FIRSTNAME = "Vous devez rentrer un prénom valide !";
    private static final String ATTRIBUTE_DESC_FULFILLFIELDS = "Vous devez remplir les champs pour valider !";
    private static final String ATTRIBUTE_DESC_LASTNAME = "Vous devez rentrer un nom valide !";
    private static final String ATTRIBUTE_DESC_LOGINFAILED = "L'adresse email et le mot de passe ne correspondent pas !";
    private static final String ATTRIBUTE_DESC_LOGOUT = "Vous n'êtes pas connecté !";
    private static final String ATTRIBUTE_DESC_MEMBERADDED ="Le membre a bien été ajouté !";
    private static final String ATTRIBUTE_DESC_MEMBERNOTEXIST ="Le membre n'existe pas !";
    private static final String ATTRIBUTE_DESC_MEMBERSADDED ="Les membres ont bien été ajoutés !";
    private static final String ATTRIBUTE_DESC_NAME = "Vous devez rentrer un nom valide !";
    private static final String ATTRIBUTE_DESC_NOTSUBSCRIBED = "Vous n'êtes pas abonné à cette association !";
    private static final String ATTRIBUTE_DESC_PASSWORD = "Les mots de passe ne correspondent pas !";
    private static final String ATTRIBUTE_DESC_PAYMENTFAILED = "Paiment invalide. Veuillez-contacter les administrateurs !";
    private static final String ATTRIBUTE_DESC_PHONENUMBER = "Vous devez rentrer un numéro de téléphone valide !";
    private static final String ATTRIBUTE_DESC_PASSWORDCHANGED = "Mot de passe modifié avec succès !";
    private static final String ATTRIBUTE_DESC_POSTALCODE = "Vous devez rentrer un code postal valide !";
    private static final String ATTRIBUTE_DESC_PRICE = "Vous devez rentrer une description d'association valide !";
    private static final String ATTRIBUTE_DESC_SEX = "Vous devez rentrer un sexe valide !";
    private static final String ATTRIBUTE_DESC_STATUS = "Vous devez choisir un statut valide !";
    private static final String ATTRIBUTE_DESC_SUBSCRIBED = "Vous êtes déjà abonné à cette association !";
    private static final String ATTRIBUTE_DESC_SUBSCRIPTIONEXPIRED = "Votre abonnement a expiré. Veuillez renouveler votre abonnement !";


    public String getControllerManageMembers() {
        return CONTROLLER_MANAGEMEMBERS;
    }

    public String getControllerManagePayments() {
        return CONTROLLER_MANAGEPAYMENTS;
    }

    public String getControllerBillingMember() {
        return CONTROLLER_BILLINGMEMBER;
    }

    public String getControllerBillingSuperAdmin() {
        return CONTROLLER_BILLINGSUPERADMIN;
    }

    public String getControllerContact() {
        return CONTROLLER_CONTACT;
    }

    public String getControllerIndex() {
        return CONTROLLER_INDEX;
    }

    public String getControllerJoin() {
        return CONTROLLER_JOIN;
    }

    public String getControllerSignup() {
        return CONTROLLER_SIGNUP;
    }

    public String getControllerLogin() {
        return CONTROLLER_LOGIN;
    }

    public String getControllerPasswordForgotten() {
        return CONTROLLER_PASSWORDFORGOTTEN;
    }

    public String getControllerProfile() {
        return CONTROLLER_PROFILE;
    }

    public String getControllerSignupMember() {
        return CONTROLLER_SIGNUPMEMBER;
    }

    public String getControllerSignupSuperAdmin() {
        return CONTROLLER_SIGNUPSUPERADMIN;
    }

    public String getHcaptchaSecretKey() {
        return HCAPTCHA_SECRETKEY;
    }

    public String getAttributeNameError() {
        return ATTRIBUTE_NAME_ERROR;
    }

    public String getAttributeNameMembers() {
        return ATTRIBUTE_NAME_MEMBERS;
    }

    public String getAttributeNamePrice() {
        return ATTRIBUTE_NAME_PRICE;
    }

    public String getAttributeNameSubscription() {
        return ATTRIBUTE_NAME_SUBSCRIPTION;
    }

    public String getAttributeNameStatus() {
        return ATTRIBUTE_NAME_STATUS;
    }

    public String getAttributeNameSuccess() {
        return ATTRIBUTE_NAME_SUCCESS;
    }

    public String getAttributeNameTransactions() {
        return ATTRIBUTE_NAME_TRANSACTIONS;
    }

    public double getOurassoPrice1() {
        return OURASSO_PRICE1;
    }

    public double getOurassoPrice2() {
        return OURASSO_PRICE2;
    }

    public double getOurassoPrice3() {
        return OURASSO_PRICE3;
    }

    public int getOurassoSize1() {
        return OURASSO_SIZE1;
    }

    public int getOurassoSize2() {
        return OURASSO_SIZE2;
    }

    public int getOurassoSize3() {
        return OURASSO_SIZE3;
    }

    public String getOurassoEmail() {
        return OURASSO_EMAIL;
    }

    public String getAttributeDescAddress() {
        return ATTRIBUTE_DESC_ADDRESS;
    }

    public String getAttributeDescAssociationName() {
        return ATTRIBUTE_DESC_ASSOCIATIONNAME;
    }

    public String getAttributeDescAssociationExist() {
        return ATTRIBUTE_DESC_ASSOCIATIONEXIST;
    }

    public String getAttributeDescAssociationNotExist() {
        return ATTRIBUTE_DESC_ASSOCIATIONNOTEXIST;
    }

    public String getAttributeDescAssociationDescription() {
        return ATTRIBUTE_DESC_ASSOCIATIONDESCRIPTION;
    }

    public String getAttributeDescBirthdate() {
        return ATTRIBUTE_DESC_BIRTHDATE;
    }

    public String getAttributeDescChooseFile() {
        return ATTRIBUTE_DESC_CHOOSEFILE;
    }

    public String getAttributeDescCity() {
        return ATTRIBUTE_DESC_CITY;
    }

    public String getAttributeDescCreditCard() {
        return ATTRIBUTE_DESC_CREDITCARD;
    }

    public String getAttributeDescCryptogram() {
        return ATTRIBUTE_DESC_CRYPTOGRAM;
    }

    public String getAttributeDescEmail() {
        return ATTRIBUTE_DESC_EMAIL;
    }

    public String getAttributeDescEmailExist() {
        return ATTRIBUTE_DESC_EMAILEXIST;
    }

    public String getAttributeDescEmailNotFound() {
        return ATTRIBUTE_DESC_EMAILNOTFOUND;
    }

    public String getAttributeDescEmailSend() {
        return ATTRIBUTE_DESC_EMAILSEND;
    }

    public String getAttributeDescError() {
        return ATTRIBUTE_DESC_ERROR;
    }

    public String getAttributeDescExpirationDate() {
        return ATTRIBUTE_DESC_EXPIRATIONDATE;
    }

    public String getAttributeDescExpirationDateExpired() {
        return ATTRIBUTE_DESC_EXPIRATIONDATEEXPIRED;
    }

    public String getAttributeDescFirstname() {
        return ATTRIBUTE_DESC_FIRSTNAME;
    }

    public String getAttributeDescFulfillFields() {
        return ATTRIBUTE_DESC_FULFILLFIELDS;
    }

    public String getAttributeDescLastname() {
        return ATTRIBUTE_DESC_LASTNAME;
    }

    public String getAttributeDescLoginFailed() {
        return ATTRIBUTE_DESC_LOGINFAILED;
    }

    public String getAttributeDescLogout() {
        return ATTRIBUTE_DESC_LOGOUT;
    }

    public String getAttributeDescMemberAdded() {
        return ATTRIBUTE_DESC_MEMBERADDED;
    }

    public String getAttributeDescMemberNotExist() {
        return ATTRIBUTE_DESC_MEMBERNOTEXIST;
    }

    public String getAttributeDescMembersAdded() {
        return ATTRIBUTE_DESC_MEMBERSADDED;
    }

    public String getAttributeDescName() {
        return ATTRIBUTE_DESC_NAME;
    }

    public String getAttributeDescNotSubscribed() {
        return ATTRIBUTE_DESC_NOTSUBSCRIBED;
    }

    public String getAttributeDescPassword() {
        return ATTRIBUTE_DESC_PASSWORD;
    }

    public String getAttributeDescPaymentFailed() {
        return ATTRIBUTE_DESC_PAYMENTFAILED;
    }

    public String getAttributeDescPhoneNumber() {
        return ATTRIBUTE_DESC_PHONENUMBER;
    }

    public String getAttributeDescPasswordChanged() {
        return ATTRIBUTE_DESC_PASSWORDCHANGED;
    }

    public String getAttributeDescPostalCode() {
        return ATTRIBUTE_DESC_POSTALCODE;
    }

    public String getAttributeDescPrice() {
        return ATTRIBUTE_DESC_PRICE;
    }

    public String getAttributeDescSex() {
        return ATTRIBUTE_DESC_SEX;
    }

    public String getAttributeDescStatus() {
        return ATTRIBUTE_DESC_STATUS;
    }

    public String getAttributeDescSubscribed() {
        return ATTRIBUTE_DESC_SUBSCRIBED;
    }

    public String getAttributeDescSubscriptionExpired() {
        return ATTRIBUTE_DESC_SUBSCRIPTIONEXPIRED;
    }
}
