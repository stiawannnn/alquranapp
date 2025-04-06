package com.example.utsquranappq.utiils

// Terjemahan nama surah (dari English ke Indonesia)
val surahTranslations = mapOf(
    "Al-Faatiha" to "Al-Fatihah",
    "Al-Baqara" to "Al-Baqarah",
    "Aal-i-Imraan" to "Ali 'Imran",
    "An-Nisaa" to "An-Nisa'",
    "Al-Maaida" to "Al-Ma'idah",
    "Al-An'aam" to "Al-An'am",
    "Al-A'raaf" to "Al-A'raf",
    "Al-Anfaal" to "Al-Anfal",
    "At-Tawba" to "At-Taubah",
    "Yunus" to "Yunus",
    "Hud" to "Hud",
    "Yusuf" to "Yusuf",
    "Ar-Ra'd" to "Ar-Ra'd",
    "Ibrahim" to "Ibrahim",
    "Al-Hijr" to "Al-Hijr",
    "An-Nahl" to "An-Nahl",
    "Al-Israa" to "Al-Isra'",
    "Al-Kahf" to "Al-Kahf",
    "Maryam" to "Maryam",
    "Taa-Haa" to "Ta-Ha",
    "Al-Anbiyaa" to "Al-Anbiya'",
    "Al-Hajj" to "Al-Hajj",
    "Al-Mu’minoon" to "Al-Mu'minun",
    "An-Noor" to "An-Nur",
    "Al-Furqaan" to "Al-Furqan",
    "Ash-Shu'araa" to "Ash-Shu'ara'",
    "An-Naml" to "An-Naml",
    "Al-Qasas" to "Al-Qasas",
    "Al-Ankaboot" to "Al-Ankabut",
    "Ar-Room" to "Ar-Rum",
    "Luqman" to "Luqman",
    "As-Sajda" to "As-Sajdah",
    "Al-Ahzaab" to "Al-Ahzab",
    "Saba" to "Saba'",
    "Faatir" to "Fatir",
    "Yaseen" to "Yasin",
    "As-Saaffaat" to "As-Saffat",
    "Saad" to "Sad",
    "Az-Zumar" to "Az-Zumar",
    "Ghafir" to "Ghafir",
    "Fussilat" to "Fussilat",
    "Ash-Shura" to "Ash-Shura",
    "Az-Zukhruf" to "Az-Zukhruf",
    "Ad-Dukhaan" to "Ad-Dukhan",
    "Al-Jaathiya" to "Al-Jathiyah",
    "Al-Ahqaf" to "Al-Ahqaf",
    "Muhammad" to "Muhammad",
    "Al-Fath" to "Al-Fath",
    "Al-Hujuraat" to "Al-Hujurat",
    "Qaaf" to "Qaf",
    "Adh-Dhaariyat" to "Adh-Dhariyat",
    "At-Tur" to "At-Tur",
    "An-Najm" to "An-Najm",
    "Al-Qamar" to "Al-Qamar",
    "Ar-Rahmaan" to "Ar-Rahman",
    "Al-Waaqia" to "Al-Waqi'ah",
    "Al-Hadid" to "Al-Hadid",
    "Al-Mujaadila" to "Al-Mujadilah",
    "Al-Hashr" to "Al-Hashr",
    "Al-Mumtahana" to "Al-Mumtahanah",
    "As-Saff" to "As-Saff",
    "Al-Jumu'a" to "Al-Jumu'ah",
    "Al-Munaafiqoon" to "Al-Munafiqun",
    "At-Taghaabun" to "At-Taghabun",
    "At-Talaaq" to "At-Talaq",
    "At-Tahrim" to "At-Tahrim",
    "Al-Mulk" to "Al-Mulk",
    "Al-Qalam" to "Al-Qalam",
    "Al-Haaqqa" to "Al-Haqqah",
    "Al-Ma'aarij" to "Al-Ma'arij",
    "Nooh" to "Nuh",
    "Al-Jinn" to "Al-Jinn",
    "Al-Muzzammil" to "Al-Muzzammil",
    "Al-Muddaththir" to "Al-Muddathir",
    "Al-Qiyaama" to "Al-Qiyamah",
    "Al-Insaan" to "Al-Insan",
    "Al-Mursalaat" to "Al-Mursalat",
    "An-Naba" to "An-Naba'",
    "An-Naazi'aat" to "An-Nazi'at",
    "Abasa" to "'Abasa",
    "At-Takwir" to "At-Takwir",
    "Al-Infitaar" to "Al-Infitar",
    "Al-Mutaffifin" to "Al-Mutaffifin",
    "Al-Inshiqaaq" to "Al-Inshiqaq",
    "Al-Burooj" to "Al-Buruj",
    "At-Taariq" to "At-Tariq",
    "Al-A'laa" to "Al-A'la",
    "Al-Ghaashiya" to "Al-Ghashiyah",
    "Al-Fajr" to "Al-Fajr",
    "Al-Balad" to "Al-Balad",
    "Ash-Shams" to "Ash-Shams",
    "Al-Lail" to "Al-Lail",
    "Ad-Duhaa" to "Ad-Duha",
    "Ash-Sharh" to "Ash-Sharh",
    "At-Tin" to "At-Tin",
    "Al-Alaq" to "Al-'Alaq",
    "Al-Qadr" to "Al-Qadr",
    "Al-Bayyina" to "Al-Bayyinah",
    "Az-Zalzala" to "Az-Zalzalah",
    "Al-Aadiyaat" to "Al-'Adiyat",
    "Al-Qaari'a" to "Al-Qari'ah",
    "At-Takaathur" to "At-Takathur",
    "Al-Asr" to "Al-'Asr",
    "Al-Humaza" to "Al-Humazah",
    "Al-Feel" to "Al-Fil",
    "Quraish" to "Quraisy",
    "Al-Maa'oon" to "Al-Ma'un",
    "Al-Kawthar" to "Al-Kawtsar",
    "Al-Kaafiroon" to "Al-Kafirun",
    "An-Nasr" to "An-Nasr",
    "Al-Masad" to "Al-Lahab",
    "Al-Ikhlaas" to "Al-Ikhlas",
    "Al-Falaq" to "Al-Falaq",
    "An-Naas" to "An-Nas"
)

