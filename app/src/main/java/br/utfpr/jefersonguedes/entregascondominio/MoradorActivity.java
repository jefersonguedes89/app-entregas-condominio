package br.utfpr.jefersonguedes.entregascondominio;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;

import br.utfpr.jefersonguedes.entregascondominio.modelo.Genero;
import br.utfpr.jefersonguedes.entregascondominio.modelo.Morador;
import br.utfpr.jefersonguedes.entregascondominio.persistencia.MoradoresDatabase;
import br.utfpr.jefersonguedes.entregascondominio.utils.UtilsAlert;
import br.utfpr.jefersonguedes.entregascondominio.utils.UtilsLocalDate;


public class MoradorActivity extends AppCompatActivity {

//    public static final String KEY_NOME = "KEY_NOME";
//    public static final String KEY_NUM = "KEY_NUM";
//    public static final String KEY_ALUGADO = "KEY_ALUGADO";
//    public static final String KEY_BLOCO = "KEY_BLOCO";
//    public static final String KEY_GENERO = "KEY_GENERO";


    public static final String KEY_MODO = "KEY_MODO";
    public static final String KEY_ID = "ID";
    public static final String KEY_SUGERIR_TIPO = "SUGERIR_TIPO";
    public static final String KEY_ULTIMO_TIPO = "ULTIMO_TIPO";
    public static final int MODO_NOVO = 0;

    public static final int MODO_EDITAR = 1;
    private EditText editTextNome, editTextNumApt, editTextDataNascimento;
    private CheckBox checkBoxAptAlugado;
    private RadioGroup radioGroupGenero;
    private Spinner spinnerBlocos;

    private RadioButton radioButtonMasc, radioButtonFem;

    private int modo;

    private Morador moradorOriginal;

    private boolean sugerirTipo = false;

    private int ultimoTipo = 0;

    private LocalDate dataNascimento;

    private int anosParaTras;

