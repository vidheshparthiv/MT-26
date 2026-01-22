import mysql.connector
from datetime import date
import csv

DB_HOST = "localhost"
DB_USER = "root"
DB_PASSWORD = "root"
DB_NAME = "money_tracker"


def get_connection():
    return mysql.connector.connect(
        host=DB_HOST,
        user=DB_USER,
        password=DB_PASSWORD,
        database=DB_NAME
    )


def add_transaction(amount, category, t_type, description):
    conn = get_connection()
    cursor = conn.cursor()

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

    cursor.execute("""
        SELECT
        SUM(CASE WHEN type='income' THEN amount ELSE 0 END) -
        SUM(CASE WHEN type='expense' THEN amount ELSE 0 END)
        FROM transactions
    """)

    balance = cursor.fetchone()[0] or 0
    print(f"\nCurrent Balance: {balance}\n")

    cursor.close()
    conn.close()

def export_to_csv():
    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute("SELECT * FROM transactions ORDER BY date DESC")
    rows = cursor.fetchall()

    with open("transactions.csv", "w", newline="") as file:
        writer = csv.writer(file)
        writer.writerow(["ID", "Amount", "Category", "Type", "Description", "Date"])
        writer.writerows(rows)

    cursor.close()
    conn.close()
    print("Data exported to transactions.csv successfully.\n")


def delete_transaction():
    transaction_id = input("Enter Transaction ID to delete: ")

    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute("DELETE FROM transactions WHERE id = %s", (transaction_id,))
    conn.commit()

    if cursor.rowcount > 0:
        print("Transaction deleted successfully.\n")
    else:
        print("Transaction ID not found.\n")

    cursor.close()
    conn.close()


def menu():
    print("=== Money Tracker ===")
    print("1. Add Income")
    print("2. Add Expense")
    print("3. View Transactions")
    print("4. View Balance")
    print("5. Export to CSV")
    print("6. Delete Transaction")
    print("7. Exit")


def main():
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
            export_to_csv()

        elif choice == "6":
            delete_transaction()

        elif choice == "7":
            print("Goodbye!")
            break

        else:
            print("Invalid choice.\n")


if __name__ == "__main__":
    main()