val surahMeaningTranslations = mapOf(
    "The Opening" to "Pembukaan",
    "The Cow" to "Sapi Betina",
    "The Family of Imraan" to "Keluarga Imran",
    "The Women" to "Wanita",
    "The Table" to "Hidangan",
    "The Cattle" to "Binatang Ternak",
    "The Heights" to "Tempat Tertinggi",
    "The Spoils of War" to "Rampasan Perang",
    "The Repentance" to "Tobat",
    "Jonah" to "Yunus",
    "Hud" to "Hud",
    "Joseph" to "Yusuf",
    "The Thunder" to "Guntur",
    "Abraham" to "Ibrahim",
    "The Rock" to "Al-Hijr (Daerah Berbatu)",
    "The Bee" to "Lebah",
    "The Night Journey" to "Perjalanan Malam",
    "The Cave" to "Gua",
    "Mary" to "Maryam",
    "Ta-Ha" to "Tha-Ha",
    "The Prophets" to "Para Nabi",
    "The Pilgrimage" to "Haji",
    "The Believers" to "Orang-Orang Mukmin",
    "The Light" to "Cahaya",
    "The Criterion" to "Pembeda",
    "The Poets" to "Penyair",
    "The Ant" to "Semut",
    "The Stories" to "Kisah-Kisah",
    "The Spider" to "Laba-Laba",
    "The Romans" to "Bangsa Romawi",
    "Luqman" to "Luqman",
    "The Prostration" to "Sujud",
    "The Clans" to "Golongan yang Bersekutu",
    "Sheba" to "Saba'",
    "The Originator" to "Pencipta",
    "Yaseen" to "Yasin",
    "Those drawn up in Ranks" to "Yang Bersaf-Saf",
    "Saad" to "Shaad",
    "The Troops" to "Rombongan",
    "The Forgiver" to "Yang Maha Pengampun",
    "Explained in Detail" to "Yang Dijelaskan",
    "The Consultation" to "Musyawarah",
    "The Ornaments of Gold" to "Perhiasan",
    "The Smoke" to "Kabut",
    "The Kneeling" to "Yang Berlutut",
    "The Wind-Curved Sandhills" to "Bukit Pasir",
    "Muhammad" to "Muhammad",
    "The Victory" to "Kemenangan",
    "The Rooms" to "Kamar-Kamar",
    "Qaf" to "Qaaf",
    "The Winnowing Winds" to "Angin yang Menerbangkan",
    "The Mount" to "Bukit",
    "The Star" to "Bintang",
    "The Moon" to "Bulan",
    "The Beneficent" to "Yang Maha Pengasih",
    "The Inevitable" to "Hari yang Pasti Terjadi",
    "The Iron" to "Besi",
    "The Pleading Woman" to "Wanita yang Mengajukan Gugatan",
    "The Exile" to "Pengusiran",
    "She that is to be examined" to "Wanita yang Diuji",
    "The Ranks" to "Barisan",
    "The Congregation" to "Jumat",
    "The Hypocrites" to "Orang-Orang Munafik",
    "Mutual Loss and Gain" to "Saling Rugi dan Untung",
    "The Divorce" to "Talak",
    "The Prohibition" to "Larangan",
    "The Sovereignty" to "Kerajaan",
    "The Pen" to "Pena",
    "The Reality" to "Kenyataan",
    "The Ascending Stairways" to "Tempat-Tempat Naik",
    "Noah" to "Nuh",
    "The Jinn" to "Jin",
    "The Enshrouded One" to "Yang Berselimut",
    "The Cloaked One" to "Yang Berkemul",
    "The Resurrection" to "Kebangkitan",
    "The Man" to "Manusia",
    "The Emissaries" to "Para Utusan",
    "The Tidings" to "Berita Besar",
    "Those who drag forth" to "Malaikat yang Mencabut",
    "He frowned" to "Ia Bermuka Masam",
    "The Overthrowing" to "Yang Membalikkan",
    "The Cleaving" to "Terbelah",
    "The Defrauding" to "Orang yang Curang",
    "The Sundering" to "Perpecahan",
    "The Mansions of the Stars" to "Tempat-Tempat Bintang",
    "The Nightcomer" to "Yang Datang di Malam Hari",
    "The Most High" to "Yang Maha Tinggi",
    "The Overwhelming" to "Hari yang Menggentarkan",
    "The Dawn" to "Fajar",
    "The City" to "Negeri",
    "The Sun" to "Matahari",
    "The Night" to "Malam",
    "The Morning Brightness" to "Waktu Dhuha",
    "The Relief" to "Kelapangan",
    "The Fig" to "Buah Tin",
    "The Clot" to "Segumpal Darah",
    "The Power" to "Kemuliaan",
    "The Clear Proof" to "Bukti yang Nyata",
    "The Earthquake" to "Kegoncangan",
    "The Courser" to "Kuda Perang",
    "The Calamity" to "Hari yang Menghebohkan",
    "Rivalry in world increase" to "Bermegah-Megahan",
    "The Declining Day" to "Masa",
    "The Traducer" to "Pengumpat",
    "The Elephant" to "Gajah",
    "Quraysh" to "Quraisy",
    "The Small kindnesses" to "Barang-Barang Kecil",
    "The Abundance" to "Nikmat yang Berlimpah",
    "The Disbelievers" to "Orang-Orang Kafir",
    "The Victory" to "Kemenangan",
    "The Palm Fiber" to "Sabut",
    "The Sincerity" to "Ikhlas",
    "The Daybreak" to "Waktu Subuh",
    "Mankind" to "Manusia"
)



// Terjemahan jenis wahyu (Meccan → Makkiyah, Medinan → Madaniyah)
val revelationTranslations = mapOf(
    "Meccan" to "Makkiyah",
    "Medinan" to "Madaniyah"
)
fun getTranslation(englishName: String, englishTranslation: String, revelationType: String): Triple<String, String, String> {
    val surahIndo = surahTranslations[englishName] ?: englishName
    val meaningIndo = surahMeaningTranslations[englishTranslation] ?: englishTranslation
    val revelationIndo = revelationTranslations[revelationType] ?: revelationType

    return Triple(surahIndo, meaningIndo, revelationIndo)
}


