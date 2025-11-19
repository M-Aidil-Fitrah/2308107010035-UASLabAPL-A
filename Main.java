import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ==================== STRATEGY PATTERN ====================
// Interface untuk strategi pricing
interface PricingStrategy {
    double calculatePrice(double basePrice, int duration);
    String getStrategyName();
}

class HourlyPricing implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice, int duration) {
        return basePrice * duration;
    }
    
    @Override
    public String getStrategyName() {
        return "Per Jam";
    }
}

class DailyPricing implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice, int duration) {
        return basePrice * duration * 20; // 20 jam per hari
    }
    
    @Override
    public String getStrategyName() {
        return "Per Hari";
    }
}

class WeeklyPricing implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice, int duration) {
        double dailyPrice = basePrice * 20;
        double weeklyPrice = dailyPrice * 7 * duration;
        return weeklyPrice * 0.85; // diskon 15%
    }
    
    @Override
    public String getStrategyName() {
        return "Per Minggu (Diskon 15%)";
    }
}

class MonthlyPricing implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice, int duration) {
        double dailyPrice = basePrice * 20;
        double monthlyPrice = dailyPrice * 30 * duration;
        return monthlyPrice * 0.75; // diskon 25%
    }
    
    @Override
    public String getStrategyName() {
        return "Per Bulan (Diskon 25%)";
    }
}

// ==================== DECORATOR PATTERN ====================
// Component interface
interface Rental {
    double getCost();
    String getDescription();
}

// Concrete Component
class BasicRental implements Rental {
    private Vehicle vehicle;
    private PricingStrategy strategy;
    private int duration;
    
    public BasicRental(Vehicle vehicle, PricingStrategy strategy, int duration) {
        this.vehicle = vehicle;
        this.strategy = strategy;
        this.duration = duration;
    }
    
    @Override
    public double getCost() {
        return strategy.calculatePrice(vehicle.getBasePrice(), duration);
    }
    
    @Override
    public String getDescription() {
        return vehicle.getName() + " - " + strategy.getStrategyName();
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
}

// Abstract Decorator
abstract class RentalDecorator implements Rental {
    protected Rental rental;
    
    public RentalDecorator(Rental rental) {
        this.rental = rental;
    }
    
    @Override
    public double getCost() {
        return rental.getCost();
    }
    
    @Override
    public String getDescription() {
        return rental.getDescription();
    }
}

// Concrete Decorators
class InsuranceDecorator extends RentalDecorator {
    public InsuranceDecorator(Rental rental) {
        super(rental);
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 50000;
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + " + Asuransi";
    }
}

class DriverDecorator extends RentalDecorator {
    public DriverDecorator(Rental rental) {
        super(rental);
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 100000;
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + " + Supir";
    }
}

class GPSDecorator extends RentalDecorator {
    public GPSDecorator(Rental rental) {
        super(rental);
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 25000;
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + " + GPS";
    }
}

class ChildSeatDecorator extends RentalDecorator {
    public ChildSeatDecorator(Rental rental) {
        super(rental);
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 30000;
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + " + Kursi Anak";
    }
}

// ==================== OBSERVER PATTERN ====================
// Observer interface
interface Observer {
    void update(String message);
}

// Concrete Observers
class AdminObserver implements Observer {
    private String adminName;
    
    public AdminObserver(String adminName) {
        this.adminName = adminName;
    }
    
    @Override
    public void update(String message) {
        System.out.println("\n[NOTIFIKASI ADMIN - " + adminName + "] " + message);
    }
}

class CustomerObserver implements Observer {
    private String customerName;
    
    public CustomerObserver(String customerName) {
        this.customerName = customerName;
    }
    
