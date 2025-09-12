package br.utfpr.jefersonguedes.entregascondominio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SobreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        setTitle(R.string.sobre);

    }


    public void abrirSiteUTFPR(View view){
        abrirSite("https://www.utfpr.edu.br/");
    }

    private void abrirSite(String endereco){
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setData(Uri.parse(endereco));

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.nenhum_app_para_abrir_p_ginas_web, Toast.LENGTH_SHORT).show();
        }

    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        int idMenuItem = item.getItemId();
//
//        if(idMenuItem == android.R.id.home){
//            finish();
//            return  true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//
//    }
}