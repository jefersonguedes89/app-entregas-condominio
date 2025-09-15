package br.utfpr.jefersonguedes.entregascondominio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.utfpr.jefersonguedes.entregascondominio.modelo.Morador;

public class MoradorAdapter extends BaseAdapter {

    private Context context;
    private List<Morador> moradorList;

    private String[] blocos;



    private static class MoradorHolder {
        public TextView textViewValorNome;
        public TextView textViewNumApt;

        public TextView textViewValorAptAlugado;

        public TextView textViewValorNumBloco;

        public TextView textViewValorGenero;
    }

    public MoradorAdapter(Context context, List<Morador> moradorList){
        this.context = context;
        this.moradorList = moradorList;

        this.blocos = context.getResources().getStringArray(R.array.blocos);

    }

    @Override
    public int getCount() {
        return moradorList.size();
    }

    @Override
    public Object getItem(int position) {
        return moradorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MoradorHolder holder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lista_linha_moradores, parent, false);

            holder = new MoradorHolder();
            holder.textViewValorNome = convertView.findViewById(R.id.textViewValorNome);
            holder.textViewNumApt = convertView.findViewById(R.id.textViewValorNumApt);
            holder.textViewValorNumBloco = convertView.findViewById(R.id.textViewValorBloco);
            holder.textViewValorGenero = convertView.findViewById(R.id.textViewValorGenero);
            holder.textViewValorAptAlugado = convertView.findViewById(R.id.textViewAptAlugado);


            convertView.setTag(holder);
        } else {
            holder = (MoradorHolder) convertView.getTag();
        }

        Morador morador = moradorList.get(position);

        holder.textViewValorNome.setText(morador.getNome());
        holder.textViewNumApt.setText(String.valueOf(morador.getNumApt()));
        if(morador.isAptAlugado()){
            holder.textViewValorAptAlugado.setText(R.string.sim);
        } else {
            holder.textViewValorAptAlugado.setText(R.string.nao);
        }

        holder.textViewValorNumBloco.setText(blocos[morador.getBloco()]);

        switch (morador.getGenero()){
            case Masculino:
                holder.textViewValorGenero.setText(R.string.masculino);
                break;
            case Feminino:
                holder.textViewValorGenero.setText(R.string.feminino);
                break;


        }


        return convertView;
    }
}