    @Override
    public void update(String message) {
        System.out.println("\n[NOTIFIKASI CUSTOMER - " + customerName + "] " + message);
    }
}

// Subject
class NotificationSystem {
    private List<Observer> observers = new ArrayList<>();
    
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}

// ==================== SINGLETON PATTERN ====================
class RentalSystemManager {
    private static RentalSystemManager instance;
    private List<Vehicle> vehicles;
    private List<Booking> bookings;
    private List<User> users;
    private NotificationSystem notificationSystem;
    
    private RentalSystemManager() {
        vehicles = new ArrayList<>();
        bookings = new ArrayList<>();
        users = new ArrayList<>();
        notificationSystem = new NotificationSystem();
        initializeData();
    }
    
    public static RentalSystemManager getInstance() {
        if (instance == null) {
            instance = new RentalSystemManager();
        }
        return instance;
    }
    
    private void initializeData() {
        // Inisialisasi kendaraan
        vehicles.add(new Vehicle("V001", "Toyota Avanza", "MPV", 15000, true));
        vehicles.add(new Vehicle("V002", "Honda Jazz", "Hatchback", 12000, true));
        vehicles.add(new Vehicle("V003", "Mitsubishi Pajero", "SUV", 25000, true));
        vehicles.add(new Vehicle("V004", "Toyota Fortuner", "SUV", 30000, true));
        vehicles.add(new Vehicle("V005", "Honda CBR 150", "Motor", 8000, true));
        
        // Inisialisasi users
        users.add(new User("admin", "admin123", "Admin", true));
        users.add(new User("customer1", "pass123", "Budi Santoso", false));
    }
    
    public List<Vehicle> getVehicles() {
        return vehicles;
    }
    
    public List<Booking> getBookings() {
        return bookings;
    }
    
    public List<User> getUsers() {
        return users;
    }
    
    public NotificationSystem getNotificationSystem() {
        return notificationSystem;
    }
    
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }
    
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
    
    public void addUser(User user) {
        users.add(user);
    }
}

// ==================== MODEL CLASSES ====================
class Vehicle {
    private String id;
    private String name;
    private String type;
    private double basePrice;
    private boolean available;
    
