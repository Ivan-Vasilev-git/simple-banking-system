import java.util.NoSuchElementException;
import java.util.Random;

public class Bank {
  private DataBase connector;

  public Bank(String fileName) {
    connector = new DataBase(fileName);
    connector.createNewDatabase(fileName);
    connector.createCardTable();
  }

  public Account createAccount() {
    Account account = new Account();
    connector.saveNewAccount(account);
    return account;
  }

  public Account findAccount(String cardNumber, String pinCode) {
    try {
      return connector.findAccountInBase(cardNumber, pinCode);
    } catch (NoSuchElementException e) {
      throw new NoSuchElementException();
    }
  }

  public int checkBalance(String cardNumber) {
    return connector.checkBalance(cardNumber);
  }

  public boolean isCardNumberCorrect(String cardNumber, String currentCardNumber) {
    if (cardNumber.equals(currentCardNumber)) {
      System.out.println("You can\'t transfer money to the same account!");
      return false;
    }
    if (!isPassLuhnAlgorithm(cardNumberFromString(cardNumber))) {
      System.out.println("Probably you made mistake in the card number. Please try again!");
      return false;
    }
    if (!isCardNumberInDatabase(cardNumber)) {
      System.out.println("Such a card does not exist.");
      return false;
    }
    return true;
  }

  public boolean deleteAccount(String cardNumber, String pinCode) {
    return connector.deleteAccount(cardNumber, pinCode);
  }

  public boolean addIncome(String cardNumber, int income) {
    return connector.addIncome(cardNumber, income);
  }

  private boolean isPassLuhnAlgorithm(int[] cardNumber) {
    if (cardNumber.length != 16) {
      return false;
    }
    int controlSum = Account.generateLastDigitWithLuhnAlg(cardNumber);
    int lastDigit = controlSum % 10;
    if (lastDigit != 0) {
      lastDigit = 10 - lastDigit;
    }
    if (cardNumber[15] != lastDigit) {
      return false;
    }
    return true;
  }

  private int[] cardNumberFromString(String cardNumber) {
    int[] result = new int[cardNumber.length()];
    for (int i = 0; i < cardNumber.length(); i++) {
      result[i] = Integer.parseInt(String.valueOf(cardNumber.charAt(i)));
    }
    return result;
  }

  private boolean isCardNumberInDatabase(String cardNumber) {
    return connector.isCardNumberInDatabase(cardNumber);
  }

  public void doTransfer(int moneyToTransfer, String cardNumber, String currentCardNumber) {
    addIncome(currentCardNumber, -moneyToTransfer);
    addIncome(cardNumber, moneyToTransfer);
  }
}

class Account {

  private int[] cardNumber = new int[16];
  private int[] pinCode = new int[4];
  private double balance = 0;

  {
    Random random = new Random();
    cardNumber[0] = 4;
    for (int i = 6; i < cardNumber.length - 1; i++) {
      cardNumber[i] = random.nextInt(10);
    }
    generateLastDigit();
    generatePinCode();
  }

  public Account() {
  }

  public Account(int[] cardNumber, int[] pinCode, double balance) {
    this.cardNumber = cardNumber;
    this.pinCode = pinCode;
    this.balance = balance;
  }

  private void generateLastDigit() {
    int controlSum = generateLastDigitWithLuhnAlg(cardNumber);
    if (controlSum % 10 != 0) {
      cardNumber[cardNumber.length - 1] = 10 - controlSum % 10;
    }
  }

  public void generatePinCode() {
    Random random = new Random();
    for (int i = 0; i < pinCode.length; i++) {
      pinCode[i] = random.nextInt(10);
    }
  }

  public String getCardNumber() {
    return arrayAsString(cardNumber);
  }

  public String getPinCode() {
    return arrayAsString(pinCode);
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  private String arrayAsString(int[] array) {
    StringBuilder sb = new StringBuilder();
    for (int i : array) {
      sb.append(i);
    }
    return sb.toString();
  }

  public static int generateLastDigitWithLuhnAlg(int[] cardNumber) {
    int controlSum = 0;
    for (int i = 0; i < cardNumber.length - 1; i++) {
      int number = cardNumber[i];
      number = i % 2 == 0 ? number * 2 : number;
      number = number > 9 ? number - 9 : number;
      controlSum += number;
    }
    return controlSum;
  }
}
