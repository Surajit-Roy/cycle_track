package com.fss.cycletrack360

class CalorieCalculator(
    private val userWeight: Double, // Weight in kilograms
    private val userGender: String // Gender as "male" or "female"
) {

    // MET values for activities
    private val MET_WALKING = 3.8
    private val MET_RUNNING = 9.8
    private val MET_CYCLING = 7.5

    // Gender multipliers
    private val GENDER_MULTIPLIER_MALE = 1.0
    private val GENDER_MULTIPLIER_FEMALE = 0.9

    /**
     * Calculates the calories burned based on activity type, distance, and user gender.
     * @param activityType The type of activity ("walking", "running", or "cycling").
     * @param distance The distance traveled in kilometers.
     * @return Calories burned for the given activity and distance.
     */
    fun calculateCalories(activityType: String, distance: Double): Double {
        if (distance <= 0) return 0.0 // No distance traveled, no calories burned

        // Select the appropriate MET value based on the activity type
        val metValue = when (activityType.lowercase()) {
            "walking" -> MET_WALKING
            "running" -> MET_RUNNING
            "cycling" -> MET_CYCLING
            else -> return 0.0 // Invalid activity type
        }

        // Determine the gender multiplier
        val genderMultiplier = when (userGender.lowercase()) {
            "male" -> GENDER_MULTIPLIER_MALE
            "female" -> GENDER_MULTIPLIER_FEMALE
            else -> return 0.0 // Invalid gender
        }

        // Assume an average speed for each activity to calculate time:
        val averageSpeed = when (activityType.lowercase()) {
            "walking" -> 5.0 // Walking speed in km/h
            "running" -> 10.0 // Running speed in km/h
            "cycling" -> 15.0 // Cycling speed in km/h
            else -> 0.0
        }

        val timeInHours = distance / averageSpeed // Time in hours

        // Calories burned formula with gender adjustment
        return metValue * userWeight * timeInHours * genderMultiplier
    }
}