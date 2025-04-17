AlQuranApp
Selamat datang di AlQuranApp, aplikasi mobile open-source yang dirancang untuk memperkaya pengalaman spiritual Anda bersama Al-Qur'an. Dengan antarmuka intuitif dan ramah pengguna, aplikasi ini menyediakan akses mudah ke ayat-ayat suci, terjemahan, tilawah audio, serta fitur pendukung seperti jadwal sholat dan arah kiblat.
Fitur Utama

Membaca Al-Qur'an:
Jelajahi 114 Surah dan 30 Juz.
Teks Arab dengan tajwid berwarna, transliterasi Latin, dan panduan baca tajwid.


Terjemahan:
Terjemahan akurat dalam bahasa Indonesia.


Tilawah Audio:
Dengarkan bacaan merdu dari berbagai Qari terkenal, tersedia per Surah atau Ayah.
Sorotan berwarna untuk mengikuti tilawah.


Pencarian Cerdas:
Temukan Surah, Ayah, atau Juz dengan cepat menggunakan kata kunci atau nomor.


Terakhir Dibaca:
Lanjutkan membaca dari ayat terakhir dengan fitur auto-scrolling.


Bookmark:
Simpan ayat favorit untuk akses cepat.


Jadwal Sholat:
Lihat jadwal sholat (saat ini hanya untuk wilayah Pekanbaru).


Arah Kiblat:
Tentukan arah kiblat berdasarkan lokasi Anda.


Notifikasi:
Pengingat baca Al-Qur'an harian yang dapat disesuaikan.
Notifikasi adzan otomatis (saat ini untuk waktu Pekanbaru dan sekitarnya).



Panduan Penggunaan
Berikut cara menggunakan fitur-fitur utama AlQuranApp:

Jelajahi Surah: Buka menu utama untuk memilih Surah atau Juz yang ingin dibaca.
Panduan Tajwid: Akses panduan membaca Al-Qur'an dengan tajwid di menu "Tajweed".
Cari Ayat: Gunakan fitur pencarian untuk menemukan ayat berdasarkan kata kunci atau nomor ayat.
Dengarkan Tilawah: Ketuk ikon audio untuk memutar tilawah. Untuk tilawah per Ayah di detail Juz, klik ayat hingga muncul dropdown, lalu pilih opsi audio. (Catatan: Jika audio tidak langsung berputar, coba beberapa kali karena potensi latensi jaringan.)
Bookmark Ayat: Klik ayat hingga muncul dropdown, pilih "Bookmark" untuk menyimpan. Lihat ayat tersimpan di menu "Bookmark" dengan navigasi langsung ke ayat tujuan.
Atur Notifikasi: Sesuaikan pengingat harian atau adzan (aktif/nonaktif) di menu pengaturan.
Cek Arah Kiblat: Aktifkan fitur kompas di menu utama untuk menentukan arah kiblat.

Teknologi yang Digunakan

Bahasa: Kotlin
Frontend: Jetpack Compose untuk antarmuka modern dan responsif
API: AlQuran.cloud API untuk data Al-Qur'an yang andal
Penjadwalan Tugas: WorkManager untuk tugas latar belakang seperti notifikasi adzan dan pengingat harian (menggunakan SQLite internal untuk penjadwalan)
Penyimpanan: Tidak menggunakan database untuk data Al-Qur'an; data offline disimpan sebagai file lokal
IDE: Android Studio (direkomendasikan untuk pengembangan)

Prasyarat
Sebelum memulai, pastikan Anda memiliki:

Android Studio (versi terbaru, misalnya Koala atau lebih baru)
JDK (versi 17 atau lebih baru)
Perangkat Android (atau emulator) dengan API level 31 (Android 12) atau lebih tinggi
Koneksi internet untuk mengakses API Al-Qur'an
Git untuk mengkloning repositori

Instalasi
Ikuti langkah-langkah berikut untuk menjalankan AlQuranApp secara lokal:

Klon Repositori:
git clone https://github.com/stiawannnn/alquranapp.git
cd alquranapp


Buka di Android Studio:

Buka Android Studio, pilih File > Open, lalu navigasikan ke folder alquranapp.
Tunggu hingga Gradle menyelesaikan sinkronisasi proyek. (Catatan: Jika terjadi error seperti Expected BEGIN_ARRAY but was BEGIN_OBJECT, periksa model data di SurahDetailViewModel agar sesuai dengan respons API AlQuran.cloud.)


Jalankan Aplikasi:

Hubungkan perangkat Android atau jalankan emulator dengan API level 31 atau lebih tinggi.
Klik tombol Run (ikon segitiga hijau) di Android Studio untuk membangun dan menjalankan aplikasi.


Verifikasi Instalasi:

Pastikan aplikasi berjalan di perangkat/emulator dan dapat mengakses data Al-Qur'an melalui API.




Catatan: Jika mengalami masalah Gradle, periksa versi Gradle di file build.gradle dan pastikan kompatibel. Untuk error API, gunakan alat seperti Postman untuk memeriksa respons atau tambahkan logika retry untuk menangani error seperti 429 (Too Many Requests).

Kontribusi
Kami mengundang Anda untuk berkontribusi pada AlQuranApp demi memperluas manfaatnya. Untuk memulai:

Fork repositori ini.

Buat branch baru:
git checkout -b fitur/nama-fitur


Lakukan perubahan dan commit:
git commit -m "Menambahkan fitur baru"


Push ke branch Anda:
git push origin fitur/nama-fitur


Buka Pull Request di GitHub.



Melaporkan Masalah
Jika menemukan bug atau memiliki saran fitur, silakan buka isu di GitHub Issues.
Lisensi
Proyek ini dilisensikan di bawah Lisensi MIT. Lihat file LICENSE untuk detail.
