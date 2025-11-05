#!/usr/bin/env python3
"""
Regular Health Checkup CLI Tool
Save health entries, compute BMI, flag abnormal vitals, and plot trends.

Usage:
    python health_checkup.py
Requirements:
    - python 3.8+
    - pandas
    - matplotlib
Install requirements:
    pip install pandas matplotlib
"""

import csv
import datetime
import os
from typing import Dict, List, Optional

import pandas as pd
import matplotlib.pyplot as plt

CSV_FILE = "health_log.csv"
FIELDNAMES = [
    "date", "age", "sex", "weight_kg", "height_cm", "bmi", "bmi_category",
    "temp_c", "hr_bpm", "bp_systolic", "bp_diastolic", "blood_glucose_mg_dl",
    "notes"
]


def ensure_csv_exists():
    if not os.path.exists(CSV_FILE):
        with open(CSV_FILE, "w", newline="") as f:
            writer = csv.DictWriter(f, fieldnames=FIELDNAMES)
            writer.writeheader()


def calc_bmi(weight_kg: float, height_cm: float) -> float:
    if height_cm <= 0:
        return 0.0
    h_m = height_cm / 100.0
    bmi = weight_kg / (h_m * h_m)
    return round(bmi, 2)


def bmi_category(bmi: float) -> str:
    if bmi == 0:
        return "unknown"
    if bmi < 18.5:
        return "Underweight"
    if bmi < 25:
        return "Normal"
    if bmi < 30:
        return "Overweight"
    return "Obese"


def flag_vitals(entry: Dict) -> List[str]:
    warnings = []
    # Temperature (normal approx 36.1 - 37.2°C)
    t = entry.get("temp_c")
    if t is not None:
        if t >= 38.0:
            warnings.append(f"Fever (temp {t}°C). Consider medical advice.")
        elif t < 35.0:
            warnings.append(f"Low temperature ({t}°C).")

    # Heart rate (adult resting: 60-100 bpm typical)
    hr = entry.get("hr_bpm")
    if hr is not None:
        if hr < 50:
            warnings.append(f"Low resting HR ({hr} bpm).")
        elif hr > 120:
            warnings.append(f"High resting HR ({hr} bpm).")

    # Blood pressure (simplified thresholds)
    sys = entry.get("bp_systolic")
    dia = entry.get("bp_diastolic")
    if sys is not None and dia is not None:
        if sys >= 180 or dia >= 120:
            warnings.append(f"Hypertensive crisis (BP {sys}/{dia}). Seek urgent care.")
        elif sys >= 140 or dia >= 90:
            warnings.append(f"High BP (hypertension) detected: {sys}/{dia}.")
        elif sys < 90 or dia < 60:
            warnings.append(f"Low BP detected: {sys}/{dia}.")

    # Blood glucose (fasting approx <100 mg/dL)
    bg = entry.get("blood_glucose_mg_dl")
    if bg is not None:
        if bg >= 200:
            warnings.append(f"Very high blood glucose ({bg} mg/dL). Seek medical advice.")
        elif bg >= 126:
            warnings.append(f"High fasting blood glucose ({bg} mg/dL).")

    # BMI category
    bmi_cat = entry.get("bmi_category", "")
    if bmi_cat == "Underweight":
        warnings.append("Underweight — consider nutrition evaluation.")
    elif bmi_cat == "Overweight":
        warnings.append("Overweight — consider lifestyle changes.")
    elif bmi_cat == "Obese":
        warnings.append("Obese — medical/lifestyle review recommended.")

    return warnings


