class FlightManagement:
    def __init__(self):
        self.flights = {}  # Stores flight details

    def add_flight(self, flight_no, destination, seats, price):
        """Add a flight to the system."""
        if flight_no in self.flights:
            print("Error: Flight already exists!")
        else:
            try:
                self.flights[flight_no] = {
                    'destination': destination,
                    'seats': int(seats),
                    'available_seats': int(seats),
                    'price': float(price)
                }
                print(f"Flight {flight_no} to {destination} added successfully with a ticket price of ${price:.2f}!")
            except ValueError:
                print("Error: Invalid input for seats or price. Please enter numeric values.")

    def update_price(self, flight_no, price):
        """Update the price of a flight ticket."""
        if flight_no not in self.flights:
            print("Error: Flight not found!")
        else:
            try:
                self.flights[flight_no]['price'] = float(price)
                print(f"Flight {flight_no} price updated to ${price:.2f} successfully!")
            except ValueError:
                print("Error: Invalid price. Please enter a numeric value.")

    def view_flights(self):
        """View all available flights."""
        print("\nAvailable Flights:")
        if not self.flights:
            print("No flights available.")
        else:
            for flight_no, details in self.flights.items():
                print(f"Flight No: {flight_no}, Destination: {details['destination']}, "
                      f"Seats: {details['available_seats']}/{details['seats']}, Price: ${details['price']:.2f}")

    def search_flights(self, destination):
        """Search flights by destination."""
        print(f"\nFlights to {destination}:")
        found = False
        for flight_no, details in self.flights.items():
            if details['destination'].lower() == destination.lower():
                print(f"Flight No: {flight_no}, Seats: {details['available_seats']}/{details['seats']}, "
                      f"Price: ${details['price']:.2f}")
                found = True
        if not found:
            print("No flights found to this destination.")


class BookingManagement:
    def __init__(self):
        self.bookings = {}  # Stores booking details

    def book_ticket(self, passenger_name, flight_no, flights):
        """Book a ticket for a flight."""
        if flight_no not in flights:
            print("Error: Invalid flight number!")
        else:
            flight = flights[flight_no]
            if flight['available_seats'] <= 0:
                print("Error: No seats available on this flight!")
            else:
                ticket_id = len(self.bookings) + 1
                self.bookings[ticket_id] = {
                    'passenger_name': passenger_name,
                    'flight_no': flight_no
                }
                flight['available_seats'] -= 1
                print(f"Ticket booked successfully! Ticket ID: {ticket_id}, Price: ${flight['price']:.2f}")

    def cancel_booking(self, ticket_id, flights):
        """Cancel a booking using ticket ID."""
        if ticket_id not in self.bookings:
            print("Error: Invalid ticket ID!")
        else:
            flight_no = self.bookings[ticket_id]['flight_no']
            flights[flight_no]['available_seats'] += 1
            del self.bookings[ticket_id]
            print("Booking canceled successfully!")

    def view_booking(self, ticket_id, flights):
        """View ticket details using ticket ID."""
        if ticket_id not in self.bookings:
            print("Error: Invalid ticket ID!")
        else:
            booking = self.bookings[ticket_id]
            flight = flights[booking['flight_no']]
            print(f"Ticket ID: {ticket_id}")
            print(f"Passenger Name: {booking['passenger_name']}")
            print(f"Flight No: {booking['flight_no']}, Destination: {flight['destination']}, "
                  f"Price: ${flight['price']:.2f}")

    def generate_report(self, flights):
        """Generate a summary report of flights and bookings."""
        print("\n--- Summary Report ---")
        print("Flights:")
        for flight_no, details in flights.items():
            print(f"Flight No: {flight_no}, Destination: {details['destination']}, "
                  f"Seats: {details['available_seats']}/{details['seats']}, Price: ${details['price']:.2f}")
        print("\nBookings:")
        for ticket_id, details in self.bookings.items():
            print(f"Ticket ID: {ticket_id}, Passenger: {details['passenger_name']}, Flight No: {details['flight_no']}")


class AirlineManagementSystem(FlightManagement, BookingManagement):
    def __init__(self):
        FlightManagement.__init__(self)
        BookingManagement.__init__(self)

    def menu(self):
        """Display menu options."""
        while True:
            print("\nAirline Management System Menu:")
            print("1. Add Flight")
            print("2. Update Flight Price")
            print("3. View Flights")
            print("4. Search Flights by Destination")
            print("5. Book Ticket")
            print("6. Cancel Booking")
            print("7. View Booking")
            print("8. Generate Report")
            print("9. Exit")

            choice = input("Enter your choice: ")
            try:
                if choice == '1':
                    flight_no = input("Enter flight number: ")
                    destination = input("Enter destination: ")
                    seats = int(input("Enter number of seats: "))
                    price = float(input("Enter ticket price: "))
                    self.add_flight(flight_no, destination, seats, price)
                elif choice == '2':
                    flight_no = input("Enter flight number: ")
                    price = float(input("Enter new ticket price: "))
                    self.update_price(flight_no, price)
                elif choice == '3':
                    self.view_flights()
                elif choice == '4':
                    destination = input("Enter destination to search: ")
                    self.search_flights(destination)
                elif choice == '5':
                    passenger_name = input("Enter passenger name: ")
                    flight_no = input("Enter flight number: ")
                    self.book_ticket(passenger_name, flight_no, self.flights)
                elif choice == '6':
                    ticket_id = int(input("Enter ticket ID to cancel: "))
                    self.cancel_booking(ticket_id, self.flights)
                elif choice == '7':
                    ticket_id = int(input("Enter ticket ID to view: "))
                    self.view_booking(ticket_id, self.flights)
                elif choice == '8':
                    self.generate_report(self.flights)
                elif choice == '9':
                    print("Exiting the system. Thank you!")
                    break
                else:
                    print("Error: Invalid choice! Please try again.")
            except ValueError:
                print("Error: Invalid input. Please enter the correct type of data.")


# Run the Airlines Management System
if __name__ == "__main__":
    system = AirlineManagementSystem()
    system.menu()

