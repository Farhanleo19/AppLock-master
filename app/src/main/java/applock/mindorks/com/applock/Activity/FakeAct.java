package applock.mindorks.com.applock.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import applock.mindorks.com.applock.R;

public class FakeAct extends AppCompatActivity {
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake);
//        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        alertDialog = new AlertDialog.Builder(this)
//                .setMessage("Unfortunately, app has been stopped?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();


        Button bOk = (Button) findViewById(R.id.b_ok);


        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                finish();
            }
        });
    }
}
