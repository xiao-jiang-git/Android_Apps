package com.xiaojiang.sbu.mycalculater;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt0,bt1,bt2,bt3,bt4,bt5,bt6,bt7,bt8,bt9,btd,btc;

    private String number = "";

    private double input=0.0,smallnum = 0;

    private int count = 1;
    private int inta ,intb ,twenty,ten,five,one,twentyfivec, tenc, fivec, onec = 0;

    private Boolean dot_flag = false;

    private TextView tvscreen,tv20,tv10,tv5,tv1,tv025,tv010,tv05,tv01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt0 = findViewById(R.id.button0);
        bt1 = findViewById(R.id.button1);
        bt2 = findViewById(R.id.button2);
        bt3 = findViewById(R.id.button3);
        bt4 = findViewById(R.id.button4);
        bt5 = findViewById(R.id.button5);
        bt6 = findViewById(R.id.button6);
        bt7 = findViewById(R.id.button7);
        bt8 = findViewById(R.id.button8);
        bt9 = findViewById(R.id.button9);
        btd = findViewById(R.id.buttondot);
        btc = findViewById(R.id.buttonclc);

        tvscreen = findViewById(R.id.screen1);
        tv1 = findViewById(R.id.one);
        tv5 = findViewById(R.id.five);
        tv10 = findViewById(R.id.ten);
        tv20 = findViewById(R.id.twenty);
        tv025 = findViewById(R.id.twenty5c);
        tv01 = findViewById(R.id.onec);
        tv05 = findViewById(R.id.fivec);
        tv010 = findViewById(R.id.tenc);
        tv025 = findViewById(R.id.twenty5c);


        bt0.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        bt6.setOnClickListener(this);
        bt7.setOnClickListener(this);
        bt8.setOnClickListener(this);
        bt9.setOnClickListener(this);
        btd.setOnClickListener(this);
        btc.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {


        switch (view.getId()){
            case(R.id.button0):
                getResult(0);
                tvscreen.setText(number);
                judgeValue();
                break;
            case(R.id.button1):
                getResult(1);
                tvscreen.setText(number);
                judgeValue();
                break;
            case(R.id.button2):
                getResult(2);
                tvscreen.setText(number);
                judgeValue();
                break;
            case(R.id.button3):
                getResult(3);
                tvscreen.setText(number);
                judgeValue();
                break;
            case(R.id.button4):
                getResult(4);
                tvscreen.setText(number);
                judgeValue();
                break;
            case(R.id.button5):
                getResult(5);
                tvscreen.setText(number);
                judgeValue();
                break;
            case(R.id.button6):
                getResult(6);
                tvscreen.setText(number);
                judgeValue();
                break;
            case(R.id.button7):
                getResult(7);
                tvscreen.setText(number);
                judgeValue();
                break;
            case(R.id.button8):
                getResult(8);
                tvscreen.setText(number);
                judgeValue();
                break;
            case(R.id.button9):
                getResult(9);
                tvscreen.setText(number);
                judgeValue();
                break;

            case(R.id.buttondot):
                dot_flag = true;
                break;
            case(R.id.buttonclc):
                number = "";
                input = 0;
                count = 1;
                dot_flag = false;
                smallnum = 0;
                setClear();
                break;

        }
    }

    public String getResult(int num){

        if(dot_flag == false) {

            input = input * 10 + num;

        }
        else if(dot_flag == true && count<=2){

            BigDecimal bd1 = new BigDecimal(input);
            BigDecimal bd2 = new BigDecimal(Math.pow(0.1,(double)count)*num);

            Double small = bd2.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            smallnum = small + smallnum;

            input = bd1.add(bd2).setScale(3, RoundingMode.HALF_EVEN).doubleValue();
            count = count+1;

        }
        number = ""+input;
        return number;
    }

    public void judgeValue(){

        inta=(new Double(input-smallnum)).intValue();
        twenty = inta/20;
        ten = (inta-twenty*20)/10;
        five = (inta-twenty*20-ten*10)/5;
        one = inta-twenty*20-ten*10-five*5;

        tv1.setText("#  of   1$ :"+one);
        tv5.setText("#  of   5$ :"+five);
        tv10.setText("# of 10$ :"+ten);
        tv20.setText("# of 20$ :"+twenty);

        intb = (new Double(smallnum*100)).intValue();

        twentyfivec = intb/25;
        tenc = (intb-25*twentyfivec)/10;
        fivec = (intb-25*twentyfivec-10*tenc)/5;
        onec = intb-25*twentyfivec-10*tenc-5*fivec;

        tv01.setText("# of  1￠ :"+onec);
        tv05.setText("# of  5￠ :"+fivec);
        tv010.setText("# of 10￠:"+tenc);
        tv025.setText("# of 25￠:"+twentyfivec);

    }

    public void setClear(){
        tv1.setText("1$");
        tv5.setText("5$");
        tv10.setText("10$");
        tv20.setText("20$");
        tv01.setText("1￠");
        tv05.setText("5￠");
        tv010.setText("10￠");
        tv025.setText("25￠");
        tvscreen.setText("0.0");

    }
}
