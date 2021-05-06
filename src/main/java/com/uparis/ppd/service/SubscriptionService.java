package com.uparis.ppd.service;

import com.uparis.ppd.model.*;
import com.uparis.ppd.properties.ConstantProperties;
import com.uparis.ppd.repository.SubscriptionRepository;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

@Service
public class SubscriptionService {

    @Autowired
    private ConstantProperties constantProperties;

    @Qualifier("getJavaMailSender")
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private TransactionService transactionService;

    public Subscription create(long start, long end, long delay, boolean delayed, boolean notified, Member member, Status status, Association association, Set<Transaction> transactions) {
        Subscription subscription = new Subscription(start, end, delay, delayed, notified, member, status, association, transactions);
        update(subscription);
        return subscription;
    }

    public void update(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    public List<Subscription> getAll() {
        return subscriptionRepository.findAll();
    }

    public List<Subscription> getSubscriptionsByAssociation(Association association) {
        return subscriptionRepository.findByAssociation(association);
    }

    public Object[] getMembersByAssociation(Subscription subscription) {
        List<Subscription> subscriptions = getSubscriptionsByAssociation(subscription.getAssociation());
        List<Member> members = new ArrayList<>();
        for (Subscription sub : subscriptions) {
            members.add(sub.getMember());
        }
        return members.toArray();
    }

    public Object[] getTransactions(Subscription subscription) {
        return subscription.getTransactions().toArray();
    }

    public Subscription getSubscription(Member member, Association association) {
        return subscriptionRepository.findByMemberAndAssociation(member, association);
    }

    public boolean getStatusSuperAdmin(Subscription subscription) {
        return subscription.getStatus().isSuperAdmin();
    }

    public boolean getStatusAdmin(Subscription subscription) {
        return subscription.getStatus().isAdmin();
    }

    public void notifyWelcome(Subscription subscription, Member admin, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(constantProperties.getOurassoEmail());
        message.setTo(subscription.getMember().getEmail());
        message.setSubject("Ourasso : Bienvenue chez Ourasso !");
        if (admin != null) {
            message.setText(
                    "Bonjour " + subscription.getMember().getFirstName() + " " + subscription.getMember().getLastName() + ", " + "bienvenue chez Ourasso !" + "\n"
                            + "\n"
                            + admin.getFirstName() + " " + admin.getLastName() + " vous a rajouté à l'association " + subscription.getAssociation().getName() + "\n"
                            + "Voici votre mot de passe que nous vous invitons à changer une fois connecté : " + password + "\n"
                            + "Voici l'adresse pour vous connecter : https://ppd-asso.herokuapp.com/login" + "\n"
                            + "\n"
                            + "Cordialement, l'équipe Ourasso.");
        }
        if (subscription.getStatus().isSuperAdmin()) {
            message.setText(
                    "Bonjour " + subscription.getMember().getFirstName() + " " + subscription.getMember().getLastName() + ", " + "bienvenue chez Ourasso !" + "\n"
                            + "\n"
                            + "Vous venez de créer et rejoindre l'association " + subscription.getAssociation().getName() + "\n"
                            + "Voici l'adresse pour vous connecter : https://ppd-asso.herokuapp.com/login" + "\n"
                            + "\n"
                            + "Cordialement, l'équipe Ourasso.");
        } else {
            message.setText(
                    "Bonjour " + subscription.getMember().getFirstName() + " " + subscription.getMember().getLastName() + ", " + "bienvenue chez Ourasso !" + "\n"
                            + "\n"
                            + "Vous venez de rejoindre l'association " + subscription.getAssociation().getName() + "\n"
                            + "Voici l'adresse pour vous connecter : https://ppd-asso.herokuapp.com/login" + "\n"
                            + "\n"
                            + "Cordialement, l'équipe Ourasso.");
        }
        emailSender.send(message);
    }

    public void notifyPaymentSuccessfull(Subscription subscription) {
        Date date = new Date(subscription.getStop());
        DateFormat dateFormat = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.MEDIUM, new Locale("FR", "fr"));
        String shortDate = (dateFormat.format(date)).substring(0, (dateFormat.format(date)).length() - 10);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(constantProperties.getOurassoEmail());
        message.setTo(subscription.getMember().getEmail());
        message.setSubject("Ourasso : Merci de vous être abonné(e) !");
        message.setText(
                "Bonjour " + subscription.getMember().getFirstName() + " " + subscription.getMember().getLastName() + " !" + "\n"
                        + "\n"
                        + "Merci de vous être abonné(e), ce dernier arrivera à expiration le " + shortDate + "\n"
                        + "\n"
                        + "Voici votre numéro d'adhésion : " + String.format("%010d", subscription.getMember().getId()) + "\n");
        emailSender.send(message);
    }

