package com.example.mathprojectavishaigozland;

import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class Exercis {
    int mana1;
    int mana2;
    int result;
    int point;
    private ExercisCallbackInterface exercisCallbackInterface;

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
        if(answer.equals(result+"")){
            return true;
        }
        else{
            return false;
        }
    }
}