    public Vehicle(String id, String name, String type, double basePrice, boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.basePrice = basePrice;
        this.available = available;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getBasePrice() { return basePrice; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
    @Override
    public String toString() {
        return String.format("%-8s | %-20s | %-10s | Rp %,.0f/jam | %s", 
            id, name, type, basePrice, available ? "Tersedia" : "Disewa");
    }
}

class User {
    private String username;
    private String password;
    private String fullName;
    private boolean isAdmin;
    
    public User(String username, String password, String fullName, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.isAdmin = isAdmin;
    }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public boolean isAdmin() { return isAdmin; }
}

class Booking {
    private static int bookingCounter = 1;
    private String bookingId;
    private User customer;
    private Rental rental;
    private String status; // PENDING, CONFIRMED, REJECTED, COMPLETED
    private LocalDateTime bookingTime;
    
    public Booking(User customer, Rental rental) {
        this.bookingId = "BK" + String.format("%03d", bookingCounter++);
        this.customer = customer;
        this.rental = rental;
        this.status = "PENDING";
        this.bookingTime = LocalDateTime.now();
    }
    
    public String getBookingId() { return bookingId; }
    public User getCustomer() { return customer; }
    public Rental getRental() { return rental; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return String.format("%-8s | %-15s | %-40s | Rp %,.0f | %-10s | %s",
            bookingId, customer.getFullName(), rental.getDescription(), 
            rental.getCost(), status, bookingTime.format(formatter));
    }
}

// ==================== MAIN APPLICATION ====================
public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static RentalSystemManager manager = RentalSystemManager.getInstance();
    private static User currentUser = null;
    private static Observer currentObserver = null;
    
    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("   SELAMAT DATANG DI RENTVEHICLE PRO SYSTEM");
        System.out.println("=======================================================");
        
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else if (currentUser.isAdmin()) {
                showAdminMenu();
            } else {
                showCustomerMenu();
            }
        }
    }
    
    private static void showLoginMenu() {
        System.out.println("\n=== LOGIN ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Pilih menu: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                System.out.println("\nTerima kasih telah menggunakan RentVehicle Pro!");
                System.exit(0);
                break;
            default:
                System.out.println("Pilihan tidak valid!");
        }
    }
    
    private static void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        for (User user : manager.getUsers()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                if (user.isAdmin()) {
                    currentObserver = new AdminObserver(user.getFullName());
                } else {
                    currentObserver = new CustomerObserver(user.getFullName());
                }
                manager.getNotificationSystem().attach(currentObserver);
                System.out.println("\nLogin berhasil! Selamat datang, " + user.getFullName());
                return;
            }
        }
        
        System.out.println("\nUsername atau password salah!");
    }
    
    private static void register() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Nama Lengkap: ");
        String fullName = scanner.nextLine();
        
        User newUser = new User(username, password, fullName, false);
        manager.addUser(newUser);
        System.out.println("\nRegistrasi berhasil! Silakan login.");
    }
    
    private static void showAdminMenu() {
        System.out.println("\n=== MENU ADMIN ===");
        System.out.println("1. Lihat Semua Kendaraan");
        System.out.println("2. Tambah Kendaraan");
        System.out.println("3. Lihat Semua Booking");
        System.out.println("4. Konfirmasi Booking");
        System.out.println("5. Reject Booking");
        System.out.println("6. Laporan Pendapatan");
        System.out.println("7. Logout");
        System.out.print("Pilih menu: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                viewAllVehicles();
                break;
            case 2:
                addVehicle();
                break;
            case 3:
                viewAllBookings();
                break;
            case 4:
                confirmBooking();
                break;
            case 5:
                rejectBooking();
                break;
            case 6:
                showRevenue();
                break;
            case 7:
                logout();
                break;
            default:
                System.out.println("Pilihan tidak valid!");
        }
    }
    
    private static void showCustomerMenu() {
        System.out.println("\n=== MENU CUSTOMER ===");
        System.out.println("1. Lihat Kendaraan Tersedia");
        System.out.println("2. Buat Booking");
        System.out.println("3. Lihat History Booking Saya");
        System.out.println("4. Logout");
        System.out.print("Pilih menu: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                viewAvailableVehicles();
                break;
            case 2:
                createBooking();
                break;
            case 3:
                viewMyBookings();
                break;
            case 4:
                logout();
                break;
            default:
                System.out.println("Pilihan tidak valid!");
        }
    }
    
    private static void viewAllVehicles() {
        System.out.println("\n=== SEMUA KENDARAAN ===");
        System.out.println("ID       | Nama                 | Tipe       | Harga/Jam    | Status");
        System.out.println("-------------------------------------------------------------------------");
        for (Vehicle vehicle : manager.getVehicles()) {
            System.out.println(vehicle);
        }
    }
    
    private static void viewAvailableVehicles() {
        System.out.println("\n=== KENDARAAN TERSEDIA ===");
        System.out.println("ID       | Nama                 | Tipe       | Harga/Jam    | Status");
        System.out.println("-------------------------------------------------------------------------");
        for (Vehicle vehicle : manager.getVehicles()) {
            if (vehicle.isAvailable()) {
                System.out.println(vehicle);
            }
        }
    }
    
    private static void addVehicle() {
        System.out.println("\n=== TAMBAH KENDARAAN ===");
        System.out.print("ID Kendaraan: ");
        String id = scanner.nextLine();
        System.out.print("Nama Kendaraan: ");
        String name = scanner.nextLine();
        System.out.print("Tipe: ");
        String type = scanner.nextLine();
        System.out.print("Harga per jam: ");
        double price = getDoubleInput();
        
        Vehicle vehicle = new Vehicle(id, name, type, price, true);
        manager.addVehicle(vehicle);
        System.out.println("\nKendaraan berhasil ditambahkan!");
    }
    
    private static void createBooking() {
        System.out.println("\n=== BUAT BOOKING ===");
        
        // Pilih kendaraan
        viewAvailableVehicles();
        System.out.print("\nMasukkan ID kendaraan: ");
        String vehicleId = scanner.nextLine();
        
        Vehicle selectedVehicle = null;
        for (Vehicle v : manager.getVehicles()) {
            if (v.getId().equals(vehicleId) && v.isAvailable()) {
                selectedVehicle = v;
                break;
            }
        }
        
        if (selectedVehicle == null) {
            System.out.println("Kendaraan tidak tersedia!");
            return;
        }
        
        // Pilih strategi pricing
        System.out.println("\n=== PILIH STRATEGI PRICING (STRATEGY PATTERN) ===");
        System.out.println("1. Per Jam");
        System.out.println("2. Per Hari");
        System.out.println("3. Per Minggu (Diskon 15%)");
        System.out.println("4. Per Bulan (Diskon 25%)");
        System.out.print("Pilih: ");
        int strategyChoice = getIntInput();
        
        PricingStrategy strategy = null;
        String durationLabel = "";
        switch (strategyChoice) {
            case 1:
                strategy = new HourlyPricing();
                durationLabel = "jam";
                break;
            case 2:
                strategy = new DailyPricing();
                durationLabel = "hari";
                break;
            case 3:
                strategy = new WeeklyPricing();
                durationLabel = "minggu";
                break;
            case 4:
                strategy = new MonthlyPricing();
                durationLabel = "bulan";
                break;
            default:
                System.out.println("Pilihan tidak valid!");
                return;
        }
        
        System.out.print("Durasi (" + durationLabel + "): ");
        int duration = getIntInput();
        
        // Buat basic rental
        Rental rental = new BasicRental(selectedVehicle, strategy, duration);
        
        // Tambah decorator
        System.out.println("\n=== TAMBAH LAYANAN (DECORATOR PATTERN) ===");
        System.out.println("Pilih layanan tambahan (pisahkan dengan koma, contoh: 1,2,3)");
        System.out.println("1. Asuransi (+Rp 50.000)");
        System.out.println("2. Supir (+Rp 100.000)");
        System.out.println("3. GPS (+Rp 25.000)");
        System.out.println("4. Kursi Anak (+Rp 30.000)");
        System.out.println("0. Tidak ada");
        System.out.print("Pilih: ");
        String addOnsInput = scanner.nextLine();
        
        if (!addOnsInput.equals("0")) {
            String[] addOns = addOnsInput.split(",");
            for (String addOn : addOns) {
                switch (addOn.trim()) {
                    case "1":
                        rental = new InsuranceDecorator(rental);
                        break;
                    case "2":
                        rental = new DriverDecorator(rental);
                        break;
                    case "3":
                        rental = new GPSDecorator(rental);
                        break;
                    case "4":
                        rental = new ChildSeatDecorator(rental);
                        break;
                }
            }
        }
        
        // Tampilkan ringkasan
        System.out.println("\n=== RINGKASAN BOOKING ===");
        System.out.println("Deskripsi: " + rental.getDescription());
        System.out.println("Total Biaya: Rp " + String.format("%,.0f", rental.getCost()));
        System.out.print("\nKonfirmasi booking (y/n)? ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            Booking booking = new Booking(currentUser, rental);
            manager.addBooking(booking);
            selectedVehicle.setAvailable(false);
            
            // OBSERVER PATTERN: Notifikasi ke admin
            manager.getNotificationSystem().notifyObservers(
                "Booking baru! " + booking.getBookingId() + " - " + currentUser.getFullName() + 
                " - " + rental.getDescription()
            );
            
            System.out.println("\nBooking berhasil dibuat dengan ID: " + booking.getBookingId());
            System.out.println("Menunggu konfirmasi admin...");
        }
    }
    
    private static void viewAllBookings() {
        System.out.println("\n=== SEMUA BOOKING ===");
        System.out.println("ID       | Customer        | Deskripsi                                | Total           | Status     | Waktu");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
        for (Booking booking : manager.getBookings()) {
            System.out.println(booking);
        }
    }
    
    private static void viewMyBookings() {
        System.out.println("\n=== HISTORY BOOKING SAYA ===");
        System.out.println("ID       | Customer        | Deskripsi                                | Total           | Status     | Waktu");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
        for (Booking booking : manager.getBookings()) {
            if (booking.getCustomer().equals(currentUser)) {
                System.out.println(booking);
            }
        }
    }
    
    private static void confirmBooking() {
        viewAllBookings();
        System.out.print("\nMasukkan ID booking yang akan dikonfirmasi: ");
        String bookingId = scanner.nextLine();
        
        for (Booking booking : manager.getBookings()) {
            if (booking.getBookingId().equals(bookingId) && booking.getStatus().equals("PENDING")) {
                booking.setStatus("CONFIRMED");
                
                // OBSERVER PATTERN: Notifikasi ke customer
                Observer customerObs = new CustomerObserver(booking.getCustomer().getFullName());
                manager.getNotificationSystem().attach(customerObs);
                manager.getNotificationSystem().notifyObservers(
                    "Booking " + bookingId + " telah dikonfirmasi! Silakan ambil kendaraan."
                );
                manager.getNotificationSystem().detach(customerObs);
                
                System.out.println("\nBooking berhasil dikonfirmasi!");
                return;
            }
        }
        
        System.out.println("Booking tidak ditemukan atau sudah diproses!");
    }
    
    private static void rejectBooking() {
        viewAllBookings();
        System.out.print("\nMasukkan ID booking yang akan direject: ");
        String bookingId = scanner.nextLine();
        
        for (Booking booking : manager.getBookings()) {
            if (booking.getBookingId().equals(bookingId) && booking.getStatus().equals("PENDING")) {
                booking.setStatus("REJECTED");
                
                // Kembalikan ketersediaan kendaraan
                BasicRental basicRental = getBasicRental(booking.getRental());
                if (basicRental != null) {
                    basicRental.getVehicle().setAvailable(true);
                }
                
                // OBSERVER PATTERN: Notifikasi ke customer
                Observer customerObs = new CustomerObserver(booking.getCustomer().getFullName());
                manager.getNotificationSystem().attach(customerObs);
                manager.getNotificationSystem().notifyObservers(
                    "Booking " + bookingId + " ditolak. Silakan hubungi admin untuk info lebih lanjut."
                );
                manager.getNotificationSystem().detach(customerObs);
                
                System.out.println("\nBooking berhasil direject!");
                return;
            }
        }
        
        System.out.println("Booking tidak ditemukan atau sudah diproses!");
    }
    
    private static BasicRental getBasicRental(Rental rental) {
        if (rental instanceof BasicRental) {
            return (BasicRental) rental;
        } else if (rental instanceof RentalDecorator) {
            return getBasicRental(((RentalDecorator) rental).rental);
        }
        return null;
    }
    
    private static void showRevenue() {
        System.out.println("\n=== LAPORAN PENDAPATAN ===");
        double totalRevenue = 0;
        int confirmedBookings = 0;
        
        for (Booking booking : manager.getBookings()) {
            if (booking.getStatus().equals("CONFIRMED")) {
                totalRevenue += booking.getRental().getCost();
                confirmedBookings++;
            }
        }
        
        System.out.println("Total Booking Terkonfirmasi: " + confirmedBookings);
        System.out.println("Total Pendapatan: Rp " + String.format("%,.0f", totalRevenue));
    }
    
    private static void logout() {
        manager.getNotificationSystem().detach(currentObserver);
        currentUser = null;
        currentObserver = null;
        System.out.println("\nLogout berhasil!");
    }
    
    private static int getIntInput() {
        try {
            int input = Integer.parseInt(scanner.nextLine());
            return input;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static double getDoubleInput() {
        try {
            double input = Double.parseDouble(scanner.nextLine());
            return input;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}