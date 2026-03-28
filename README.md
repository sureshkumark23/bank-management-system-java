# 🏦 Bank Management System

A full-featured **desktop banking application** built in **Core Java** demonstrating enterprise-level software architecture with Swing GUI, JDBC, and MySQL.

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![MySQL](https://img.shields.io/badge/MySQL-9.6-blue?style=for-the-badge&logo=mysql)
![Swing](https://img.shields.io/badge/GUI-Swing%2FAWT-green?style=for-the-badge)
![JDBC](https://img.shields.io/badge/DB-JDBC-red?style=for-the-badge)

---

## 📌 Project Overview

The Bank Management System is a role-based desktop application that allows:
- **Admin** to create customer accounts and manage users
- **Customers** to deposit, withdraw, view transaction history and export bank statements

Built with a clean **layered architecture** — Model → DAO → Service → UI — every layer has one clear job.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 Role-Based Login | Admin and Customer roles with separate access |
| 💰 Deposit & Withdraw | Real-time balance updates with JDBC transaction rollback |
| 📋 Transaction History | Last 5 transactions loaded via SwingWorker |
| 📄 Statement Export | Export full transaction history to `.txt` file |
| ⏰ Session Timeout | Auto logout after 5 minutes using ScheduledExecutorService |
| 👤 Customer Registration | Admin can register new customers with initial balance |
| 🏦 Account Types | Savings (4% interest) and Current (0% interest) accounts |

---

## 🧱 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 (Core Java) |
| GUI | Swing / AWT |
| Database | MySQL 9.6 |
| DB Driver | MySQL Connector/J 8.3.0 |
| IDE | VS Code + Java Extension Pack |

---

## 🗂️ Project Structure

```
BankManagementSystem/
├── src/
│   ├── Main.java
│   └── com/bankapp/
│       ├── model/
│       │   ├── Account.java              ← Abstract parent class
│       │   ├── SavingsAccount.java       ← Extends Account (4% interest)
│       │   ├── CurrentAccount.java       ← Extends Account (0% interest)
│       │   ├── User.java
│       │   └── Transaction.java
│       ├── dao/
│       │   ├── AccountDAO.java           ← Interface
│       │   ├── TransactionDAO.java       ← Interface
│       │   ├── UserDAO.java              ← Interface
│       │   └── impl/
│       │       ├── AccountDAOImpl.java   ← JDBC implementation
│       │       ├── TransactionDAOImpl.java
│       │       └── UserDAOImpl.java
│       ├── service/
│       │   └── AccountService.java       ← Business logic
│       ├── exception/
│       │   ├── BankException.java
│       │   ├── InsufficientFundsException.java
│       │   └── AccountLockedException.java
│       ├── ui/
│       │   ├── LoginFrame.java
│       │   ├── DashboardFrame.java
│       │   └── RegisterFrame.java
│       └── util/
│           ├── DBConnection.java         ← Singleton pattern
│           └── SessionManager.java       ← Thread-based timeout
├── lib/
│   └── mysql-connector-j-8.3.0.jar
└── bin/
```

---

## ☕ Core Java Concepts Demonstrated

| Concept | Where |
|---|---|
| **OOP — Encapsulation** | Private fields + getters/setters in all model classes |
| **OOP — Inheritance** | SavingsAccount, CurrentAccount extend Account |
| **OOP — Polymorphism** | `calculateInterest()` behaves differently per account type |
| **OOP — Abstraction** | `Account` is abstract with abstract method |
| **Singleton Pattern** | `DBConnection.java` — one DB connection for entire app |
| **DAO Pattern** | Interface + Impl separation — SQL isolated from business logic |
| **Custom Exceptions** | `BankException → InsufficientFundsException` hierarchy |
| **JDBC Transactions** | `commit()` / `rollback()` in withdraw — ACID compliance |
| **Collections — ArrayList** | Transaction history results from DB |
| **Collections — HashMap** | Active session tracking in SessionManager |
| **Multithreading — ScheduledExecutorService** | Session timeout after 5 min inactivity |
| **Multithreading — SwingWorker** | Non-blocking DB calls — UI never freezes |
| **File I/O — BufferedWriter** | Export transaction statement to `.txt` |
| **Event-Driven Programming** | ActionListeners on all Swing buttons |

---

## 🗄️ Database Schema

```sql
-- Users table
CREATE TABLE users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50) UNIQUE NOT NULL,
    password  VARCHAR(255) NOT NULL,
    role      ENUM('ADMIN', 'CUSTOMER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Accounts table
CREATE TABLE accounts (
    account_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL,
    account_type ENUM('SAVINGS', 'CURRENT') NOT NULL,
    balance      DECIMAL(15, 2) DEFAULT 0.00,
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Transactions table
CREATE TABLE transactions (
    txn_id       INT AUTO_INCREMENT PRIMARY KEY,
    account_id   INT NOT NULL,
    txn_type     ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER') NOT NULL,
    amount       DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,
    description  VARCHAR(255),
    txn_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
```

---

## 🚀 Getting Started

### Prerequisites
- JDK 21+
- MySQL 9.6
- VS Code with Java Extension Pack
- MySQL Connector/J 8.3.0 JAR

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/bank-management-system-java.git
cd bank-management-system-java
```

### 2. Setup MySQL Database
```bash
mysql -u root
```
```sql
CREATE DATABASE bankapp;
USE bankapp;
-- Run all CREATE TABLE statements from schema above
INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN');
INSERT INTO accounts (user_id, account_type, balance, is_active) VALUES (1, 'SAVINGS', 10000.00, true);
```

### 3. Add MySQL Connector JAR
Place `mysql-connector-j-8.3.0.jar` inside the `lib/` folder.

### 4. Compile
```bash
javac -cp src:lib/mysql-connector-j-8.3.0.jar -d bin \
  src/com/bankapp/util/DBConnection.java \
  src/com/bankapp/util/SessionManager.java \
  src/com/bankapp/exception/BankException.java \
  src/com/bankapp/exception/InsufficientFundsException.java \
  src/com/bankapp/exception/AccountLockedException.java \
  src/com/bankapp/model/Account.java \
  src/com/bankapp/model/SavingsAccount.java \
  src/com/bankapp/model/CurrentAccount.java \
  src/com/bankapp/model/Transaction.java \
  src/com/bankapp/model/User.java \
  src/com/bankapp/dao/AccountDAO.java \
  src/com/bankapp/dao/TransactionDAO.java \
  src/com/bankapp/dao/UserDAO.java \
  src/com/bankapp/dao/impl/AccountDAOImpl.java \
  src/com/bankapp/dao/impl/TransactionDAOImpl.java \
  src/com/bankapp/dao/impl/UserDAOImpl.java \
  src/com/bankapp/service/AccountService.java \
  src/com/bankapp/ui/RegisterFrame.java \
  src/com/bankapp/ui/DashboardFrame.java \
  src/com/bankapp/ui/LoginFrame.java \
  src/Main.java
```

### 5. Run
```bash
java -cp bin:lib/mysql-connector-j-8.3.0.jar Main
```

---

## 🔑 Default Login

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Customer | *(create via New Customer button)* | *(set during registration)* |

---

## 📐 Architecture

```
UI Layer       →  LoginFrame, DashboardFrame, RegisterFrame
     ↓
Service Layer  →  AccountService (business rules + coordination)
     ↓
DAO Layer      →  AccountDAO, TransactionDAO, UserDAO (interfaces)
     ↓
DAO Impl       →  AccountDAOImpl, TransactionDAOImpl, UserDAOImpl (JDBC)
     ↓
Database       →  MySQL (users, accounts, transactions)
```

---

## 🎯 Key Design Decisions

**Why Singleton for DB Connection?**
Only one connection exists throughout the app — prevents resource waste and connection conflicts.

**Why DAO Pattern?**
All SQL is isolated in DAOImpl classes. UI and Service layers never touch SQL. Switching databases requires only rewriting the Impl layer.

**Why abstract Account class?**
Account has shared state (balance, accountId) that an interface cannot hold. The abstract `calculateInterest()` method forces each child to provide its own implementation.

**Why SwingWorker for transactions?**
Swing's Event Dispatch Thread must not be blocked. SwingWorker runs DB calls in a background thread and safely updates the UI when done.

---

## 👨‍💻 Author

**Suresh Kumar K**

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).