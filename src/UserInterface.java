import java.util.NoSuchElementException;
import java.util.Scanner;

public class UserInterface {

  private Scanner scanner;
  private Bank bank;
  private Account loggedAccount;

  public UserInterface(String fileName) {
    scanner = new Scanner(System.in);
    bank = new Bank(fileName);
  }

  public void start() {
    while (true) {
      System.out.println(
          "1. Create an account\n" +
              "2. Log into account\n" +
              "0. Exit"
      );
      String action = scanner.nextLine();
      switch (action) {
        case "1":
          createAccount();
          break;
        case "2":
          if (loginData()) {
            System.out.println("\nYou have successfully logged in!");
            if (accountMenu()) {
              return;
            }
          } else {
            System.out.println("\nWrong card number or PIN!\n");
          }
          break;
        case "0":
          System.out.println("\nBye!");
          scanner.close();
          return;
        default:
          System.out.println("Incorrect input");
      }
    }
  }

  private boolean accountMenu() {
    while (true) {
      System.out.println("\n1. Balance\n" +
          "2. Add income\n" +
          "3. Do transfer\n" +
          "4. Close account\n" +
          "5. Log out\n" +
          "0. Exit");
      String action = scanner.nextLine();
      switch (action) {
        case "1":
          System.out.printf("\nBalance: %d\n", checkBalance());
          break;
        case "2":
          if (!addIncome()) {
            System.out.println("\nIncorrect input");
          }
          break;
        case "3":
          doTransfer();
          break;
        case "4":
          closeAccount();
          return false;
        case "5":
          System.out.println("\nYou have successfully logged out!\n");
          return false;
        case "0":
          scanner.close();
          return true;
        default:
          System.out.println("Incorrect input");
      }
    }
  }

  private void createAccount() {
    Account account = bank.createAccount();
    System.out.printf(
        "\nYour card has been created\nYour card number:\n%s\nYour card PIN:\n%s\n\n",
        account.getCardNumber(),
        account.getPinCode());
  }

  private String[] readLoginData() {
    System.out.println("\nEnter your card number:");
    String cardNumber = scanner.nextLine();
    System.out.println("Enter your PIN:");
    String pinCode = scanner.nextLine();
    return new String[]{cardNumber, pinCode};
  }

  private boolean loginData() {
    String[] loginData = readLoginData();
    return checkLoginData(loginData[0], loginData[1]);
  }

  private boolean checkLoginData(String cardNumber, String pinCode) {
    try {
      loggedAccount = bank.findAccount(cardNumber, pinCode);
    } catch (NoSuchElementException e) {
      return false;
    }
    return true;
  }

  private int checkBalance() {
    int balance = bank.checkBalance(loggedAccount.getCardNumber());
    loggedAccount.setBalance(balance);
    return balance;
  }

  private boolean addIncome() {
    System.out.println("\nEnter income:");
    try {
      int income = Integer.parseInt(scanner.nextLine());
      if (income < 0) {
        return false;
      }
      System.out.println("Income was added!");
      return bank.addIncome(loggedAccount.getCardNumber(), income);
    } catch (Exception e) {
      return false;
    }
  }

  private void doTransfer() {
    System.out.println("\nTransfer\n" +
        "Enter your card number:");
    String cardNumber = scanner.nextLine();
    if (bank.isCardNumberCorrect(cardNumber, loggedAccount.getCardNumber())) {
      System.out.println("Enter how much money you want to transfer:");
      int moneyToTransfer = Integer.parseInt(scanner.nextLine());
      if (checkBalance() - moneyToTransfer < 0) {
        System.out.println("Not enough money!");
        return;
      }
      bank.doTransfer(moneyToTransfer, cardNumber, loggedAccount.getCardNumber());
      System.out.println("Success!");
    }
  }

  private boolean closeAccount() {
    boolean result = bank.deleteAccount(loggedAccount.getCardNumber(),
        loggedAccount.getPinCode());
    System.out.println("\nThe account has been closed!\n");
    loggedAccount = null;
    return result;
  }
}