    private Button buttonEntregas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morador);


        editTextNome = findViewById(R.id.editTextNome);
        editTextNumApt = findViewById(R.id.editTextNumApt);
        editTextDataNascimento = findViewById(R.id.editTextDataNascimento);

        checkBoxAptAlugado = findViewById(R.id.checkBoxAptAlugado);
        radioGroupGenero = findViewById(R.id.radioGroupGenero);
        spinnerBlocos = findViewById(R.id.spinnerBlocos);

        radioButtonFem = findViewById(R.id.radioButtonFem);

        radioButtonMasc = findViewById(R.id.radioButtonMasc);

        editTextDataNascimento.setFocusable(false);

        buttonEntregas = findViewById(R.id.buttonEntregas);

        editTextDataNascimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDiolog();
            }
        });

        lerPreferencias();

        anosParaTras = getResources().getInteger(R.integer.anos_para_tras);

        dataNascimento = LocalDate.now().minusYears(anosParaTras);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();



        if (bundle != null) {
            modo = bundle.getInt(KEY_MODO);

            if (modo == MODO_NOVO) {
                setTitle(getString(R.string.novo_morador));

                if (sugerirTipo) {
                    spinnerBlocos.setSelection(ultimoTipo);
                }

                buttonEntregas.setVisibility(View.INVISIBLE);



            } else {
                setTitle(getString(R.string.editar_morador));

                long id = bundle.getLong(KEY_ID);

                MoradoresDatabase database = MoradoresDatabase.getInstance(this);

                moradorOriginal = database.getMoradorDao().queryForId(id);

//                String nome = bundle.getString(MoradorActivity.KEY_NOME);
//                int numApt = bundle.getInt(MoradorActivity.KEY_NUM);
//                boolean aptAlugado = bundle.getBoolean(MoradorActivity.KEY_ALUGADO);
//                int bloco = bundle.getInt(MoradorActivity.KEY_BLOCO);
//                String generoTexto = bundle.getString(MoradorActivity.KEY_GENERO);
//                moradorOriginal = new Morador(nome, numApt, aptAlugado, bloco, genero);


                editTextNome.setText(moradorOriginal.getNome());
                editTextNumApt.setText(String.valueOf(moradorOriginal.getNumApt()));



                if (moradorOriginal.getDataNascimento() != null) {
                    dataNascimento = moradorOriginal.getDataNascimento();
                }

                editTextDataNascimento.setText(UtilsLocalDate.formatLocalDate(dataNascimento));


                checkBoxAptAlugado.setChecked(moradorOriginal.isAptAlugado());
                spinnerBlocos.setSelection(moradorOriginal.getBloco());

                Genero genero = moradorOriginal.getGenero();

                if (genero == Genero.Masculino) {
                    radioButtonMasc.setChecked(true);
                } else {
                    radioButtonFem.setChecked(true);
                }
                editTextNome.requestFocus();
                editTextNome.setSelection(editTextNome.getText().length());

                int totalEntregas = database.getEntregaDao().totalIdMorador(moradorOriginal.getId());
                buttonEntregas.setText(getString(R.string.entregas, totalEntregas));

            }

        }

    }

    private void mostrarDatePickerDiolog() {

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dataNascimento = LocalDate.of(year, month + 1, dayOfMonth);

                editTextDataNascimento.setText(UtilsLocalDate.formatLocalDate(dataNascimento));
            }
        };



        DatePickerDialog picker = new DatePickerDialog(this,
                R.style.SpinnerDatePickerDialogTheme,
                listener,
                dataNascimento.getYear(),
                dataNascimento.getMonthValue() - 1,
                dataNascimento.getDayOfMonth());


        long dataMaximaMilis = UtilsLocalDate.toMillissegundos(LocalDate.now());

        picker.getDatePicker().setMaxDate(dataMaximaMilis);
        picker.show();

    }

    public void limparCampos() {


        final String nome = editTextNome.getText().toString();
        final String numApt = editTextNumApt.getText().toString();

        final LocalDate dataNascimentoAnterior = dataNascimento;

        final boolean aptAlugado = checkBoxAptAlugado.isChecked();
        final int radioButtonId = radioGroupGenero.getCheckedRadioButtonId();
        final int bloco = spinnerBlocos.getSelectedItemPosition();

        final ScrollView scrollView = findViewById(R.id.main);

        final View viewComFoco = scrollView.findFocus();


        editTextNome.setText(null);
        editTextNumApt.setText(null);
        editTextDataNascimento.setText(null);
        dataNascimento = LocalDate.now().minusYears(anosParaTras);
        checkBoxAptAlugado.setChecked(false);
        editTextNome.requestFocus();
        radioGroupGenero.clearCheck();
        spinnerBlocos.setSelection(0);

        Snackbar snackbar = Snackbar.make(scrollView,
                R.string.os_campos_foram_limpos, Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.desfazer, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNome.setText(nome);
                editTextNumApt.setText(numApt);

                dataNascimento = dataNascimentoAnterior;
                editTextDataNascimento.setText(UtilsLocalDate.formatLocalDate(dataNascimento));

                checkBoxAptAlugado.setChecked(aptAlugado);

                if (radioButtonId == R.id.radioButtonMasc) {
                    radioButtonMasc.setChecked(true);
                } else {
                    radioButtonFem.setChecked(true);
                }

                spinnerBlocos.setSelection(bloco);

                if (viewComFoco != null) {
                    viewComFoco.requestFocus();
                }

            }
        });

        snackbar.show();

