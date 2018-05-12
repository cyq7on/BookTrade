package cn.bmob.imdemo.util;

import java.text.DecimalFormat;

public class BMIUtils {

    //Variables
    private double result; //Stores the result
    private String bmi; //Stores the Bmi result in String
    private String status; //Stores the status (Normal......)
    private static DecimalFormat decimalFormat = new DecimalFormat(".#"); //Formating the result
    private int statusInt;

    public BMIUtils(double weight,double height) {
        calculateBmi(weight,height);
    }

    /**
     * Get the BMI from calculation and returns the result in String format
     *
     * @return BMI
     */
    public String getBmi() {
        /**
         * Following code example : bmi = decimalFormat.format(result);
         *
         * if bmi is 23.12345 then remove 2345 and return 23.1 using DecimalFormat class provided by Java
         *
         */
        bmi = decimalFormat.format(result);
        return bmi;
    }

    /**
     * Get the status according to the BMI result and returns the Status in
     * String format
     *
     * @return Status
     */
    public String getStatus() {
        return status;
    }

    public int getStatusInt() {
        return statusInt;
    }

    /**
     * Get weight and height in double format and do the calculation for you
     *
     * @param weight - accepts weight in kilograms
     * @param height - accept height in centimeters
     */
    public void calculateBmi(double weight, double height) {
        /**
         * First get weight and height then convert height to meters then
         * multiply the height itself to height and then divide the height with
         * weight then set the actual value to result variable
         */

        //Convert the height from cm to m
        double meters = height / 100;

        //Multiply height by height
        double multiply = meters * meters;

        //divide the height with weight in kg
        double division = weight / multiply;

        //set the value to result variable
        result = division;

        //call checkStatus method for checking Status
        checkStatus();
    }

    /**
     * Private method for checking bmi and returns the status It remains private
     * because we don't need to access this from somewhere else.
     */
    private void checkStatus() {
        /**
         * Check :
         *
         * if BMI is less than 18.5 then set status to Underweight if BMI is
         * greater than 18.5 and less than 25 then set status to Normal if BMI
         * is greater than 25 and less than 30 then set status to Overweight if
         * BMI equals to 30 or greater than 30 then set status to Obese
         */
        /*if (result < 18.5) {
            status = "Underweight";
        } else if (result > 18.5 && result < 25) {
            status = "Normal";
        } else if (result > 25 && result < 30) {
            status = "Overweight";
        } else if (result == 30 || result > 30) {
            status = "Obese";
        }*/

        if (result < 18.5) {
            status = "偏瘦，推荐您多吃热量菜品";
            statusInt = 0 ;
        } else if (result > 18.5 && result < 25) {
            status = "正常，身体较为健康";
            statusInt = 1 ;
        } else if (result > 25 && result < 30) {
            status = "过重，少吃能量过多的食物，迈开腿管住嘴";
            statusInt = 2 ;
        } else if (result >= 30) {
            status = "肥胖，禁食高热量食物，多运动";
            statusInt = 3 ;
        }
    }
}

