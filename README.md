# 📱 AlQuranApp – Aplikasi Al-Qur'an Android

**AlQuranApp** adalah aplikasi mobile open-source yang dirancang untuk memperkaya pengalaman spiritual Anda dalam membaca dan mendengarkan Al-Qur'an. Dibangun dengan **Kotlin** dan **Jetpack Compose**, aplikasi ini menyuguhkan antarmuka modern, performa ringan, serta fitur-fitur islami yang kaya dan bermanfaat.

---

## ✨ Fitur Unggulan

- 📖 **Baca Al-Qur'an**  
  Jelajahi seluruh 114 Surah dan 30 Juz lengkap dengan:
  - Teks Arab dengan **tajwid berwarna**
  - **Transliterasi Latin** untuk membantu pelafalan
  - **Terjemahan Bahasa Indonesia** yang jelas

- 🔊 **Tilawah Audio**  
  Dengarkan bacaan merdu dari Qari favorit Anda. Bisa diputar:
  - Per ayat
  - Per surah
  - Fitur "Play All" untuk pemutaran berkelanjutan

- ⭐ **Bookmark Ayat**  
  Tandai ayat favorit untuk dibaca ulang kapan saja.

- 📌 **Riwayat Bacaan Otomatis**  
  Lanjutkan dari posisi terakhir tanpa harus mengingat sendiri.

- 🔍 **Pencarian Cerdas**  
  Temukan ayat, surah, atau juz dengan mudah berdasarkan kata kunci atau nomor.

- 🕌 **Jadwal Sholat Harian** *(lokasi: Pekanbaru)*  
  Dapatkan pengingat waktu sholat sesuai zona waktu lokal.

- 🧭 **Arah Kiblat Real-Time**  
  Gunakan kompas digital untuk menentukan arah kiblat.

- 🔔 **Notifikasi Islami**  
  Ingatkan Anda untuk membaca Al-Qur'an dan jadwal adzan otomatis.

---

## 🔧 Teknologi yang Digunakan

| Komponen          | Teknologi                          |
| ----------------- | ---------------------------------- |
| Bahasa            | Kotlin                             |
| UI                | Jetpack Compose, Material 3        |
| Arsitektur        | MVVM                               |
| Navigasi          | Jetpack Navigation Compose         |
| Network           | Retrofit, Gson,                    |
| Audio             | MediaPlayer                        |
| Database          | Room (bookmark)                    |
| Background Tasks  | WorkManager                        |
| save Last Reads   | SharedPreferences                  |
| Gambar            | Drawable bawaan                    |

---

## 🚀 Cara Menjalankan Proyek

1. **Clone Repositori**

   ```bash
   git clone https://github.com/stiawannnnh/alquranapp.git
   cd alquranapp
   ```

2. **Buka dengan Android Studio**

   Jalankan Android Studio → `Open an Existing Project` → arahkan ke folder `alquranapp`

3. **Sync & Jalankan**

   Sinkronkan dependensi Gradle → pilih emulator atau perangkat fisik → klik **Run** ▶️

---

## 🧭 Panduan Penggunaan

Berikut cara menggunakan fitur-fitur utama **AlQuranApp**:

- 📚 **Jelajahi Surah**  
  Buka menu utama untuk memilih Surah atau Juz yang ingin dibaca.

- 📘 **Panduan Tajwid**  
  Akses panduan membaca Al-Qur'an dengan tajwid di menu "Tajweed".

- 🔍 **Cari Ayat**  
  Gunakan fitur pencarian untuk menemukan ayat berdasarkan kata kunci atau nomor ayat.

- 🔊 **Dengarkan Tilawah**  
  Ketuk ikon audio untuk memutar tilawah. Untuk tilawah per Ayah di detail Juz, klik ayat hingga muncul dropdown, lalu pilih opsi audio.  
  *(Catatan: Jika audio tidak langsung berputar, coba beberapa kali karena potensi latensi jaringan.)*

- ⭐ **Bookmark Ayat**  
  Klik ayat hingga muncul dropdown, pilih "Bookmark" untuk menyimpan. Lihat ayat tersimpan di menu "Bookmark" dengan navigasi langsung ke ayat tujuan.

- 🔔 **Atur Notifikasi**  
  Sesuaikan pengingat harian atau adzan (aktif/nonaktif) di menu pengaturan.

- 🧭 **Cek Arah Kiblat**  
  Aktifkan fitur kompas di menu utama untuk menentukan arah kiblat.

---

## ⚙️ Prasyarat

Sebelum memulai, pastikan Anda memiliki:

- ✅ Android Studio (versi terbaru, misalnya Koala atau lebih baru)
- ✅ JDK (versi 17 atau lebih baru)
- ✅ Perangkat Android (atau emulator) dengan API level 31 (Android 12) atau lebih tinggi
- ✅ Koneksi internet untuk mengakses API Al-Qur'an
- ✅ Git untuk mengkloning repositori

---

## 🤝 Kontribusi & Fork

Ingin ikut kontribusi atau membuat versi modifikasi? Silakan!

🔧 **Untuk Fork & Modifikasi:**
- Klik tombol `⭐ Star` jika Anda suka proyek ini
- Klik tombol `Fork` (pojok kanan atas GitHub)
- Setelah fork:
  ```bash
  git clone https://github.com/USERNAME/alquranapp.git
  ```
- Mulai sesuaikan atau eksplorasi fitur sesuai kebutuhan

📥 **Untuk Kontribusi (Pull Request):**
- Lakukan perubahan pada branch baru
- Buka pull request ke repositori utama
- Jelaskan perubahan Anda secara singkat dan jelas

Kami terbuka untuk perbaikan bug, fitur baru, atau peningkatan dokumentasi 🙌

---

## 👤 Kontak Developer

- 📧 Email: [12350113006@students.uin-suska.ac.id](mailto:12350113006@students.uin-suska.ac.id)
- 🧑‍💻 GitHub: [stiawannnnn](https://github.com/stiawannnnn)

---

Terima kasih telah menggunakan **AlQuranApp**. Semoga aplikasi ini menjadi wasilah kebaikan, ilmu, dan pahala bagi semua yang menggunakannya maupun yang turut mengembangkan. 🌙📿

