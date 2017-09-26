package com.yfbx.mockdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        checkMockLoc();
    }


    private void checkMockLoc() {
        MockLoc.checkMock(this, new MockLoc.OnMockListener() {
            @Override
            public void onMockRunning(List<String> mockAppNames) {
                String info = "请关闭" + mockAppNames.get(0) + "再试！";
                Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMockClear() {
                // TODO: 2017/9/19 正常执行任务
                Toast.makeText(MainActivity.this, "打卡成功！", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
