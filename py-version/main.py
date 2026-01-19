import mysql.connector
from mysql.connector import Error
from datetime import date

# ---------- CONFIG ----------
DB_HOST = "localhost"
DB_USER = "root"
DB_PASSWORD = "root"
DB_NAME = "money_tracker"
# ----------------------------


def get_connection():
    return mysql.connector.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASSWORD
    )


def setup_database():
    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute(f"CREATE DATABASE IF NOT EXISTS {DB_NAME}")
    cursor.execute(f"USE {DB_NAME}")

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS transactions (
            id INT AUTO_INCREMENT PRIMARY KEY,
            amount DECIMAL(10,2) NOT NULL,
            category VARCHAR(100),
            type ENUM('income', 'expense') NOT NULL,
            description TEXT,
            date DATE NOT NULL
        )
    """)

    conn.commit()
    cursor.close()
    conn.close()


def add_transaction(amount, category, t_type, description):
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(f"USE {DB_NAME}")

    sql = """
        INSERT INTO transactions (amount, category, type, description, date)
        VALUES (%s, %s, %s, %s, %s)
    """
    cursor.execute(sql, (amount, category, t_type, description, date.today()))
    conn.commit()

    cursor.close()
    conn.close()
    print("Transaction added successfully.\n")


def view_transactions():
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(f"USE {DB_NAME}")

    cursor.execute("SELECT * FROM transactions ORDER BY date DESC")
    rows = cursor.fetchall()

    print("\nID | Amount | Type | Category | Date | Description")
    print("-" * 60)
    for r in rows:
        print(f"{r[0]} | {r[1]} | {r[3]} | {r[2]} | {r[5]} | {r[4]}")

    cursor.close()
    conn.close()
    print()


def view_balance():
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(f"USE {DB_NAME}")

    cursor.execute("""
        SELECT
        SUM(CASE WHEN type='income' THEN amount ELSE 0 END) -
        SUM(CASE WHEN type='expense' THEN amount ELSE 0 END)
        FROM transactions
    """)

    balance = cursor.fetchone()[0]
    balance = balance if balance else 0

    print(f"\nCurrent Balance: {balance}\n")

    cursor.close()
    conn.close()


def menu():
    print("=== Money Tracker ===")
    print("1. Add Income")
    print("2. Add Expense")
    print("3. View Transactions")
    print("4. View Balance")
    print("5. Exit")


def main():
    setup_database()

    while True:
        menu()
        choice = input("Choose an option: ")

        if choice == "1":
            amount = float(input("Amount: "))
            category = input("Category: ")
            description = input("Description: ")
            add_transaction(amount, category, "income", description)

        elif choice == "2":
            amount = float(input("Amount: "))
            category = input("Category: ")
            description = input("Description: ")
            add_transaction(amount, category, "expense", description)

        elif choice == "3":
            view_transactions()

        elif choice == "4":
            view_balance()

        elif choice == "5":
            print("Goodbye!")
            break

        else:
            print("Invalid choice.\n")


if __name__ == "__main__":
    main()
