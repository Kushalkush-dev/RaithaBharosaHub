package com.raithabharosa.hub

import android.content.Context

object TranslationManager {
    private val englishToKannada = mapOf(
        // Onboarding
        "Welcome Farmer!" to "ಸ್ವಾಗತ ರೈತ!",
        "Enter your details to get started" to "ಪ್ರಾರಂಭಿಸಲು ನಿಮ್ಮ ವಿವರಗಳನ್ನು ನಮೂಸಿ",
        "Your Name" to "ನಿಮ್ಮ ಹೆಸರು",
        "Location (e.g., Mysore, Karnataka)" to "ಸ್ಥಳ (ಉದಾ. ಮೈಸೂರು, ಕರ್ನಾಟಕ)",
        "Plot Size (Acres)" to "ಜಮ್ಮಾ ಗಾತ್ರ (ಎಕರೆ)",
        "Select Your Crop" to "ನಿಮ್ಮ ಬೆಳೆಯನ್ನು ಆರಿಸಿ",
        "Get Started" to "ಪ್ರಾರಂಭಿಸಿ",
        
        // Crops
        "Sugarcane" to "ಕಬ್ಬು",
        "Ragi" to "ರಾಗಿ",
        "Paddy" to "ಬತ್ತ",
        "Wheat" to "ಗೋಧಿ",
        "Maize" to "ಮೆಕ್ಸ್",
        
        // Dashboard
        "Welcome!" to "ಸ್ವಾಗತ!",
        "Sowing Index" to "ಬಿತ್ತಲು ಸೂಚ್ಯಂಕ",
        "GO" to "ಮುಂದುವರಿಸಿ",
        "CAUTION" to "ಎಚ್ಚರಿಕೆ",
        "WAIT" to "ನಿರೀಕ್ಷಿಸಿ",
        "STOP" to "ನಿಲ್ಲಿಸಿ",
        "Current Weather" to "ಪ್ರಸ್ತುತ ಹವಾಮಾನ",
        "Recommendation" to "ಶಿಫಾರಸು",
        
        // Weather conditions
        "Clear" to "ಸ್ಪಷ್ಟ",
        "Clouds" to "ಮೋಡ",
        "Rain" to "ಮಳೆ",
        "Storm" to "	bಹೂಬಲ",
        
        // Input
        "Input Center" to "ಇನ್ಪುಟ್ ಸೆಂಟರ್",
        "Enter soil test results" to "ಮಣ್ಣಿನ ಪರೀಕ್ಷಾ ಫಲಿತಾಂಶಗಳನ್ನು ನಮೂಸಿ",
        "Soil Moisture (%)" to "ಮಣ್ಣಿನ ತೇವಾಂಶ (%)",
        "Nitrogen - N (kg/ha)" to "ಸಾರಜನಕ - N (kg/ha)",
        "Phosphorus - P (kg/ha)" to "ರಂಜಕ - P (kg/ha)",
        "Potassium - K (kg/ha)" to "ಪೊಟ್ಯಾಷಿಯಮ್ - K (kg/ha)",
        "Temperature (C)" to "ತಾಪಮಾನ (C)",
        "Save" to "ಉಳಿಸಿ",
        "Saved!" to "ಉಳಿತಾಯಿತು!",
        
        // Calendar
        "Krishi Calendar" to "ಕೃಷಿ ಕ್ಯಾಲೆಂಡರ್",
        "7-Day Action Plan" to "7 ದಿನಗಳ ಕಾರ್ಯಯೋಜನೆ",
        "Finish fertilization TODAY" to "ಇಂದೇ ಗೊಬ್ಬರಿಸುವಿಕೆ ಮುಗಿಸಿ",
        "Monitor conditions" to "ಸ್ಥಿತಿಯನ್ನು ಮೇಲ್ವಿಚಾರಿಸಿ",
        "Prepare seeds" to "ಬೀಜಗಳನ್ನು ಸಿದ್ಧಪಡಿಸಿ",
        "Apply fertilizer" to "ಗೊಬ್ಬರಿಸಿ",
        "Prepare field" to "ಜಮ್ಮಾ ಸಿದ್ಧಪಡಿಸಿ",
        "Sow seeds" to "ಬಿತ್ತಿ",
        "Water field" to "ನೀರು ಹಾಕಿ",
        "Check germination" to "ಮೊಳೆಯುವಿಕೆ ಪರೀಕ್ಷಿಸಿ",
        "Rest day" to "ವಿಶ್ರಮಿಸುವ ದಿನ",
        "Delay field work" to "ಜಮ್ಮ�� ಕಾರ್ಯ ಮುಂದೂಡಿ",
        
        // History
        "History" to "ಇತಿಹಾಸ",
        "Previous Seasons" to "ಹಿಂದಿನ ಋತುಗಳು",
        "No history yet" to "ಇನ್ನೂ ಇತಿಹಾಸ ಇಲ್ಲ",
        "Your crop history will appear here" to "ನಿಮ್ಮ ಬೆಳೆ ಇತಿಹಾಸ ಇಲ್ಲಿ ಕಾಣಿಸುತ್ತದೆ",
        "Sowed" to "ಬಿತ್ತ",
        "Yield" to "ಇಳುವರಿ",
        "Notes" to "ಟಿಪ್ಪಣಿಗಳು",
        
        // General
        "Refresh" to "ತಾಳೆಮರುವ",
        "Temperature" to "ತಾಪಮಾನ",
        "Humidity" to "ಆರ್ದ್ರತೆ",
        "Weather" to "ಹವಾಮಾನ",
        "Monday" to "ಸೋಮವಾರ",
        "Tuesday" to "ಮಂಗಳವಾರ",
        "Wednesday" to "ಬುಧವಾರ",
        "Thursday" to "ಗುರುವಾರ",
        "Friday" to "ಶುಕ್ರವಾರ",
        "Saturday" to "ಶನಿವಾರ",
        "Sunday" to "ಭಾನುವಾರ",
        
        // Reasons
        "Moisture is optimal" to "ತೇವಾಂಶ ಸೂಕ್ಷ್ಮ",
        "Soil moisture too high" to "ಮಣ್ಣಿನ ತೇವಾಂಶ ಹೆಚ್ಚಾಗಿದೆ",
        "Soil moisture too low" to "ಮಣ್ಣಿನ ತೇವಾಂಶ ಕಡಿಮೆ",
        "Temperature is optimal" to "ತಾಪಮಾನ ಸೂಕ್ಷ್ಮ",
        "Temperature too high" to "ತಾಪಮಾನ ಹೆಚ್ಚಾಗಿದೆ",
        "Temperature too low" to "ತಾಪಮಾನ ಕಡಿಮೆ",
        "Weather is clear" to "ಹವಾಮಾನ ಸ್ಪಷ್ಟ",
        "Bad weather" to "ಕೆಟ್ಟ ಹವಾಮಾನ",
        "Cloudy conditions" to "ಮೋಡ ಸ್ಥಿತಿ",
        "Storm warning" to "ಬಿರುಗಣ್ಣಿನ ಎಚ್ಚರಿಕೆ",
        "Nitrogen adequate" to "ಸಾರಜನಕ ಸಾಕಷ್ಟು",
        "Nitrogen deficient" to "ಸಾರಜನಕ ಕೊರತೆ",
        "Phosphorus adequate" to "ರಂಜಕ ಸಾಕಷ್ಟು",
        "Phosphorus deficient" to "ರಂಜಕ ಕೊರತೆ",
        "Potassium adequate" to "ಪೊಟ್ಯಾಷಿಯಮ್ ಸಾಕಷ್ಟು",
        "Potassium deficient" to "ಪೊಟ್ಯಾಷಿಯಮ್ ಕೊರತೆ",

        // Trend Screen
        "Trends" to "ಪ್ರವೃತ್ತಿಗಳು",
        "Soil Health Trends" to "ಮಣ್ಣಿನ ಆರೋಗ್ಯ ಪ್ರವೃತ್ತಿಗಳು",
        "Weather Trends" to "ಹವಾಮಾನ ಪ್ರವೃತ್ತಿಗಳು",
        "Yield Trends" to "ಇಳುವರಿ ಪ್ರವೃತ್ತಿಗಳು",
        "Last 30 Days" to "ಕಳೆದ 30 ದಿನಗಳು",
        "Last 6 Months" to "ಕಳೆದ 6 ತಿಂಗಳುಗಳು",
        "Last 12 Months" to "ಕಳೆದ 12 ತಿಂಗಳುಗಳು",
        "Nitrogen (N)" to "ಸಾರಜನಕ (N)",
        "Phosphorus (P)" to "ರಂಜಕ (P)",
        "Potassium (K)" to "ಪೊಟ್ಯಾಷಿಯಮ್ (K)",
        "Soil Moisture" to "ಮಣ್ಣಿನ ತೇವಾಂಶ",
        "Temperature" to "ತಾಪಮಾನ",
        "Humidity" to "ಆರ್ದ್ರತೆ",
        "No data available" to "ಯಾವುದೇ ಡೇಟಾ ಲಭ್ಯವಿಲ್ಲ",
        "Add soil data to see trends" to "ಪ್ರವೃತ್ತಿಗಳನ್ನು ನೋಡಲು ಮಣ್ಣಿನ ಡೇಟಾ ಸೇರಿಸಿ",
        "kg/ha" to "kg/ha",
        "%" to "%",
        "No yield records yet" to "ಇನ್ನೂ ಇಳುವರಿ ದಾಖಲೆಗಳಿಲ್ಲ",
        "Record your first harvest" to "ನಿಮ್ಮ ಮೊದಲ ಕೊಯ್ಲನ್ನು ದಾಖಲಿಸಿ",
        "Filter by Crop" to "ಬೆಳೆಯಿಂದ ಫಿಲ್ಟರ್ ಮಾಡಿ",
        "Time Period" to "ಸಮಯಾವಧಿ",
        "No data for selected period" to "ಆಯ್ಕೆಮಾಡಿದ ಅವಧಿಯಲ್ಲಿ ಯಾವುದೇ ಡೇಟಾ ಇಲ್ಲ",

        // Input Screen
        "Soil Input Center" to "ಮಣ್ಣಿನ ಇನ್ಪುಟ್ ಸೆಂಟರ್",
        "Enter soil test results to get accurate recommendations" to "ನಿಖರ ಶಿಫಾರಸುಗಳನ್ನು ಪಡೆಯಲು ಮಣ್ಣಿನ ಪರೀಕ್ಷಾ ಫಲಿತಾಂಶಗಳನ್ನು ನಮೂಸಿ",
        "e.g., 25" to "ಉದಾ. 25",
        "e.g., 60" to "ಉದಾ. 60",
        "e.g., 30" to "ಉದಾ. 30",
        "Clear" to "ಅಳಿಸಿ",
        "Note: Leave fields empty to use default values. Clear resets all values." to "ಸೂಚನೆ: ಪೂರ್ವನಿರ್ಧರಿತ ಮೌಲ್ಯಗಳನ್ನು ಬಳಸಲು ಫೀಲ್ಡ್‌ಗಳನ್ನು ಖಾಲಿ ಬಿಡಿ. ಅಳಿಸಿ ಎಲ್ಲಾ ಮೌಲ್ಯಗಳನ್ನು ಮರುಹೊಂದಿಸುತ್ತದೆ.",
        "pH" to "pH",
        "Soil Moisture (%)" to "ಮಣ್ಣಿನ ತೇವಾಂಶ (%)",
        "Phosphorus - P (kg/ha)" to "ರಂಜಕ - P (kg/ha)",

        // Calendar Screen
        "7-Day Action Plan" to "7 ದಿನಗಳ ಕಾರ್ಯಯೋಜನೆ",
        "URGENT" to "ತುರ್ತು",

        // History Screen (My Crops)
        "My Crops" to "ನನ್ನ ಬೆಳೆಗಳು",
        "Manage your farm profiles" to "ನಿಮ್ಮ ಜಮ್ಮಾ ಪ್ರೊಫೈಲ್‌ಗಳನ್ನು ನಿರ್ವಹಿಸಿ",
        "No crops yet" to "ಇನ್ನೂ ಬೆಳೆಗಳಿಲ್ಲ",
        "Tap + to add your first crop" to "ನಿಮ್ಮ ಮೊದಲ ಬೆಳೆಯನ್ನು ಸೇರಿಸಲು + ಟ್ಯಾಪ್ ಮಾಡಿ",
        "Active" to "ಸಕ್ರಿಯ",
        "Add Crop" to "ಬೆಳೆ ಸೇರಿಸಿ",
        "Add New Crop" to "ಹೊಸ ಬೆಳೆ ಸೇರಿಸಿ",
        "Crop Name" to "ಬೆಳೆಯ ಹೆಸರು",
        "e.g., North Field" to "ಉದಾ. ಉತ್ತರ ಜಮ್ಮಾ",
        "Crop Type" to "ಬೆಳೆಯ ಪ್ರಕಾರ",
        "Select Crop Type" to "ಬೆಳೆಯ ಪ್ರಕಾರ ಆರಿಸಿ",
        "Done" to "ಮಾಡಿದ",
        "Plot Size (Acres)" to "ಜಮ್ಮಾ ಗಾತ್ರ (ಎಕರೆ)",
        "Cancel" to "ರದ್ದುಮಾಡಿ",
        "acres" to "ಎಕರೆ",
        "Set Active" to "ಸಕ್ರಿಯಗೊಳಿಸಿ",
        "Delete" to "ಅಳಿಸಿ",

        // Harvest Recording
        "Record Harvest" to "ಕೊಯ್ಲು ದಾಖಲಿಸಿ",
        "Harvest Date" to "ಕೊಯ್ಲು ದಿನಾಂಕ",
        "Yield" to "ಇಳುವರಿ",
        "Yield (Quintals)" to "ಇಳುವರಿ (ಕ್ವಿಂಟಲ್)",
        "Notes" to "ಟಿಪ್ಪಣಿಗಳು",
        "Save Harvest" to "ಕೊಯ್ಲು ಉಳಿಸಿ",
        "Harvest saved!" to "ಕೊಯ್ಲು ಉಳಿತಾಯಿತು!",
        "Harvest already recorded" to "ಕೊಯ್ಲು ಈಗಾಗಲೇ ದಾಖಲಾಗಿದೆ"
    )
    
    private val englishOnly = englishToKannada.keys.toList()
    
    fun translate(text: String, toKannada: Boolean): String {
        return if (toKannada) {
            englishToKannada[text] ?: text
        } else {
            text
        }
    }
    
    fun translateList(texts: List<String>, toKannada: Boolean): List<String> {
        return texts.map { translate(it, toKannada) }
    }
    
    fun hasTranslation(text: String): Boolean {
        return englishToKannada.containsKey(text)
    }
}