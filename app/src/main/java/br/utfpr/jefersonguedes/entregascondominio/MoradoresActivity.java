package br.utfpr.jefersonguedes.entregascondominio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.utfpr.jefersonguedes.entregascondominio.modelo.Genero;
import br.utfpr.jefersonguedes.entregascondominio.modelo.Morador;
import br.utfpr.jefersonguedes.entregascondominio.persistencia.MoradoresDatabase;
import br.utfpr.jefersonguedes.entregascondominio.utils.UtilsAlert;

public class MoradoresActivity extends AppCompatActivity {

    private ListView listViewMoradores;

    private List<Morador> moradores;

    private MoradorAdapter moradorAdapter;

    private int posicaoSelecionada = -1;

    private View viewSelecionada;

    private Drawable backgroundDrawable;


    private ActionMode actionMode;

    public static final String ARQUIVOS_PREFERENCIAS = "br.utfpr.jefersonguedes.entregascondominio.PREFERENCIAS";

    public static final String KEY_ORDENACAO_ASCENDENTE = "ORDENACAO_ASCENDENTE";
    private boolean ordenacaoAscendente = PADRAO_INICIAL_ORDENACAO_ASCENDENTE;

    private MenuItem menuItemOrdenacao;


    public static final boolean PADRAO_INICIAL_ORDENACAO_ASCENDENTE = true;

    private ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.moradores_item_selecionado, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int idMenuItem = item.getItemId();

            if (idMenuItem == R.id.menuItemEditar) {
                editarMorador();
                return true;
            } else {
                if (idMenuItem == R.id.menuItemExcluir) {
                    excluirMorador();
                    return true;
                } else {
                    return false;
                }
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (viewSelecionada != null) {
                viewSelecionada.setBackground(backgroundDrawable);
            }

            actionMode = null;
            viewSelecionada = null;
            backgroundDrawable = null;

            listViewMoradores.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_moradores);

        setTitle(getString(R.string.moradores));

        listViewMoradores = findViewById(R.id.listViewMoradores);

