# Sistem Rental Kendaraan

Sistem manajemen rental kendaraan berbasis CLI yang mengimplementasikan 4 Design Pattern (Strategy, Decorator, Observer, Singleton) untuk demonstrasi prinsip OOP yang baik.

---

## Deskripsi

Aplikasi rental kendaraan dengan 2 role:
- **Customer**: Browse kendaraan, buat booking dengan berbagai opsi pricing dan layanan tambahan
- **Admin**: Kelola kendaraan, konfirmasi booking, dan lihat laporan pendapatan

---

## Design Patterns

### 1. Strategy Pattern - Perhitungan Harga
**Masalah**: Berbagai cara perhitungan harga (jam, hari, minggu, bulan) dengan if-else panjang.

**Solusi**: Setiap strategi pricing punya class sendiri yang implement `PricingStrategy` interface.

```java
interface PricingStrategy {
    double calculatePrice(double basePrice, int duration);
}

class WeeklyPricing implements PricingStrategy {
    public double calculatePrice(double basePrice, int duration) {
        return (basePrice * 20 * 7 * duration) * 0.85; // Diskon 15%
    }
}
```

**Implementasi dalam Sistem:**
```java
// Di menu customer, saat buat booking
System.out.println("=== PILIH STRATEGI PRICING ===");
System.out.println("1. Per Jam");
System.out.println("2. Per Hari");
System.out.println("3. Per Minggu (Diskon 15%)");
System.out.println("4. Per Bulan (Diskon 25%)");

int choice = scanner.nextInt();
PricingStrategy strategy;

switch (choice) {
    case 1: strategy = new HourlyPricing(); break;
    case 2: strategy = new DailyPricing(); break;
    case 3: strategy = new WeeklyPricing(); break;
    case 4: strategy = new MonthlyPricing(); break;
}

// Strategy digunakan di BasicRental
Rental rental = new BasicRental(vehicle, strategy, duration);
double price = rental.getCost(); // Strategy pattern bekerja di sini!
```

**Keuntungan**: Tambah strategi baru tanpa ubah kode existing, hindari if-else.

---

### 2. Decorator Pattern - Layanan Tambahan
**Masalah**: Kombinasi layanan (asuransi, supir, GPS) = 2^n class (16, 32, 64...).

**Solusi**: Stack decorator untuk tambah fitur dinamis.

```java
Rental rental = new BasicRental(vehicle, strategy, duration);
rental = new InsuranceDecorator(rental);  // +Rp 50.000
rental = new GPSDecorator(rental);        // +Rp 25.000
rental = new DriverDecorator(rental);     // +Rp 100.000
// Total otomatis terhitung!
```

**Implementasi dalam Sistem:**
```java
// Di menu customer, setelah pilih pricing strategy
System.out.println("=== TAMBAH LAYANAN ===");
System.out.println("1. Asuransi (+Rp 50.000)");
System.out.println("2. Supir (+Rp 100.000)");
System.out.println("3. GPS (+Rp 25.000)");
System.out.println("4. Kursi Anak (+Rp 30.000)");
System.out.print("Pilih (pisahkan dengan koma): ");

String input = scanner.nextLine(); // Contoh: "1,2,3"
String[] choices = input.split(",");

// Buat basic rental dulu
Rental rental = new BasicRental(vehicle, strategy, duration);

// Stack decorator sesuai pilihan user
for (String choice : choices) {
    switch (choice.trim()) {
        case "1": rental = new InsuranceDecorator(rental); break;
        case "2": rental = new DriverDecorator(rental); break;
        case "3": rental = new GPSDecorator(rental); break;
        case "4": rental = new ChildSeatDecorator(rental); break;
    }
}

// Setiap decorator menambah cost dan description
System.out.println("Deskripsi: " + rental.getDescription());
System.out.println("Total: Rp " + rental.getCost());
```

**Keuntungan**: Kombinasi fleksibel tanpa class explosion, biaya akumulatif otomatis.

---

### 3. Observer Pattern - Notifikasi Real-time
**Masalah**: BookingService harus tahu semua pihak yang perlu dinotif (tight coupling).

**Solusi**: Subject (NotificationSystem) broadcast ke semua Observer yang terdaftar.

```java
// Customer booking
notificationSystem.notifyObservers("Booking baru: BK001 - Avanza");
// Output: [NOTIFIKASI ADMIN] Booking baru: BK001 - Avanza

// Admin konfirmasi
notificationSystem.notifyObservers("Booking BK001 dikonfirmasi");
// Output: [NOTIFIKASI CUSTOMER] Booking BK001 dikonfirmasi
```

**Implementasi dalam Sistem:**
```java
// 1. Saat user login, attach observer
if (currentUser.isAdmin()) {
    currentObserver = new AdminObserver(currentUser.getFullName());
} else {
    currentObserver = new CustomerObserver(currentUser.getFullName());
}
manager.getNotificationSystem().attach(currentObserver);

// 2. Saat customer buat booking
Booking booking = new Booking(currentUser, rental);
manager.addBooking(booking);

// Notify semua admin yang sedang login
manager.getNotificationSystem().notifyObservers(
    "Booking baru! " + booking.getBookingId() + " - " + 
    currentUser.getFullName() + " - " + rental.getDescription()
);

// 3. Saat admin konfirmasi booking
booking.setStatus("CONFIRMED");

// Attach temporary customer observer untuk notifikasi
Observer customerObs = new CustomerObserver(booking.getCustomer().getFullName());
manager.getNotificationSystem().attach(customerObs);

// Notify customer
manager.getNotificationSystem().notifyObservers(
    "Booking " + bookingId + " telah dikonfirmasi! Silakan ambil kendaraan."
);

// Detach setelah notifikasi terkirim
manager.getNotificationSystem().detach(customerObs);

// 4. Saat user logout, detach observer
manager.getNotificationSystem().detach(currentObserver);
```