//        Toast.makeText(this,
//                R.string.os_campos_foram_limpos,
//                Toast.LENGTH_LONG).show();

    }


    public void salvarValores() {
        String nome = editTextNome.getText().toString();
        if (nome == null || nome.trim().isEmpty()) {
            UtilsAlert.mostrarAviso(this, R.string.por_favor_digite_o_nome_do_morador);


//            Toast.makeText(this,
//                    R.string.por_favor_digite_o_nome_do_morador,
//                    Toast.LENGTH_LONG).show();
            editTextNome.requestFocus();
            editTextNome.setSelection(nome.length());
            return;
        }

        if (nome.length() > 80) {

            UtilsAlert.mostrarAviso(this, R.string.o_nome_nao_pode_ter_mais_que_80_caracteres);

//            Toast.makeText(this,
//                    R.string.o_nome_nao_pode_ter_mais_que_80_caracteres,
//                    Toast.LENGTH_LONG).show();

            editTextNome.requestFocus();
            editTextNome.setSelection(nome.length());
            return;
        }

        nome = nome.trim();


        String dataNascimentoString = editTextDataNascimento.getText().toString();
        if (dataNascimentoString == null || dataNascimentoString.trim().isEmpty()) {

            UtilsAlert.mostrarAviso(this, R.string.faltou_entrar_com_data_nascimento);

            return;
        }

        int idade = UtilsLocalDate.diferencaEmAnosParaHoje(dataNascimento);

        if(idade < 0 || idade > 100){
           UtilsAlert.mostrarAviso(this, R.string.data_nascimento_invalida);
           return;
        }


        String numAptString = editTextNumApt.getText().toString();
        if (numAptString == null || numAptString.trim().isEmpty()) {

            UtilsAlert.mostrarAviso(this, R.string.por_favor_digite_o_n_mero_do_apartamento);

//            Toast.makeText(this,
//                    R.string.por_favor_digite_o_n_mero_do_apartamento,
//                    Toast.LENGTH_LONG).show();

            editTextNumApt.requestFocus();
            return;
        }
        int numApt = 0;
        try {
            numApt = Integer.parseInt(numAptString);
        } catch (NumberFormatException e) {

            UtilsAlert.mostrarAviso(this, R.string.o_n_mero_deve_ser_um_inteiro);

//            Toast.makeText(this, R.string.o_n_mero_deve_ser_um_inteiro,
//                    Toast.LENGTH_LONG).show();

            editTextNumApt.requestFocus();
            return;
        }
        if (numApt < 0) {

            UtilsAlert.mostrarAviso(this, R.string.n_o_permitido_valores_menores_que_zero);

//            Toast.makeText(this, R.string.n_o_permitido_valores_menores_que_zero,
//                    Toast.LENGTH_LONG).show();

            editTextNumApt.requestFocus();
            editTextNumApt.setSelection(0, editTextNumApt.getText().toString().length());
            return;
        }

        int radioButtonId = radioGroupGenero.getCheckedRadioButtonId();

        Genero genero;


        if (radioButtonId == R.id.radioButtonFem) {
            genero = Genero.Feminino;
        } else if (radioButtonId == R.id.radioButtonMasc) {
            genero = Genero.Masculino;
        } else {

            UtilsAlert.mostrarAviso(this, R.string.por_favor_informe_o_g_nero_do_morador);

//            Toast.makeText(this, R.string.por_favor_informe_o_g_nero_do_morador,
//                    Toast.LENGTH_LONG).show();
            return;
        }

        int bloco = spinnerBlocos.getSelectedItemPosition();
        if (bloco == AdapterView.INVALID_POSITION) {

            UtilsAlert.mostrarAviso(this, R.string.n_o_foi_carregado_o_spinner_com_os_blocos);

//            Toast.makeText(this, R.string.n_o_foi_carregado_o_spinner_com_os_blocos,
//                    Toast.LENGTH_LONG).show();
            return;
        }

        boolean aptAlugado = checkBoxAptAlugado.isChecked();

        Morador morador = new Morador(nome, numApt, aptAlugado, bloco, genero, dataNascimento);

        if (morador.equals(moradorOriginal)) {
            setResult(MoradorActivity.RESULT_CANCELED);
            finish();
            return;
        }


//        if(modo == MODO_EDITAR &&
//           nome.equalsIgnoreCase(moradorOriginal.getNome()) &&
//           numApt == moradorOriginal.getNumApt() &&
//           aptAlugado == moradorOriginal.isAptAlugado() &&
//           bloco == moradorOriginal.getBloco() &&
//           genero == moradorOriginal.getGenero()){
//            setResult(MoradorActivity.RESULT_CANCELED);
//            finish();
//           return;
//        }


        Intent intentResposta = new Intent();

        MoradoresDatabase moradoresDatabase = MoradoresDatabase.getInstance(this);
        if (modo == MODO_NOVO) {
            long novoId = moradoresDatabase.getMoradorDao().insert(morador);

            if (novoId <= 0) {
                UtilsAlert.mostrarAviso(this, R.string.erro_ao_tentar_inserir);
                return;
            }
            morador.setId(novoId);


        } else {
            morador.setId(moradorOriginal.getId());

            int quantidadeAlterada = moradoresDatabase.getMoradorDao().update(morador);

            if (quantidadeAlterada != 1) {
                UtilsAlert.mostrarAviso(this, R.string.erro_ao_tentar_alterar);
                return;
            }

        }
        salvarUltimoTipo(bloco);

        intentResposta.putExtra(KEY_ID, morador.getId());

//        intentResposta.putExtra(KEY_NOME, nome);


//        intentResposta.putExtra(KEY_NUM, numApt);
//        intentResposta.putExtra(KEY_ALUGADO, aptAlugado);
//        intentResposta.putExtra(KEY_BLOCO, bloco);
//        intentResposta.putExtra(KEY_GENERO, genero.toString());

        setResult(MoradoresActivity.RESULT_OK, intentResposta);


        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.morador_opcoes, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuItem = menu.findItem(R.id.menuItemSugerirTipo);

        menuItem.setChecked(sugerirTipo);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemSalvar) {
            salvarValores();
            return true;
        } else {
            if (idMenuItem == R.id.menuItemLimpar) {
                limparCampos();
                return true;
            } else if (idMenuItem == R.id.menuItemSugerirTipo) {

                boolean valor = !item.isChecked();

                salvarSugerirTipo(valor);

                item.setChecked(valor);

                if (sugerirTipo) {
                    spinnerBlocos.setSelection(ultimoTipo);
                }

                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }

        }
    }

    private void lerPreferencias() {
        SharedPreferences sharedPreferences = getSharedPreferences(MoradoresActivity.ARQUIVOS_PREFERENCIAS, Context.MODE_PRIVATE);

        sugerirTipo = sharedPreferences.getBoolean(KEY_SUGERIR_TIPO, sugerirTipo);
        ultimoTipo = sharedPreferences.getInt(KEY_ULTIMO_TIPO, ultimoTipo);


    }

    private void salvarSugerirTipo(boolean novoValor) {
        SharedPreferences sharedPreferences = getSharedPreferences(MoradoresActivity.ARQUIVOS_PREFERENCIAS, Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putBoolean(KEY_SUGERIR_TIPO, novoValor);

        edit.commit();

        sugerirTipo = novoValor;
    }

    private void salvarUltimoTipo(int novoValor) {
        SharedPreferences sharedPreferences = getSharedPreferences(MoradoresActivity.ARQUIVOS_PREFERENCIAS, Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putInt(KEY_ULTIMO_TIPO, novoValor);

        edit.commit();

        ultimoTipo = novoValor;
    }

    ActivityResultLauncher<Intent> launcherEntregas = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            MoradoresDatabase database = MoradoresDatabase.getInstance(MoradorActivity.this);
            int totalEntregas = database.getEntregaDao().totalIdMorador(moradorOriginal.getId());
            buttonEntregas.setText(getString(R.string.entregas, totalEntregas));

        }
    });


    public void abrirEntregas(View view){
        Intent intentAbertura = new Intent(this, EntregasActivity.class);
        intentAbertura.putExtra(EntregasActivity.KEY_ID_MORADOR, moradorOriginal.getId());

        launcherEntregas.launch(intentAbertura);

    }





}