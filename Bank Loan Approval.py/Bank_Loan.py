import sqlite3

# --- DATABASE SETUP ---
conn = sqlite3.connect('bank_loan.db')
cursor = conn.cursor()

# Create tables
cursor.execute('''
CREATE TABLE IF NOT EXISTS customers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    email TEXT UNIQUE,
    phone TEXT
)
''')

cursor.execute('''
CREATE TABLE IF NOT EXISTS loans (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER,
    amount REAL,
    interest_rate REAL,
    term INTEGER,
    status TEXT,
    balance REAL,
    FOREIGN KEY(customer_id) REFERENCES customers(id)
)
''')

conn.commit()

# --- CORE FUNCTIONS ---

def add_customer():
    name = input("Enter Customer Name: ")
    email = input("Enter Email: ")
    phone = input("Enter Phone: ")
    cursor.execute("INSERT INTO customers (name, email, phone) VALUES (?, ?, ?)", (name, email, phone))
    conn.commit()
    print("✅ Customer added successfully!\n")

def apply_loan():
    email = input("Enter Customer Email: ")
    cursor.execute("SELECT id FROM customers WHERE email=?", (email,))
    customer = cursor.fetchone()
    if not customer:
        print("❌ Customer not found. Please register first.")
        return

    amount = float(input("Enter Loan Amount: "))
    rate = float(input("Enter Interest Rate (%): "))
    term = int(input("Enter Term (in months): "))
    balance = amount
    cursor.execute("INSERT INTO loans (customer_id, amount, interest_rate, term, status, balance) VALUES (?, ?, ?, ?, ?, ?)",
                   (customer[0], amount, rate, term, "Pending", balance))
    conn.commit()
    print("✅ Loan application submitted!\n")

def approve_loan():
    loan_id = input("Enter Loan ID to Approve: ")
    cursor.execute("UPDATE loans SET status='Approved' WHERE id=?", (loan_id,))
    conn.commit()
    print("✅ Loan Approved!\n")

def reject_loan():
    loan_id = input("Enter Loan ID to Reject: ")
    cursor.execute("UPDATE loans SET status='Rejected' WHERE id=?", (loan_id,))
    conn.commit()
    print("❌ Loan Rejected!\n")

def view_loans():
    cursor.execute('''
    SELECT loans.id, customers.name, amount, interest_rate, term, status, balance
    FROM loans JOIN customers ON loans.customer_id = customers.id
    ''')
    data = cursor.fetchall()
    print("\n--- Loan Details ---")
    for row in data:
        print(f"Loan ID: {row[0]} | Name: {row[1]} | Amount: {row[2]} | Rate: {row[3]}% | Term: {row[4]} months | Status: {row[5]} | Balance: {row[6]}")
    print()

def repay_loan():
    loan_id = input("Enter Loan ID: ")
    payment = float(input("Enter Repayment Amount: "))
    cursor.execute("SELECT balance FROM loans WHERE id=?", (loan_id,))
    loan = cursor.fetchone()
    if loan:
        new_balance = loan[0] - payment
        if new_balance < 0:
            new_balance = 0
        cursor.execute("UPDATE loans SET balance=? WHERE id=?", (new_balance, loan_id))
        conn.commit()
        print(f"✅ Payment successful! Remaining Balance: {new_balance}\n")
    else:
        print("❌ Loan not found.\n")

def menu():
    while True:
        print("====== BANK LOAN MANAGEMENT SYSTEM ======")
        print("1. Add Customer")
        print("2. Apply Loan")
        print("3. Approve Loan")
        print("4. Reject Loan")
        print("5. View All Loans")
        print("6. Repay Loan")
        print("7. Exit")

        choice = input("Enter choice: ")

        if choice == '1':
            add_customer()
        elif choice == '2':
            apply_loan()
        elif choice == '3':
            approve_loan()
        elif choice == '4':
            reject_loan()
        elif choice == '5':
            view_loans()
        elif choice == '6':
            repay_loan()
        elif choice == '7':
            print("👋 Exiting system. Goodbye!")
            break
        else:
            print("❌ Invalid choice, please try again.\n")

# --- RUN PROGRAM ---
menu()

# Close connection
conn.close()