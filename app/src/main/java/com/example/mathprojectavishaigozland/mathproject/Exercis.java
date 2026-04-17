package com.example.mathprojectavishaigozland.mathproject;

import android.widget.EditText;

import java.util.Random;

public class Exercis {
    int mana1;    // המספר הראשון בתרגיל
    int mana2;   // המספר השני בתרגיל
    int result;  // תוצאת המכפלה הנכונה
    int point;   // כמות הנקודות שהמשתמש יקבל על פתרון תרגיל זה (משתנה לפי רמת הקושי)

    /**
     * מחזיק את הכתובת של הפעולה שהוגדרה ב-Activity
     */
    private final ExercisCallbackInterface exercisCallbackInterface;

    //פעולה בונה למחלקת Exercis
    public Exercis(ExercisCallbackInterface exercisCallbackInterface){
        this.exercisCallbackInterface = exercisCallbackInterface;
    }

    //פעולה לכפתור הראשון - לוח הכפל
    public void multy(){
        Random random = new Random();
        mana1 = random.nextInt(10);
        mana2 = random.nextInt(10);
        result = mana1 * mana2;
        point = 5;

        //הפעלת הפעולה המוגדרת ב-Activity
        exercisCallbackInterface.showNumber(mana1, mana2);
    }

    //פעולה לכפתור שני - כפל עד 20
    public void multy20(){
        Random random = new Random();
        mana1 = random.nextInt(10);
        mana2 = random.nextInt(10)+10;
        result = mana1 * mana2;
        point = 10;

        exercisCallbackInterface.showNumber(mana1, mana2);

    }

    //פעולה לכפתור שלישי - אתגר
    public void multyEtgar(){
        Random random = new Random();
        mana1 = random.nextInt(10);
        mana2 = random.nextInt(90)+10;
        result = mana1 * mana2;
        point = 20;

        exercisCallbackInterface.showNumber(mana1, mana2);

    }

    //פעולת תשובה
    public boolean answearAfirst(EditText userAns){
        String answer = userAns.getText().toString();
        return answer.equals(result + "");
    }
}
