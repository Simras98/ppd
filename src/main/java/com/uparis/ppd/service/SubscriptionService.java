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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SubscriptionService {

    @Autowired
    private ConstantProperties constantProperties;

    @Qualifier("getJavaMailSender")
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private FormatService formatService;

    public Subscription create(long join, long start, long end, long delay, boolean delayed, boolean notified, Member member, Status status, Association association, Set<Transaction> transactions) {
        Subscription subscription = new Subscription(join, start, end, delay, delayed, notified, member, status, association, transactions);
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

    public List<Member> getMembersByAssociation(Subscription subscription) {
        List<Subscription> subscriptions = getSubscriptionsByAssociation(subscription.getAssociation());
        List<Member> members = new ArrayList<>();
        for (Subscription sub : subscriptions) {
            members.add(sub.getMember());
        }
        return members;
    }

    public List<Member> getMembersByAssociationInLastMonth(Subscription subscription) {
        List<Subscription> subscriptions = getSubscriptionsByAssociation(subscription.getAssociation());
        List<Member> members = new ArrayList<>();
        Calendar date = Calendar.getInstance();

        Calendar nowDate = Calendar.getInstance();
        nowDate.add(Calendar.DAY_OF_MONTH, -1);
        nowDate.set(Calendar.DAY_OF_MONTH, 1);
        for (Subscription sub : subscriptions) {
            date.setTimeInMillis(subscription.getArrived());
            if (date.after(nowDate)) {
                members.add(sub.getMember());
            }
        }
        return members;
    }

    public List<Status> getStatusByAssociation(Subscription subscription) {
        List<Subscription> subscriptions = getSubscriptionsByAssociation(subscription.getAssociation());
        List<Status> status = new ArrayList<>();
        for (Subscription sub : subscriptions) {
            status.add(sub.getStatus());
        }
        return status;
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
    
    public String convertLongToDateString(Subscription subscription) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        if (subscription.isDelayed()) {
            calendar.setTimeInMillis(subscription.getDelay());
        } else {
            calendar.setTimeInMillis(subscription.getStop());
        }
        return formatter.format(calendar.getTime());
    }

    public void notifyWelcome(Subscription subscription, Member admin, String password) {
        String customMessage;
        String textTemp;
        if (admin != null) {
            customMessage =
                    "<p style=\"text-align: center;\">Bonjour " + subscription.getMember().getFirstName() + " "
                            + subscription.getMember().getLastName() + ", " + "bienvenue chez Ourasso ! <br>"
                            + admin.getFirstName() + " " + admin.getLastName() + " vous a rajouté à l'association "
                            + subscription.getAssociation().getName() + ".<br>"
                            + "Voici votre mot de passe que nous vous invitons à changer une fois connecté : "
                            + password + "<br>"
                            + "<a href=\" https://ourasso.herokuapp.com/login\">Veuillez cliquer ici pour vous connecter</a>"
                            + " <br><br>Cordialement, L’équipe Ourasso.</p>";
            textTemp = formatService.mailTemplateGenerator(subscription.getMember().getFirstName(), customMessage, "OurAsso");
        } else if (subscription.getStatus().isSuperAdmin()) {
            customMessage = "<p style=\"text-align: center;\">Bonjour " + subscription.getMember().getFirstName()
                    + " " + subscription.getMember().getLastName() + ", " + "bienvenue chez Ourasso !" + "<br>"
                    + "Vous venez de créer et rejoindre l'association " + subscription.getAssociation().getName() + "."
                    + "<br>"
                    + "<a href=\" https://ourasso.herokuapp.com/login\">Veuillez cliquer ici pour vous connecter</a>"
                    + "<br>"
                    + "<br>" + "Cordialement, l'équipe Ourasso.</p>";
            textTemp = formatService.mailTemplateGenerator(subscription.getMember().getFirstName(), customMessage, "OurAsso");
        } else {
            customMessage = "<p style=\"text-align: center;\">Bonjour " + subscription.getMember().getFirstName()
                    + " " + subscription.getMember().getLastName() + ", " + "bienvenue chez Ourasso !" + "\n" + "<br>"
                    + "Vous venez de rejoindre l'association " + subscription.getAssociation().getName() + ". <br>"
                    + "<a href=\" https://ourasso.herokuapp.com/login\">Veuillez cliquer ici pour vous connecter</a>"
                    + "<br>"
                    + "<br>" + "Cordialement, l'équipe Ourasso.</p>";
            textTemp = formatService.mailTemplateGenerator(subscription.getMember().getFirstName(), customMessage, "OurAsso");
        }
        final String messageText = textTemp;
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(subscription.getMember().getEmail()));
            mimeMessage.setFrom(new InternetAddress(constantProperties.getOurassoEmail()));
            mimeMessage.setSubject("Ourasso : Bienvenue chez Ourasso !");
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setText(messageText, true);
        };
        javaMailSender.send(preparator);

    }

    public void notifyPaymentSuccessfull(Subscription subscription) {
        Date date = new Date(subscription.getStop());
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
                new Locale("FR", "fr"));
        String shortDate = (dateFormat.format(date)).substring(0, (dateFormat.format(date)).length() - 10).trim();
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(subscription.getMember().getEmail()));
            mimeMessage.setFrom(new InternetAddress(constantProperties.getOurassoEmail()));
            mimeMessage.setSubject("Ourasso : Merci de vous être abonné(e) !");
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            String customMessage =
                    "<p style=\"text-align: center;\">Merci de vous être abonné(e), ce dernier arrivera à expiration le "
                            + shortDate + "." + "<br>Voici votre numéro d'adhésion : <br></p>"
                            + "<div style=\" padding: 30px;\"><div style=\" border-bottom-left-radius: 15px;\n"
                            + "  border-bottom-right-radius: 15px;\n" + "  border-top-right-radius: 15px;\n"
                            + "  border-top-left-radius: 15px;margin: 0 auto;\n"
                            + "     width: 150px; \n" + "  box-sizing: border-box;\n"
                            + "  background: linear-gradient(135deg,#71b7e6,#9b59b6);\n" + "  font-weight : bold;\n" + "  font-size : 15px;\n"
                            + "  color: white;text-align:center;padding:15px;\">\n" + "<p>"
                            + String.format("%010d", subscription.getMember().getId())  + "</div></div>"
                            + "<br>" 
                            + "<br><p style=\"text-align: center;\">Cordialement, L’équipe Ourasso.</p>";
            String messageText = formatService.mailTemplateGenerator(subscription.getMember().getFirstName(), customMessage, "OurAsso");
            helper.setText(messageText, true);
        };
        javaMailSender.send(preparator);
    }

    @Scheduled(fixedDelay = 5000)
    public void notifyPaymentMissing() {
        List<Subscription> subscriptions = getAll();
        for (Subscription subscription : subscriptions) {
            if (!isValid(subscription) && !subscription.isNotified()) {
                MimeMessagePreparator preparator = mimeMessage -> {
                    mimeMessage.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(subscription.getMember().getEmail()));
                    mimeMessage.setFrom(new InternetAddress(constantProperties.getOurassoEmail()));
                    mimeMessage.setSubject("Ourasso : Votre abonnement a expiré !");
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    String customMessage =
                            "<p style=\"text-align: center;\">Bonjour " + subscription.getMember().getFirstName() + " "
                                    + subscription.getMember().getLastName() + ", " + "bienvenue chez Ourasso !" + "\n" + "<br>"
                                    + "Votre abonnement a expiré, veuillez le renouveler !" + "\n" + "<br>"
                                    + "<a href=\" https://ourasso.herokuapp.com/login\">Veuillez cliquer ici pour vous connecter</a>"
                                    + " <br><br>Cordialement, L’équipe Ourasso.</p>";
                    String messageText = formatService.mailTemplateGenerator(subscription.getMember().getFirstName(), customMessage, "OurAsso");
                    helper.setText(messageText, true);
                };
                javaMailSender.send(preparator);
                subscription.setNotified(true);
                update(subscription);
            }
        }
    }

    public boolean subscribe(Subscription subscription, String duration) {
        long time = System.currentTimeMillis();
        Transaction transaction;
        if (subscription.getStatus().isSuperAdmin()) {
            transaction = transactionService.create(time, getPrice(subscription), subscription);
            subscription.setStop(time + ((31556952L / 12) * 1000));
        } else {
            switch (duration) {
                case "1":
                    transaction = transactionService.create(time, subscription.getAssociation().getPrice1Month(), subscription);
                    subscription.setStop(time + ((31556952L / 12) * 1000));
                    break;
                case "3":
                    transaction = transactionService.create(time, subscription.getAssociation().getPrice3Months(), subscription);
                    subscription.setStop(time + ((3 * 31556952L / 12) * 1000));
                    break;
                case "12":
                    transaction = transactionService.create(time, subscription.getAssociation().getPrice12Months(), subscription);
                    subscription.setStop(time + ((12 * 31556952L / 12) * 1000));
                    break;
                default:
                    return false;
            }
        }
        Set<Transaction> transactions = subscription.getTransactions();

        Set<Transaction> newTransactions = new HashSet<>();
        newTransactions.addAll(transactions);
        newTransactions.add(transaction);

        subscription.setTransactions(newTransactions);
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
            subscription.setStop(System.currentTimeMillis());
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
        subscription.setDelay(time + ((31556952L / 365) * 7) * 1000);
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

    public void sendEmailToAll(String object, String body, Subscription subscription) {
        List<Member> members = getMembersByAssociation(subscription);
        for (Member member : members) {
            sendEmail(object, body, member, subscription);
        }
    }

    public void sendEmail(String object, String body, Member member, Subscription subscription) {
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(member.getEmail()));
            mimeMessage.setFrom(new InternetAddress(constantProperties.getOurassoEmail()));
            mimeMessage.setSubject(object);
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            String customMessage =
                    "<p style=\"text-align: center;\">Bonjour " + member.getFirstName() + " " + member.getLastName() + ", " +
                            subscription.getMember().getFirstName() + " " + subscription.getMember().getLastName() + " vous a envoyé un message :" + "<br>"
                            + "\n"
                            + body + "\n"
                            + "\n"
                            + "<br>"
                            + "<br>Cordialement, l'équipe Ourasso.<br></p>";
            String messageText = formatService.mailTemplateGenerator(member.getFirstName(), customMessage,
                    "OurAsso");
            helper.setText(messageText, true);
        };
        javaMailSender.send(preparator);
    }
}