        listViewMoradores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Morador morador = (Morador) listViewMoradores.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(), getString(R.string.morador) +morador.getNome()+ getString(R.string.foi_clicado), Toast.LENGTH_LONG).show();
                posicaoSelecionada = position;
                editarMorador();
            }
        });

        listViewMoradores.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (actionMode != null) {
                    return false;
                }

                posicaoSelecionada = position;

                viewSelecionada = view;

                backgroundDrawable = view.getBackground();

                view.setBackgroundColor(getColor(R.color.corSelecionada));

                listViewMoradores.setEnabled(false);

                actionMode = startSupportActionMode(actionCallback);

                return true;
            }
        });

        lerPreferencias();

        popularMoradores();
        registerForContextMenu(listViewMoradores);
    }

    private void popularMoradores() {


        MoradoresDatabase moradoresDatabase = MoradoresDatabase.getInstance(this);

        if (ordenacaoAscendente) {
            moradores = moradoresDatabase.getMoradorDao().queryAllAscending();
        } else {
            moradores = moradoresDatabase.getMoradorDao().queryAllDownward();
        }


        moradorAdapter = new MoradorAdapter(this, moradores);

        listViewMoradores.setAdapter(moradorAdapter);

    }

    public void abrirSobre() {

        Intent intentAbertura = new Intent(this, SobreActivity.class);
        startActivity(intentAbertura);

    }

    ActivityResultLauncher<Intent> launcherNovoMorador = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == MoradoresActivity.RESULT_OK) {
                        Intent intent = result.getData();
                        Bundle bundle = intent.getExtras();
                        if (bundle != null) {
//                                    String nome = bundle.getString(MoradorActivity.KEY_NOME);
//                                    int numApt = bundle.getInt(MoradorActivity.KEY_NUM);
//                                    boolean aptAlugado = bundle.getBoolean(MoradorActivity.KEY_ALUGADO);
//                                    int bloco = bundle.getInt(MoradorActivity.KEY_BLOCO);
//                                    String genero = bundle.getString(MoradorActivity.KEY_GENERO);

                            long id = bundle.getLong(MoradorActivity.KEY_ID);

                            MoradoresDatabase database = MoradoresDatabase.getInstance(MoradoresActivity.this);
                            Morador morador = database.getMoradorDao().queryForId(id);
//                                    Morador morador = new Morador(nome, numApt, aptAlugado, bloco, Genero.valueOf(genero));

                            moradores.add(morador);

                            ordenarLista();


                        }

                    }
                    posicaoSelecionada = -1;
                    if (actionMode != null) {
                        actionMode.finish();
                    }

                }
            });

    public void abrirNovoMorador() {
        Intent intent = new Intent(this, MoradorActivity.class);
        intent.putExtra(MoradorActivity.KEY_MODO, MoradorActivity.MODO_NOVO);

        launcherNovoMorador.launch(intent);
    }

    private void excluirMorador() {

        final Morador morador = moradores.get(posicaoSelecionada);

//        String mensagem = getString(R.string.deseja_apagar) + morador.getNome() + "\"";

        String mensagem = getString(R.string.deseja_apagar, morador.getNome());

        DialogInterface.OnClickListener listenerSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                MoradoresDatabase database = MoradoresDatabase.getInstance(MoradoresActivity.this);

                int quantidadeAlterada = database.getMoradorDao().delete(morador);

                if (quantidadeAlterada != 1) {
                    UtilsAlert.mostrarAviso(MoradoresActivity.this, R.string.erro_ao_tentar_excluir);
                    return;
                }

                moradores.remove(posicaoSelecionada);
                moradorAdapter.notifyDataSetChanged();
                actionMode.finish();
            }
        };

        UtilsAlert.confirmarAcao(this, mensagem, listenerSim, null);


    }

    ActivityResultLauncher<Intent> launcherEditarMorador = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == MoradoresActivity.RESULT_OK) {
                        Intent intent = result.getData();
                        Bundle bundle = intent.getExtras();
                        if (bundle != null) {


                            final Morador moradorOriginal = moradores.get(posicaoSelecionada);

                            long id = bundle.getLong(MoradorActivity.KEY_ID);

                            final MoradoresDatabase database = MoradoresDatabase.getInstance(MoradoresActivity.this);
                            final Morador moradorEditado = database.getMoradorDao().queryForId(id);


//                            String nome = bundle.getString(MoradorActivity.KEY_NOME);
//                            int numApt = bundle.getInt(MoradorActivity.KEY_NUM);
//                            boolean aptAlugado = bundle.getBoolean(MoradorActivity.KEY_ALUGADO);
//                            int bloco = bundle.getInt(MoradorActivity.KEY_BLOCO);
//                            String generoTexto = bundle.getString(MoradorActivity.KEY_GENERO);

//                            final Morador morador = moradores.get(posicaoSelecionada);

//                            final Morador cloneMoradorOriginal;
//
//                            try {
//                                cloneMoradorOriginal = (Morador) morador.clone();
//                            } catch (CloneNotSupportedException e) {
//                                e.printStackTrace();
//                                UtilsAlert.mostrarAviso(MoradoresActivity.this, R.string.erro_conversao_tipos);
//                                return;
//                            }

//                            morador.setNome(nome);
//                            morador.setNumApt(numApt);
//                            morador.setAptAlugado(aptAlugado);
//                            morador.setBloco(bloco);
//
//                            Genero genero = Genero.valueOf(generoTexto);
//                            morador.setGenero(genero);

                            moradores.set(posicaoSelecionada, moradorEditado);

                            ordenarLista();

                            final ConstraintLayout constraintLayout = findViewById(R.id.main);

                            Snackbar snackbar = Snackbar.make(constraintLayout,
                                    R.string.alteracao_realizada, Snackbar.LENGTH_LONG);

                            snackbar.setAction(R.string.desfazer, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    int quantidadeAlterada = database.getMoradorDao().update(moradorOriginal);

                                    if (quantidadeAlterada != 1) {
                                        UtilsAlert.mostrarAviso(MoradoresActivity.this, R.string.erro_ao_tentar_alterar);
                                        return;
                                    }

                                    moradores.remove(moradorEditado);
                                    moradores.add(moradorOriginal);
                                    ordenarLista();
                                }
                            });

                            snackbar.show();


                        }

                    }
                    posicaoSelecionada = -1;

                }
            });

    private void editarMorador() {

//        posicaoSelecionada = posicao;

        Morador morador = moradores.get(posicaoSelecionada);

        Intent intent = new Intent(this, MoradorActivity.class);

        intent.putExtra(MoradorActivity.KEY_MODO, MoradorActivity.MODO_EDITAR);
        intent.putExtra(MoradorActivity.KEY_ID, morador.getId());

//        intent.putExtra(MoradorActivity.KEY_NOME, morador.getNome());
//        intent.putExtra(MoradorActivity.KEY_NUM, morador.getNumApt());
//        intent.putExtra(MoradorActivity.KEY_ALUGADO, morador.isAptAlugado());
//        intent.putExtra(MoradorActivity.KEY_BLOCO, morador.getBloco());
//        intent.putExtra(MoradorActivity.KEY_GENERO, morador.getGenero().toString());

        launcherEditarMorador.launch(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.moradores_opcoes, menu);

        menuItemOrdenacao = menu.findItem(R.id.menuItemOrdenacao);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        atualizarIconeOrdenacao();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemAdicionar) {
            abrirNovoMorador();
            return true;
        } else {
            if (idMenuItem == R.id.menuItemSobre) {
                abrirSobre();
                return true;
            } else if (idMenuItem == R.id.menuItemOrdenacao) {
                salvarPreferenciaOrdenacao(!ordenacaoAscendente);
                atualizarIconeOrdenacao();
                ordenarLista();
                return true;
            } else if (idMenuItem == R.id.menuItemRestaurar) {
                confirmarRestaurarPadroes();
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }


    }

    private void ordenarLista() {
        if (ordenacaoAscendente) {
            Collections.sort(moradores, Morador.ordenacaoCrescente);
        } else {
            Collections.sort(moradores, Morador.ordenacaoDecrescente);
        }

        moradorAdapter.notifyDataSetChanged();
    }

    private void atualizarIconeOrdenacao() {
        if (ordenacaoAscendente) {
            menuItemOrdenacao.setIcon(R.drawable.ic_action_ascending_order);

        } else {
            menuItemOrdenacao.setIcon(R.drawable.ic_action_descending_order);
        }
    }

    private void lerPreferencias() {
        SharedPreferences sharedPreferences = getSharedPreferences(MoradoresActivity.ARQUIVOS_PREFERENCIAS, Context.MODE_PRIVATE);

        ordenacaoAscendente = sharedPreferences.getBoolean(KEY_ORDENACAO_ASCENDENTE, ordenacaoAscendente);

    }

    private void salvarPreferenciaOrdenacao(boolean novoValor) {
        SharedPreferences sharedPreferences = getSharedPreferences(MoradoresActivity.ARQUIVOS_PREFERENCIAS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(KEY_ORDENACAO_ASCENDENTE, novoValor);

        editor.commit();

        ordenacaoAscendente = novoValor;
    }

    private void confirmarRestaurarPadroes() {
        DialogInterface.OnClickListener listenerSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restaurarPadroes();
                atualizarIconeOrdenacao();
                ordenarLista();
                Toast.makeText(MoradoresActivity.this,
                        R.string.as_configuracoes_voltaram_ao_padrao_de_instalacao,
                        Toast.LENGTH_SHORT).show();
            }
        };


        UtilsAlert.confirmarAcao(this, R.string.desaja_limpar_padroes, listenerSim, null);

    }


    private void restaurarPadroes() {

        SharedPreferences sharedPreferences = getSharedPreferences(MoradoresActivity.ARQUIVOS_PREFERENCIAS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.commit();

        ordenacaoAscendente = PADRAO_INICIAL_ORDENACAO_ASCENDENTE;

    }


}