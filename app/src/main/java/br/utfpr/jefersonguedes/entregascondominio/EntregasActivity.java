package br.utfpr.jefersonguedes.entregascondominio;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import br.utfpr.jefersonguedes.entregascondominio.modelo.Entrega;
import br.utfpr.jefersonguedes.entregascondominio.modelo.Morador;
import br.utfpr.jefersonguedes.entregascondominio.persistencia.MoradoresDatabase;
import br.utfpr.jefersonguedes.entregascondominio.utils.UtilsAlert;

public class EntregasActivity extends AppCompatActivity {

    public static final String KEY_ID_MORADOR = "KEY_ID_MORADOR";

    private RecyclerView recyclerViewEntregas;
    private RecyclerView.LayoutManager  layoutManager;
    private EntregaRecyclerViewAdapter entregaRecyclerViewAdapter;

    private List<Entrega> listaEntregas;

    private int posicaoSelecionada = -1;

    private ActionMode actionMode;

    private View viewSelecionada;
    private Drawable backgroundDrawable;

    private Morador morador;

    private ActionMode.Callback actionCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.item_selecionado, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int idMenuItem = item.getItemId();

            if (idMenuItem == R.id.menuItemEditar){
                editarAnotacao();
                return true;
            }else
            if (idMenuItem == R.id.menuItemExcluir){
                excluirAnotacao();
                return true;
            }else{
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            if (viewSelecionada != null){
                viewSelecionada.setBackground(backgroundDrawable);
            }

            actionMode         = null;
            viewSelecionada    = null;
            backgroundDrawable = null;

            recyclerViewEntregas.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entregas);

        Intent intentAbertura = getIntent();

        Bundle bundle = intentAbertura.getExtras();

        if (bundle != null){

            long id = bundle.getLong(KEY_ID_MORADOR);

            MoradoresDatabase database = MoradoresDatabase.getInstance(this);

            morador = database.getMoradorDao().queryForId(id);

            setTitle(getString(R.string.entregas_do_morador, morador.getNome()));
        }else{
            // Faltou passar o parâmetro na abertura da Activity
        }

        recyclerViewEntregas = findViewById(R.id.recycleViewEntregas);

        layoutManager = new LinearLayoutManager(this);

        recyclerViewEntregas.setLayoutManager(layoutManager);
        recyclerViewEntregas.setHasFixedSize(true);
        recyclerViewEntregas.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        popularListaAnotacoes();
    }

    private void popularListaAnotacoes(){

        MoradoresDatabase database = MoradoresDatabase.getInstance(this);

        listaEntregas = database.getEntregaDao().queryForIdMorador(morador.getId());

        entregaRecyclerViewAdapter = new EntregaRecyclerViewAdapter(this, listaEntregas);

        entregaRecyclerViewAdapter.setOnItemClickListener(new EntregaRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                posicaoSelecionada = position;
                editarAnotacao();
            }
        });

        entregaRecyclerViewAdapter.setOnItemLongClickListener(new EntregaRecyclerViewAdapter.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(View view, int position) {

                if (actionMode != null){
                    return false;
                }

                posicaoSelecionada = position;

                viewSelecionada    = view;
                backgroundDrawable = view.getBackground();

                view.setBackgroundColor(getColor(R.color.corSelecionada));

                recyclerViewEntregas.setEnabled(false);

                actionMode = startSupportActionMode(actionCallback);

                return true;
            }
        });

        recyclerViewEntregas.setAdapter(entregaRecyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.entregas_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemAdicionar){
            novaAnotacao();
            return true;
        }else
        if (idMenuItem == android.R.id.home){
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void excluirAnotacao(){

        final Entrega entrega = listaEntregas.get(posicaoSelecionada);

        DialogInterface.OnClickListener listenerSim = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                MoradoresDatabase database = MoradoresDatabase.getInstance(EntregasActivity.this);

                int quantidadeAlterada = database.getEntregaDao().delete(entrega);

                if (quantidadeAlterada != 1){
                    UtilsAlert.mostrarAviso(EntregasActivity.this, R.string.erro_ao_tentar_excluir);
                    return;
                }

                listaEntregas.remove(posicaoSelecionada);
                entregaRecyclerViewAdapter.notifyItemRemoved(posicaoSelecionada); // no caso do ListView use notifyDataSetChanged()

                posicaoSelecionada = -1;

                actionMode.finish();
            }
        };

        UtilsAlert.confirmarAcao(this, R.string.deseja_apagar, listenerSim, null);
    }

    private void novaAnotacao(){

        UtilsAlert.OnTextEnteredListener listener = new UtilsAlert.OnTextEnteredListener() {

            @Override
            public void onTextEntered(String texto) {

                if (texto == null || texto.trim().isEmpty()){
                    UtilsAlert.mostrarAviso(EntregasActivity.this, R.string.o_campo_nao_pode_ser_vazio);
                    return;
                }

                Entrega entrega = new Entrega(morador.getId(), LocalDateTime.now(), null, texto);

                MoradoresDatabase database = MoradoresDatabase.getInstance(EntregasActivity.this);

                long novoId = database.getEntregaDao().insert(entrega);

                if (novoId <= 0){
                    UtilsAlert.mostrarAviso(EntregasActivity.this, R.string.erro_ao_tentar_inserir);
                    return;
                }

                entrega.setId(novoId);

                listaEntregas.add(entrega);

                Collections.sort(listaEntregas, Entrega.ordenacaoDescrescente);
                entregaRecyclerViewAdapter.notifyDataSetChanged();
            }
        };

        UtilsAlert.lerTexto(this, R.string.nova_entrega, R.layout.entrada_entrega, R.id.editTextTextDescricao, null, listener);
    }

    private void editarAnotacao(){

        final Entrega entrega = listaEntregas.get(posicaoSelecionada);

        UtilsAlert.OnTextEnteredListener listener = new UtilsAlert.OnTextEnteredListener() {

            @Override
            public void onTextEntered(String texto) {

                if (texto == null || texto.trim().isEmpty()){
                    UtilsAlert.mostrarAviso(EntregasActivity.this, R.string.o_campo_nao_pode_ser_vazio);
                    return;
                }

                if (texto.equalsIgnoreCase(entrega.getDescricao())){
                    // O texto é o mesmo, não faz nada
                    return;
                }

                entrega.setDescricao(texto);

                MoradoresDatabase database = MoradoresDatabase.getInstance(EntregasActivity.this);

                long novoId = database.getEntregaDao().update(entrega);

                if (novoId <= 0){
                    UtilsAlert.mostrarAviso(EntregasActivity.this, R.string.erro_ao_tentar_alterar);
                    return;
                }

                entregaRecyclerViewAdapter.notifyItemChanged(posicaoSelecionada);

                posicaoSelecionada = -1;

                if (actionMode != null){
                    actionMode.finish();
                }
            }
        };

        UtilsAlert.lerTexto(this, R.string.editar_entrega, R.layout.entrada_entrega, R.id.editTextTextDescricao,
                entrega.getDescricao(), listener);
    }
}