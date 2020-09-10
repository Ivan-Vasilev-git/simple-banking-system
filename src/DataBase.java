import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.NoSuchElementException;

public class DataBase {
  private String url = "jdbc:sqlite:";
  private SQLiteDataSource source;

  public DataBase() {
    this("card.s3db");
  }

  public DataBase(String fileName) {
    source = new SQLiteDataSource();
    source.setUrl(url + fileName);
  }

  public void createNewDatabase(String fileName) {
    try (Connection conn = DriverManager.getConnection(url + fileName)) {
      if (conn != null) {
        DatabaseMetaData meta = conn.getMetaData();
        System.out.println("The driver name is " + meta.getDriverName());
        System.out.println("A new database has been created.");
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  //Create "card" table if not exists
  public void createCardTable() {
    try (Connection connection = source.getConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("CREATE TABLE IF NOT EXISTS card" +
          "(id INTEGER PRIMARY KEY," +
          "number TEXT," +
          "pin TEXT," +
          "balance INTEGER DEFAULT 0)");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  //Save new Account in data base
  public void saveNewAccount(Account account) {
    String sql = "INSERT INTO card (number, pin) VALUES (?, ?)";
    try (Connection connection = source.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, account.getCardNumber());
      statement.setString(2, account.getPinCode());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public int checkBalance(String cardNumber) {
    String sql = "SELECT balance " +
        "FROM card " +
        "WHERE number = ?";
    int balance = 0;
    try (Connection connection = source.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, cardNumber);
      ResultSet set = statement.executeQuery();
      balance = Integer.parseInt(set.getString(1));
    } catch (SQLException e) {
      throw new NoSuchElementException();
    }
    return balance;
  }

  public Account findAccountInBase(String cardNumber, String pin) {
    String sql = "SELECT number, pin, balance " +
        "FROM card " +
        "WHERE number = ? AND pin = ?";
    try (Connection connection = source.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, cardNumber);
      statement.setString(2, pin);
      ResultSet set = statement.executeQuery();
      if (set != null) {
        int[] cardNum = stringToArray(set.getString(1));
        int[] pinCode = stringToArray(set.getString(2));
        double balance = Double.parseDouble(set.getString(3));
        return new Account(cardNum, pinCode, balance);
      }
    } catch (SQLException e) {
      throw new NoSuchElementException();
    }
    throw new NoSuchElementException();
  }

  public boolean isCardNumberInDatabase(String cardNumber) {
    String sql = "SELECT number " +
        "FROM card " +
        "WHERE number = ?";
    try (Connection connection = source.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, cardNumber);
      ResultSet set = statement.executeQuery();
      if (set != null) {
        return true;
      }
    } catch (SQLException e) {
      return false;
    }
    return false;
  }

  public boolean addIncome(String cardNumber, int income) {
    String sql = "UPDATE card SET balance = balance + ? " +
        "WHERE number = ?";
    try (Connection connection = source.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, income);
      statement.setString(2, cardNumber);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public boolean deleteAccount(String cardNumber, String pin) {
    String sql = "DELETE FROM card " +
        "WHERE number = ? AND pin = ?";
    try (Connection connection = source.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, cardNumber);
      statement.setString(2, pin);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public void selectAll() {
    String sql = "SELECT id, number, pin, balance FROM card";

    try (Connection conn = source.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      // loop through the result set
      while (rs.next()) {
        System.out.println(rs.getInt("id") + "\t" +
            rs.getString("number") + "\t" +
            rs.getString("pin") + "\t" +
            rs.getInt("balance"));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  //helper method to transform string of numbers into array of ints
  private int[] stringToArray(String input) {
    int[] result = new int[input.length()];
    for (int i = 0; i < input.length(); i++) {
      result[i] = Integer.parseInt(String.valueOf(input.charAt(i)));
    }
    return result;
  }
}
