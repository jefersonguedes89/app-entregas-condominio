package br.utfpr.jefersonguedes.entregascondominio;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.List;

import br.utfpr.jefersonguedes.entregascondominio.modelo.Entrega;
import br.utfpr.jefersonguedes.entregascondominio.persistencia.MoradoresDatabase;
import br.utfpr.jefersonguedes.entregascondominio.utils.UtilsLocalDateTime;

public class EntregaRecyclerViewAdapter extends RecyclerView.Adapter<EntregaRecyclerViewAdapter.EntregaHolder>{

    private OnItemClickListener        onItemClickListener;
    private OnItemLongClickListener    onItemLongClickListener;

    private OnCreateContextMenu        onCreateContextMenu;
    private OnContextMenuClickListener onContextMenuClickListener;

    private Context context;

    private List<Entrega> listaEntregas;

    interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

    interface OnCreateContextMenu {
        void onCreateContextMenu(ContextMenu menu,
                                 View v,
                                 ContextMenu.ContextMenuInfo menuInfo,
                                 int position,
                                 MenuItem.OnMenuItemClickListener menuItemClickListener);
    }

    interface OnContextMenuClickListener {
        boolean onContextMenuItemClick(MenuItem menuItem, int position);
    }

    public class EntregaHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener,
            View.OnCreateContextMenuListener {

        public TextView textViewValorDiaHoraChegada;
        public TextView textViewValorDescricao;

        public CheckBox checkRetirada;

        public TextView textViewValorDiaHoraRetirada; // ✅ novo campo

        public EntregaHolder(@NonNull View itemView) {
            super(itemView);

            textViewValorDiaHoraChegada = itemView.findViewById(R.id.textViewValorDiaHoraChegada);
            textViewValorDescricao          = itemView.findViewById(R.id.textViewValorDescricao);

            checkRetirada               = itemView.findViewById(R.id.checkRetirada);
            textViewValorDiaHoraRetirada= itemView.findViewById(R.id.textViewValorDiaHoraRetirada); // ✅




            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {

            if (onItemClickListener != null){
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if (onItemLongClickListener != null){
                onItemLongClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            if (onCreateContextMenu != null){
                onCreateContextMenu.onCreateContextMenu(menu,
                        v,
                        menuInfo,
                        getAdapterPosition(),
                        onMenuItemClickListener);
            }
        }

        MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {

                if (onContextMenuClickListener != null){
                    onContextMenuClickListener.onContextMenuItemClick(item, getAdapterPosition());
                    return true;
                }
                return false;
            }
        };
    }

    public EntregaRecyclerViewAdapter(Context context, List<Entrega> listaEntregas) {
        this.context        = context;
        this.listaEntregas = listaEntregas;
    }

    @NonNull
    @Override
    public EntregaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.linha_lista_entregas, parent, false);

        return new EntregaHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull EntregaHolder holder, int position) {
        Entrega entrega = listaEntregas.get(position);

        holder.textViewValorDiaHoraChegada.setText(
                UtilsLocalDateTime.formatLocalDateTime(entrega.getDiaHoraChegada())
        );

        holder.textViewValorDescricao.setText(entrega.getDescricao());

        // Se já tem retirada, checkbox marcado e mostra a data
        holder.checkRetirada.setOnCheckedChangeListener(null);
        boolean retiradaFeita = entrega.getDiaHoraRetirada() != null;
        holder.checkRetirada.setChecked(retiradaFeita);

        if (retiradaFeita) {
            holder.textViewValorDiaHoraRetirada.setText(
                    context.getString(R.string.retirada_item) +
                            UtilsLocalDateTime.formatLocalDateTime(entrega.getDiaHoraRetirada())
            );
        } else {
            holder.textViewValorDiaHoraRetirada.setText(
                    context.getString(R.string.retirada_item)
            );
        }

        // Listener do checkbox
        holder.checkRetirada.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MoradoresDatabase db = MoradoresDatabase.getInstance(context);

            if (isChecked) {
                entrega.setDiaHoraRetirada(LocalDateTime.now());
            } else {
                entrega.setDiaHoraRetirada(null);
            }

            db.getEntregaDao().update(entrega);
            notifyItemChanged(position); // ✅ atualiza a linha para refletir a mudança
        });
    }

    /* Importante não esquecer de retornar a quantidade de elementos na lista */
    @Override
    public int getItemCount() {
        return listaEntregas.size();
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public OnCreateContextMenu getOnCreateContextMenu() {
        return onCreateContextMenu;
    }

    public void setOnCreateContextMenu(OnCreateContextMenu onCreateContextMenu) {
        this.onCreateContextMenu = onCreateContextMenu;
    }

    public OnContextMenuClickListener getOnContextMenuClickListener() {
        return onContextMenuClickListener;
    }

    public void setOnContextMenuClickListener(OnContextMenuClickListener onContextMenuClickListener) {
        this.onContextMenuClickListener = onContextMenuClickListener;
    }
}