def add_entry_interactive():
    print("\nEnter new health checkup data (press Enter to skip an optional field):")
    date = input("Date (YYYY-MM-DD) [default today]: ").strip()
    if not date:
        date = datetime.date.today().isoformat()
    age = input("Age (years): ").strip()
    sex = input("Sex (M/F/Other): ").strip() or ""
    weight = input("Weight (kg): ").strip()
    height = input("Height (cm): ").strip()
    temp = input("Temperature (°C): ").strip()
    hr = input("Resting heart rate (bpm): ").strip()
    bp_sys = input("BP systolic (mmHg): ").strip()
    bp_dia = input("BP diastolic (mmHg): ").strip()
    bg = input("Blood glucose (mg/dL) [optional]: ").strip()
    notes = input("Notes (medications, symptoms): ").strip()

    # convert where possible
    def to_float_or_none(x):
        try:
            return float(x) if x != "" else None
        except:
            return None

    def to_int_or_none(x):
        try:
            return int(x) if x != "" else None
        except:
            return None

    entry = {
        "date": date,
        "age": to_int_or_none(age),
        "sex": sex,
        "weight_kg": to_float_or_none(weight),
        "height_cm": to_float_or_none(height),
        "temp_c": to_float_or_none(temp),
        "hr_bpm": to_int_or_none(hr),
        "bp_systolic": to_int_or_none(bp_sys),
        "bp_diastolic": to_int_or_none(bp_dia),
        "blood_glucose_mg_dl": to_float_or_none(bg),
        "notes": notes,
    }

    # compute BMI
    if entry["weight_kg"] is not None and entry["height_cm"] is not None:
        entry["bmi"] = calc_bmi(entry["weight_kg"], entry["height_cm"])
        entry["bmi_category"] = bmi_category(entry["bmi"])
    else:
        entry["bmi"] = ""
        entry["bmi_category"] = ""

    # write to CSV
    ensure_csv_exists()
    # transform to strings for CSV saving
    save_row = {k: ("" if entry.get(k) is None else entry.get(k)) for k in FIELDNAMES}
    # fill missing fields with computed ones
    save_row["bmi"] = entry["bmi"]
    save_row["bmi_category"] = entry["bmi_category"]
    with open(CSV_FILE, "a", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=FIELDNAMES)
        writer.writerow(save_row)

    print("\nSaved entry:")
    for k in FIELDNAMES:
        print(f"  {k}: {save_row.get(k,'')}")
    warnings = flag_vitals(entry)
    if warnings:
        print("\n⚠️ Warnings / flags:")
        for w in warnings:
            print("  -", w)
    else:
        print("\nAll vitals look OK (based on simple thresholds).")


def load_entries() -> pd.DataFrame:
    ensure_csv_exists()
    df = pd.read_csv(CSV_FILE, parse_dates=["date"])
    return df


def show_summary():
    df = load_entries()
    if df.empty:
        print("No entries yet. Add one first.")
        return
    last = df.sort_values("date").iloc[-1]
    print("\nLast entry summary:")
    for col in FIELDNAMES:
        val = last.get(col, "")
        print(f"  {col}: {val}")
    print("\nSimple trends (last 5 entries):")
    print(df.sort_values("date").tail(5)[["date", "weight_kg", "bmi", "hr_bpm", "bp_systolic", "bp_diastolic"]].to_string(index=False))


def plot_trends():
    df = load_entries()
    if df.empty:
        print("No data to plot.")
        return
    df["date"] = pd.to_datetime(df["date"])
    df = df.sort_values("date")
    # convert numeric columns
    for c in ["weight_kg", "bmi", "hr_bpm", "bp_systolic", "bp_diastolic"]:
        df[c] = pd.to_numeric(df[c], errors="coerce")

    plt.figure(figsize=(10, 6))
    # Subplots with simple separate plots (one chart at a time)
    print("Choose metric to plot: 1) weight 2) BMI 3) HR 4) BP (systolic & diastolic)")
    choice = input("Enter number: ").strip()
    if choice == "1":
        plt.plot(df["date"], df["weight_kg"], marker="o")
        plt.title("Weight (kg) over time")
        plt.xlabel("Date")
        plt.ylabel("Weight (kg)")
    elif choice == "2":
        plt.plot(df["date"], df["bmi"], marker="o")
        plt.title("BMI over time")
        plt.xlabel("Date")
        plt.ylabel("BMI")
    elif choice == "3":
        plt.plot(df["date"], df["hr_bpm"], marker="o")
        plt.title("Resting Heart Rate (bpm) over time")
        plt.xlabel("Date")
        plt.ylabel("BPM")
    elif choice == "4":
        plt.plot(df["date"], df["bp_systolic"], marker="o", label="Systolic")
        plt.plot(df["date"], df["bp_diastolic"], marker="o", label="Diastolic")
        plt.title("Blood Pressure over time")
        plt.xlabel("Date")
        plt.ylabel("mmHg")
        plt.legend()
    else:
        print("Unknown choice.")
        return
    plt.grid(True)
    plt.tight_layout()
    plt.show()


def export_to_excel(outfile="health_log.xlsx"):
    df = load_entries()
    if df.empty:
        print("No data to export.")
        return
    df.to_excel(outfile, index=False)
    print(f"Exported to {outfile}")


def show_all_entries():
    df = load_entries()
    if df.empty:
        print("No entries.")
        return
    print(df.sort_values("date").to_string(index=False))


def main_menu():
    ensure_csv_exists()
    while True:
        print("\n=== Regular Health Checkup Tool ===")
        print("1) Add new entry")
        print("2) Show last entry & summary")
        print("3) Show all entries")
        print("4) Plot trends")
        print("5) Export to Excel")
        print("0) Exit")
        choice = input("Choose an option: ").strip()
        if choice == "1":
            add_entry_interactive()
        elif choice == "2":
            show_summary()
        elif choice == "3":
            show_all_entries()
        elif choice == "4":
            plot_trends()
        elif choice == "5":
            export_to_excel()
        elif choice == "0":
            print("Bye!")
            break
        else:
            print("Unknown option. Try again.")


if __name__ == "__main__":
    main_menu()