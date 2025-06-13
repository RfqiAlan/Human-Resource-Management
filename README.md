# Human Resource Management

Aplikasi sederhana untuk manajemen sumber daya manusia (HRM) berbasis Java & JavaFX, menyediakan fitur absensi, pengajuan cuti, serta manajemen gaji dan karyawan.  


---

## ✨ Fitur Aplikasi

- **Login Multi-Role**  
  Pengguna dapat login sebagai admin atau karyawan, dengan tampilan dan fitur yang berbeda sesuai perannya.

- **Manajemen Karyawan (Admin)**
    - Mendaftarkan karyawan baru beserta gaji per hari.
    - Melihat daftar absensi semua karyawan.
    - Melihat dan mengelola permohonan cuti (menyetujui/menolak).
    - Menghapus data cuti yang sudah disetujui.

- **Absensi (Karyawan)**
    - Melakukan absen harian (hanya bisa satu kali per hari).
    - Melihat riwayat absensi sendiri lengkap dengan keterangan (terlambat/tepat waktu).

- **Pengajuan & Status Cuti (Karyawan)**
    - Mengajukan cuti dengan alasan yang dapat dicatat di sistem.
    - Melihat status pengajuan cuti (proses, disetujui, ditolak).

- **Perhitungan Gaji Otomatis (Karyawan)**
    - Melihat total hari kerja & total gaji yang sudah didapat berdasarkan jumlah kehadiran.

- **Marquee/Running Text**
    - Teks berjalan di dashboard untuk informasi penting.

---

## 📦 Struktur Kode

```
.
├── controller/      # Berisi semua controller untuk JavaFX (AdminController, KaryawanController, dsb)
├── model/           # Berisi model data utama (AbsensiRecord, CutiItem, CutiDisetujuiRecord, User, Karyawan, Admin, dsb)
├── util/            # Utility/helper class, misal koneksi database
├── view/            # File FXML tampilan (login.fxml, admin.fxml, karyawan.fxml, dsb)
├── database/        # Database(SQLite)
└── Main.java        # Entry point aplikasi
```

---

## 🚀 Cara Menjalankan Aplikasi

### 1. **Persiapan**

- Pastikan sudah terpasang **Java JDK 11/17+** dan **JavaFX SDK**.
- Siapkan **database SQLite** (file atau script sudah tersedia jika ada di folder `database/`).
- Pastikan semua dependency sudah tersedia (JavaFX, JDBC driver SQLite jika diperlukan).

## ▶️ Cara Menjalankan (JavaFX Gradle)

1. Pastikan Java dan JavaFX SDK telah diinstal.
2. Jalankan aplikasi via Gradle:
```bash
  gradle run
```


## 🏛️ Penerapan 4 Pilar OOP

Aplikasi ini didesain menerapkan **4 pilar OOP** secara nyata, terutama pada bagian model data (`model/`):

### 1. **Enkapsulasi (Encapsulation)**
- Field pada class model *private* & diakses lewat getter/setter.
- Contoh:
  ```java
  private String alasan;
  public String getAlasan() {
   return alasan; 
  }
  ```

### 2. **Pewarisan (Inheritance)**
- Ada class dasar (misal `UserBase` atau `RecordBase`) yang diturunkan ke class lain (`Karyawan`, `Admin`, `AbsensiRecord`, dsb).
- Contoh:
  ```java
  public class Karyawan extends UserBase { ... }
  ```

### 3. **Polimorfisme (Polymorphism)**
- Method yang sama (`toString` atau method lain) di-*override* pada masing-masing turunan.
- Contoh:
  ```java
  @Override
  public String toString() {
      return "Karyawan: " + username;
  }
  ```

### 4. **Abstraksi (Abstraction)**
- Ada abstract class (`UserBase`, `RecordBase`) yang hanya mendefinisikan method/kontrak umum, tanpa implementasi detail.
- Turunan wajib mengimplementasikan method tersebut.
- Contoh:
  ```java
  public abstract double getGajiPerHari();
  ```

---

## Tim Pengembang

**Kelompok 13**
- RIFQI ALAN MAULANA  - H071241040
- ANGEL CATRINA SOBBU - H071241094
- TRIVANIA BULI KAROMA - H071241039

---

