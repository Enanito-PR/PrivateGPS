package com.example.data

import com.example.model.CityLocation

object WorldCitiesRepository {

    val continents = listOf("Todos", "Europa", "América", "Asia", "Oceanía", "África")

    val defaultCities: List<CityLocation> = listOf(
        // Europa
        CityLocation("madrid", "Madrid", "España", "Europa", 40.416775, -3.703790, 667.0, "🇪🇸"),
        CityLocation("barcelona", "Barcelona", "España", "Europa", 41.3874, 2.1686, 12.0, "🇪🇸"),
        CityLocation("paris", "París", "Francia", "Europa", 48.8566, 2.3522, 35.0, "🇫🇷"),
        CityLocation("london", "Londres", "Reino Unido", "Europa", 51.5074, -0.1278, 25.0, "🇬🇧"),
        CityLocation("berlin", "Berlín", "Alemania", "Europa", 52.5200, 13.4050, 34.0, "🇩🇪"),
        CityLocation("rome", "Roma", "Italia", "Europa", 41.9028, 12.4964, 21.0, "🇮🇹"),
        CityLocation("amsterdam", "Ámsterdam", "Países Bajos", "Europa", 52.3676, 4.9041, -2.0, "🇳🇱"),
        CityLocation("zurich", "Zúrich", "Suiza", "Europa", 47.3769, 8.5417, 408.0, "🇨🇭"),
        CityLocation("lisbon", "Lisboa", "Portugal", "Europa", 38.7223, -9.1393, 15.0, "🇵🇹"),
        CityLocation("vienna", "Viena", "Austria", "Europa", 48.2082, 16.3738, 171.0, "🇦🇹"),

        // América
        CityLocation("newyork", "Nueva York", "Estados Unidos", "América", 40.7128, -74.0060, 10.0, "🇺🇸"),
        CityLocation("losangeles", "Los Ángeles", "Estados Unidos", "América", 34.0522, -118.2437, 71.0, "🇺🇸"),
        CityLocation("miami", "Miami", "Estados Unidos", "América", 25.7617, -80.1918, 2.0, "🇺🇸"),
        CityLocation("mexicocity", "Ciudad de México", "México", "América", 19.4326, -99.1332, 2240.0, "🇲🇽"),
        CityLocation("buenosaires", "Buenos Aires", "Argentina", "América", -34.6037, -58.3816, 25.0, "🇦🇷"),
        CityLocation("bogota", "Bogotá", "Colombia", "América", 4.7110, -74.0721, 2640.0, "🇨🇴"),
        CityLocation("lima", "Lima", "Perú", "América", -12.0464, -77.0428, 154.0, "🇵🇪"),
        CityLocation("santiago", "Santiago", "Chile", "América", -33.4489, -70.6693, 570.0, "🇨🇱"),
        CityLocation("saopaulo", "São Paulo", "Brasil", "América", -23.5505, -46.6333, 760.0, "🇧🇷"),
        CityLocation("toronto", "Toronto", "Canadá", "América", 43.6532, -79.3832, 76.0, "🇨🇦"),

        // Asia
        CityLocation("tokyo", "Tokio", "Japón", "Asia", 35.6762, 139.6503, 40.0, "🇯🇵"),
        CityLocation("seoul", "Seúl", "Corea del Sur", "Asia", 37.5665, 126.9780, 38.0, "🇰🇷"),
        CityLocation("singapore", "Singapur", "Singapur", "Asia", 1.3521, 103.8198, 15.0, "🇸🇬"),
        CityLocation("hongkong", "Hong Kong", "China", "Asia", 22.3193, 114.1694, 9.0, "🇭🇰"),
        CityLocation("dubai", "Dubái", "Emiratos Árabes", "Asia", 25.2048, 55.2708, 16.0, "🇦🇪"),
        CityLocation("bangkok", "Bangkok", "Tailandia", "Asia", 13.7563, 100.5018, 1.5, "🇹🇭"),
        CityLocation("istanbul", "Estambul", "Turquía", "Asia", 41.0082, 28.9784, 40.0, "🇹🇷"),

        // Oceanía
        CityLocation("sydney", "Sídney", "Australia", "Oceanía", -33.8688, 151.2093, 19.0, "🇦🇺"),
        CityLocation("melbourne", "Melbourne", "Australia", "Oceanía", -37.8136, 144.9631, 31.0, "🇦🇺"),
        CityLocation("auckland", "Auckland", "Nueva Zelanda", "Oceanía", -36.8485, 174.7633, 20.0, "🇳🇿"),

        // África
        CityLocation("cairo", "El Cairo", "Egipto", "África", 30.0444, 31.2357, 23.0, "🇪🇬"),
        CityLocation("capetown", "Ciudad del Cabo", "Sudáfrica", "África", -33.9249, 18.4241, 12.0, "🇿🇦"),
        CityLocation("casablanca", "Casablanca", "Marruecos", "África", 33.5731, -7.5898, 27.0, "🇲🇦")
    )
}
