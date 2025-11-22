import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// --- 1. Custom Exception (Shows you know Exception Handling) ---
class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}

class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

// --- 2. Abstraction & Encapsulation ---
abstract class Account {
    private double balance;
    private int accountNumber;
    private int pin;

    public Account(int accountNumber, int pin, double balance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
    }

    // Getters and Setters (Encapsulation)
    public double getBalance() { return balance; }
    public int getAccountNumber() { return accountNumber; }

    public boolean validatePin(int enteredPin) {
        return this.pin == enteredPin;
    }

    protected void setBalance(double newBalance) {
        this.balance = newBalance;
    }
}

// --- 3. Interface (Shows you know Polymorphism/Interfaces) ---
interface ATMOperations {
    void viewBalance();
    void withdraw(double amount);
    void deposit(double amount);
    void transfer(Account targetAccount, double amount);
}

// --- 4. Concrete Implementation ---
class BankAccount extends Account implements ATMOperations {

    public BankAccount(int accountNumber, int pin, double balance) {
        super(accountNumber, pin, balance);
    }

    @Override
    public void viewBalance() {
        System.out.println("Current Balance: $" + getBalance());
    }

    @Override
    public void withdraw(double amount) {
        try {
            if (amount <= 0) {
                throw new InvalidAmountException("Amount must be greater than zero.");
            }
            if (amount > getBalance()) {
                throw new InsufficientFundsException("Insufficient balance!");
            }
            setBalance(getBalance() - amount);
            System.out.println("Withdrawal Successful. Collect your cash.");
            viewBalance();
        } catch (InvalidAmountException | InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void deposit(double amount) {
        try {
            if (amount <= 0) {
                throw new InvalidAmountException("Invalid deposit amount.");
            }
            setBalance(getBalance() + amount);
            System.out.println("Deposit Successful.");
            viewBalance();
        } catch (InvalidAmountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void transfer(Account targetAccount, double amount) {
        // Safe casting or logic to handle the transfer
        try {
            if (amount > getBalance()) {
                throw new InsufficientFundsException("Cannot transfer. Low balance.");
            }
            // Deduct from sender
            this.setBalance(this.getBalance() - amount);
            // Add to receiver (Using protected setter via method or assuming same package logic)
            // For this simple example, we assume we can modify target via public methods if available
            // But since setBalance is protected, we cheat slightly for the exam or create a method:
            targetAccount.setBalance(targetAccount.getBalance() + amount);

            System.out.println("Transferred $" + amount + " to Account " + targetAccount.getAccountNumber());
            viewBalance();
        } catch (InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

// --- 5. Main Driver Class ---
public class Main {
    // rudimentary database simulation
    private static HashMap<Integer, BankAccount> accounts = new HashMap<>();

    public static void main(String[] args) {
        // Create dummy data
        accounts.put(12345, new BankAccount(12345, 1111, 5000.0));
        accounts.put(67890, new BankAccount(67890, 2222, 1000.0));

        Scanner scanner = new Scanner(System.in);
        System.out.println("=== WELCOME TO JAVA ATM ===");

        System.out.print("Enter Account Number: ");
        int accNum = scanner.nextInt();

        System.out.print("Enter PIN: ");
        int pin = scanner.nextInt();

        if (accounts.containsKey(accNum) && accounts.get(accNum).validatePin(pin)) {
            BankAccount currentAccount = accounts.get(accNum);
            boolean sessionActive = true;

            while (sessionActive) {
                System.out.println("\n--- MENU ---");
                System.out.println("1. View Balance");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. Transfer");
                System.out.println("5. Exit");
                System.out.print("Select option: ");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        currentAccount.viewBalance();
                        break;
                    case 2:
                        System.out.print("Enter amount to deposit: ");
                        double depAmt = scanner.nextDouble();
                        currentAccount.deposit(depAmt);
                        break;
                    case 3:
                        System.out.print("Enter amount to withdraw: ");
                        double withAmt = scanner.nextDouble();
                        currentAccount.withdraw(withAmt);
                        break;
                    case 4:
                        System.out.print("Enter target Account Number: ");
                        int targetAcc = scanner.nextInt();
                        if(accounts.containsKey(targetAcc)) {
                            System.out.print("Enter amount: ");
                            double transAmt = scanner.nextDouble();
                            currentAccount.transfer(accounts.get(targetAcc), transAmt);
                        } else {
                            System.out.println("Target account not found.");
                        }
                        break;
                    case 5:
                        sessionActive = false;
                        System.out.println("Thank you for banking with us.");
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        } else {
            System.out.println("Authentication Failed. Invalid Account Number or PIN.");
        }
        scanner.close();
    }
}