    /*
        @Scheduled(fixedDelay = 5000)
        public void notifyPaymentMissing() {
            List<Subscription> subscriptions = getAll();
            for (Subscription subscription : subscriptions) {
                if (!isValid(subscription) && !subscription.isNotified()) {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(constantProperties.getOurassoEmail());
                    message.setTo(subscription.getMember().getEmail());
                    message.setSubject("Ourasso : Votre abonnement a expiré !");
                    message.setText(
                            "Bonjour " + subscription.getMember().getFirstName() + " " + subscription.getMember().getLastName() + ", " + "bienvenue chez Ourasso !" + "\n"
                                    + "\n"
                                    + "Votre abonnement a expiré, veuillez le renouveler !" + "\n"
                                    + "\n"
                                    + "Voici l'adresse pour vous connecter : " + "\n"
                                    + "https://ppd-asso.herokuapp.com/login");
                    emailSender.send(message);
                    subscription.setNotified(true);
                    update(subscription);
                }
            }
        }
     */
    public boolean subscribe(Subscription subscription, String duration) {
        long time = System.currentTimeMillis();
        Transaction transaction;
        if (subscription.getStatus().isSuperAdmin()) {
            transaction = transactionService.create(time, getPrice(subscription), subscription);
            subscription.setStop(time + (3600 * 1000));
            // subscription.setStop(time + ((31556952L / 12) * 1000));
        } else {
            switch (duration) {
                case "1":
                    transaction = transactionService.create(time, subscription.getAssociation().getPrice1Month(), subscription);
                    subscription.setStop(time + (3600 * 1000));
                    // subscription.setStop(time + ((31556952L / 12) * 1000));
                    break;
                case "3":
                    transaction = transactionService.create(time, subscription.getAssociation().getPrice3Months(), subscription);
                    subscription.setStop(time + (3600 * 1000));
                    // subscription.setStop(time + ((3 * 31556952L / 12) * 1000));
                    break;
                case "12":
                    transaction = transactionService.create(time, subscription.getAssociation().getPrice12Months(), subscription);
                    subscription.setStop(time + (3600 * 1000));
                    // subscription.setStop(time + ((12 * 31556952L / 12) * 1000));
                    break;
                default:
                    return false;
            }
        }
        Set<Transaction> transactions = subscription.getTransactions();
        transactions.add(transaction);
        subscription.setTransactions(transactions);
        subscription.setStart(time);
        subscription.setDelay(0);
        subscription.setDelayed(false);
        subscription.setNotified(false);
        update(subscription);
        notifyPaymentSuccessfull(subscription);
        return true;
    }

    public boolean isValid(Subscription subscription) {
        List<Subscription> subscriptions = getSubscriptionsByAssociation(subscription.getAssociation());
        if (subscription.getStatus().isSuperAdmin() && subscriptions.size() < constantProperties.getOurassoSize1()) {
            return true;
        }
        if (subscription.isDelayed()) {
            return System.currentTimeMillis() <= subscription.getDelay();
        } else if (!subscription.isDelayed()) {
            if (subscription.getStop() == 0) {
                return false;
            } else return System.currentTimeMillis() <= subscription.getStop();
        }
        return true;
    }

    public void skipSubscription(Subscription subscription) {
        long time = System.currentTimeMillis();
        subscription.setDelay(time + (60 * 1000));
        // subscription.setDelay(time + ((31556952L / 365) * 7) * 1000);
        subscription.setDelayed(true);
        subscription.setStart(0);
        subscription.setStop(0);
        subscription.setNotified(false);
        update(subscription);
    }

    public double getPrice(Subscription subscription) {
        List<Subscription> subscriptions = getSubscriptionsByAssociation(subscription.getAssociation());
        double price = 0;
        if (constantProperties.getOurassoSize1() < subscriptions.size() && subscriptions.size() < constantProperties.getOurassoSize2()) {
            price = constantProperties.getOurassoPrice1();
        }
        if (constantProperties.getOurassoSize2() < subscriptions.size() && subscriptions.size() < constantProperties.getOurassoSize3()) {
            price = constantProperties.getOurassoPrice2();
        }
        if (subscriptions.size() > constantProperties.getOurassoSize3()) {
            price = constantProperties.getOurassoPrice3();
        }
        return price;
    }

    public ByteArrayOutputStream exportMembers(Subscription subscription) {
        List<Subscription> subscriptions = getSubscriptionsByAssociation(subscription.getAssociation());
        List<Member> members = new ArrayList<>();
        for (Subscription sub : subscriptions) {
            members.add(sub.getMember());
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Membres assos");
        XSSFRow header = sheet.createRow(0);

        XSSFCell headerCell = header.createCell(0);
        headerCell.setCellValue("Prenom");

        headerCell = header.createCell(1);
        headerCell.setCellValue("Nom");

        headerCell = header.createCell(2);
        headerCell.setCellValue("Sexe");

        headerCell = header.createCell(3);
        headerCell.setCellValue("Date de naissance");

        headerCell = header.createCell(4);
        headerCell.setCellValue("Adresse");

        headerCell = header.createCell(5);
        headerCell.setCellValue("Ville");

        headerCell = header.createCell(6);
        headerCell.setCellValue("Code postal");

        headerCell = header.createCell(7);
        headerCell.setCellValue("Adresse email");

        headerCell = header.createCell(8);
        headerCell.setCellValue("Téléphone");

        headerCell = header.createCell(9);
        headerCell.setCellValue("Admin");

        for (int i = 0; i < members.size(); i++) {
            XSSFRow row = sheet.createRow(i + 1);
            XSSFCell cell = row.createCell(0);
            cell.setCellValue(members.get(i).getFirstName());
            cell = row.createCell(1);
            cell.setCellValue(members.get(i).getLastName());
            cell = row.createCell(2);
            cell.setCellValue(members.get(i).getSex());
            cell = row.createCell(3);
            cell.setCellValue(members.get(i).getBirthDate());
            cell = row.createCell(4);
            cell.setCellValue(members.get(i).getAddress());
            cell = row.createCell(5);
            cell.setCellValue(members.get(i).getCity());
            cell = row.createCell(6);
            cell.setCellValue(members.get(i).getPostalCode());
            cell = row.createCell(7);
            cell.setCellValue(members.get(i).getEmail());
            cell = row.createCell(8);
            cell.setCellValue(members.get(i).getPhoneNumber());
            cell = row.createCell(9);
            cell.setCellValue(String.valueOf(subscriptions.get(i).getStatus().isAdmin()));
        }
        for (int i = 0; i < 10; i++) {
            sheet.autoSizeColumn(i);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            workbook.write(byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream;
    }
}