**Keuntungan**: Loose coupling, mudah tambah observer baru (email, SMS, WhatsApp).

---

### 4. Singleton Pattern - Konsistensi Data
**Masalah**: Setiap user buat `RentalSystemManager` sendiri = data tidak sinkron.

**Solusi**: Hanya 1 instance untuk semua user.

```java
class RentalSystemManager {
    private static RentalSystemManager instance;
    
    private RentalSystemManager() { }
    
    public static RentalSystemManager getInstance() {
        if (instance == null) {
            instance = new RentalSystemManager();
        }
        return instance;
    }
}

// Semua user akses instance yang sama
RentalSystemManager manager = RentalSystemManager.getInstance();
```

**Implementasi dalam Sistem:**
```java
// Di Main class, deklarasi singleton di level class
private static RentalSystemManager manager = RentalSystemManager.getInstance();

// Semua method menggunakan instance yang sama:

// 1. Admin tambah kendaraan
void addVehicle() {
    Vehicle vehicle = new Vehicle(...);
    manager.addVehicle(vehicle); // Masuk ke singleton instance
}

// 2. Customer lihat kendaraan
void viewVehicles() {
    List<Vehicle> vehicles = manager.getVehicles(); // Dari singleton instance
    // Kendaraan yang ditambah admin LANGSUNG terlihat di sini!
}

// 3. Customer buat booking
void createBooking() {
    Booking booking = new Booking(...);
    manager.addBooking(booking); // Masuk ke singleton instance
}

// 4. Admin lihat semua booking
void viewAllBookings() {
    List<Booking> bookings = manager.getBookings(); // Dari singleton instance
    // Booking yang dibuat customer LANGSUNG terlihat di sini!
}

// Semua operasi CRUD mengakses data yang SAMA karena singleton!
```

**Bukti Konsistensi:**
```java
// User A (Customer)
RentalSystemManager managerA = RentalSystemManager.getInstance();
System.out.println(managerA.getVehicles().size()); // Output: 5

// User B (Admin) tambah kendaraan
RentalSystemManager managerB = RentalSystemManager.getInstance();
managerB.addVehicle(new Vehicle("V006", "Innova", "MPV", 18000, true));

// User A cek lagi
System.out.println(managerA.getVehicles().size()); // Output: 6
// LANGSUNG TERUPDATE! Karena managerA == managerB (objek yang sama)

// Cek apakah sama
System.out.println(managerA == managerB); // true
```

**Keuntungan**: Data konsisten, hemat memory, semua perubahan langsung terlihat semua user.

---

## Instalasi & Menjalankan

```bash
# Compile
javac Main.java

# Run
java Main
```

**Default Login:**
- Admin: `admin` / `admin123`
- Customer: `customer1` / `pass123`

---

## Cara Penggunaan

### Customer Flow:
1. Login → Pilih "Buat Booking"
2. Pilih kendaraan (V001 - Avanza)
3. Pilih pricing: **Per Minggu** (diskon 15%)
4. Durasi: **2 minggu**
5. Tambah layanan: **Insurance + GPS**
6. Konfirmasi

**Hasil**: Total Rp 5.430.000 - Status PENDING - Admin dapat notifikasi

### Admin Flow:
1. Login → Pilih "Lihat Semua Booking"
2. Pilih "Konfirmasi Booking" → Input: BK001
3. Booking dikonfirmasi → Customer dapat notifikasi

---

## Contoh Perhitungan

**Avanza (Rp 15.000/jam) - 2 minggu - Weekly Pricing:**
```
Harga per hari = 15.000 × 20 jam = 300.000
Harga per minggu = 300.000 × 7 hari = 2.100.000
2 minggu = 4.200.000
Diskon 15% = 4.200.000 × 0.85 = Rp 3.570.000

+ Insurance: Rp 50.000
+ GPS: Rp 25.000
─────────────────────────
TOTAL: Rp 3.645.000
```

---

## Struktur

```
├── Strategy Pattern
│   └── PricingStrategy, HourlyPricing, DailyPricing, WeeklyPricing, MonthlyPricing
├── Decorator Pattern
│   └── Rental, BasicRental, InsuranceDecorator, DriverDecorator, GPSDecorator
├── Observer Pattern
│   └── Observer, AdminObserver, CustomerObserver, NotificationSystem
├── Singleton Pattern
│   └── RentalSystemManager
└── Models
    └── Vehicle, User, Booking, Main
```

---

## Fitur

| Admin | Customer |
|-------|----------|
| ✅ Kelola kendaraan | ✅ Browse kendaraan |
| ✅ Konfirmasi/Reject booking | ✅ Buat booking |
| ✅ Laporan pendapatan | ✅ History booking |
| ✅ Notifikasi booking baru | ✅ Notifikasi status |

---

## Keunggulan

- **Scalable**: Mudah tambah pricing/layanan baru
- **Maintainable**: Kode terorganisir, mudah di-debug
- **Flexible**: Customer bebas kombinasi layanan
- **Consistent**: Semua user lihat data yang sama (Singleton)
- **Real-time**: Notifikasi instant (Observer)

---

## Teknologi

- Java 11+
- CLI Interface
- 4 Design Patterns (Strategy, Decorator, Observer, Singleton)

---